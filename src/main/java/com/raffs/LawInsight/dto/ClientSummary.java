package com.raffs.LawInsight.dto;

import com.raffs.LawInsight.domain.enumeration.ClientType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientSummary {

    private Long id;
    private String name;
    private ClientType clientType;
    private String documentNumber;
}
