package com.example.quiz.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.example.quiz.util.Dates;
import org.hibernate.validator.constraints.Length;
import org.joda.time.LocalDateTime;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@Entity
@Table(name="quiz")
public class Quiz implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    // Created date of the quiz
    @NotNull
    @Column(nullable = false, updatable = false)
    private Date createdAt = Dates.nowUTC();

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("createdAt")
    public LocalDateTime calcCreatedAt() {
        return Dates.atLocalTime(createdAt);
    }

    // Player is the foreign key of the player table, connects both tables
    @ManyToOne
    @JoinColumn(name = "player_id", referencedColumnName = "id")
    @JsonIgnore
    private Player player;

    public String getQuizSubject() {
        return quizSubject;
    }

    public void setQuizSubject(String quizSubject) {
        this.quizSubject = quizSubject;
    }

    @Length(max = 60)
    private String quizSubject;

    @Min(0)
    @Max(10)
    private Integer quizScore;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "quiz_question_answers", joinColumns = @JoinColumn(name = "quiz_id"))
    @MapKeyJoinColumn(name = "question_id")
    @Column(name = "answer_id")
    private Map<Question, Answer> questionsAndAnswers = new LinkedHashMap<>();

    public Map<Question, Answer> getQuestionsAndAnswers() {
        return questionsAndAnswers;
    }

    public void setQuestionsAndAnswers(Map<Question, Answer> questionsAndAnswers) {
        this.questionsAndAnswers = questionsAndAnswers;
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

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public String getquizSubject() {
        return quizSubject;
    }

    public void setquizSubject(String quizSubject) {
        this.quizSubject = quizSubject;
    }

    public Integer getQuizScore() {
        return quizScore;
    }

    public void setQuizScore(Integer quizScore) {
        this.quizScore = quizScore;
    }

    public static final class QuizBuilder {
        private Long id;
        private @NotNull Date createdAt;
        private Player player;
        private @Length(max = 60) String quizSubject;
        private @Min(0) @Max(10) Integer quizScore;
        private Map<Question, Answer> questionsAndAnswers;

        private QuizBuilder() {
        }

        public static QuizBuilder aQuiz() {
            return new QuizBuilder();
        }

        public QuizBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public QuizBuilder createdAt(Date createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public QuizBuilder player(Player player) {
            this.player = player;
            return this;
        }

        public QuizBuilder quizSubject(String quizSubject) {
            this.quizSubject = quizSubject;
            return this;
        }

        public QuizBuilder quizScore(Integer quizScore) {
            this.quizScore = quizScore;
            return this;
        }

        public QuizBuilder questionsAndAnswers(Map<Question, Answer> questionsAndAnswers) {
            this.questionsAndAnswers = questionsAndAnswers;
            return this;
        }

        public Quiz build() {
            Quiz quiz = new Quiz();
            quiz.setId(id);
            quiz.setCreatedAt(createdAt);
            quiz.setPlayer(player);
            quiz.setQuizSubject(quizSubject);
            quiz.setQuizScore(quizScore);
            quiz.setQuestionsAndAnswers(questionsAndAnswers);
            return quiz;
        }
    }
}