package com.taylors.csc61204.service;

import com.taylors.csc61204.model.QuizResult;
import com.taylors.csc61204.model.StudentPerformance;

import java.util.List;
import java.util.Objects;

/**
 * Generates personalised feedback messages from a student's performance history.
 * <p>
 * Kept separate from {@link QuizService} (Single Responsibility Principle):
 * QuizService creates and grades quizzes; FeedbackService interprets the results.
 */
public class FeedbackService {

    private static final double EXCELLENT_THRESHOLD = 85.0;
    private static final double GOOD_THRESHOLD = 70.0;
    private static final double PASS_THRESHOLD = 50.0;

    private final StudentPerformance performance;

    public FeedbackService(StudentPerformance performance) {
        this.performance = Objects.requireNonNull(performance);
    }

    /**
     * Produces a short summary string for a single quiz attempt.
     */
    public String summaryFor(QuizResult result) {
        Objects.requireNonNull(result);
        double percent = result.getScorePercent();
        return performanceTier(percent) + " — " + result.getCorrectCount()
                + " of " + result.getTotalCount() + " correct ("
                + String.format("%.0f%%", percent) + ")";
    }

    /**
     * Suggests the next focus area based on weak categories.
     */
    public String recommendation() {
        if (performance.attemptCount() == 0) {
            return "Take your first quiz to get a personalised recommendation.";
        }
        List<String> weak = performance.weakCategories();
        if (weak.isEmpty()) {
            return "Strong across all categories — try a harder difficulty next.";
        }
        return "Focus area: " + String.join(", ", weak);
    }

    /**
     * High-level progress report combining average score, attempts, and weak areas.
     */
    public String progressReport() {
        if (performance.attemptCount() == 0) {
            return "No quizzes attempted yet.";
        }
        return "Attempts: " + performance.attemptCount()
                + " | Average: " + String.format("%.1f%%", performance.averageScorePercent())
                + " | " + recommendation();
    }

    private String performanceTier(double percent) {
        if (percent >= EXCELLENT_THRESHOLD) return "Excellent";
        if (percent >= GOOD_THRESHOLD) return "Good";
        if (percent >= PASS_THRESHOLD) return "Pass";
        return "Needs work";
    }
}
