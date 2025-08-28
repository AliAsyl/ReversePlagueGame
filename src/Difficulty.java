import javax.swing.*;
import java.awt.*;
import java.io.IOException;

class Difficulty extends JDialog {
    public Difficulty(JFrame parent) {
        super(parent, "Select Difficulty", true);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(4, 1));
        setSize(300, 200);

        JButton easyButton = new JButton("Easy");
        JButton mediumButton = new JButton("Medium");
        JButton hardButton = new JButton("Hard");

        easyButton.addActionListener(e -> startGame("Easy"));
        mediumButton.addActionListener(e -> startGame("Medium"));
        hardButton.addActionListener(e -> startGame("Hard"));

        add(new JLabel("Select Difficulty:", SwingConstants.CENTER));
        add(easyButton);
        add(mediumButton);
        add(hardButton);

        setLocationRelativeTo(parent);
        setVisible(true);
    }

    private void startGame(String difficulty) {
        SwingUtilities.invokeLater(() -> {
            try {
                new GameWindow(difficulty);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        dispose();
    }
}

