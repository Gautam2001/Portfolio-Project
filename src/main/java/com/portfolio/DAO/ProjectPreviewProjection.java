package com.portfolio.DAO;

import java.util.List;

import com.portfolio.DTO.Image;
import com.portfolio.DTO.Link;

public interface ProjectPreviewProjection {
	
	String getId();
    String getTitle();
    Image getTitleImage();
    String getShortDesc();
    List<Link> getLinks();

}
