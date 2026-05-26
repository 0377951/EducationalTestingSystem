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
        mathQ = new Question("2 + 2 = ?", List.of("3", "4", "5"), 1, "Math", "easy");
        scienceQ = new Question("Water boils at?", List.of("90C", "100C", "110C"), 1, "Science", "easy");
        historyQ = new Question("WWII ended in?", List.of("1943", "1945", "1947"), 1, "History", "medium");
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
        int score = quiz.grade(List.of(1, 1, 1));
        assertEquals(3, score);
    }

    @Test
    void grade_allWrongAnswers_returnsZero() {
        int score = quiz.grade(List.of(0, 0, 0));
        assertEquals(0, score);
    }

    @ParameterizedTest
    @CsvSource({
            "1, 1, 1, 3",
            "1, 1, 0, 2",
            "1, 0, 0, 1",
            "0, 0, 0, 0",
            "0, 1, 0, 1"
    })
    void grade_variousAnswerSets_returnsExpectedScore(int a, int b, int c, int expected) {
        int score = quiz.grade(List.of(a, b, c));
        assertEquals(expected, score);
    }

    @Test
    void grade_wrongNumberOfAnswers_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> quiz.grade(List.of(1, 1)));
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
