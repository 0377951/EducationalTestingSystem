package com.taylors.csc61204.controller;

import com.taylors.csc61204.model.Quiz;
import com.taylors.csc61204.model.QuizResult;
import com.taylors.csc61204.pattern.strategy.DifficultyBasedStrategy;
import com.taylors.csc61204.service.FeedbackService;
import com.taylors.csc61204.service.QuizService;
import com.taylors.csc61204.view.MainFrame;
import com.taylors.csc61204.view.QuizScreen;
import com.taylors.csc61204.view.ResultsScreen;
import com.taylors.csc61204.view.StartScreen;

/**
 * Wires the View to the Service layer. The only class allowed to call services
 * from the GUI side. Demonstrates clean MVC separation: views collect input,
 * the controller routes it, the service does the work.
 */
public class QuizController {

    private static final int DEFAULT_TIME_LIMIT_SECONDS = 600;
    private static final String STUDENT_ID = "S-001";

    private final MainFrame view;
    private final QuizService quizService;
    private final FeedbackService feedbackService;

    public QuizController(MainFrame view, QuizService quizService,
                          FeedbackService feedbackService) {
        this.view = view;
        this.quizService = quizService;
        this.feedbackService = feedbackService;
    }

    public void start() {
        showStartScreen();
        view.setVisible(true);
    }

    private void showStartScreen() {
        StartScreen screen = new StartScreen(this::onStartClicked);
        view.showStart(screen);
    }

    private void onStartClicked(String difficulty, int count) {
        try {
            ensureBankPopulated(difficulty, count);
            Quiz quiz = quizService.generateQuiz(
                    "Quiz — " + difficulty,
                    count,
                    new DifficultyBasedStrategy(difficulty),
                    DEFAULT_TIME_LIMIT_SECONDS);
            view.showQuiz(new QuizScreen(quiz, answers -> onSubmit(quiz, answers)));
        } catch (Exception ex) {
            view.showError("Could not start quiz: " + ex.getMessage());
        }
    }

    private void ensureBankPopulated(String difficulty, int count) {
        if (quizService.getBankSize() < count) {
            boolean ok = quizService.refreshBankFromApi(count + 5, difficulty);
            if (!ok && quizService.getBankSize() < count) {
                throw new IllegalStateException(
                        "Could not load questions — the trivia service is unavailable.");
            }
        }
    }

    private void onSubmit(Quiz quiz, java.util.List<String> answers) {
        QuizResult result = quizService.submit(quiz, answers, STUDENT_ID);
        String summary = feedbackService.summaryFor(result);
        String report = feedbackService.progressReport();
        view.showResults(new ResultsScreen(result, summary, report, this::showStartScreen));
    }
}
