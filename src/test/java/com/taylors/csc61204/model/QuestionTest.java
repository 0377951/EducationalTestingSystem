package com.taylors.csc61204.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class QuestionTest {

    private Question makeValidQuestion() {
        return new Question(
                "What is 2 + 2?",
                List.of("3", "4", "5", "6"),
                1,
                "Math",
                "easy"
        );
    }

    @Test
    void isCorrect_correctAnswerIndex_returnsTrue() {
        Question q = makeValidQuestion();
        assertTrue(q.isCorrect(1));
    }

    @Test
    void isCorrect_wrongAnswerIndex_returnsFalse() {
        Question q = makeValidQuestion();
        assertFalse(q.isCorrect(0));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 2, 3})
    void isCorrect_anyWrongIndex_returnsFalse(int wrongIndex) {
        Question q = makeValidQuestion();
        assertFalse(q.isCorrect(wrongIndex));
    }

    @Test
    void constructor_blankPrompt_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () ->
                new Question("", List.of("a", "b"), 0, "Math", "easy")
        );
    }

    @Test
    void constructor_correctIndexOutOfRange_throwsIndexOutOfBoundsException() {
        assertThrows(IndexOutOfBoundsException.class, () ->
                new Question("Q?", List.of("a", "b"), 5, "Math", "easy")
        );
    }

    @Test
    void getters_validQuestion_returnAllFieldsCorrectly() {
        Question q = makeValidQuestion();
        assertAll(
                () -> assertEquals("What is 2 + 2?", q.getPrompt()),
                () -> assertEquals(4, q.getOptions().size()),
                () -> assertEquals(1, q.getCorrectIndex()),
                () -> assertEquals("Math", q.getCategory()),
                () -> assertEquals("easy", q.getDifficulty())
        );
    }
}
