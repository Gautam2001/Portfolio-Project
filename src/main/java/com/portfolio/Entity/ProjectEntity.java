package com.portfolio.Entity;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.portfolio.DTO.Image;
import com.portfolio.DTO.Link;
import com.portfolio.DTO.ProjectDetail;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Document(collection = "Project")
@Data
public class ProjectEntity {

    @Id
    private String id;

    @NotBlank
    private String title;

    @NotNull
    private Image titleImage;

    @NotBlank
    private String shortDesc;

    // GitHub frontend/backend + demo link
    @Valid
    private List<Link> links;

    // The full detail for the project page
    @Valid
    @NotNull
    private ProjectDetail detail;

    private String uploadedBy;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime uploadAt;
}

