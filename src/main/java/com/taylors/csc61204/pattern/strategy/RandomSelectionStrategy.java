package com.taylors.csc61204.pattern.strategy;

import com.taylors.csc61204.model.Question;
import com.taylors.csc61204.model.QuestionBank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Selects questions uniformly at random from the entire bank.
 */
public class RandomSelectionStrategy implements QuestionSelectionStrategy {

    private final Random random;

    public RandomSelectionStrategy() {
        this(new Random());
    }

    /** Constructor for tests — accepts a seeded Random for determinism. */
    public RandomSelectionStrategy(Random random) {
        this.random = random;
    }

    @Override
    public List<Question> select(QuestionBank bank, int count) {
        validate(bank, count);
        List<Question> shuffled = new ArrayList<>(bank.all());
        Collections.shuffle(shuffled, random);
        return List.copyOf(shuffled.subList(0, count));
    }

    private void validate(QuestionBank bank, int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("count must be positive");
        }
        if (count > bank.size()) {
            throw new IllegalArgumentException("count exceeds bank size");
        }
    }
}
