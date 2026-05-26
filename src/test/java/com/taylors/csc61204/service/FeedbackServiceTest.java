package com.taylors.csc61204.service;

import com.taylors.csc61204.model.QuizResult;
import com.taylors.csc61204.model.StudentPerformance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class FeedbackServiceTest {

    private StudentPerformance perf;
    private FeedbackService feedback;

    @BeforeEach
    void setUp() {
        perf = new StudentPerformance("S-001");
        feedback = new FeedbackService(perf);
    }

    @ParameterizedTest
    @CsvSource({
            "9, 10, Excellent",
            "7, 10, Good",
            "5, 10, Pass",
            "2, 10, Needs work"
    })
    void summaryFor_variousScores_returnsExpectedTier(int correct, int total, String tier) {
        QuizResult r = new QuizResult("Q1", "S-001", correct, total, "Math");
        assertTrue(feedback.summaryFor(r).startsWith(tier));
    }

    @Test
    void recommendation_noAttempts_promptsToTakeFirstQuiz() {
        assertTrue(feedback.recommendation().contains("first quiz"));
    }

    @Test
    void recommendation_weakInMath_recommendsMath() {
        perf.record(new QuizResult("Q1", "S-001", 3, 10, "Math"));
        assertTrue(feedback.recommendation().contains("Math"));
    }

    @Test
    void recommendation_strongAcrossBoard_suggestsHarderDifficulty() {
        perf.record(new QuizResult("Q1", "S-001", 9, 10, "Math"));
        perf.record(new QuizResult("Q2", "S-001", 10, 10, "Science"));
        assertTrue(feedback.recommendation().toLowerCase().contains("harder"));
    }

    @Test
    void progressReport_noAttempts_returnsNoQuizzesMessage() {
        assertEquals("No quizzes attempted yet.", feedback.progressReport());
    }

    @Test
    void progressReport_withAttempts_includesAttemptsAndAverage() {
        perf.record(new QuizResult("Q1", "S-001", 8, 10, "Math"));
        String report = feedback.progressReport();
        assertAll(
                () -> assertTrue(report.contains("Attempts: 1")),
                () -> assertTrue(report.contains("80.0%"))
        );
    }
}
