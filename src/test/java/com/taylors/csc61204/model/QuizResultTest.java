package com.taylors.csc61204.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class QuizResultTest {

    @Test
    void getScorePercent_eightOfTen_returnsEighty() {
        QuizResult r = new QuizResult("Q1", "S1", 8, 10, "Math");
        assertEquals(80.0, r.getScorePercent(), 0.001);
    }

    @ParameterizedTest
    @CsvSource({"5, 10, true", "10, 10, true", "4, 10, false", "0, 10, false"})
    void isPassing_variousScores_returnsExpected(int correct, int total, boolean expected) {
        QuizResult r = new QuizResult("Q1", "S1", correct, total, "Math");
        assertEquals(expected, r.isPassing());
    }

    @Test
    void constructor_correctExceedsTotal_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> new QuizResult("Q1", "S1", 11, 10, "Math"));
    }

    @Test
    void constructor_negativeCorrect_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> new QuizResult("Q1", "S1", -1, 10, "Math"));
    }

    @Test
    void constructor_zeroTotal_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> new QuizResult("Q1", "S1", 0, 0, "Math"));
    }

    @Test
    void getters_constructed_returnAllFields() {
        LocalDateTime ts = LocalDateTime.of(2026, 5, 1, 10, 0);
        QuizResult r = new QuizResult("Q1", "S1", 7, 10, "Math", ts);
        assertAll(
                () -> assertEquals("Q1", r.getQuizId()),
                () -> assertEquals("S1", r.getStudentId()),
                () -> assertEquals(7, r.getCorrectCount()),
                () -> assertEquals(10, r.getTotalCount()),
                () -> assertEquals("Math", r.getCategory()),
                () -> assertEquals(ts, r.getCompletedAt())
        );
    }
}
