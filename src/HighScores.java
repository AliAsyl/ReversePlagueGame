import javax.swing.*;
import java.awt.*;
import java.io.*;

class HighScores extends JDialog {
    public HighScores(JFrame parent) {
        super(parent, "High Scores", true);
        setSize(400, 300);

        DefaultListModel<String> scoresModel = new DefaultListModel<>();

        loadScores(scoresModel);

        JList<String> scoresList = new JList<>(scoresModel);
        scoresList.setFont(new Font("Arial", Font.PLAIN, 14)); 
        scoresList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        scoresList.setVisibleRowCount(-1); 

        JScrollPane scrollPane = new JScrollPane(scoresList);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        add(mainPanel);

        setLocationRelativeTo(parent); 
        setVisible(true);
    }

    private void loadScores(DefaultListModel<String> scoresModel) {
        File file = new File("highscores.txt");
        try {
            if (!file.exists()) {
                file.createNewFile(); 
            }
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split("\t");
                    if (parts.length == 2) {
                        scoresModel.addElement(parts[0] + " : " + parts[1]);
                    }
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error handling high scores file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


}
