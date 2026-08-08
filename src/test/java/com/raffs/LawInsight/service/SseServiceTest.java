package com.raffs.LawInsight.service;

import com.raffs.LawInsight.domain.enumeration.ContractStatus;
import com.raffs.LawInsight.dto.SseEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class SseServiceTest {

    private SseService sseService;

    @BeforeEach
    void setUp() {
        sseService = new SseService();
    }

    @Test
    void shouldRegisterEmitterAndEmitEvent() throws IOException {
        var emitter = mock(SseEmitter.class);
        sseService.register(1L, emitter);

        var event = SseEvent.builder()
                .contractId(1L)
                .status(ContractStatus.PROCESSING)
                .message("Test")
                .build();

        sseService.emitEvent(1L, event);

        verify(emitter).send(any(SseEmitter.SseEventBuilder.class));
    }

    @Test
    void shouldNotThrowWhenEmittingWithNoSubscribers() {
        var event = SseEvent.builder()
                .contractId(1L)
                .status(ContractStatus.PROCESSING)
                .message("Test")
                .build();

        assertDoesNotThrow(() -> sseService.emitEvent(1L, event));
    }

    @Test
    void shouldRemoveEmitterOnError() throws IOException {
        var emitter = mock(SseEmitter.class);
        sseService.register(1L, emitter);

        doThrow(new IOException("Connection broken")).when(emitter).send(any(SseEmitter.SseEventBuilder.class));

        var event = SseEvent.builder()
                .contractId(1L)
                .status(ContractStatus.PROCESSING)
                .message("Test")
                .build();

        // The service should catch the exception and remove the emitter
        sseService.emitEvent(1L, event);

        // A second emit shouldn't try to send via the removed emitter
        sseService.emitEvent(1L, event);

        // Still only 1 invocation since the second time the emitter wasn't there
        verify(emitter, org.mockito.Mockito.times(1)).send(any(SseEmitter.SseEventBuilder.class));
    }
}
