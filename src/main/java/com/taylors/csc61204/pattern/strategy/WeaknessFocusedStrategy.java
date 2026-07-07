package com.taylors.csc61204.pattern.strategy;

import com.taylors.csc61204.model.Question;
import com.taylors.csc61204.model.QuestionBank;
import com.taylors.csc61204.model.StudentPerformance;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;

/**
 * Adaptive selection strategy: prioritises questions from categories where the
 * student has previously performed poorly.
 * <p>
 * This closes the personalised-feedback loop required by the scenario:
 * {@link StudentPerformance#weakCategories()} identifies areas of struggle, and
 * this strategy focuses the next quiz on them. If the student has no history
 * yet, or the weak categories don't have enough questions, we delegate to a
 * fallback strategy (random by default). This keeps the class useful on day one
 * without special-casing at the call site.
 * <p>
 * Adding this strategy required <b>no changes</b> to any existing class — the
 * Strategy interface stayed identical. That is the Open/Closed Principle
 * demonstrated concretely.
 */
public class WeaknessFocusedStrategy implements QuestionSelectionStrategy {

    private final StudentPerformance performance;
    private final QuestionSelectionStrategy fallback;

    public WeaknessFocusedStrategy(StudentPerformance performance) {
        this(performance, new RandomSelectionStrategy());
    }

    public WeaknessFocusedStrategy(StudentPerformance performance,
                                   QuestionSelectionStrategy fallback) {
        this.performance = Objects.requireNonNull(performance, "performance cannot be null");
        this.fallback = Objects.requireNonNull(fallback, "fallback cannot be null");
    }

    @Override
    public List<Question> select(QuestionBank bank, int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("count must be positive");
        }
        if (performance.attemptCount() == 0) {
            return fallback.select(bank, count);
        }
        List<String> weak = performance.weakCategories();
        if (weak.isEmpty()) {
            return fallback.select(bank, count);
        }
        List<Question> picked = drawFromWeakCategories(bank, weak, count);
        if (picked.size() < count) {
            picked = topUpFromRestOfBank(bank, picked, count);
        }
        return List.copyOf(picked);
    }

    private List<Question> drawFromWeakCategories(QuestionBank bank,
                                                  List<String> weakCategories, int count) {
        List<Question> selected = new ArrayList<>();
        for (String category : weakCategories) {
            for (Question q : bank.byCategory(category)) {
                if (selected.size() == count) {
                    return selected;
                }
                selected.add(q);
            }
        }
        return selected;
    }

    private List<Question> topUpFromRestOfBank(QuestionBank bank,
                                               List<Question> weakPicks, int count) {
        // LinkedHashSet preserves order and prevents duplicates.
        LinkedHashSet<Question> combined = new LinkedHashSet<>(weakPicks);
        for (Question q : bank.all()) {
            if (combined.size() == count) {
                break;
            }
            combined.add(q);
        }
        if (combined.size() < count) {
            throw new IllegalArgumentException("Not enough questions in bank to reach count");
        }
        return new ArrayList<>(combined);
    }
}
