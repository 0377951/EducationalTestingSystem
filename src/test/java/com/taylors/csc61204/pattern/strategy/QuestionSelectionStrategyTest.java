package com.taylors.csc61204.pattern.strategy;

import com.taylors.csc61204.model.Question;
import com.taylors.csc61204.model.QuestionBank;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class QuestionSelectionStrategyTest {

    private QuestionBank bank;

    @BeforeEach
    void setUp() {
        bank = new QuestionBank();
        bank.add(new Question("Math Q1", List.of("a", "b"), 0, "Math", "easy"));
        bank.add(new Question("Math Q2", List.of("a", "b"), 1, "Math", "medium"));
        bank.add(new Question("Math Q3", List.of("a", "b"), 0, "Math", "hard"));
        bank.add(new Question("Sci Q1",  List.of("a", "b"), 1, "Science", "easy"));
        bank.add(new Question("Sci Q2",  List.of("a", "b"), 0, "Science", "medium"));
        bank.add(new Question("His Q1",  List.of("a", "b"), 1, "History", "easy"));
    }

    @Test
    void randomStrategy_validCount_returnsExactCount() {
        QuestionSelectionStrategy s = new RandomSelectionStrategy(new Random(42));
        List<Question> result = s.select(bank, 3);
        assertEquals(3, result.size());
    }

    @Test
    void randomStrategy_seededRandom_returnsReproducibleResult() {
        QuestionSelectionStrategy s1 = new RandomSelectionStrategy(new Random(42));
        QuestionSelectionStrategy s2 = new RandomSelectionStrategy(new Random(42));
        assertEquals(s1.select(bank, 3), s2.select(bank, 3));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 6})
    void randomStrategy_variousCounts_returnsExactCount(int count) {
        QuestionSelectionStrategy s = new RandomSelectionStrategy(new Random(1));
        assertEquals(count, s.select(bank, count).size());
    }

    @Test
    void randomStrategy_countExceedsBank_throwsIllegalArgumentException() {
        QuestionSelectionStrategy s = new RandomSelectionStrategy();
        assertThrows(IllegalArgumentException.class, () -> s.select(bank, 100));
    }

    @Test
    void randomStrategy_zeroCount_throwsIllegalArgumentException() {
        QuestionSelectionStrategy s = new RandomSelectionStrategy();
        assertThrows(IllegalArgumentException.class, () -> s.select(bank, 0));
    }

    @Test
    void difficultyStrategy_easyOnly_returnsOnlyEasyQuestions() {
        QuestionSelectionStrategy s = new DifficultyBasedStrategy("easy");
        List<Question> result = s.select(bank, 3);
        assertAll(
                () -> assertEquals(3, result.size()),
                () -> assertTrue(result.stream().allMatch(q -> q.getDifficulty().equals("easy")))
        );
    }

    @Test
    void difficultyStrategy_notEnoughHard_throwsIllegalArgumentException() {
        QuestionSelectionStrategy s = new DifficultyBasedStrategy("hard");
        assertThrows(IllegalArgumentException.class, () -> s.select(bank, 5));
    }

    @ParameterizedTest
    @ValueSource(strings = {"easy", "medium"})
    void difficultyStrategy_validDifficulties_returnsCorrectCount(String difficulty) {
        QuestionSelectionStrategy s = new DifficultyBasedStrategy(difficulty);
        assertEquals(2, s.select(bank, 2).size());
    }

    @Test
    void weakTopicStrategy_singleWeakCategory_drawsFromThatCategory() {
        QuestionSelectionStrategy s = new WeakTopicStrategy(List.of("Math"));
        List<Question> result = s.select(bank, 2);
        assertAll(
                () -> assertEquals(2, result.size()),
                () -> assertTrue(result.stream().allMatch(q -> q.getCategory().equals("Math")))
        );
    }

    @Test
    void weakTopicStrategy_multipleCategoriesInOrder_prioritisesFirst() {
        QuestionSelectionStrategy s = new WeakTopicStrategy(List.of("History", "Science"));
        List<Question> result = s.select(bank, 3);
        assertEquals("History", result.get(0).getCategory());
    }

    @Test
    void weakTopicStrategy_insufficientQuestions_throwsIllegalArgumentException() {
        QuestionSelectionStrategy s = new WeakTopicStrategy(List.of("History"));
        assertThrows(IllegalArgumentException.class, () -> s.select(bank, 5));
    }

    @Test
    void strategiesAreSwappable_sameInterfaceSameBank_runtimePolymorphism() {
        QuestionSelectionStrategy random = new RandomSelectionStrategy(new Random(1));
        QuestionSelectionStrategy difficulty = new DifficultyBasedStrategy("easy");
        QuestionSelectionStrategy weak = new WeakTopicStrategy(List.of("Math"));
        assertAll(
                () -> assertEquals(2, random.select(bank, 2).size()),
                () -> assertEquals(2, difficulty.select(bank, 2).size()),
                () -> assertEquals(2, weak.select(bank, 2).size())
        );
    }
}
