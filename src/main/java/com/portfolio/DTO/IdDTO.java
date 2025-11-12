package com.portfolio.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class IdDTO {

	@NotBlank(message = "Id is required")
	private String id;
	
	private Long confCode;

}
