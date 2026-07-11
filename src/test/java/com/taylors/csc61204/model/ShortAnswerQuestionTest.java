package com.taylors.csc61204.model;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
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

    @Test
    @Disabled("INTENTIONAL — documents exact-match limitation; see report Section 4")
    @DisplayName("INTENTIONAL FAIL: short-answer semantic equivalence not supported")
    void isCorrect_shortAnswerSemanticEquivalent_returnsTrue() {
        ShortAnswerQuestion q = new ShortAnswerQuestion(
                "Chemical symbol for water?", "H2O", "Sci", "medium");
        assertTrue(q.isCorrect("water"),
                "System uses exact string match, so 'water' is NOT accepted as equivalent to 'H2O'.");
    }
}
