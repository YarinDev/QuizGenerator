package com.example.quiz.controller;

import com.example.quiz.model.Answer;
import com.example.quiz.model.Player;
import com.example.quiz.model.Question;
import com.example.quiz.model.Quiz;
import com.example.quiz.repo.PlayerService;
import com.example.quiz.repo.QuizService;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.*;
import java.util.logging.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.web.bind.annotation.RequestHeader;


@Controller
public class GameController {
    Logger logger = Logger.getLogger(GameController.class.getName());
    Map<Question, Answer> quizQuestionsAndAnswers2 = new LinkedHashMap<>(); // Use LinkedHashMap to preserve insertion order
    Map<Question, Answer> quizQuestionsAndAnswers3 = new LinkedHashMap<>(); // Use LinkedHashMap to preserve insertion order
    @Autowired
    private PlayerService playerService;
    @Autowired
    private QuizService quizService;

    @Value("${chatgpt.api.key}")
    private String apiKey;

    @GetMapping("/quiz")
    public String quiz() {
        return "quiz";
    }

    @GetMapping("/register")
    public String showRegisterPage() {
        return "register"; // This should match the name of your register.html file without the ".html" extension
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login"; // Assuming "login.html" is your login page template
    }

    @GetMapping("/quizsubject")
    public String showQuizSubjectPage() {
//        logger.info("Inside// showQuizSubjectPage");
//        logger.info("Authorization Header: {}" + authorizationHeader);
        return "quizSubject";
    }

