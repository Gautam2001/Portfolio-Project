package com.portfolio.Entity;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Document(collection = "User")
public class UserEntity {

	@Id
	private String id;

	@NotBlank(message = "username is required in About Me")
	private String username; // email

	@NotBlank(message = "Name is required in About Me")
	private String name;

	private Instant joinedAt = Instant.now();

}
