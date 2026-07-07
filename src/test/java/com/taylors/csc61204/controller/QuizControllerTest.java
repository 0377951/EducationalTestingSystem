package com.taylors.csc61204.controller;

import com.taylors.csc61204.api.ApiException;
import com.taylors.csc61204.api.TriviaApiClient;
import com.taylors.csc61204.model.MultipleChoiceQuestion;
import com.taylors.csc61204.model.Question;
import com.taylors.csc61204.model.QuestionBank;
import com.taylors.csc61204.model.StudentPerformance;
import com.taylors.csc61204.persistence.DataStore;
import com.taylors.csc61204.persistence.DataStoreException;
import com.taylors.csc61204.service.FeedbackService;
import com.taylors.csc61204.service.QuizService;
import com.taylors.csc61204.view.MainFrame;
import org.junit.jupiter.api.Test;

import java.awt.GraphicsEnvironment;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

class QuizControllerTest {

    @Test
    void controller_constructedWithDependencies_doesNotThrow() {
        // Swing components cannot be instantiated on a headless CI runner; skip there.
        assumeFalse(GraphicsEnvironment.isHeadless(), "Skipped in headless environment");

        QuestionBank bank = new QuestionBank();
        for (int i = 0; i < 5; i++) {
            bank.add(new MultipleChoiceQuestion("Q" + i, List.of("a", "b"), "a", "Math", "easy"));
        }
        TriviaApiClient api = new StubApi();
        StudentPerformance perf = new StudentPerformance("S-001");
        QuizService quizService = new QuizService(bank, api);
        FeedbackService feedback = new FeedbackService(perf);

        MainFrame frame = new MainFrame();
        QuizController controller = new QuizController(
                frame, quizService, feedback, perf, new NoOpStore());

        assertNotNull(controller);
        frame.dispose();
    }

    private static class StubApi extends TriviaApiClient {
        @Override
        public List<Question> fetchQuestions(int amount, String difficulty) throws ApiException {
            return List.of();
        }
    }

    private static class NoOpStore implements DataStore {
        @Override public List<Question> loadQuestions() { return List.of(); }
        @Override public void saveQuestions(List<Question> questions) { /* no-op */ }
        @Override public Optional<StudentPerformance> loadPerformance(String id) { return Optional.empty(); }
        @Override public void savePerformance(StudentPerformance performance) { /* no-op */ }
    }
}
