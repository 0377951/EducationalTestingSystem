package com.taylors.csc61204.pattern.strategy;

import com.taylors.csc61204.model.Question;
import com.taylors.csc61204.model.QuestionBank;

import java.util.List;
import java.util.Objects;

/**
 * Selects questions matching a specific difficulty level.
 */
public class DifficultyBasedStrategy implements QuestionSelectionStrategy {

    private final String targetDifficulty;

    public DifficultyBasedStrategy(String targetDifficulty) {
        this.targetDifficulty = Objects.requireNonNull(targetDifficulty);
    }

    @Override
    public List<Question> select(QuestionBank bank, int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("count must be positive");
        }
        List<Question> filtered = bank.byDifficulty(targetDifficulty);
        if (filtered.size() < count) {
            throw new IllegalArgumentException(
                    "Not enough questions of difficulty '" + targetDifficulty + "'");
        }
        return List.copyOf(filtered.subList(0, count));
    }
}
