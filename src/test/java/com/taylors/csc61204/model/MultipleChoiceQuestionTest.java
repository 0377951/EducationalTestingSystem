package com.taylors.csc61204.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MultipleChoiceQuestionTest {

    private MultipleChoiceQuestion make() {
        return new MultipleChoiceQuestion(
                "Capital of France?",
                List.of("Paris", "Rome", "Berlin", "Madrid"),
                "Paris", "Geography", "easy");
    }

    @Test
    void isCorrect_matchingAnswer_returnsTrue() {
        assertTrue(make().isCorrect("Paris"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"Rome", "Berlin", "Madrid", "paris", ""})
    void isCorrect_nonMatchingResponse_returnsFalse(String response) {
        assertFalse(make().isCorrect(response));
    }

    @Test
    void constructor_correctAnswerNotAmongOptions_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> new MultipleChoiceQuestion(
                        "Q?", List.of("a", "b"), "c", "Cat", "easy"));
    }

    @Test
    void constructor_fewerThanTwoOptions_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> new MultipleChoiceQuestion(
                        "Q?", List.of("only"), "only", "Cat", "easy"));
    }

    @Test
    void getOptions_returnedList_isImmutable() {
        assertThrows(UnsupportedOperationException.class,
                () -> make().getOptions().add("Extra"));
    }

    @Test
    void getCorrectAnswer_returnsStoredAnswer() {
        assertEquals("Paris", make().getCorrectAnswer());
    }
}
