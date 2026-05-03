package com.brassbook.dto.request;

import com.brassbook.enums.UserRole;
import com.brassbook.enums.UserStatus;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegistrationRequest {
    @NotBlank()
    @Size(max = 100)
    @Email
    private String email;
    @NotBlank()
    @Size(min = 8, max = 25)
    private String password;
    @NotNull()
    private UserRole roleName;
    @NotNull()
    private UserStatus status;
    @NotBlank()
    private String code;
    @Size(max = 25)
    private String firstName;
    @Size(max = 25)
    private String lastName;
    @Size(max = 50)
    private String companyName;
    @Size(max = 50)
    private String profession;
    private Long inn;
}
