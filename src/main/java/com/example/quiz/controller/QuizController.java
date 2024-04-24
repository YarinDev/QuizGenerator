package com.example.quiz.controller;

import com.example.quiz.model.QuizIn;
import com.example.quiz.model.Quiz;
import com.example.quiz.repo.PlayerService;

import com.example.quiz.repo.QuizService;
import com.example.quiz.model.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


@RestController
@RequestMapping("/api/users")
public class QuizController {
    @Autowired
    PlayerService usersService;

    @Autowired
    QuizService quizService;


    // add a new quiz to a user
    @RequestMapping(value = "/{usersId}/quiz", method = RequestMethod.POST)
    public ResponseEntity<?> insertQuiz(Long usersId,  @RequestBody QuizIn quizIn)
    {
        var users = usersService.findById(usersId);
        if (users.isEmpty()) throw new RuntimeException("Player:" + usersId +" not found");
        Quiz quiz = quizIn.toScore(users.get());
        quiz = quizService.saveQuizWithQuestionsAndAnswers(quiz);
        return new ResponseEntity<>(quiz, HttpStatus.OK);
    }
    // update a quiz of a user
    @RequestMapping(value = "/{usersId}/quiz/{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> updatePlayer(@PathVariable Long usersId, @PathVariable Long id, @RequestBody QuizIn quizIn)
    {
        Optional<Player> dbPlayer = usersService.findById(usersId); // find the user
        if (dbPlayer.isEmpty()) throw new RuntimeException("Player with id: " + usersId + " not found");

        Optional<Quiz> dbQuiz = quizService.findById(id);
        if (dbQuiz.isEmpty()) throw new RuntimeException("Player quiz with id: " + id + " not found");

        quizIn.updateQuiz(dbQuiz.get());
        Quiz updatedQuiz = quizService.saveQuizWithQuestionsAndAnswers(dbQuiz.get());
        return new ResponseEntity<>(updatedQuiz, HttpStatus.OK);
    }
    // delete a quiz of a user by id
    @RequestMapping(value = "/{usersId}/quiz/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteQuiz(@PathVariable Long usersId, @PathVariable Long id)
    {
        Optional<Player> dbPlayer = usersService.findById(usersId);
        if (dbPlayer.isEmpty()) throw new RuntimeException("Player with id: " + usersId + " not found");

        Optional<Quiz> dbQuiz = quizService.findById(id);
        if (dbQuiz.isEmpty()) throw new RuntimeException("Player quiz with id: " + id + " not found");

        quizService.delete(dbQuiz.get());
        return new ResponseEntity<>("DELETED", HttpStatus.OK);
    }
}
