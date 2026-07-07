package com.taylors.csc61204.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class ShortAnswerQuestionTest {

    private ShortAnswerQuestion make() {
        return new ShortAnswerQuestion("Chemical symbol for water?", "H2O", "Sci", "medium");
    }

    @Test
    void isCorrect_exactMatch_returnsTrue() {
        assertTrue(make().isCorrect("H2O"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"h2o", "  H2O ", "H2o"})
    void isCorrect_caseAndWhitespaceInsensitive_returnsTrue(String response) {
        assertTrue(make().isCorrect(response));
    }

    @Test
    void isCorrect_wrongAnswer_returnsFalse() {
        assertFalse(make().isCorrect("HHO"));
    }

    @Test
    void isCorrect_nullResponse_returnsFalse() {
        assertFalse(make().isCorrect(null));
    }

    @Test
    void constructor_blankExpectedAnswer_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> new ShortAnswerQuestion("Q?", "  ", "Cat", "easy"));
    }
}
