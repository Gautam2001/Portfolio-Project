package com.portfolio.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class Link {

    @NotBlank(message = "Name is required in Link")
    private String name;

    @NotBlank(message = "URL is required in Link")
    private String url;
}
