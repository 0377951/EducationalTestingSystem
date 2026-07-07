package com.taylors.csc61204.pattern.strategy;

import com.taylors.csc61204.model.MultipleChoiceQuestion;
import com.taylors.csc61204.model.Question;
import com.taylors.csc61204.model.QuestionBank;
import com.taylors.csc61204.model.QuizResult;
import com.taylors.csc61204.model.StudentPerformance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class WeaknessFocusedStrategyTest {

    private QuestionBank bank;
    private StudentPerformance perf;

    private static Question mcq(String prompt, String category) {
        return new MultipleChoiceQuestion(prompt, List.of("a", "b"), "a", category, "easy");
    }

    @BeforeEach
    void setUp() {
        bank = new QuestionBank();
        bank.add(mcq("Math Q1", "Math"));
        bank.add(mcq("Math Q2", "Math"));
        bank.add(mcq("Math Q3", "Math"));
        bank.add(mcq("Sci Q1", "Science"));
        bank.add(mcq("Sci Q2", "Science"));
        bank.add(mcq("His Q1", "History"));
        perf = new StudentPerformance("S-001");
    }

    @Test
    void select_studentWithWeakMath_returnsMathQuestions() {
        perf.record(new QuizResult("Q1", "S-001", 2, 10, "Math"));
        QuestionSelectionStrategy strategy = new WeaknessFocusedStrategy(
                perf, new RandomSelectionStrategy(new Random(1)));

        List<Question> picks = strategy.select(bank, 2);

        assertAll(
                () -> assertEquals(2, picks.size()),
                () -> assertTrue(picks.stream().allMatch(q -> q.getCategory().equals("Math")))
        );
    }

    @Test
    void select_noHistoryYet_delegatesToFallback() {
        QuestionSelectionStrategy fallback = new RandomSelectionStrategy(new Random(42));
        QuestionSelectionStrategy strategy = new WeaknessFocusedStrategy(perf, fallback);

        assertEquals(3, strategy.select(bank, 3).size());
    }

    @Test
    void select_strongAcrossAllCategories_delegatesToFallback() {
        perf.record(new QuizResult("Q1", "S-001", 10, 10, "Math"));
        perf.record(new QuizResult("Q2", "S-001", 10, 10, "Science"));
        QuestionSelectionStrategy strategy = new WeaknessFocusedStrategy(
                perf, new RandomSelectionStrategy(new Random(1)));

        List<Question> picks = strategy.select(bank, 2);

        assertEquals(2, picks.size());
    }

    @Test
    void select_weakCategoryHasFewerQuestionsThanCount_topsUpFromBank() {
        // History has only 1 question in the bank, but student is weak in History.
        perf.record(new QuizResult("Q1", "S-001", 1, 10, "History"));
        QuestionSelectionStrategy strategy = new WeaknessFocusedStrategy(
                perf, new RandomSelectionStrategy(new Random(1)));

        List<Question> picks = strategy.select(bank, 3);

        assertAll(
                () -> assertEquals(3, picks.size()),
                () -> assertTrue(picks.stream().anyMatch(q -> q.getCategory().equals("History")))
        );
    }

    @Test
    void select_zeroCount_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> new WeaknessFocusedStrategy(perf).select(bank, 0));
    }

    @Test
    void constructor_nullPerformance_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new WeaknessFocusedStrategy(null));
    }
}
