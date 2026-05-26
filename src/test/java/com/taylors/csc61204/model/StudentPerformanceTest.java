package com.taylors.csc61204.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StudentPerformanceTest {

    private StudentPerformance perf;

    @BeforeEach
    void setUp() {
        perf = new StudentPerformance("S-001");
    }

    @Test
    void record_resultForSameStudent_addsToHistory() {
        perf.record(new QuizResult("Q1", "S-001", 8, 10, "Math"));
        assertEquals(1, perf.attemptCount());
    }

    @Test
    void record_resultForDifferentStudent_throwsIllegalArgumentException() {
        QuizResult other = new QuizResult("Q1", "S-OTHER", 8, 10, "Math");
        assertThrows(IllegalArgumentException.class, () -> perf.record(other));
    }

    @Test
    void averageScorePercent_noAttempts_returnsZero() {
        assertEquals(0.0, perf.averageScorePercent(), 0.001);
    }

    @Test
    void averageScorePercent_twoAttempts_returnsMean() {
        perf.record(new QuizResult("Q1", "S-001", 8, 10, "Math"));
        perf.record(new QuizResult("Q2", "S-001", 6, 10, "Math"));
        assertEquals(70.0, perf.averageScorePercent(), 0.001);
    }

    @Test
    void weakCategories_lowAverageInMath_identifiesMath() {
        perf.record(new QuizResult("Q1", "S-001", 3, 10, "Math"));
        perf.record(new QuizResult("Q2", "S-001", 9, 10, "Science"));
        assertTrue(perf.weakCategories().contains("Math"));
        assertFalse(perf.weakCategories().contains("Science"));
    }

    @Test
    void getHistory_attemptModification_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class,
                () -> perf.getHistory().add(new QuizResult("X", "S-001", 1, 1, "Math")));
    }
}
