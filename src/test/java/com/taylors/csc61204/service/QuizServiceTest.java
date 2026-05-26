package com.taylors.csc61204.service;

import com.taylors.csc61204.api.ApiException;
import com.taylors.csc61204.api.TriviaApiClient;
import com.taylors.csc61204.model.Question;
import com.taylors.csc61204.model.QuestionBank;
import com.taylors.csc61204.model.Quiz;
import com.taylors.csc61204.model.QuizResult;
import com.taylors.csc61204.pattern.strategy.RandomSelectionStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class QuizServiceTest {

    private QuestionBank bank;
    private QuizService service;

    @BeforeEach
    void setUp() {
        bank = new QuestionBank();
        for (int i = 0; i < 10; i++) {
            bank.add(new Question("Q" + i, List.of("a", "b"), 0, "Math", "easy"));
        }
        service = new QuizService(bank, new StubApiClient());
    }

    @Test
    void generateQuiz_validParameters_returnsConfiguredQuiz() {
        Quiz quiz = service.generateQuiz("Test", 5,
                new RandomSelectionStrategy(new Random(1)), 300);
        assertAll(
                () -> assertEquals("Test", quiz.getTitle()),
                () -> assertEquals(5, quiz.size()),
                () -> assertEquals(300, quiz.getTimeLimitSeconds())
        );
    }

    @Test
    void submit_allCorrect_returnsFullScoreResult() {
        Quiz quiz = service.generateQuiz("Test", 3,
                new RandomSelectionStrategy(new Random(1)), 300);
        QuizResult result = service.submit(quiz, List.of(0, 0, 0), "S-001");
        assertAll(
                () -> assertEquals(3, result.getCorrectCount()),
                () -> assertEquals(3, result.getTotalCount()),
                () -> assertEquals("S-001", result.getStudentId())
        );
    }

    @Test
    void refreshBankFromApi_apiFails_returnsFalseForGracefulDegradation() {
        QuizService svc = new QuizService(bank, new FailingApiClient());
        assertFalse(svc.refreshBankFromApi(5, "easy"));
    }

    @Test
    void refreshBankFromApi_apiSucceeds_returnsTrueAndAddsQuestions() {
        QuizService svc = new QuizService(new QuestionBank(), new StubApiClient());
        boolean ok = svc.refreshBankFromApi(3, "easy");
        assertAll(
                () -> assertTrue(ok),
                () -> assertEquals(3, svc.getBankSize())
        );
    }

    // --- Stubs ---

    private static class StubApiClient extends TriviaApiClient {
        @Override
        public List<Question> fetchQuestions(int amount, String difficulty) {
            return List.of(
                    new Question("API Q1", List.of("a", "b"), 0, "Math", "easy"),
                    new Question("API Q2", List.of("a", "b"), 0, "Math", "easy"),
                    new Question("API Q3", List.of("a", "b"), 0, "Math", "easy")
            );
        }
    }

    private static class FailingApiClient extends TriviaApiClient {
        @Override
        public List<Question> fetchQuestions(int amount, String difficulty) throws ApiException {
            throw new ApiException("simulated outage", new java.io.IOException());
        }
    }
}
