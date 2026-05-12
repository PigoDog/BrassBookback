package com.brassbook.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank()
    @Size(max = 100)
    @Email(message = "Введите корректную почту")
    private String email;
    @NotBlank()
    @Size(min = 8, max = 25)
    private String password;
}
