package com.portfolio.DAO;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.portfolio.Entity.ProjectEntity;

public interface ProjectDao extends MongoRepository<ProjectEntity, String> {

	boolean existsByTitle(String title);

	List<ProjectPreviewProjection> findAllBy();

}
