package com.taylors.csc61204.service;

import com.taylors.csc61204.api.ApiException;
import com.taylors.csc61204.api.TriviaApiClient;
import com.taylors.csc61204.model.Question;
import com.taylors.csc61204.model.QuestionBank;
import com.taylors.csc61204.model.Quiz;
import com.taylors.csc61204.model.QuizResult;
import com.taylors.csc61204.pattern.builder.QuizBuilder;
import com.taylors.csc61204.pattern.strategy.QuestionSelectionStrategy;

import java.util.List;
import java.util.Objects;

/**
 * Orchestrates quiz generation and grading.
 * <p>
 * Sits between the controller (GUI) and the model/API layers. The GUI never
 * touches the API client, Builder, or Strategy directly — it only talks to this
 * service. That separation is what makes MVC enforceable in this codebase.
 */
public class QuizService {

    private final QuestionBank bank;
    private final TriviaApiClient apiClient;

    public QuizService(QuestionBank bank, TriviaApiClient apiClient) {
        this.bank = Objects.requireNonNull(bank);
        this.apiClient = Objects.requireNonNull(apiClient);
    }

    /**
     * Builds a quiz using the supplied strategy. The Builder pattern is hidden
     * from the caller; they only see "give me a quiz with these parameters".
     */
    public Quiz generateQuiz(String title, int count,
                             QuestionSelectionStrategy strategy, int timeLimitSeconds) {
        return new QuizBuilder(bank)
                .withTitle(title)
                .withCount(count)
                .withStrategy(strategy)
                .withTimeLimit(timeLimitSeconds)
                .build();
    }

    /**
     * Grades a submission and produces a {@link QuizResult}.
     */
    public QuizResult submit(Quiz quiz, List<String> answers, String studentId) {
        int correct = quiz.grade(answers);
        String category = inferCategory(quiz);
        return new QuizResult(quiz.getQuizId(), studentId, correct, quiz.size(), category);
    }

    /**
     * Refreshes the local question bank from the external API.
     * <p>
     * <b>Graceful degradation:</b> if the API fails for any reason, this method
     * returns {@code false} rather than throwing, so the GUI can show a
     * friendly message and fall back to whatever is already in the bank.
     */
    public boolean refreshBankFromApi(int amount, String difficulty) {
        try {
            List<Question> fetched = apiClient.fetchQuestions(amount, difficulty);
            for (Question q : fetched) {
                bank.add(q);
            }
            return true;
        } catch (ApiException e) {
            return false;
        }
    }

    public int getBankSize() {
        return bank.size();
    }

    /** Most quizzes have a dominant category — use the first question's. */
    private String inferCategory(Quiz quiz) {
        return quiz.getQuestions().get(0).getCategory();
    }
}
