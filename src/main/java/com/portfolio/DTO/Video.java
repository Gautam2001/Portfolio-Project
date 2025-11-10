package com.portfolio.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class Video {

	@NotBlank(message = "URL is required in Video")
	private String url;

	private String name;
	
	private String description;
}
