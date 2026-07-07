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

    /**
     * DELIBERATELY DISABLED — required by the assignment rubric ("one deliberately
     * failing test case with a written explanation"). Kept in the codebase as
     * living documentation of a known limitation.
     * <p>
     * <b>Written explanation:</b> {@link ShortAnswerQuestion} judges a response
     * correct only if it matches the expected answer as a plain string (after
     * trim + case-fold). Semantic equivalence — treating "water" as a valid
     * answer when the expected answer is "H2O" — would require a synonym
     * dictionary or an NLP model, both of which are out of scope for a 2nd-year
     * project. The limitation is called out in Section 4 (Reflection) of the
     * report. If we enable this test today it fails; {@code @Disabled} keeps
     * CI green while preserving the record.
     */
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
