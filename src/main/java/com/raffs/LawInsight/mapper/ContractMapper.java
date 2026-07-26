package com.raffs.LawInsight.mapper;

import com.raffs.LawInsight.domain.Client;
import com.raffs.LawInsight.domain.Contract;
import com.raffs.LawInsight.domain.User;
import com.raffs.LawInsight.dto.ContractRequest;
import com.raffs.LawInsight.dto.ContractResponse;
import com.raffs.LawInsight.dto.ContractResponse.ClientSummary;
import com.raffs.LawInsight.dto.ContractResponse.UserSummary;
import com.raffs.LawInsight.dto.ContractSummary;
import org.springframework.stereotype.Component;

@Component
public class ContractMapper {

    public Contract toEntity(ContractRequest request, User uploadedBy, Client client) {
        var contract = new Contract();
        contract.setTitle(request.getTitle());
        contract.setOriginalFileName(request.getOriginalFileName());
        contract.setFileType(request.getFileType());
        contract.setExtractedContent(request.getExtractedContent());
        contract.setFileHash(request.getFileHash());
        contract.setUploadedBy(uploadedBy);
        contract.setClient(client);
        if (request.getStatus() != null) {
            contract.setStatus(request.getStatus());
        }
        return contract;
    }

    public ContractResponse toResponse(Contract contract) {
        var response = new ContractResponse();
        response.setId(contract.getId());
        response.setVersion(contract.getVersion());
        response.setCreatedAt(contract.getCreatedAt());
        response.setLastModifiedAt(contract.getLastModifiedAt());
        response.setCreatedBy(contract.getCreatedBy());
        response.setLastModifiedBy(contract.getLastModifiedBy());

        response.setTitle(contract.getTitle());
        response.setOriginalFileName(contract.getOriginalFileName());
        response.setFileType(contract.getFileType());
        response.setExtractedContent(contract.getExtractedContent());
        response.setFilePath(contract.getFilePath());
        response.setFileHash(contract.getFileHash());
        response.setStatus(contract.getStatus());

        if (contract.getUploadedBy() != null) {
            var userSummary = new UserSummary();
            userSummary.setId(contract.getUploadedBy().getId());
            userSummary.setEmail(contract.getUploadedBy().getEmail());
            userSummary.setFirstName(contract.getUploadedBy().getFirstName());
            userSummary.setLastName(contract.getUploadedBy().getLastName());
            response.setUploadedBy(userSummary);
        }

        if (contract.getClient() != null) {
            var clientSummary = new ClientSummary();
            clientSummary.setId(contract.getClient().getId());
            clientSummary.setName(contract.getClient().getName());
            clientSummary.setDocumentNumber(contract.getClient().getDocumentNumber());
            response.setClient(clientSummary);
        }

        return response;
    }

    public ContractSummary toSummary(Contract contract) {
        var summary = new ContractSummary();
        summary.setId(contract.getId());
        summary.setTitle(contract.getTitle());
        summary.setStatus(contract.getStatus());
        summary.setFileType(contract.getFileType());
        summary.setCreatedAt(contract.getCreatedAt());

        if (contract.getUploadedBy() != null) {
            summary.setUploadedByName(contract.getUploadedBy().getFirstName()
                    + " " + contract.getUploadedBy().getLastName());
        }

        if (contract.getClient() != null) {
            summary.setClientName(contract.getClient().getName());
        }

        return summary;
    }
}
