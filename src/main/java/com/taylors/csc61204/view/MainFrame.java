package com.taylors.csc61204.view;

import javax.swing.*;
import java.awt.*;

/**
 * Top-level window. Hosts a CardLayout that swaps between the three screens.
 * Pure View — knows nothing about services or business logic.
 */
public class MainFrame extends JFrame {

    private static final String CARD_START = "start";
    private static final String CARD_QUIZ = "quiz";
    private static final String CARD_RESULTS = "results";

    private final CardLayout cards = new CardLayout();
    private final JPanel root = new JPanel(cards);

    public MainFrame() {
        super("Educational Testing System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(700, 500);
        setLocationRelativeTo(null);
        add(root);
    }

    public void showStart(StartScreen screen) {
        replaceCard(CARD_START, screen);
    }

    public void showQuiz(QuizScreen screen) {
        replaceCard(CARD_QUIZ, screen);
    }

    public void showResults(ResultsScreen screen) {
        replaceCard(CARD_RESULTS, screen);
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    private void replaceCard(String name, JPanel screen) {
        for (Component c : root.getComponents()) {
            if (name.equals(c.getName())) {
                root.remove(c);
                break;
            }
        }
        screen.setName(name);
        root.add(screen, name);
        cards.show(root, name);
    }
}
