package com.portfolio.Service.Impl;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.portfolio.DAO.AboutMeDao;
import com.portfolio.DAO.ProjectDao;
import com.portfolio.DAO.ProjectPreviewProjection;
import com.portfolio.DAO.UserDAO;
import com.portfolio.DTO.IdDTO;
import com.portfolio.DTO.Image;
import com.portfolio.DTO.ProjectListWrapper;
import com.portfolio.DTO.ProjectSection;
import com.portfolio.DTO.UsernameDTO;
import com.portfolio.DTO.Video;
import com.portfolio.Entity.AboutMeEntity;
import com.portfolio.Entity.ProjectEntity;
import com.portfolio.Entity.UserEntity;
import com.portfolio.Service.ProjectService;
import com.portfolio.ServiceExt.CallLoginService;
import com.portfolio.Utility.AppException;
import com.portfolio.Utility.CommonUtils;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import jakarta.validation.Validator;

@Service
public class ProjectServiceImpl implements ProjectService {

	@Autowired
	Validator validator;

	@Autowired
	AboutMeDao aboutMeDao;

	@Autowired
	ProjectDao ProjectDao;
	
	@Autowired
	UserDAO userDAO;
	
	@Autowired
	CallLoginService callLoginService;
	
	@Value("${confirmation.code}")
	private Long confirmationCode;
	
