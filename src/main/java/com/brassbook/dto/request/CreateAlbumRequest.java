package com.brassbook.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateAlbumRequest {

    @NotBlank(message = "Название альбома обязательно")
    @Size(max = 50, message = "Название альбома не должно превышать 50 символов")
    private String name;
}