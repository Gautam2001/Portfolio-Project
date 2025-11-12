package com.portfolio.DTO;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProjectSection {

	@NotBlank(message = "Name is required in Project Section")
	private String name;

	private String description;

	@Valid
	private List<Image> images;

	@Valid
	private List<Video> videos;

	@Valid
	private List<Link> links;
}
