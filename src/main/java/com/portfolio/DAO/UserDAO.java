package com.portfolio.DAO;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.portfolio.Entity.UserEntity;

public interface UserDAO extends MongoRepository<UserEntity, String> {

	Optional<UserEntity> findTopByUsername(String username);

}
