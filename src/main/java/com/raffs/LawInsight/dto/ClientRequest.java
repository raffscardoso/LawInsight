package com.raffs.LawInsight.dto;

import com.raffs.LawInsight.domain.enumeration.ClientType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientRequest {

    @NotBlank
    @Size(max = 200)
    private String name;

    @NotNull
    private ClientType clientType;

    @Email
    @Size(max = 320)
    private String email;

    @Pattern(regexp = "^\\+?[0-9.\\-() ]{7,30}$")
    @Size(max = 30)
    private String phone;

    @NotBlank
    @Size(max = 20)
    private String documentNumber;

    @Size(max = 500)
    private String address;

    private String notes;
}
