package com.example.quiz.controller;

import com.example.quiz.jwt.DBUser;
import com.example.quiz.model.*;
import com.example.quiz.repo.PlayerService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static com.example.quiz.util.Dates.atUtc;
import static com.example.quiz.util.FPS.FPSBuilder.aFPS;
import static com.example.quiz.util.FPSCondition.FPSConditionBuilder.aFPSCondition;
import static com.example.quiz.util.FPSField.FPSFieldBuilder.aFPSField;
import static com.example.quiz.util.Strings.likeLowerOrNull;

@RestController
@RequestMapping("/api/Player")
public class PlayerController {
    @Autowired
    PlayerService playerService;

    @Autowired
    EntityManager em;

    @Autowired
    ObjectMapper om;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<PaginationAndList> search(@RequestParam(required = false) String full_name,
                                                    @RequestParam(defaultValue = "1") Integer page,
                                                    @RequestParam(defaultValue = "50") @Min(1) Integer count,
                                                    @RequestParam(defaultValue = "id") PlayerSortField sort,
                                                    @RequestParam(defaultValue = "asc") SortDirection sortDirection) throws JsonProcessingException {

        var res = aFPS().select(List.of(
                        aFPSField().field("p.id").alias("id").build(),
                        aFPSField().field("p.created_at").alias("createdAt").build(),
                        aFPSField().field("p.full_name").alias("fullName").build()
                ))
                .from(List.of(" player p"))
                .conditions(List.of(
                        aFPSCondition().condition("( lower(p.full_name) like :fullName )").parameterName("fullName").value(likeLowerOrNull(full_name)).build()
                )).sortField(sort.fieldName).sortDirection(sortDirection).page(page).count(count)
                .itemClass(PlayerOut.class)
                .build().exec(em, om);
        return ResponseEntity.ok(res);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getOnePlayer(@PathVariable Long id) {
        return new ResponseEntity<>(playerService.findById(id), HttpStatus.OK);
    }


    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<?> insertPlayer(@RequestBody PlayerIn playerIn) {
        Player player = playerIn.toPlayer();
        player = playerService.save(player);
        return new ResponseEntity<>(player, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> updatePlayer(@PathVariable Long id, @RequestBody PlayerIn player) {
        Optional<Player> dbPlayer = playerService.findById(id);
        if (dbPlayer.isEmpty()) throw new RuntimeException("User with id: " + id + " not found");
        player.updatePlayer(dbPlayer.get());
        Player updatedPlayer = playerService.save(dbPlayer.get());
        return new ResponseEntity<>(updatedPlayer, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deletePlayer(@PathVariable Long id) {
        Optional<Player> dbPlayer = playerService.findById(id);
        if (dbPlayer.isEmpty()) throw new RuntimeException("User with id: " + id + " not found");
        playerService.delete(dbPlayer.get());
        return new ResponseEntity<>("DELETED", HttpStatus.OK);
    }

    // Method to get the user associated with a player
    @RequestMapping(value = "/{id}/user", method = RequestMethod.GET)
    public ResponseEntity<?> getPlayerUser(@PathVariable Long id) {
        Optional<Player> player = playerService.findById(id);
        if (player.isEmpty()) throw new RuntimeException("Player with id: " + id + " not found");
        DBUser user = player.get().getUser();
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public ResponseEntity<?> getAllPlayers() {
        List<Player> players = playerService.findAll();
        return ResponseEntity.ok(players);
    }
}