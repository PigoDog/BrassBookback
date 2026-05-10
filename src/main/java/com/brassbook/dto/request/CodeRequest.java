package com.brassbook.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CodeRequest {
    @NotBlank()
    @Size(max = 100)
    @Email
    private String email;
    @NotNull()
    private Boolean isConfirmed;
}
