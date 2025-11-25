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
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.portfolio.DAO.AboutMeDao;
import com.portfolio.DAO.ContactUsDao;
import com.portfolio.DAO.ProjectDao;
import com.portfolio.DAO.ProjectPreviewProjection;
import com.portfolio.DAO.UserDAO;
import com.portfolio.DTO.ContactUsDTO;
import com.portfolio.DTO.IdDTO;
import com.portfolio.DTO.Image;
import com.portfolio.DTO.ProjectListWrapper;
import com.portfolio.DTO.ProjectSection;
import com.portfolio.DTO.UsernameDTO;
import com.portfolio.DTO.Video;
import com.portfolio.Entity.AboutMeEntity;
import com.portfolio.Entity.ContactUsEntity;
import com.portfolio.Entity.ProjectEntity;
import com.portfolio.Entity.UserEntity;
import com.portfolio.Service.ProjectService;
import com.portfolio.ServiceExt.CallLoginService;
import com.portfolio.Utility.AppException;
import com.portfolio.Utility.CommonUtils;
import com.portfolio.Utility.EmailService;

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
	ContactUsDao contactUsDao;

	@Autowired
	CallLoginService callLoginService;
	
	@Autowired
	private EmailService emailService;
	
	@Value("${spring.mail.username}")
	private String email;
	
	@Override
	public HashMap<String, Object> userExistsCheck(@Valid UsernameDTO usernameDTO) {
		String username = CommonUtils.normalizeUsername(usernameDTO.getUsername());
		CommonUtils.logMethodEntry(this, "User Exists Check Request for: " + username);
		HashMap<String, Object> response = new HashMap<>();

		Optional<UserEntity> userOpt = userDAO.findTopByUsername(username);
		if (userOpt.isPresent()) {
			UserEntity user = userOpt.get();
			response.put("id", user.getId());
			response.put("name", user.getUsername());
			response.put("joinedAt", user.getJoinedAt());
			return CommonUtils.prepareResponse(response, "User exists in Portfolio.", true);
		} else {
			return CommonUtils.prepareResponse(response, "User does not exists in Portfolio.", false);
		}
	}

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
	    	ObjectMapper objectMapper = JsonMapper.builder()
	    	        .addModule(new JavaTimeModule())
	    	        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
	    	        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
	    	        .build();

	        AboutMeEntity aboutMe = objectMapper.readValue(aboutMeFile.getInputStream(), AboutMeEntity.class);

	        // Validation
	        Set<ConstraintViolation<AboutMeEntity>> violations = validator.validate(aboutMe);
	        if (!violations.isEmpty()) {
	            String message = violations.stream()
	                    .map(ConstraintViolation::getMessage)
	                    .collect(Collectors.joining(", "));
	            throw new AppException("Validation failed: " + message, HttpStatus.BAD_REQUEST);
	        }

	        // Generate sections METADATA
	        List<String> sections = new ArrayList<>();

	        if (aboutMe.getMyData() != null) {
	            sections.add("Home");
	        }

	        if (aboutMe.getExperience() != null && !aboutMe.getExperience().isEmpty()) {
	            sections.add("Experience");
	        }

	        if (aboutMe.getEducation() != null && !aboutMe.getEducation().isEmpty()) {
	            sections.add("Education");
	        }

	        if (aboutMe.getSkills() != null && !aboutMe.getSkills().isEmpty()) {
	            sections.add("Skills");
	        }

	        if (aboutMe.getContact() != null && !aboutMe.getContact().isEmpty()) {
	            sections.add("Contact");
	        }

	        aboutMe.setSections(sections);

	        // Handle existing document
	        Optional<AboutMeEntity> existing = aboutMeDao.findTopByOrderByUploadAtDesc();
	        aboutMe.setUploadAt(LocalDateTime.now());

	        existing.ifPresent(e -> aboutMe.setId(e.getId()));

	        // Save
	        aboutMeDao.save(aboutMe);

	        response.put("uploadedBy", aboutMe.getUploadedBy());
	        response.put("uploadAt", aboutMe.getUploadAt());

	        String message = existing.isPresent()
	                ? "About Me updated successfully"
	                : "About Me uploaded successfully";

	        return CommonUtils.prepareResponse(response, message, true);

	    } catch (MismatchedInputException mie) {
	        throw new AppException(
	                "JSON structure is invalid or missing fields: " + mie.getOriginalMessage(),
	                HttpStatus.BAD_REQUEST
	        );
	    } catch (JsonParseException jpe) {
	        throw new AppException(
	                "Failed to parse JSON: " + jpe.getOriginalMessage(),
	                HttpStatus.BAD_REQUEST
	        );
	    } catch (IOException e) {
	        throw new AppException(
	                "Error reading JSON file: " + e.getMessage(),
	                HttpStatus.INTERNAL_SERVER_ERROR
	        );
	    }
	}


	@Override
	public HashMap<String, Object> getAboutMe() {
	    CommonUtils.logMethodEntry(this, "Fetching AboutMe");
	    HashMap<String, Object> response = new HashMap<>();

	    Optional<AboutMeEntity> aboutMeOpt = aboutMeDao.findTopByOrderByUploadAtDesc();

	    if (aboutMeOpt.isEmpty()) {
	        throw new AppException("AboutMe not found, check DB and try again.", HttpStatus.BAD_REQUEST);
	    }

	    AboutMeEntity aboutMe = aboutMeOpt.get();

	    List<ProjectPreviewProjection> projectPreviews = ProjectDao.findAllBy();
	    List<String> sections = new ArrayList<>(aboutMe.getSections()); // existing stored sections

	    if (projectPreviews != null && !projectPreviews.isEmpty()) {
	        response.put("projects", projectPreviews);

	        int insertIndex = sections.size();
	        int contactIndex = sections.indexOf("Contact");
	        int certificatesIndex = sections.indexOf("Certificates");

	        if (contactIndex != -1 && certificatesIndex != -1) {
	            insertIndex = Math.min(contactIndex, certificatesIndex);
	        } else if (contactIndex != -1) {
	            insertIndex = contactIndex;
	        } else if (certificatesIndex != -1) {
	            insertIndex = certificatesIndex;
	        }

	        sections.add(insertIndex, "Projects");
	    }

	    aboutMe.setSections(sections);
	    response.put("aboutMe", aboutMe);

	    return CommonUtils.prepareResponse(response, "aboutMe fetched Successfully.", true);
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
			ObjectMapper objectMapper = JsonMapper.builder()
	    	        .addModule(new JavaTimeModule())
	    	        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
	    	        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
	    	        .build();

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
	public HashMap<String, Object> downloadProjectById(@Valid IdDTO idDTO) {
		CommonUtils.logMethodEntry(this, "Fetching Project by Id: " + idDTO.getId());
		HashMap<String, Object> response = new HashMap<>();

		Optional<ProjectEntity> project = ProjectDao.findById(idDTO.getId());
		if (project.isPresent()) {
			ProjectEntity fetchedProjectEntity = project.get();
			fetchedProjectEntity.setId(null);
			ProjectListWrapper projectListWrapper = new ProjectListWrapper();
			projectListWrapper.setProjects(List.of(fetchedProjectEntity));
			projectListWrapper.setUploadedBy(fetchedProjectEntity.getUploadedBy());
			response.put("projectListWrapper", projectListWrapper);
			return CommonUtils.prepareResponse(response, "Project with Id: " + idDTO.getId() + " fetched Successfully.",
					true);
		} else {
			return CommonUtils.prepareResponse(response,
					"No projetc found with Id: " + idDTO.getId() + ", check DB and try again.", false);
		}
	}

	@Override
	public HashMap<String, Object> downloadAllProjects() {
		CommonUtils.logMethodEntry(this, "Fetching all Projects");
		HashMap<String, Object> response = new HashMap<>();

		List<ProjectEntity> Projects = ProjectDao.findAll();
		if (!Projects.isEmpty()) {
			ProjectListWrapper projectListWrapper = new ProjectListWrapper();
			projectListWrapper.setProjects(Projects);
			projectListWrapper.setUploadedBy(Projects.get(0).getUploadedBy());
			response.put("projectListWrapper", projectListWrapper);
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
			response.put("project", project.get());
			return CommonUtils.prepareResponse(response, "Project with Id: " + idDTO.getId() + " fetched Successfully.",
					true);
		} else {
			return CommonUtils.prepareResponse(response,
					"No projetc found with Id: " + idDTO.getId() + ", check DB and try again.", false);
		}
	}

	@Override
	public HashMap<String, Object> deleteProjectById(@Valid IdDTO idDTO) {
		CommonUtils.logMethodEntry(this, "Deleting Project by Id: " + idDTO.getId());
		HashMap<String, Object> response = new HashMap<>();

		Optional<ProjectEntity> project = ProjectDao.findById(idDTO.getId());
		if (project.isPresent()) {
			ProjectDao.deleteById(idDTO.getId());
				return CommonUtils.prepareResponse(response,
						"Project with Id: " + idDTO.getId() + " deleted Successfully.", true);
		} else {
			return CommonUtils.prepareResponse(response,
					"No projetc found with Id: " + idDTO.getId() + ", check DB and try again.", false);
		}
	}
	
	@Override
	public HashMap<String, Object> deleteAllProjects() {
		CommonUtils.logMethodEntry(this, "Deleting All Projects");
		HashMap<String, Object> response = new HashMap<>();

		ProjectDao.deleteAll();
				return CommonUtils.prepareResponse(response,
						"All Projects deleted Successfully.", true);
		
	}
	
	@Override
	public HashMap<String, Object> contactUs(@Valid ContactUsDTO contactUsDTO) {
		CommonUtils.logMethodEntry(this);
		HashMap<String, Object> response = new HashMap<>();

		try {
			ContactUsEntity contactRequestsEntity = new ContactUsEntity(contactUsDTO.getName(),
					contactUsDTO.getEmail(), contactUsDTO.getMessage());

			ContactUsEntity contactSaved = contactUsDao.save(contactRequestsEntity);
			if (contactSaved == null) {
				return CommonUtils.prepareResponse(response, "Failed to save the Feedback. Please try again.", false);
			}
			try {
				
				emailService.sendOtpEmail(email, contactUsDTO.getName(), contactUsDTO.getEmail(), contactUsDTO.getMessage());
			} catch (Exception e) {
				throw new AppException("Email Failed. Feedback stored in Database", HttpStatus.BAD_REQUEST);
			}

			return CommonUtils.prepareResponse(response, " Thank you for the Feedback!", true);

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to save the member. Please try again.");
		}
	}

}
