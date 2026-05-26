package com.taylors.csc61204.view;

import javax.swing.*;
import java.awt.*;
import java.util.function.BiConsumer;

/**
 * Landing screen: lets the user choose difficulty and question count, then start.
 * Pure View — collects input and notifies the controller via a callback.
 */
public class StartScreen extends JPanel {

    private static final String[] DIFFICULTIES = {"easy", "medium", "hard"};
    private static final Integer[] COUNTS = {5, 10, 15, 20};

    private final JComboBox<String> difficultyBox = new JComboBox<>(DIFFICULTIES);
    private final JComboBox<Integer> countBox = new JComboBox<>(COUNTS);
    private final JLabel statusLabel = new JLabel(" ");

    public StartScreen(BiConsumer<String, Integer> onStartClicked) {
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildForm(onStartClicked), BorderLayout.CENTER);
    }

    private JPanel buildHeader() {
        JLabel title = new JLabel("Educational Testing System", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 24f));
        JLabel subtitle = new JLabel("SDG 4 — Quality Education", SwingConstants.CENTER);
        subtitle.setFont(subtitle.getFont().deriveFont(Font.PLAIN, 14f));

        JPanel header = new JPanel(new GridLayout(2, 1, 0, 6));
        header.add(title);
        header.add(subtitle);
        return header;
    }

    private JPanel buildForm(BiConsumer<String, Integer> onStartClicked) {
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 8, 8, 8);
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx = 0; c.gridy = 0; form.add(new JLabel("Difficulty:"), c);
        c.gridx = 1; form.add(difficultyBox, c);

        c.gridx = 0; c.gridy = 1; form.add(new JLabel("Question count:"), c);
        c.gridx = 1; form.add(countBox, c);

        JButton startBtn = new JButton("Start Quiz");
        startBtn.addActionListener(e -> handleStart(onStartClicked));
        c.gridx = 0; c.gridy = 2; c.gridwidth = 2; form.add(startBtn, c);

        c.gridy = 3;
        statusLabel.setForeground(Color.RED);
        form.add(statusLabel, c);
        return form;
    }

    private void handleStart(BiConsumer<String, Integer> onStartClicked) {
        String difficulty = (String) difficultyBox.getSelectedItem();
        Integer count = (Integer) countBox.getSelectedItem();
        if (difficulty == null || count == null || count <= 0) {
            statusLabel.setText("Please choose a difficulty and question count.");
            return;
        }
        statusLabel.setText(" ");
        onStartClicked.accept(difficulty, count);
    }

    public void showError(String message) {
        statusLabel.setText(message);
    }
}
