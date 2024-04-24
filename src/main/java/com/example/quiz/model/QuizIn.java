package com.example.quiz.model;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

import static com.example.quiz.model.Quiz.QuizBuilder.aQuiz;

public class QuizIn {

    @NotEmpty
    @Length(max = 60)
    private String quizSubject;

    @Min(10)
    @Max(100)
    private Integer quizScore;

    public Quiz toScore(Player player) {
        return aQuiz().player(player).quizSubject(quizSubject).quizScore(quizScore).build();
    }

    public void updateQuiz(Quiz quiz) {
        quiz.setquizSubject(quizSubject);
        quiz.setQuizScore(quizScore);
    }

    public String getquizSubject() {
        return quizSubject;
    }

    public Integer getQuizScore() {
        return quizScore;
    }
}
