package com.taylors.csc61204.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Immutable record of a single completed quiz attempt.
 * Stored in {@link StudentPerformance} to track learning over time.
 */
public class QuizResult {

    private final String quizId;
    private final String studentId;
    private final int correctCount;
    private final int totalCount;
    private final String category;
    private final LocalDateTime completedAt;

    public QuizResult(String quizId, String studentId, int correctCount,
                      int totalCount, String category) {
        this(quizId, studentId, correctCount, totalCount, category, LocalDateTime.now());
    }

    /** Test constructor — allows injection of a fixed timestamp. */
    public QuizResult(String quizId, String studentId, int correctCount,
                      int totalCount, String category, LocalDateTime completedAt) {
        if (correctCount < 0 || correctCount > totalCount) {
            throw new IllegalArgumentException("correctCount must be in [0, totalCount]");
        }
        if (totalCount <= 0) {
            throw new IllegalArgumentException("totalCount must be positive");
        }
        this.quizId = Objects.requireNonNull(quizId);
        this.studentId = Objects.requireNonNull(studentId);
        this.correctCount = correctCount;
        this.totalCount = totalCount;
        this.category = Objects.requireNonNull(category);
        this.completedAt = Objects.requireNonNull(completedAt);
    }

    public double getScorePercent() {
        return (correctCount * 100.0) / totalCount;
    }

    public boolean isPassing() {
        return getScorePercent() >= 50.0;
    }

    public String getQuizId() { return quizId; }
    public String getStudentId() { return studentId; }
    public int getCorrectCount() { return correctCount; }
    public int getTotalCount() { return totalCount; }
    public String getCategory() { return category; }
    public LocalDateTime getCompletedAt() { return completedAt; }
}
