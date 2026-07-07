package com.taylors.csc61204.pattern.builder;

import com.taylors.csc61204.model.MultipleChoiceQuestion;
import com.taylors.csc61204.model.Question;
import com.taylors.csc61204.model.QuestionBank;
import com.taylors.csc61204.model.Quiz;
import com.taylors.csc61204.pattern.strategy.DifficultyBasedStrategy;
import com.taylors.csc61204.pattern.strategy.RandomSelectionStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class QuizBuilderTest {

    private QuestionBank bank;

    @BeforeEach
    void setUp() {
        bank = new QuestionBank();
        for (int i = 0; i < 10; i++) {
            String difficulty = (i < 5) ? "easy" : "hard";
            bank.add(new MultipleChoiceQuestion(
                    "Q" + i, List.of("a", "b"), "a", "Math", difficulty));
        }
    }

    @Test
    void build_withDefaults_returnsValidQuiz() {
        Quiz quiz = new QuizBuilder(bank).build();
        assertAll(
                () -> assertNotNull(quiz),
                () -> assertEquals(10, quiz.size()),
                () -> assertEquals("Untitled Quiz", quiz.getTitle()),
                () -> assertEquals(600, quiz.getTimeLimitSeconds())
        );
    }

    @Test
    void build_withCustomTitle_setsTitle() {
        Quiz quiz = new QuizBuilder(bank).withTitle("Algebra Test").build();
        assertEquals("Algebra Test", quiz.getTitle());
    }

    @Test
    void build_withCustomCount_setsSize() {
        Quiz quiz = new QuizBuilder(bank).withCount(5).build();
        assertEquals(5, quiz.size());
    }

    @Test
    void build_withCustomTimeLimit_setsTimeLimit() {
        Quiz quiz = new QuizBuilder(bank).withTimeLimit(1200).build();
        assertEquals(1200, quiz.getTimeLimitSeconds());
    }

    @Test
    void build_withCustomQuizId_setsId() {
        Quiz quiz = new QuizBuilder(bank).withQuizId("FINAL-2026").build();
        assertEquals("FINAL-2026", quiz.getQuizId());
    }

    @Test
    void build_fullFluentChain_appliesAllSettings() {
        Quiz quiz = new QuizBuilder(bank)
                .withQuizId("Q-100")
                .withTitle("Hard Quiz")
                .withCount(3)
                .withTimeLimit(300)
                .withStrategy(new DifficultyBasedStrategy("hard"))
                .build();
        assertAll(
                () -> assertEquals("Q-100", quiz.getQuizId()),
                () -> assertEquals("Hard Quiz", quiz.getTitle()),
                () -> assertEquals(3, quiz.size()),
                () -> assertEquals(300, quiz.getTimeLimitSeconds()),
                () -> assertTrue(quiz.getQuestions().stream()
                        .allMatch(q -> q.getDifficulty().equals("hard")))
        );
    }

    @Test
    void build_strategyNotEnoughQuestions_throwsIllegalStateException() {
        QuizBuilder builder = new QuizBuilder(bank)
                .withStrategy(new DifficultyBasedStrategy("hard"))
                .withCount(20);
        assertThrows(IllegalStateException.class, builder::build);
    }

    @Test
    void build_countExceedsBank_throwsIllegalStateException() {
        QuizBuilder builder = new QuizBuilder(bank).withCount(100);
        assertThrows(IllegalStateException.class, builder::build);
    }

    @Test
    void constructor_nullBank_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new QuizBuilder(null));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -100})
    void withCount_zeroOrNegative_throwsIllegalArgumentException(int badCount) {
        assertThrows(IllegalArgumentException.class,
                () -> new QuizBuilder(bank).withCount(badCount));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   "})
    void withTitle_blankTitle_throwsIllegalArgumentException(String badTitle) {
        assertThrows(IllegalArgumentException.class,
                () -> new QuizBuilder(bank).withTitle(badTitle));
    }

    @Test
    void withStrategy_nullStrategy_throwsNullPointerException() {
        assertThrows(NullPointerException.class,
                () -> new QuizBuilder(bank).withStrategy(null));
    }

    @Test
    void withTimeLimit_zeroOrNegative_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> new QuizBuilder(bank).withTimeLimit(0));
    }

    @Test
    void fluentApi_eachWithMethod_returnsSameBuilderInstance() {
        QuizBuilder builder = new QuizBuilder(bank);
        assertAll(
                () -> assertSame(builder, builder.withCount(5)),
                () -> assertSame(builder, builder.withTitle("X")),
                () -> assertSame(builder, builder.withTimeLimit(100)),
                () -> assertSame(builder, builder.withQuizId("ID-1")),
                () -> assertSame(builder, builder.withStrategy(new RandomSelectionStrategy()))
        );
    }
}
