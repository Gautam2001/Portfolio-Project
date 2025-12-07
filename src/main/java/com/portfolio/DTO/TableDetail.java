package com.portfolio.DTO;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TableDetail {

	@NotBlank(message = "name is required.")
	private String name;

	@NotBlank(message = "description is required.")
	private String description;

	@NotBlank(message = "coulmns is required.")
	private List<String> columns;

}
