package com.raffs.LawInsight.dto;

import com.raffs.LawInsight.domain.enumeration.UserRole;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class UserResponse {

    private Long id;
    private Long version;
    private Instant createdAt;
    private Instant lastModifiedAt;
    private String createdBy;
    private String lastModifiedBy;

    private String email;
    private String firstName;
    private String lastName;
    private String barNumber;
    private UserRole role;
    private boolean active;
}
