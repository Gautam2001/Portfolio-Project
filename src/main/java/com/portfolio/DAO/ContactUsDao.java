package com.portfolio.DAO;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.portfolio.Entity.ContactUsEntity;

public interface ContactUsDao extends MongoRepository<ContactUsEntity, String> {

}