    @PostMapping("/generateQuiz")
    public String generateQuiz(@RequestParam("subject") String subject, Model model) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);
        model.addAttribute("subject", subject);

        String requestBody = "{\"model\": \"gpt-3.5-turbo\", \"messages\": [{\"role\": \"user\", \"content\": \"give me a numbered multiple-choice quiz about " + subject + " with 4 answers for each question and also list of the answers for each question end the end of the quiz - like this structure for example - 1. What is the capital of England?\\nA) Manchester\\nB) Birmingham\\nC) London\\nD) Liverpool\\n\\n2. What is the name of the famous clock tower in London?\\nA) Westminster Tower\\nB) Buckingham Tower\\nC) Big Ben\\nD) Tower Bridge\\n\\n3. What traditional meal is commonly associated with England?\\nA) Fish and Chips\\nB) Sushi\\nC) Tacos\\nD) Pizza\\n\\nAnswers:\\n1. C) London\\n2. C) Big Ben\\n3. A) Fish and Chips.\"}]}";
        logger.info(requestBody);
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        RestTemplate restTemplate = new RestTemplate();
        String apiUrl = "https://api.openai.com/v1/chat/completions";
        String response = restTemplate.exchange(apiUrl, HttpMethod.POST, requestEntity, String.class).getBody();
        logger.info("response is: " + response);

        quizQuestionsAndAnswers3 = extractQuizQuestionsAndAnswers(response);
        logger.info("QuizQuestionsAndAnswers after extractQuizQuestionsAndAnswers: " + quizQuestionsAndAnswers3);

        model.addAttribute("quizQuestionsAndAnswers", quizQuestionsAndAnswers3);

        return "quiz";
    }

    @PostMapping("/updateQuiz")
    public String updateQuiz(@RequestParam("id") Long quizId, Model model) {
        // Retrieve the quiz by its ID
        Optional<Quiz> quizOptional = quizService.findById(quizId);

        if (quizOptional.isPresent()) {
            Quiz quiz = quizOptional.get();

            // Get questions and answers from the quiz object
            Map<Question, Answer> quizQuestionsAndAnswers = quiz.getQuestionsAndAnswers();

            logger.info("QuizQuestionsAndAnswers: " + quizQuestionsAndAnswers);

            // Pass quizId and questionsAndAnswers to the view
            model.addAttribute("quizId", quizId);
            model.addAttribute("quizQuestionsAndAnswers", quizQuestionsAndAnswers);

            // Return the view name
            return "quizNewSubmit";
        } else {
            // Handle the case where the quiz with the given ID is not found
            // You can redirect to an error page or show an error message
            return "error";
        }
    }

    @PostMapping("/submitUpdatedQuiz")
    public String submitUpdatedQuiz(@RequestParam Map<String, String> params, @RequestParam Long quizId, Model model) {
        logger.info(" Received  Params: " + params);

        quizQuestionsAndAnswers3 = quizService.findById(quizId).get().getQuestionsAndAnswers();
        int score = calculateScore(params);
        logger.info("updated Score: " + score);

        logger.info("QuizId: " + quizId);
        Quiz quiz = quizService.findById(quizId).get();
        logger.info("Quiz: " + quiz);
        logger.info("Quiz to string: " + quiz.toString());

        quiz.setQuizScore(score);
        logger.info("Quiz: " + quiz);
        logger.info("Quiz to string: " + quiz.toString());
        quizService.saveQuizWithQuestionsAndAnswers(quiz);
        model.addAttribute("score", quizService.findById(quizId).get().getQuizScore());

        return "quizResult";
    }

    @PostMapping("/submitQuiz")
    public String submitQuiz(@RequestParam Map<String, String> params, Model model, @RequestParam("token") String jwtToken, @RequestParam("subject") String subject, @RequestParam("quizQuestionsAndAnswers") String quizQuestionsAndAnswers) {
        // Parse the JWT token to extract user details
        Jwt jwt = JwtHelper.decode(jwtToken);
        String claims = jwt.getClaims();
        logger.info("Claims: " + claims);
        String username = extractUsernameFromToken(jwt); // Extract username from token
        logger.info("Username: " + username);

        int score = calculateScore(params);

        // Create a new Quiz object with the current time, score, and associate it with the player object
        Quiz quiz = new Quiz();
        quiz.setQuizScore(score);
        quiz.setquizSubject(subject);
        //set date using Date
        quiz.setCreatedAt(new Date());
        // Assuming you have a Player object associated with the user
        Player player = playerService.findByFullName(username); // Retrieve player by username
        logger.info("quizQuestionsAndAnswers inside submit: " + quizQuestionsAndAnswers);
        logger.info("Player: " + player.toString());
        quiz.setPlayer(player);
        quiz.setQuestionsAndAnswers(quizQuestionsAndAnswers3);

        logger.info("Quiz subject: " + quiz.getQuizSubject());
        logger.info("Quiz questions and asnwers: " + quiz.getQuestionsAndAnswers());

        // Save the Quiz object to your database
        quizService.saveQuizWithQuestionsAndAnswers(quiz);

        model.addAttribute("score", score);
        return "quizResult";
    }

    @ModelAttribute("player")
    @PostMapping("/playerDetails")
    public String getPlayerDetails(Model model, @RequestParam("token") String jwtToken) {
        // Parse the JWT token to extract user details
        Jwt jwt = JwtHelper.decode(jwtToken);
        String username = extractUsernameFromToken(jwt); // Extract username from token

        // Retrieve player details from the database using the username
        Player player = playerService.findByFullName(username);
        logger.info("Player is retrived: " + player);

        // Check if player exists
        if (player == null) {
            // Player not found, handle the error (e.g., redirect to an error page)
            logger.info("Player not found");
            return "error"; // Assuming you have an error page named "error.html"
        }

        // Add player object to the model
        model.addAttribute("playerObject", player);

        // Return the name of the player details view
        return "playerDetails";
    }


    private String extractUsernameFromToken(Jwt jwt) {
        // Parse JWT claims to extract username
        // For example, if 'sub' contains the username, you can use:
        String claims = jwt.getClaims();
        JsonObject jsonObject = JsonParser.parseString(claims).getAsJsonObject();
        return jsonObject.get("sub").getAsString();
    }

    private Map<Question, Answer> extractQuizQuestionsAndAnswers(String response) {
        Map<Question, Answer> quizQuestionsAndAnswers = new LinkedHashMap<>(); // Use LinkedHashMap to preserve insertion order
        try {
            JsonParser parser = new JsonParser();
            JsonObject jsonObject = parser.parse(response).getAsJsonObject();
            JsonArray choicesArray = jsonObject.getAsJsonArray("choices");
            JsonObject firstChoice = choicesArray.get(0).getAsJsonObject();
            JsonObject messageObject = firstChoice.getAsJsonObject("message");
            String content = messageObject.get("content").getAsString();
            logger.info("Content from API response: " + content);

            // Split the content into questions and answers
            String[] parts = content.split("Answers:\n");
            logger.info("Parts: " + Arrays.toString(parts)); // [questions, answers]
            String questionsContent = parts[0].trim(); //remove leading/trailing whitespace from questions
            logger.info("questionsContent:" + questionsContent);
            String answersContent = parts[1].trim(); //remove leading/trailing whitespace from answers
            logger.info("answersContent:" + answersContent);

            // Extract questions
            String[] questions = questionsContent.split("\\n\\n"); //split questions by number followed by period
            logger.info("Questions are: " + Arrays.toString(questions));
            for (String question : questions) {
                if (question.trim().isEmpty()) continue; //skip empty questions
                String[] questionParts = question.split("\\n"); //split question by newline
                logger.info("Question parts: " + Arrays.toString(questionParts)); // [prompt, option1, option2, option3, option4]
                String prompt = questionParts[0].trim(); //remove leading/trailing whitespace from prompt
                logger.info("Prompt: " + prompt);
                List<Answer> options = new ArrayList<>();
                for (int i = 1; i < questionParts.length; i++) {
                    String option = questionParts[i].trim(); //remove leading/trailing whitespace from option
                    logger.info("Option: " + option);
                    char letter = option.charAt(0);       // Extract the letter from the option
                    logger.info("Letter: " + letter);
                    String content2 = option.substring(3).trim(); //remove leading/trailing whitespace from content
                    logger.info("Content2: " + content2);
                    options.add(new Answer(letter, content2));
                    logger.info("Options: " + options);
                }
                quizQuestionsAndAnswers.put(new Question(prompt, options), null);
                logger.info("QuizQuestionsAndAnswers: " + quizQuestionsAndAnswers);
            }

            // Extract answers
            String[] answers = answersContent.split("\\n"); //split answers by newline
            logger.info("Answers are: " + Arrays.toString(answers));
            for (String answer : answers) {
                String[] answerParts = answer.split("\\) ", 3); //split answer by parenthesis followed by space -> [1. C, London]
                logger.info("Answer parts: " + Arrays.toString(answerParts));
                char letter = answerParts[0].charAt(3); // Extract the letter from the answer
                logger.info("Letter: " + letter);
                // Extract the single element from the array
                String input = answerParts[1].trim(); // (London)
                logger.info("Input: " + input);
                // Split the input string based on period
                String[] partsContent = input.split("\\."); //split input by period

                // Select the last part (trim to remove leading/trailing whitespace)
                String lastPart = partsContent[partsContent.length - 1].trim(); // (C)
                logger.info("lastPart: " + lastPart);
                Answer ans = new Answer(letter, lastPart);
                logger.info("Answer ans: " + ans);
                // Iterate through the map and set the answer for the question
                for (Map.Entry<Question, Answer> entry : quizQuestionsAndAnswers.entrySet()) {
                    // Check if the question content contains the answer letter
                    logger.info("Entry key content: " + entry.getKey().getContent());
                    logger.info("Answer parts 0: " + answerParts[0] + ". ");
                    if (entry.getKey().getContent().contains(answerParts[0].charAt(0) + ". ")) {
                        logger.info("Setting answer for question: " + entry.getKey().getContent());
                        entry.setValue(ans);
                        break;
                    }
                }
                logger.info("QuizQuestionsAndAnswers: " + quizQuestionsAndAnswers); // [Question{content='What is the capital of England?', options=[Answer{letter=A, content='Manchester'}, Answer{letter=B, content='Birmingham'}, Answer{letter=C, content='London'}, Answer{letter=D, content='Liverpool'}]}=null, Question{content='What is the name of the famous clock tower in London?', options=[Answer{letter=A, content='Westminster Tower'}, Answer{letter=B, content='Buckingham Tower'}, Answer{letter=C, content='Big Ben'}, Answer{letter=D, content='Tower Bridge'}]}=null, Question{content='What traditional meal is commonly associated with England?', options=[Answer{letter=A, content='Fish and Chips'}, Answer{letter=B, content='Sushi'}, Answer{letter=C, content='Tacos'}, Answer{letter=D, content='Pizza'}]}=null]
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        quizQuestionsAndAnswers2 = quizQuestionsAndAnswers;
        return quizQuestionsAndAnswers;
    }

    public static Map<Question, Answer> parseQuizString(String quizString) {
        Map<Question, Answer> quizQuestionsAndAnswers = new LinkedHashMap<>();

        // Split the string by 'Question{content=' and '}, '
        String[] questionAnswerPairs = quizString.split("(?=Question\\{content=)");

        // Iterate through the pairs
        for (String pair : questionAnswerPairs) {
            String[] parts = pair.split("}=Answer\\{");
            if (parts.length != 2) {
                continue; // Skip if the pair is not in expected format
            }

            // Extract question content
            String questionContent = parts[0].substring(parts[0].indexOf("'") + 1);
            List<Answer> answers = new ArrayList<>();

            // Extract answer content
            String[] answerParts = parts[1].split("', ");
            for (int i = 0; i < answerParts.length; i++) {
                char letter = answerParts[i].charAt(answerParts[i].indexOf("letter=") + 8);
                String content = answerParts[i].substring(answerParts[i].indexOf("content='") + 9);
                if (i == answerParts.length - 1) {
                    content = content.substring(0, content.length() - 2); // Remove trailing '}'
                }
                answers.add(new Answer(letter, content));
            }

            // Add question and answers to the map
            Question question = new Question(questionContent, answers);
            quizQuestionsAndAnswers.put(question, answers.get(0)); // Assuming the first answer is correct
        }

        return quizQuestionsAndAnswers;
    }


    private int calculateScore(Map<String, String> params) {
        int score = 0;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            logger.info("params entrySet: " + params.entrySet());
            logger.info("Entry: " + entry);
            String questionIndex = entry.getKey().replaceAll("^question-(\\d+).*", "$1");
            logger.info("Question index: " + questionIndex);
            String selectedOption = entry.getValue();
            logger.info("Selected option: " + selectedOption);

            logger.info("QuizQuestionsAndAnswers3: " + quizQuestionsAndAnswers3);
            logger.info("QuizQuestionsAndAnswers2: " + quizQuestionsAndAnswers2);

            for (Map.Entry<Question, Answer> questionEntry : quizQuestionsAndAnswers3.entrySet()) {
                logger.info("Question entry: " + questionEntry);
                Question question = questionEntry.getKey();
                logger.info("Question: " + question);

                Answer correctAnswer = questionEntry.getValue();
                logger.info("Correct answer: " + correctAnswer);

                //logger.info("Question content: " + question.getContent().contains(questionIndex));
                logger.info("correct answer getcontetn: " + correctAnswer.getLetter());
                logger.info("Correct answer content: " + (correctAnswer.getLetter() == selectedOption.charAt(0)));
                if (question.getContent().startsWith(questionIndex) && correctAnswer.getLetter() == selectedOption.charAt(0)) {
                    logger.info("Correct answer selected");
                    score++;
                    logger.info("Score: " + score);
                    break; // Move to the next question
                }
            }
        }
        return score;
    }
}
