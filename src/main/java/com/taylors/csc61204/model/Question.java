package com.taylors.csc61204.model;

import java.util.List;
import java.util.Objects;

/**
 * Represents a single quiz question with multiple choice answers.
 */
public class Question {

    private final String prompt;
    private final List<String> options;
    private final int correctIndex;
    private final String category;
    private final String difficulty;

    public Question(String prompt, List<String> options, int correctIndex,
                    String category, String difficulty) {
        if (prompt == null || prompt.isBlank()) {
            throw new IllegalArgumentException("Prompt cannot be null or blank");
        }
        if (options == null || options.size() < 2) {
            throw new IllegalArgumentException("Must provide at least 2 options");
        }
        if (correctIndex < 0 || correctIndex >= options.size()) {
            throw new IndexOutOfBoundsException("correctIndex out of range");
        }
        this.prompt = prompt;
        this.options = List.copyOf(options);
        this.correctIndex = correctIndex;
        this.category = Objects.requireNonNull(category);
        this.difficulty = Objects.requireNonNull(difficulty);
    }

    public boolean isCorrect(int answerIndex) {
        return answerIndex == correctIndex;
    }

    public String getPrompt() { return prompt; }
    public List<String> getOptions() { return options; }
    public int getCorrectIndex() { return correctIndex; }
    public String getCategory() { return category; }
    public String getDifficulty() { return difficulty; }
}
