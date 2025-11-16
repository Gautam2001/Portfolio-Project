package com.portfolio.DTO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MyData {

	@NotBlank(message = "Name is required in About Me")
	private String name;

	@Valid
	@NotNull(message = "Image is required in About Me")
	private Image image;

	@NotBlank(message = "One Liner is required in About Me")
	private String oneLiner;

	@NotBlank(message = "Description is required in About Me")
	private String description;
}
