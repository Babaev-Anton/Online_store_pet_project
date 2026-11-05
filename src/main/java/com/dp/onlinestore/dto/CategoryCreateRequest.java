package com.dp.onlinestore.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CategoryCreateRequest {

    @NotBlank(message = "Название категории не может быть пустым")
    @Size(max = 120, message = "Название не может быть длиннее 120 символов")
    private String name;

    @NotBlank(message = "Slug не может быть пустым")
    @Size(max = 150, message = "Slug не может быть длиннее 150 символов")
    private String slug;
}
