package com.taylors.csc61204.view;

import com.taylors.csc61204.model.MultipleChoiceQuestion;
import com.taylors.csc61204.model.Question;
import com.taylors.csc61204.model.Quiz;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Renders one question at a time with multiple-choice radio buttons.
 * Collects answers and submits the full list to the controller when finished.
 */
public class QuizScreen extends JPanel {

    private final Quiz quiz;
    private final Consumer<List<String>> onSubmit;
    private final List<String> answers = new ArrayList<>();
    private int currentIndex = 0;

    private final JLabel progressLabel = new JLabel();
    private final JLabel promptLabel = new JLabel();
    private final JPanel optionsPanel = new JPanel();
    private final ButtonGroup optionGroup = new ButtonGroup();
    private final JButton nextButton = new JButton("Next");

    public QuizScreen(Quiz quiz, Consumer<List<String>> onSubmit) {
        this.quiz = quiz;
        this.onSubmit = onSubmit;
        for (int i = 0; i < quiz.size(); i++) {
            answers.add(null);
        }
        buildLayout();
        renderQuestion();
    }

    private void buildLayout() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JPanel top = new JPanel(new BorderLayout());
        progressLabel.setFont(progressLabel.getFont().deriveFont(Font.BOLD, 14f));
        top.add(progressLabel, BorderLayout.WEST);
        top.add(new JLabel(quiz.getTitle(), SwingConstants.RIGHT), BorderLayout.EAST);
        add(top, BorderLayout.NORTH);

        promptLabel.setFont(promptLabel.getFont().deriveFont(Font.PLAIN, 16f));
        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));

        JPanel centre = new JPanel(new BorderLayout(10, 20));
        centre.add(promptLabel, BorderLayout.NORTH);
        centre.add(optionsPanel, BorderLayout.CENTER);
        add(centre, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        nextButton.addActionListener(e -> handleNext());
        bottom.add(nextButton);
        add(bottom, BorderLayout.SOUTH);
    }

    private void renderQuestion() {
        Question q = quiz.getQuestion(currentIndex);
        progressLabel.setText("Question " + (currentIndex + 1) + " of " + quiz.size());
        promptLabel.setText("<html><body style='width: 500px'>" + q.getPrompt() + "</body></html>");

        optionsPanel.removeAll();
        for (AbstractButton b : java.util.Collections.list(optionGroup.getElements())) {
            optionGroup.remove(b);
        }
        // GUI renders MCQ only. TF/ShortAnswer subclasses exist in the model
        // for the IS-A story but aren't exposed by the current selection strategies.
        List<String> opts = ((MultipleChoiceQuestion) q).getOptions();
        for (String opt : opts) {
            JRadioButton btn = new JRadioButton(opt);
            btn.setActionCommand(opt);
            optionGroup.add(btn);
            optionsPanel.add(btn);
        }
        nextButton.setText(isLastQuestion() ? "Submit" : "Next");
        optionsPanel.revalidate();
        optionsPanel.repaint();
    }

    private void handleNext() {
        if (optionGroup.getSelection() == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select an answer before continuing.",
                    "No answer selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        answers.set(currentIndex, optionGroup.getSelection().getActionCommand());
        if (isLastQuestion()) {
            onSubmit.accept(List.copyOf(answers));
        } else {
            currentIndex++;
            renderQuestion();
        }
    }

    private boolean isLastQuestion() {
        return currentIndex == quiz.size() - 1;
    }
}
