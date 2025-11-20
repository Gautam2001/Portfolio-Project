package com.portfolio.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ContactUsDTO {

	@NotBlank(message = "Email cannot be blank")
	@Email(message = "Invalid Email format")
	private String email;

	@NotBlank(message = "Name cannot be blank")
	private String name;

	@NotBlank(message = "Message cannot be blank")
	private String message;

}
