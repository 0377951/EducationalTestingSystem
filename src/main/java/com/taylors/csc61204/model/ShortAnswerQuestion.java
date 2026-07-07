package com.taylors.csc61204.model;

import java.util.Objects;

/**
 * Short-answer question. A response is correct if, after trimming and
 * lower-casing, it exactly matches the expected answer.
 * <p>
 * <b>Known limitation (documented, not a bug):</b> the comparison is a plain
 * string match. Semantically equivalent answers such as "H2O" vs "water" are
 * <i>not</i> recognised. See the deliberately-disabled test
 * {@code isCorrect_shortAnswerSemanticEquivalent_returnsTrue} for the
 * documented failure case.
 */
public class ShortAnswerQuestion extends Question {

    private final String expectedAnswer;

    public ShortAnswerQuestion(String prompt, String expectedAnswer,
                               String category, String difficulty) {
        super(prompt, category, difficulty);
        Objects.requireNonNull(expectedAnswer, "expectedAnswer cannot be null");
        if (expectedAnswer.isBlank()) {
            throw new IllegalArgumentException("expectedAnswer cannot be blank");
        }
        this.expectedAnswer = expectedAnswer;
    }

    @Override
    public boolean isCorrect(String response) {
        if (response == null) {
            return false;
        }
        return expectedAnswer.trim().equalsIgnoreCase(response.trim());
    }

    public String getExpectedAnswer() { return expectedAnswer; }
}
