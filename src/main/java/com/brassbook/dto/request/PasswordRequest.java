package com.brassbook.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PasswordRequest {
    @NotBlank()
    @Size(min = 8, max = 25)
    private String password;
    @NotNull()
    private Long id;
}
