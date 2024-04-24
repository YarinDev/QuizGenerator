package com.example.quiz.model;

import com.example.quiz.util.Dates;
import com.fasterxml.jackson.annotation.JsonFormat;

import org.hibernate.validator.constraints.Length;
import org.joda.time.LocalDate;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.io.Serializable;

import static com.example.quiz.model.Player.PlayerBuilder.anPlayer;

public class PlayerIn implements Serializable {

    @Length(max = 60)
    private String fullname;






    // toP
    public Player toPlayer() {
        return anPlayer().createdAt(Dates.nowUTC()).fullName(fullname)
                .build();
    }

    public void updatePlayer(Player player) {
        player.setFullname(fullname);
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }}





