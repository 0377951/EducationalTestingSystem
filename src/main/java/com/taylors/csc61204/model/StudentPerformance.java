package com.taylors.csc61204.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Tracks a student's quiz history and computes performance metrics
 * used by the feedback service to drive personalised recommendations.
 */
public class StudentPerformance {

    private static final double WEAK_CATEGORY_THRESHOLD = 60.0;

    private final String studentId;
    private final List<QuizResult> history;

    public StudentPerformance(String studentId) {
        this.studentId = Objects.requireNonNull(studentId);
        this.history = new ArrayList<>();
    }

    public void record(QuizResult result) {
        Objects.requireNonNull(result);
        if (!result.getStudentId().equals(studentId)) {
            throw new IllegalArgumentException("Result belongs to a different student");
        }
        history.add(result);
    }

    public double averageScorePercent() {
        if (history.isEmpty()) {
            return 0.0;
        }
        return history.stream().mapToDouble(QuizResult::getScorePercent).average().orElse(0.0);
    }

    /** Returns categories where the student's average is below the weak threshold. */
    public List<String> weakCategories() {
        Map<String, List<QuizResult>> byCategory = history.stream()
                .collect(Collectors.groupingBy(QuizResult::getCategory));
        return byCategory.entrySet().stream()
                .filter(e -> averagePercent(e.getValue()) < WEAK_CATEGORY_THRESHOLD)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    private double averagePercent(List<QuizResult> results) {
        return results.stream().mapToDouble(QuizResult::getScorePercent).average().orElse(0.0);
    }

    public int attemptCount() {
        return history.size();
    }

    public List<QuizResult> getHistory() {
        return Collections.unmodifiableList(history);
    }

    public String getStudentId() {
        return studentId;
    }
}
