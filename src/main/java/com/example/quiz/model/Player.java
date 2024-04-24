package com.example.quiz.model;

import com.example.quiz.jwt.DBUser;
import com.example.quiz.util.Dates;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.hibernate.validator.constraints.Length;
import org.joda.time.LocalDateTime;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

@Entity
@Table(name = "player")
public class Player implements Serializable {
    private static final long serialVersionUID = 1L;

    @Override
    public String toString() {
        return "Player{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", fullName='" + fullName + '\'' +
                ", quizzes=" + quizzes +
                '}';
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(nullable = false, updatable = false)
    private Date createdAt = Dates.nowUTC();

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("createdAt")
    public LocalDateTime calcCreatedAt() {
        return Dates.atLocalTime(createdAt);
    }

    @NotEmpty
    @Length(max = 60)
    private String fullName;


    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private DBUser user; // User associated with the player


    public DBUser getUser() {
        return user;
    }

    public void setUser(DBUser user) {
        this.user = user;
    }




    // each player can have multiple quizzes
    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL)
    private Collection<Quiz> quizzes = new ArrayList<>();

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getFullname() {
        return fullName;
    }

    public void setFullname(String fullName) {
        this.fullName = fullName;
    }





    public Collection<Quiz> getQuizzes() {
        return quizzes;
    }

    public void setQuizzes(Collection<Quiz> quizzes) {
        this.quizzes = quizzes;
    }

    public static final class PlayerBuilder {
        private Long id;
        private @NotNull Date createdAt;
        private @NotEmpty @Length(max = 60) String fullName;
        private DBUser user;
        private Collection<Quiz> quizzes;

        public static PlayerBuilder anPlayer() {
            return new PlayerBuilder();
        }
        private PlayerBuilder() {
        }

        public static PlayerBuilder aPlayer() {
            return new PlayerBuilder();
        }

        public PlayerBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public PlayerBuilder createdAt(Date createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public PlayerBuilder fullName(String fullName) {
            this.fullName = fullName;
            return this;
        }



        public PlayerBuilder user(DBUser user) {
            this.user = user;
            return this;
        }



        public PlayerBuilder quizzes(Collection<Quiz> quizzes) {
            this.quizzes = quizzes;
            return this;
        }

        public Player build() {
            Player player = new Player();
            player.setId(id);
            player.setCreatedAt(createdAt);
            player.setUser(user);
            player.setQuizzes(quizzes);
            player.fullName = this.fullName;
            return player;
        }
    }
}
