package com.taylors.csc61204.model;

/**
 * True/false question. A response is correct if it parses to the same boolean
 * value as the stored answer. Accepted response strings are "true"/"false"
 * (case-insensitive, trimmed).
 */
public class TrueFalseQuestion extends Question {

    private final boolean correctAnswer;

    public TrueFalseQuestion(String prompt, boolean correctAnswer,
                             String category, String difficulty) {
        super(prompt, category, difficulty);
        this.correctAnswer = correctAnswer;
    }

    @Override
    public boolean isCorrect(String response) {
        if (response == null) {
            return false;
        }
        String normalised = response.trim().toLowerCase();
        if (!normalised.equals("true") && !normalised.equals("false")) {
            return false;
        }
        return Boolean.parseBoolean(normalised) == correctAnswer;
    }

    public boolean getCorrectAnswer() { return correctAnswer; }
}
