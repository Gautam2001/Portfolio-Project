package com.portfolio.DTO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ExpAndEdu {

	@NotBlank(message = "Position is required in ExpAndEdu")
	private String position;

	@NotBlank(message = "Name is required in ExpAndEdu")
	private String name;

	@NotBlank(message = "Year is required in ExpAndEdu")
	private String year;

	@NotBlank(message = "Description is required in ExpAndEdu")
	private String description;

	@Valid
	@NotNull(message = "Logo is required in ExpAndEdu")
	private Image logo;

}
