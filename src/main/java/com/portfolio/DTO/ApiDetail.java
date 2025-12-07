package com.portfolio.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ApiDetail {

	@NotBlank(message = "endpoint is required.")
	private String endpoint;

	@NotBlank(message = "method is required.")
	private String method;

	@NotBlank(message = "purpose is required.")
	private String purpose;

	@NotBlank(message = "auth is required.")
	private String auth;
}
