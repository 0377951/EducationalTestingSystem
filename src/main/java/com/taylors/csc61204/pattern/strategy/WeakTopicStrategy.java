package com.taylors.csc61204.pattern.strategy;

import com.taylors.csc61204.model.Question;
import com.taylors.csc61204.model.QuestionBank;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Selects questions from categories where the student has previously performed poorly.
 * Categories are evaluated in priority order — earlier categories in the list are
 * considered weaker and contribute questions first.
 */
public class WeakTopicStrategy implements QuestionSelectionStrategy {

    private final List<String> weakCategoriesInPriorityOrder;

    public WeakTopicStrategy(List<String> weakCategoriesInPriorityOrder) {
        this.weakCategoriesInPriorityOrder =
                List.copyOf(Objects.requireNonNull(weakCategoriesInPriorityOrder));
    }

    @Override
    public List<Question> select(QuestionBank bank, int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("count must be positive");
        }
        List<Question> selected = new ArrayList<>();
        for (String category : weakCategoriesInPriorityOrder) {
            for (Question q : bank.byCategory(category)) {
                if (selected.size() == count) {
                    return List.copyOf(selected);
                }
                selected.add(q);
            }
        }
        if (selected.size() < count) {
            throw new IllegalArgumentException("Not enough questions in weak categories");
        }
        return List.copyOf(selected);
    }
}
