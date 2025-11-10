package com.portfolio.Entity;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.portfolio.DTO.ExpAndEdu;
import com.portfolio.DTO.Image;
import com.portfolio.DTO.Link;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Document(collection = "AboutMe")
public class AboutMeEntity {

	@Id
	private String id;
	
	@NotBlank(message = "Name is required in About Me")
    private String name;

	@Valid
    @NotNull(message = "Image is required in About Me")
    private Image image;
    
    @NotBlank(message = "One Liner is required in About Me")
    private String oneLiner;
    
    @NotBlank(message = "Description is required in About Me")
    private String description;
    
    @Valid
    @NotNull(message = "Experience is required in About Me")
    private List<ExpAndEdu> experience;
    
    @Valid
    @NotNull(message = "Education is required in About Me")
    private List<ExpAndEdu> education;
    
    @NotEmpty(message = "Skills is required in About Me")
    private List<String> skills;
    
    @Valid
    @NotNull(message = "Contact is required in About Me")
    private List<Link> contact;
    
    @NotBlank(message = "uploadedBy is required in About Me")
    private String uploadedBy;
    private LocalDateTime uploadAt;

}
