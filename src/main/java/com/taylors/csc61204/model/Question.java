package com.taylors.csc61204.model;

import java.util.Objects;

/**
 * A single quiz question.
 * <p>
 * This is the root of an IS-A hierarchy: {@link MultipleChoiceQuestion},
 * {@link TrueFalseQuestion}, and {@link ShortAnswerQuestion} extend it and each
 * supply their own answer-checking rule via {@link #isCorrect(String)}. The
 * hierarchy is what lets the rest of the system (grader, selection strategies,
 * builder) work with any question type uniformly — a textbook use of polymorphism.
 */
public abstract class Question {

    private final String prompt;
    private final String category;
    private final String difficulty;

    protected Question(String prompt, String category, String difficulty) {
        if (prompt == null || prompt.isBlank()) {
            throw new IllegalArgumentException("Prompt cannot be null or blank");
        }
        this.prompt = prompt;
        this.category = Objects.requireNonNull(category);
        this.difficulty = Objects.requireNonNull(difficulty);
    }

    /**
     * Returns true if {@code response} matches this question's correct answer.
     * The comparison rule is subclass-specific.
     *
     * @param response the student's submitted answer (never null)
     * @return true if the response is judged correct
     */
    public abstract boolean isCorrect(String response);

    public String getPrompt() { return prompt; }
    public String getCategory() { return category; }
    public String getDifficulty() { return difficulty; }
}
