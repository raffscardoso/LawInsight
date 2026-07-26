package com.raffs.LawInsight.mapper;

import com.raffs.LawInsight.domain.Client;
import com.raffs.LawInsight.dto.ClientRequest;
import com.raffs.LawInsight.dto.ClientResponse;
import com.raffs.LawInsight.dto.ClientSummary;
import org.springframework.stereotype.Component;

@Component
public class ClientMapper {

    public Client toEntity(ClientRequest request) {
        var client = new Client();
        client.setName(request.getName());
        client.setClientType(request.getClientType());
        client.setEmail(request.getEmail());
        client.setPhone(request.getPhone());
        client.setDocumentNumber(request.getDocumentNumber());
        client.setAddress(request.getAddress());
        client.setNotes(request.getNotes());
        return client;
    }

    public ClientResponse toResponse(Client client) {
        var response = new ClientResponse();
        response.setId(client.getId());
        response.setVersion(client.getVersion());
        response.setCreatedAt(client.getCreatedAt());
        response.setLastModifiedAt(client.getLastModifiedAt());
        response.setCreatedBy(client.getCreatedBy());
        response.setLastModifiedBy(client.getLastModifiedBy());

        response.setName(client.getName());
        response.setClientType(client.getClientType());
        response.setEmail(client.getEmail());
        response.setPhone(client.getPhone());
        response.setDocumentNumber(client.getDocumentNumber());
        response.setAddress(client.getAddress());
        response.setNotes(client.getNotes());
        return response;
    }

    public ClientSummary toSummary(Client client) {
        var summary = new ClientSummary();
        summary.setId(client.getId());
        summary.setName(client.getName());
        summary.setClientType(client.getClientType());
        summary.setDocumentNumber(client.getDocumentNumber());
        return summary;
    }
}
