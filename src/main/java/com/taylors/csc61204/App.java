package com.taylors.csc61204;

import com.taylors.csc61204.api.TriviaApiClient;
import com.taylors.csc61204.controller.QuizController;
import com.taylors.csc61204.model.Question;
import com.taylors.csc61204.model.QuestionBank;
import com.taylors.csc61204.model.StudentPerformance;
import com.taylors.csc61204.persistence.DataStore;
import com.taylors.csc61204.persistence.DataStoreException;
import com.taylors.csc61204.persistence.JsonDataStore;
import com.taylors.csc61204.service.FeedbackService;
import com.taylors.csc61204.service.QuizService;
import com.taylors.csc61204.view.MainFrame;

import javax.swing.SwingUtilities;
import java.nio.file.Path;
import java.util.List;

/**
 * Entry point. Performs dependency injection by hand — no framework needed.
 * Wires Model, Persistence, API, Services, and View+Controller together, then
 * launches on the Swing event-dispatch thread.
 */
public class App {

    private static final String DEFAULT_STUDENT_ID = "S-001";
    private static final Path DATA_DIR = Path.of("data");

    public static void main(String[] args) {
        SwingUtilities.invokeLater(App::launch);
    }

    private static void launch() {
        DataStore store = new JsonDataStore(DATA_DIR);

        QuestionBank bank = loadBank(store);
        StudentPerformance performance = loadPerformance(store, DEFAULT_STUDENT_ID);

        TriviaApiClient apiClient = new TriviaApiClient();
        QuizService quizService = new QuizService(bank, apiClient);
        FeedbackService feedbackService = new FeedbackService(performance);

        MainFrame frame = new MainFrame();
        QuizController controller = new QuizController(
                frame, quizService, feedbackService, performance, store);
        controller.start();
    }

    /** Load seed questions; fall back to an empty bank if the file is missing/malformed. */
    private static QuestionBank loadBank(DataStore store) {
        try {
            List<Question> seed = store.loadQuestions();
            return new QuestionBank(seed);
        } catch (DataStoreException e) {
            System.err.println("Warning: could not load seed questions — " + e.getMessage());
            return new QuestionBank();
        }
    }

    /** Load prior performance or start a fresh record if none exists. */
    private static StudentPerformance loadPerformance(DataStore store, String studentId) {
        try {
            return store.loadPerformance(studentId)
                    .orElseGet(() -> new StudentPerformance(studentId));
        } catch (DataStoreException e) {
            System.err.println("Warning: could not load performance — " + e.getMessage());
            return new StudentPerformance(studentId);
        }
    }
}
