package com.taylors.csc61204.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class QuizTest {

    private Question mathQ;
    private Question scienceQ;
    private Question historyQ;
    private Quiz quiz;

    @BeforeEach
    void setUp() {
        // Mixed subclasses to prove Quiz grades polymorphically.
        mathQ = new MultipleChoiceQuestion(
                "2 + 2 = ?", List.of("3", "4", "5"), "4", "Math", "easy");
        scienceQ = new TrueFalseQuestion(
                "Water boils at 100°C at sea level.", true, "Science", "easy");
        historyQ = new ShortAnswerQuestion(
                "Which year did WWII end?", "1945", "History", "medium");
        quiz = new Quiz("Q-001", "Sample Quiz", List.of(mathQ, scienceQ, historyQ), 600);
    }

    @Test
    void getQuestion_validIndex_returnsExpectedQuestion() {
        assertEquals(scienceQ, quiz.getQuestion(1));
    }

    @Test
    void size_threeQuestions_returnsThree() {
        assertEquals(3, quiz.size());
    }

    @Test
    void grade_allCorrectAnswers_returnsFullScore() {
        int score = quiz.grade(List.of("4", "true", "1945"));
        assertEquals(3, score);
    }

    @Test
    void grade_allWrongAnswers_returnsZero() {
        int score = quiz.grade(List.of("3", "false", "1943"));
        assertEquals(0, score);
    }

    @ParameterizedTest
    @CsvSource({
            "4,    true,  1945, 3",
            "4,    true,  1943, 2",
            "4,    false, 1943, 1",
            "3,    false, 1943, 0",
            "3,    true,  1943, 1"
    })
    void grade_variousAnswerSets_returnsExpectedScore(
            String a, String b, String c, int expected) {
        assertEquals(expected, quiz.grade(List.of(a, b, c)));
    }

    @Test
    void grade_wrongNumberOfAnswers_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> quiz.grade(List.of("4", "true")));
    }

    @Test
    void grade_nullAnswers_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> quiz.grade(null));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   "})
    void constructor_blankQuizId_throwsIllegalArgumentException(String badId) {
        assertThrows(IllegalArgumentException.class,
                () -> new Quiz(badId, "Title", List.of(mathQ), 60));
    }

    @Test
    void constructor_blankTitle_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> new Quiz("Q-1", "", List.of(mathQ), 60));
    }

    @Test
    void constructor_emptyQuestionList_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> new Quiz("Q-1", "Title", List.of(), 60));
    }

    @Test
    void constructor_zeroOrNegativeTimeLimit_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> new Quiz("Q-1", "Title", List.of(mathQ), 0));
    }

    @Test
    void getters_constructedQuiz_returnAllFieldsCorrectly() {
        assertAll(
                () -> assertEquals("Q-001", quiz.getQuizId()),
                () -> assertEquals("Sample Quiz", quiz.getTitle()),
                () -> assertEquals(3, quiz.getQuestions().size()),
                () -> assertEquals(600, quiz.getTimeLimitSeconds())
        );
    }

    @Test
    void getQuestions_attemptToModify_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class,
                () -> quiz.getQuestions().add(mathQ));
    }
}
