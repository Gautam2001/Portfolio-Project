package com.portfolio.DTO;

import java.util.List;

import com.portfolio.Entity.ProjectEntity;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProjectListWrapper {

	@NotBlank(message = "uploadedBy is required in ProjectListWrapper")
	private String uploadedBy;

	@Valid
	@NotNull(message = "Projects is required in ProjectListWrapper")
	private List<ProjectEntity> projects;
}
