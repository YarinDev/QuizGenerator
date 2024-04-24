package com.example.quiz.jwt;


import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DBUserService {


    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(DBUserService.class);

    @Autowired
    private DBUserRepository repository;

    public Optional<DBUser> findUserName(String userName) {
        return repository.findByName(userName);
    }

    public List<DBUser> findAll() {
        return (List<DBUser>) repository.findAll();
    }

    public void save(DBUser user) {
        try {
            repository.save(user);
        } catch (DataIntegrityViolationException e) {
            // Handle validation errors (e.g., duplicate key exception)
            logger.error("error!!!!!!!!!!!!!!!!!!!!!!!!-" + e.getMessage());

        }
    }

    public Optional<DBUser> findById(Long id) {
        return repository.findById(id);
    }

    public void delete(DBUser user) {
        repository.delete(user);
    }
}
