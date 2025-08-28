import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameProgressSaveMenue extends JFrame {
    private JTextField nameField;
    private JButton saveButton;
    public GameProgressSaveMenue(){
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 200);
        setLayout(new GridLayout(3, 2, 10, 10));

        nameField = new JTextField();
        saveButton = new JButton("Save Score");

        add(new JLabel("Please save your score!"));
        add(new JLabel());
        add(new JLabel("Your name:"));
        add(nameField);
        add(saveButton, BorderLayout.CENTER);

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processSaveClick();
            }
        });

        setLocationRelativeTo(null); 
        setVisible(true);
    }
    public void processSaveClick(){
        String playerName = nameField.getText();

        if (playerName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter not empty name!", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            onPlayerNameEntered(playerName);
            JOptionPane.showMessageDialog(this, "Score saved successfully for " + playerName);
        }
    }

    public void onPlayerNameEntered(String name){}
}
