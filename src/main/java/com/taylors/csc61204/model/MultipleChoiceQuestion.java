package com.taylors.csc61204.model;

import java.util.List;
import java.util.Objects;

/**
 * Multiple-choice question. A response is correct if it exactly matches the
 * stored correct answer string. Options are held immutably; the correct answer
 * must appear in the options list.
 */
public class MultipleChoiceQuestion extends Question {

    private static final int MIN_OPTIONS = 2;

    private final List<String> options;
    private final String correctAnswer;

    public MultipleChoiceQuestion(String prompt, List<String> options,
                                  String correctAnswer, String category, String difficulty) {
        super(prompt, category, difficulty);
        if (options == null || options.size() < MIN_OPTIONS) {
            throw new IllegalArgumentException("Must provide at least 2 options");
        }
        Objects.requireNonNull(correctAnswer, "correctAnswer cannot be null");
        if (!options.contains(correctAnswer)) {
            throw new IllegalArgumentException("correctAnswer must be one of the options");
        }
        this.options = List.copyOf(options);
        this.correctAnswer = correctAnswer;
    }

    @Override
    public boolean isCorrect(String response) {
        return correctAnswer.equals(response);
    }

    public List<String> getOptions() { return options; }
    public String getCorrectAnswer() { return correctAnswer; }
}
