package com.taylors.csc61204.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * A categorised pool of {@link Question}s that quiz-generation strategies draw from.
 */
public class QuestionBank {

    private final List<Question> pool;

    public QuestionBank() {
        this.pool = new ArrayList<>();
    }

    public QuestionBank(List<Question> initial) {
        this.pool = new ArrayList<>(Objects.requireNonNull(initial));
    }

    public void add(Question question) {
        pool.add(Objects.requireNonNull(question));
    }

    public List<Question> byCategory(String category) {
        Objects.requireNonNull(category);
        return pool.stream()
                .filter(q -> q.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toUnmodifiableList());
    }

    public List<Question> byDifficulty(String difficulty) {
        Objects.requireNonNull(difficulty);
        return pool.stream()
                .filter(q -> q.getDifficulty().equalsIgnoreCase(difficulty))
                .collect(Collectors.toUnmodifiableList());
    }

    public List<Question> all() {
        return Collections.unmodifiableList(pool);
    }

    public int size() {
        return pool.size();
    }
}
