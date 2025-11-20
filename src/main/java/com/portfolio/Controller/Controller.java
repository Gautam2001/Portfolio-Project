package com.portfolio.Controller;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.portfolio.DTO.ContactUsDTO;
import com.portfolio.DTO.IdDTO;
import com.portfolio.DTO.UsernameDTO;
import com.portfolio.Service.ProjectService;
import com.portfolio.Utility.CommonUtils;

import jakarta.validation.Valid;

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

	@PostMapping("/join")
	public ResponseEntity<HashMap<String, Object>> joinPortfolioApp(@RequestBody @Valid UsernameDTO usernameDTO) {
		CommonUtils.logMethodEntry(this);

		HashMap<String, Object> response = ProjectService.joinPortfolioApp(usernameDTO);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/getAboutMe")
	public ResponseEntity<HashMap<String, Object>> getAboutMe() {
		CommonUtils.logMethodEntry(this);

		HashMap<String, Object> response = ProjectService.getAboutMe();

		return ResponseEntity.ok(response);
	}

	@PostMapping("/getProjectById")
	public ResponseEntity<HashMap<String, Object>> getProjectById(@RequestBody @Valid IdDTO idDTO) {
		CommonUtils.logMethodEntry(this);

		HashMap<String, Object> response = ProjectService.getProjectById(idDTO);

		return ResponseEntity.ok(response);
	}
	
	@PostMapping("/contactUs")
	public ResponseEntity<HashMap<String, Object>> contactUs(@RequestBody @Valid ContactUsDTO contactUsDTO) {
		CommonUtils.logMethodEntry(this);
		HashMap<String, Object> response = ProjectService.contactUs(contactUsDTO);

		return ResponseEntity.ok(response);
	}

}
