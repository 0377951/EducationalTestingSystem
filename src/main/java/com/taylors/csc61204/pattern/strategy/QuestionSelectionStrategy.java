package com.taylors.csc61204.pattern.strategy;

import com.taylors.csc61204.model.Question;
import com.taylors.csc61204.model.QuestionBank;

import java.util.List;

/**
 * Strategy interface for selecting a subset of questions from a {@link QuestionBank}.
 * <p>
 * Implementations encapsulate different selection algorithms (random, difficulty-based,
 * weak-topic focused) and are interchangeable at runtime. This is the Strategy GoF pattern,
 * demonstrating the Open/Closed Principle: new algorithms can be added without modifying
 * existing code that depends on this interface.
 */
public interface QuestionSelectionStrategy {

    /**
     * Selects {@code count} questions from the given bank according to this strategy.
     *
     * @param bank  the source pool of questions (must not be null)
     * @param count number of questions to select (must be positive and ≤ bank size)
     * @return an immutable list containing exactly {@code count} questions
     * @throws IllegalArgumentException if count is invalid
     */
    List<Question> select(QuestionBank bank, int count);
}
