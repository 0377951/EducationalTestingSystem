package com.taylors.csc61204;

import com.taylors.csc61204.api.TriviaApiClient;
import com.taylors.csc61204.controller.QuizController;
import com.taylors.csc61204.model.QuestionBank;
import com.taylors.csc61204.model.StudentPerformance;
import com.taylors.csc61204.service.FeedbackService;
import com.taylors.csc61204.service.QuizService;
import com.taylors.csc61204.view.MainFrame;

import javax.swing.SwingUtilities;

/**
 * Entry point. Performs dependency injection by hand — no framework needed.
 * Wires Model (bank, performance), API (TriviaApiClient), Services, and
 * View+Controller together, then launches on the Swing event-dispatch thread.
 */
public class App {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(App::launch);
    }

    private static void launch() {
        QuestionBank bank = new QuestionBank();
        TriviaApiClient apiClient = new TriviaApiClient();
        QuizService quizService = new QuizService(bank, apiClient);

        StudentPerformance performance = new StudentPerformance("S-001");
        FeedbackService feedbackService = new FeedbackService(performance);

        MainFrame frame = new MainFrame();
        QuizController controller = new QuizController(frame, quizService, feedbackService);
        controller.start();
    }
}
