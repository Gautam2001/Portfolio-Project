package com.portfolio.DAO;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.portfolio.Entity.AboutMeEntity;

public interface AboutMeDao extends MongoRepository<AboutMeEntity, String> {
	
	Optional<AboutMeEntity> findTopByOrderByUploadAtDesc();
	
}
