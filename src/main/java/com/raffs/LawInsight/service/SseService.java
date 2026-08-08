package com.raffs.LawInsight.service;

import com.raffs.LawInsight.dto.SseEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Service
public class SseService {
    
    private final Map<Long, List<SseEmitter>> emitters = new ConcurrentHashMap<>();

    public void register(Long contractId, SseEmitter emitter) {
        emitters.computeIfAbsent(contractId, k -> new CopyOnWriteArrayList<>()).add(emitter);
        
        emitter.onCompletion(() -> removeEmitter(contractId, emitter));
        emitter.onTimeout(() -> removeEmitter(contractId, emitter));
        emitter.onError(e -> removeEmitter(contractId, emitter));
    }
    
    public void emitEvent(Long contractId, SseEvent event) {
        List<SseEmitter> contractEmitters = emitters.get(contractId);
        if (contractEmitters != null) {
            for (SseEmitter emitter : contractEmitters) {
                try {
                    emitter.send(SseEmitter.event()
                            .id(String.valueOf(event.getTimestamp().toEpochMilli()))
                            .name("contract-update")
                            .data(event));
                } catch (IOException e) {
                    log.warn("Failed to send SSE event for contract {}, removing emitter", contractId);
                    removeEmitter(contractId, emitter);
                }
            }
        }
    }

    private void removeEmitter(Long contractId, SseEmitter emitter) {
        List<SseEmitter> contractEmitters = emitters.get(contractId);
        if (contractEmitters != null) {
            contractEmitters.remove(emitter);
            if (contractEmitters.isEmpty()) {
                emitters.remove(contractId);
            }
        }
    }
}
