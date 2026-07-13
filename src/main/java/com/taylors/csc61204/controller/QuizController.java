package com.taylors.csc61204.controller;

import com.taylors.csc61204.model.Quiz;
import com.taylors.csc61204.model.QuizResult;
import com.taylors.csc61204.model.StudentPerformance;
import com.taylors.csc61204.pattern.strategy.DifficultyBasedStrategy;
import com.taylors.csc61204.pattern.strategy.QuestionSelectionStrategy;
import com.taylors.csc61204.pattern.strategy.WeaknessFocusedStrategy;
import com.taylors.csc61204.persistence.DataStore;
import com.taylors.csc61204.persistence.DataStoreException;
import com.taylors.csc61204.service.FeedbackService;
import com.taylors.csc61204.service.QuizService;
import com.taylors.csc61204.view.MainFrame;
import com.taylors.csc61204.view.QuizScreen;
import com.taylors.csc61204.view.ResultsScreen;
import com.taylors.csc61204.view.StartScreen;

/**
 * Wires the View to the Service and Persistence layers. The only class allowed
 * to call services from the GUI side. Demonstrates clean MVC separation:
 * views collect input, the controller routes it, the service does the work,
 * the data store makes the outcome survive restarts.
 */
public class QuizController {

    private static final int DEFAULT_TIME_LIMIT_SECONDS = 600;

    private final MainFrame view;
    private final QuizService quizService;
    private final FeedbackService feedbackService;
    private final StudentPerformance performance;
    private final DataStore store;

    public QuizController(MainFrame view, QuizService quizService,
                          FeedbackService feedbackService,
                          StudentPerformance performance, DataStore store) {
        this.view = view;
        this.quizService = quizService;
        this.feedbackService = feedbackService;
        this.performance = performance;
        this.store = store;
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
                    selectionStrategyFor(difficulty),
                    DEFAULT_TIME_LIMIT_SECONDS);
            view.showQuiz(new QuizScreen(quiz, answers -> onSubmit(quiz, answers)));
        } catch (Exception ex) {
            view.showError("Could not start quiz: " + ex.getMessage());
        }
    }

    /**
     * Adaptive selection: once the student has quiz history, focus on their
     * weak categories ({@link WeaknessFocusedStrategy}); until then — and when
     * the weak categories run dry — fall back to the difficulty the user chose.
     */
    private QuestionSelectionStrategy selectionStrategyFor(String difficulty) {
        return new WeaknessFocusedStrategy(
                performance, new DifficultyBasedStrategy(difficulty));
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
        QuizResult result = quizService.submit(quiz, answers, performance.getStudentId());
        performance.record(result);
        persist();
        String summary = feedbackService.summaryFor(result);
        String report = feedbackService.progressReport();
        view.showResults(new ResultsScreen(result, summary, report, this::showStartScreen));
    }

    /** Best-effort save — a persistence failure must not crash the results screen. */
    private void persist() {
        try {
            store.savePerformance(performance);
        } catch (DataStoreException e) {
            view.showError("Could not save your progress: " + e.getMessage());
        }
    }
}
