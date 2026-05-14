package com.brassbook.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateProfileRequest {

    @Size(max = 100, message = "Имя не должно превышать 100 символов")
    private String displayName;

    @Size(max = 100, message = "Фамилия не должна превышать 100 символов")
    private String displaySurname;

    @Size(max = 50, message = "Почта не должна превышать 50 символов")
    private String email;



}