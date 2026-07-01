package com.raffs.LawInsight.domain;

import com.raffs.LawInsight.domain.enumeration.ClientType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "clients")
public class Client extends BaseEntity {

    @Column(nullable = false, length = 200)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ClientType clientType;

    @Column(length = 320)
    private String email;

    @Column(length = 30)
    private String phone;

    @Column(nullable = false, unique = true, length = 20)
    private String documentNumber;

    @Column(length = 500)
    private String address;

    @Column(columnDefinition = "TEXT")
    private String notes;
}