	@Override
	public HashMap<String, Object> joinPortfolioApp(@Valid UsernameDTO usernameDTO) {
		String username = CommonUtils.normalizeUsername(usernameDTO.getUsername());
		CommonUtils.logMethodEntry(this, "Join Portfolio Request for: " + username);
		HashMap<String, Object> response = new HashMap<>();

		Optional<String> nameOpt = callLoginService.checkUserExistsInLoginService(username);

		if (nameOpt.isEmpty()) {
			return CommonUtils.prepareResponse(response, "User does not exist, Please Signup.", false);
		}
		String name = nameOpt.get();

		CommonUtils.ensureUserDoesNotExist(userDAO, username);

		UserEntity user = new UserEntity();
		user.setUsername(username);
		user.setName(name);
		UserEntity savedUser = userDAO.save(user);
		if (savedUser == null || savedUser.getId() == null) {
			throw new AppException("Failed to Join. Please try again.", HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return CommonUtils.prepareResponse(response, "User successfully joined Portfolio.", true);
	}

	@Override
	public HashMap<String, Object> uploadAboutMe(MultipartFile aboutMeFile) {
		CommonUtils.logMethodEntry(this, "Uploading About Me");
		HashMap<String, Object> response = new HashMap<>();

		if (aboutMeFile.isEmpty()) {
			throw new AppException("No data in file found.", HttpStatus.BAD_REQUEST);
		}

		if (!Objects.requireNonNull(aboutMeFile.getOriginalFilename()).endsWith(".json")) {
			throw new AppException("Invalid file type. Only .json files are allowed.", HttpStatus.BAD_REQUEST);
		}

		try {
			ObjectMapper objectMapper = new ObjectMapper();
			AboutMeEntity aboutMe = objectMapper.readValue(aboutMeFile.getInputStream(), AboutMeEntity.class);

			Set<ConstraintViolation<AboutMeEntity>> violations = validator.validate(aboutMe);
			if (!violations.isEmpty()) {
				String message = violations.stream().map(ConstraintViolation::getMessage)
						.collect(Collectors.joining(", "));
				throw new AppException("Validation failed: " + message, HttpStatus.BAD_REQUEST);
			}

			// If AboutMe already exists
			Optional<AboutMeEntity> existing = aboutMeDao.findTopByOrderByUploadAtDesc();
			aboutMe.setUploadAt(LocalDateTime.now());

			existing.ifPresent(e -> aboutMe.setId(e.getId()));

			aboutMeDao.save(aboutMe);

			response.put("uploadedBy", aboutMe.getUploadedBy());
			response.put("uploadAt", aboutMe.getUploadAt());

			String message = existing.isPresent() ? "About Me updated successfully" : "About Me uploaded successfully";

			return CommonUtils.prepareResponse(response, message, true);

		} catch (MismatchedInputException mie) {
			throw new AppException("JSON structure is invalid or missing fields: " + mie.getOriginalMessage(),
					HttpStatus.BAD_REQUEST);
		} catch (JsonParseException jpe) {
			throw new AppException("Failed to parse JSON: " + jpe.getOriginalMessage(), HttpStatus.BAD_REQUEST);
		} catch (IOException e) {
			throw new AppException("Error reading JSON file: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public HashMap<String, Object> getAboutMe() {
		CommonUtils.logMethodEntry(this, "Fetching AboutMe");
		HashMap<String, Object> response = new HashMap<>();

		Optional<AboutMeEntity> aboutMe = aboutMeDao.findTopByOrderByUploadAtDesc();

		if (aboutMe.isPresent()) {
			response.put("aboutMe", aboutMeDao.findTopByOrderByUploadAtDesc());
			List<ProjectPreviewProjection> projectPreviews = ProjectDao.findAllBy();
			response.put("projects", projectPreviews);
			return CommonUtils.prepareResponse(response, "aboutMe fetched Successfully.", true);
		} else {
			throw new AppException("AboutMe not found, check DB and try again.", HttpStatus.BAD_REQUEST);
		}
	}

	@Override
	public HashMap<String, Object> uploadProjects(MultipartFile projects) {
		CommonUtils.logMethodEntry(this, "Uploading Projects");
		HashMap<String, Object> response = new HashMap<>();

		if (projects.isEmpty()) {
			throw new AppException("No data in File found.", HttpStatus.BAD_REQUEST);
		}

		if (!Objects.requireNonNull(projects.getOriginalFilename()).endsWith(".json")) {
			throw new AppException("Invalid file type. Only .json files are allowed", HttpStatus.BAD_REQUEST);
		}

		try {
			ObjectMapper objectMapper = new ObjectMapper();
			ProjectListWrapper wrapper = objectMapper.readValue(projects.getInputStream(), ProjectListWrapper.class);
			List<ProjectEntity> projectList = wrapper.getProjects();

			// Fetch all existing titles from DB
			Set<String> existingTitles = ProjectDao.findAll().stream().map(ProjectEntity::getTitle)
					.collect(Collectors.toSet());

			List<ProjectEntity> successfulProjects = new ArrayList<>();
			List<HashMap<String, String>> failedProjects = new ArrayList<>();
			Set<String> titlesInFile = new HashSet<>();
			LocalDateTime now = LocalDateTime.now();

			for (ProjectEntity project : projectList) {
				HashMap<String, String> errorMap = new HashMap<>();
				Set<ConstraintViolation<ProjectEntity>> violations = validator.validate(project);
				if (!violations.isEmpty()) {
					String message = violations.stream().map(ConstraintViolation::getMessage)
							.collect(Collectors.joining(", "));
					errorMap.put("title", project.getTitle() != null ? project.getTitle() : "Untitled");
					errorMap.put("message", "Validation failed: " + message);
					failedProjects.add(errorMap);
					continue;
				}

				project.setUploadedBy(wrapper.getUploadedBy());
				project.setUploadAt(now);

				// Check duplicate in uploaded file
				if (!titlesInFile.add(project.getTitle())) {
					errorMap.put("title", project.getTitle());
					errorMap.put("message", "Duplicate project in uploaded file");
					failedProjects.add(errorMap);
					continue;
				}

				// Check duplicate in DB
				if (existingTitles.contains(project.getTitle())) {
					errorMap.put("title", project.getTitle());
					errorMap.put("message", "Project already exists in database");
					failedProjects.add(errorMap);
					continue;
				}

				try {
					validateProject(project);
					successfulProjects.add(project);
					existingTitles.add(project.getTitle());
				} catch (ValidationException ve) {
					errorMap.put("title", project.getTitle());
					errorMap.put("message", ve.getMessage());
					failedProjects.add(errorMap);
				}
			}

			if (!successfulProjects.isEmpty()) {
				ProjectDao.saveAll(successfulProjects);
			}

			response.put("successfulCount", successfulProjects.size());
			response.put("failedCount", failedProjects.size());
			response.put("failedProjects", failedProjects);

			boolean success = !successfulProjects.isEmpty();
			return CommonUtils.prepareResponse(response, "Upload status", success);
		} catch (MismatchedInputException mie) {
			throw new AppException("JSON structure is invalid or missing fields: " + mie.getOriginalMessage(),
					HttpStatus.BAD_REQUEST);
		} catch (JsonParseException jpe) {
			throw new AppException("Failed to parse JSON: " + jpe.getOriginalMessage(), HttpStatus.BAD_REQUEST);
		} catch (IOException e) {
			throw new AppException("Error reading JSON file: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	private void validateProject(ProjectEntity project) {
		CommonUtils.logMethodEntry(this, "Validating Project: " + project.getTitle());

		if (project.getSections() == null || project.getSections().isEmpty()) {
			throw new ValidationException("Project must have at least one section");
		}

		for (ProjectSection section : project.getSections()) {
			String type = section.getName() != null ? section.getName().toLowerCase() : "";

			// Demo section must have at least one video
			if ("demo".equals(type) && (section.getVideos() == null || section.getVideos().isEmpty())) {
				throw new ValidationException("Demo section must include at least one video");
			}

			// Architecture section must have at least one image
			if ("architecture".equals(type) && (section.getImages() == null || section.getImages().isEmpty())) {
				throw new ValidationException("Architecture section must include at least one image");
			}

			// Validate image URLs
			if (section.getImages() != null) {
				for (Image img : section.getImages()) {
					if (img.getUrl() == null || !img.getUrl().startsWith("http")) {
						throw new ValidationException("Invalid image URL: " + img.getUrl());
					}
				}
			}

			// Optional: validate video URLs similarly
			if (section.getVideos() != null) {
				for (Video vid : section.getVideos()) {
					if (vid.getUrl() == null || !vid.getUrl().startsWith("http")) {
						throw new ValidationException("Invalid video URL: " + vid.getUrl());
					}
				}
			}
		}
	}

	@Override
	public HashMap<String, Object> getAllProjects() {
		CommonUtils.logMethodEntry(this, "Fetching all Projects");
		HashMap<String, Object> response = new HashMap<>();

		List<ProjectEntity> Projects = ProjectDao.findAll();
		if (!Projects.isEmpty()) {
			response.put("projects", Projects);
			response.put("projectCount", Projects.size());
			return CommonUtils.prepareResponse(response, "Projects fetched Successfully.", true);
		} else {
			return CommonUtils.prepareResponse(response, "No projetcs found, check DB and try again.", false);
		}
	}

	@Override
	public HashMap<String, Object> getProjectById(@Valid IdDTO idDTO) {
		CommonUtils.logMethodEntry(this, "Fetching Project by Id: " + idDTO.getId());
		HashMap<String, Object> response = new HashMap<>();

		Optional<ProjectEntity> project = ProjectDao.findById(idDTO.getId());
		if (project.isPresent()) {
			response.put("projects", project);
			return CommonUtils.prepareResponse(response, "Project with Id: " + idDTO.getId() + " fetched Successfully.", true);
		} else {
			return CommonUtils.prepareResponse(response, "No projetc found with Id: " + idDTO.getId() + ", check DB and try again.", false);
		}
	}

	@Override
	public HashMap<String, Object> deleteProjectById(@Valid IdDTO idDTO) {
		CommonUtils.logMethodEntry(this, "Deleting Project by Id: " + idDTO.getId());
		HashMap<String, Object> response = new HashMap<>();

		Optional<ProjectEntity> project = ProjectDao.findById(idDTO.getId());
		if (project.isPresent()) {
			if (Objects.equals(confirmationCode, idDTO.getConfCode())) {
				ProjectDao.deleteById(idDTO.getId());
				return CommonUtils.prepareResponse(response, "Project with Id: " + idDTO.getId() + " deleted Successfully.", true);
			}else {
				return CommonUtils.prepareResponse(response, "Verification failed, cannot delete project with Id: " + idDTO.getId(), false);
			}
		} else {
			return CommonUtils.prepareResponse(response, "No projetc found with Id: " + idDTO.getId() + ", check DB and try again.", false);
		}
	}

}
