package com.portfolio.Entity;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Document(collection = "ContactUs")
public class ContactUsEntity {

	@Id
	private String id;

	@NotBlank(message = "Name cannot be Empty")
	private String name;

	@NotBlank(message = "Email cannot be Empty")
	@Email(message = "Invalid email format")
	private String email;

	@NotBlank(message = "Message cannot be Empty")
	private String message;
	
	private Instant joinedAt = Instant.now();
	
	public ContactUsEntity(@NotBlank(message = "Name cannot be Empty") String name,
			@NotBlank(message = "Email cannot be Empty") @Email(message = "Invalid email format") String email,
			@NotBlank(message = "Message cannot be Empty") String message) {
		super();
		this.name = name;
		this.email = email;
		this.message = message;
		this.joinedAt = Instant.now();
	}

}
