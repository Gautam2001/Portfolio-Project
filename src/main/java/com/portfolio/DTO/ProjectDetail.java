package com.portfolio.DTO;

import java.util.List;

import jakarta.validation.Valid;
import lombok.Data;

@Data
public class ProjectDetail {

    // 2–3 paragraphs
	private List<String> overview;

    // Short bullet-like strings
    private List<String> keyFeatures;

    // Single paragraph OR split into frontend/backend
    private List<String> techStack;

    // One paragraph max
    private String engineeringHighlight;
    
    // List of tables
    private List<TableDetail> tables;
    
    // List of APIs
    private List<ApiDetail> Apis;

    // one video only
    @Valid
    private Video video;
    
    // Screenshots only (images are enough)
    @Valid
    private List<Image> screenshots;
}

