package com.portfolio.Service;

import java.util.HashMap;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public interface ProjectService {
	
	HashMap<String, Object> uploadAboutMe(MultipartFile aboutMe);
	
	HashMap<String, Object> getAboutMe();

	HashMap<String, Object> uploadProjects(MultipartFile projects);
	
	HashMap<String, Object> getAllProjects();

}
