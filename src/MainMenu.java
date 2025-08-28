import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

class MainMenu extends JFrame {
    public static JFrame mainMenue;
    public MainMenu() {



        mainMenue = this;
        setLocationRelativeTo(null);
        setTitle("Reverse Plague Inc.");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLayout(new GridLayout(3, 1));

        JButton newGameButton = new JButton("New Game");
        JButton highScoresButton = new JButton("High Scores");
        JButton exitButton = new JButton("Exit");

        newGameButton.addActionListener(e -> new Difficulty(this));
        highScoresButton.addActionListener(e -> new HighScores(this));
        exitButton.addActionListener(e -> System.exit(0));

        add(newGameButton);
        add(highScoresButton);
        add(exitButton);
    }
}