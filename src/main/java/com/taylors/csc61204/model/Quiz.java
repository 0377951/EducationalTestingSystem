package com.taylors.csc61204.model;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents a complete quiz: an ordered set of questions with metadata.
 * Once constructed, the question list is immutable; grading takes a list
 * of response strings (one per question, in order) and returns the number
 * of correct responses.
 */
public class Quiz {

    private static final int MIN_QUESTIONS = 1;

    private final String quizId;
    private final String title;
    private final List<Question> questions;
    private final int timeLimitSeconds;

    public Quiz(String quizId, String title, List<Question> questions, int timeLimitSeconds) {
        if (quizId == null || quizId.isBlank()) {
            throw new IllegalArgumentException("quizId cannot be null or blank");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("title cannot be null or blank");
        }
        if (questions == null || questions.size() < MIN_QUESTIONS) {
            throw new IllegalArgumentException("Quiz must contain at least one question");
        }
        if (timeLimitSeconds <= 0) {
            throw new IllegalArgumentException("timeLimitSeconds must be positive");
        }
        this.quizId = quizId;
        this.title = title;
        this.questions = List.copyOf(questions);
        this.timeLimitSeconds = timeLimitSeconds;
    }

    /**
     * Returns the question at the given index.
     *
     * @param index zero-based index
     * @return the Question at that position
     * @throws IndexOutOfBoundsException if index is out of range
     */
    public Question getQuestion(int index) {
        return questions.get(index);
    }

    /**
     * Grades a list of response strings and returns the number of correct answers.
     * Polymorphic: each question decides for itself whether a response is correct.
     *
     * @param responses one response per question, in order
     * @return number of correct answers (0 to size())
     * @throws IllegalArgumentException if the response count does not match the quiz size
     */
    public int grade(List<String> responses) {
        Objects.requireNonNull(responses, "responses cannot be null");
        if (responses.size() != questions.size()) {
            throw new IllegalArgumentException(
                    "Expected " + questions.size() + " responses but got " + responses.size());
        }
        int correct = 0;
        for (int i = 0; i < questions.size(); i++) {
            if (questions.get(i).isCorrect(responses.get(i))) {
                correct++;
            }
        }
        return correct;
    }

    public int size() {
        return questions.size();
    }

    public String getQuizId() { return quizId; }
    public String getTitle() { return title; }
    public List<Question> getQuestions() { return Collections.unmodifiableList(questions); }
    public int getTimeLimitSeconds() { return timeLimitSeconds; }
}
