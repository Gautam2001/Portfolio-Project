package com.portfolio.Service;

import java.util.HashMap;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.portfolio.DTO.IdDTO;
import com.portfolio.DTO.UsernameDTO;

import jakarta.validation.Valid;

@Component
public interface ProjectService {

	HashMap<String, Object> joinPortfolioApp(@Valid UsernameDTO usernameDTO);

	HashMap<String, Object> uploadAboutMe(MultipartFile aboutMe);

	HashMap<String, Object> getAboutMe();

	HashMap<String, Object> uploadProjects(MultipartFile projects);

	HashMap<String, Object> getAllProjects();

	HashMap<String, Object> getProjectById(@Valid IdDTO idDTO);

	HashMap<String, Object> deleteProjectById(@Valid IdDTO idDTO);

}
