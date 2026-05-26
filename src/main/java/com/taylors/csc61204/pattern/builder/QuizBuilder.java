package com.taylors.csc61204.pattern.builder;

import com.taylors.csc61204.model.Question;
import com.taylors.csc61204.model.QuestionBank;
import com.taylors.csc61204.model.Quiz;
import com.taylors.csc61204.pattern.strategy.QuestionSelectionStrategy;
import com.taylors.csc61204.pattern.strategy.RandomSelectionStrategy;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Fluent builder for constructing {@link Quiz} instances.
 * <p>
 * Encapsulates the multi-step process of picking questions (via a Strategy),
 * applying metadata, and validating the result. Demonstrates the Builder GoF
 * pattern and the Single Responsibility Principle: the Quiz class is responsible
 * for representing a quiz, while this class is responsible for constructing one.
 */
public class QuizBuilder {

    private static final int DEFAULT_QUESTION_COUNT = 10;
    private static final int DEFAULT_TIME_LIMIT_SECONDS = 600;
    private static final String DEFAULT_TITLE = "Untitled Quiz";

    private final QuestionBank bank;
    private QuestionSelectionStrategy strategy;
    private int count;
    private String title;
    private int timeLimitSeconds;
    private String quizId;

    public QuizBuilder(QuestionBank bank) {
        this.bank = Objects.requireNonNull(bank, "bank cannot be null");
        this.strategy = new RandomSelectionStrategy();
        this.count = DEFAULT_QUESTION_COUNT;
        this.title = DEFAULT_TITLE;
        this.timeLimitSeconds = DEFAULT_TIME_LIMIT_SECONDS;
        this.quizId = UUID.randomUUID().toString();
    }

    public QuizBuilder withStrategy(QuestionSelectionStrategy strategy) {
        this.strategy = Objects.requireNonNull(strategy, "strategy cannot be null");
        return this;
    }

    public QuizBuilder withCount(int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("count must be positive");
        }
        this.count = count;
        return this;
    }

    public QuizBuilder withTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("title cannot be null or blank");
        }
        this.title = title;
        return this;
    }

    public QuizBuilder withTimeLimit(int seconds) {
        if (seconds <= 0) {
            throw new IllegalArgumentException("time limit must be positive");
        }
        this.timeLimitSeconds = seconds;
        return this;
    }

    public QuizBuilder withQuizId(String quizId) {
        if (quizId == null || quizId.isBlank()) {
            throw new IllegalArgumentException("quizId cannot be null or blank");
        }
        this.quizId = quizId;
        return this;
    }

    /**
     * Constructs the {@link Quiz} using the configured strategy and metadata.
     *
     * @return a fully-formed Quiz
     * @throws IllegalStateException if the bank does not have enough questions
     */
    public Quiz build() {
        if (bank.size() < count) {
            throw new IllegalStateException(
                    "Bank has only " + bank.size() + " questions but " + count + " requested");
        }
        List<Question> selected = strategy.select(bank, count);
        return new Quiz(quizId, title, selected, timeLimitSeconds);
    }
}
