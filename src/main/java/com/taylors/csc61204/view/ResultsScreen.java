package com.taylors.csc61204.view;

import com.taylors.csc61204.model.QuizResult;

import javax.swing.*;
import java.awt.*;

/**
 * Displays the score, performance tier, and personalised feedback after a quiz.
 */
public class ResultsScreen extends JPanel {

    public ResultsScreen(QuizResult result, String summary, String progressReport,
                         Runnable onRetry) {
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));

        JLabel title = new JLabel("Quiz Complete", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 24f));
        add(title, BorderLayout.NORTH);

        JPanel centre = new JPanel(new GridLayout(3, 1, 0, 15));
        centre.add(scoreLabel(result));
        centre.add(wrapped(summary));
        centre.add(wrapped(progressReport));
        add(centre, BorderLayout.CENTER);

        JButton retryBtn = new JButton("Take Another Quiz");
        retryBtn.addActionListener(e -> onRetry.run());
        JPanel south = new JPanel(new FlowLayout(FlowLayout.CENTER));
        south.add(retryBtn);
        add(south, BorderLayout.SOUTH);
    }

    private JLabel scoreLabel(QuizResult result) {
        String text = String.format("Score: %d / %d  (%.0f%%)",
                result.getCorrectCount(), result.getTotalCount(), result.getScorePercent());
        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setFont(lbl.getFont().deriveFont(Font.BOLD, 20f));
        lbl.setForeground(result.isPassing() ? new Color(0, 130, 0) : new Color(180, 0, 0));
        return lbl;
    }

    private JLabel wrapped(String text) {
        return new JLabel("<html><body style='width: 400px; text-align: center'>"
                + text + "</body></html>", SwingConstants.CENTER);
    }
}
