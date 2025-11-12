package com.portfolio.Controller;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.portfolio.DTO.IdDTO;
import com.portfolio.Service.ProjectService;
import com.portfolio.Utility.CommonUtils;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/admin-portfolio")
public class SuperAdminController {

	@Autowired
	ProjectService ProjectService;
	
	@PostMapping("/aboutMeUpload")
	public ResponseEntity<HashMap<String, Object>> aboutMeUpload(@RequestParam MultipartFile aboutMe) {
		CommonUtils.logMethodEntry(this);

		HashMap<String, Object> response = ProjectService.uploadAboutMe(aboutMe);

		return ResponseEntity.ok(response);
	}

	@PostMapping("/projectsUpload")
	public ResponseEntity<HashMap<String, Object>> uploadProjects(@RequestParam MultipartFile projects) {
		CommonUtils.logMethodEntry(this);

		HashMap<String, Object> response = ProjectService.uploadProjects(projects);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/getAllProjects")
	public ResponseEntity<HashMap<String, Object>> getAllProjects() {
		CommonUtils.logMethodEntry(this);

		HashMap<String, Object> response = ProjectService.getAllProjects();

		return ResponseEntity.ok(response);
	}
	
	@PostMapping("/deleteProjectById")
	public ResponseEntity<HashMap<String, Object>> deleteProjectById(@RequestBody @Valid IdDTO idDTO) {
		CommonUtils.logMethodEntry(this);

		HashMap<String, Object> response = ProjectService.deleteProjectById(idDTO);

		return ResponseEntity.ok(response);
	}

}
