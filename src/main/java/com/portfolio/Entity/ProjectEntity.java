package com.portfolio.Entity;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.portfolio.DTO.Image;
import com.portfolio.DTO.Link;
import com.portfolio.DTO.ProjectSection;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Document(collection = "Project")
public class ProjectEntity {

	@Id
	private String id;

	@NotBlank(message = "Title is required in Project")
	private String title;

	@NotNull(message = "Title Image is required in Project")
	private Image titleImage;

	@NotBlank(message = "Short Description is required in Project")
	private String shortDesc;

	@Valid
	private List<Link> links;

	@Valid
	@NotEmpty(message = "Project must contain at least one section in Project")
	private List<ProjectSection> sections;

	private String uploadedBy;
	
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private LocalDateTime uploadAt;
}
