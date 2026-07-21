package com.raffs.LawInsight.util;

import com.raffs.LawInsight.domain.Client;
import com.raffs.LawInsight.domain.Contract;
import com.raffs.LawInsight.domain.ContractClause;
import com.raffs.LawInsight.domain.User;
import com.raffs.LawInsight.domain.enumeration.ClientType;
import com.raffs.LawInsight.domain.enumeration.ContractStatus;
import com.raffs.LawInsight.domain.enumeration.FileType;
import com.raffs.LawInsight.domain.enumeration.RiskLevel;
import com.raffs.LawInsight.domain.enumeration.UserRole;

public final class TestDataFactory {

    private TestDataFactory() {}

    public static User createUser(String suffix) {
        var user = new User();
        user.setEmail(suffix + "@lawfirm.com");
        user.setPassword("$2a$10$dummyBcryptHash");
        user.setFirstName("Test");
        user.setLastName("User");
        var barSuffix = suffix.length() > 12 ? suffix.substring(0, 12) : suffix;
        user.setBarNumber("OAB-" + barSuffix.toUpperCase());
        user.setRole(UserRole.ATTORNEY);
        return user;
    }

    public static Client createClient(String suffix, String documentNumber) {
        var client = new Client();
        client.setName(suffix + " Client");
        client.setClientType(ClientType.COMPANY);
        client.setEmail(suffix + "-client@test.com");
        client.setDocumentNumber(documentNumber);
        return client;
    }

    public static Contract createContract(String fileHash, User uploadedBy, Client client) {
        var contract = new Contract();
        contract.setTitle("Test Agreement");
        contract.setOriginalFileName("test.pdf");
        contract.setFileType(FileType.PDF);
        contract.setExtractedContent("Test content");
        contract.setFileHash(fileHash);
        contract.setStatus(ContractStatus.UPLOADED);
        contract.setUploadedBy(uploadedBy);
        contract.setClient(client);
        return contract;
    }

    public static ContractClause createClause(int number, String title, String content, RiskLevel riskLevel, Contract contract) {
        var clause = new ContractClause();
        clause.setNumber(number);
        clause.setTitle(title);
        clause.setContent(content);
        clause.setRiskLevel(riskLevel);
        clause.setContract(contract);
        return clause;
    }
}
