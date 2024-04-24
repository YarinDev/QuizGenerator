package com.example.quiz.repo;

import com.example.quiz.model.Player;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

@Service
public class PlayerService {

    @Autowired
    PlayerRepository repository;



    public Iterable<Player> all() {
        return repository.findAll();
    }
    public List<Player> findAll() {
        return (List<Player>) repository.findAll();
    }

    // find a player by name
    public Player findByFullName(String name) {
        return repository.findByFullName(name);
    }


    public Optional<Player> findById(Long id) {
        return repository.findById(id);
    }


    public Player save(Player player) {
        return repository.save(player);
    }

    public void delete(Player player) {
        repository.delete(player);
    }

}
