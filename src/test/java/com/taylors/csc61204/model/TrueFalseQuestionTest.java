package com.taylors.csc61204.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class TrueFalseQuestionTest {

    @Test
    void isCorrect_matchingTrue_returnsTrue() {
        assertTrue(new TrueFalseQuestion("Sky blue?", true, "Sci", "easy").isCorrect("true"));
    }

    @Test
    void isCorrect_matchingFalse_returnsTrue() {
        assertTrue(new TrueFalseQuestion("Sun cold?", false, "Sci", "easy").isCorrect("false"));
    }

    @ParameterizedTest
    @CsvSource({"true, TRUE", "true, True", "false, FALSE", "false, false "})
    void isCorrect_variousCasesAndWhitespace_returnsExpected(boolean expected, String response) {
        assertTrue(new TrueFalseQuestion("Q?", expected, "Sci", "easy").isCorrect(response));
    }

    @ParameterizedTest
    @ValueSource(strings = {"yes", "no", "1", "0", ""})
    void isCorrect_nonBooleanResponse_returnsFalse(String response) {
        assertFalse(new TrueFalseQuestion("Q?", true, "Sci", "easy").isCorrect(response));
    }

    @Test
    void isCorrect_nullResponse_returnsFalse() {
        assertFalse(new TrueFalseQuestion("Q?", true, "Sci", "easy").isCorrect(null));
    }

    @Test
    void getCorrectAnswer_returnsStoredBoolean() {
        assertTrue(new TrueFalseQuestion("Q?", true, "Sci", "easy").getCorrectAnswer());
    }
}
