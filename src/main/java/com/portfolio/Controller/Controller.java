package com.portfolio.Controller;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.portfolio.Service.ProjectService;
import com.portfolio.Utility.CommonUtils;

@RestController
@RequestMapping("/portfolio")
public class Controller {

	@Autowired
	ProjectService ProjectService;

	@GetMapping("/ping")
	public ResponseEntity<HashMap<String, Object>> ping() {
		CommonUtils.logMethodEntry(this);
		HashMap<String, Object> response = new HashMap<>();

		return ResponseEntity.ok(CommonUtils.prepareResponse(response, "pong", true));
	}
	
	@PostMapping("/aboutMeUpload")
	public ResponseEntity<HashMap<String, Object>> aboutMeUpload(@RequestParam MultipartFile aboutMe) {
		CommonUtils.logMethodEntry(this);

		HashMap<String, Object> response = ProjectService.uploadAboutMe(aboutMe);

		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/getAboutMe")
	public ResponseEntity<HashMap<String, Object>> getAboutMe() {
		CommonUtils.logMethodEntry(this);

		HashMap<String, Object> response = ProjectService.getAboutMe();

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

}
