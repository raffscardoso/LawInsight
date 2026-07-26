package com.raffs.LawInsight.dto;

import com.raffs.LawInsight.domain.enumeration.ClientType;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class ClientResponse {

    private Long id;
    private Long version;
    private Instant createdAt;
    private Instant lastModifiedAt;
    private String createdBy;
    private String lastModifiedBy;

    private String name;
    private ClientType clientType;
    private String email;
    private String phone;
    private String documentNumber;
    private String address;
    private String notes;
}
