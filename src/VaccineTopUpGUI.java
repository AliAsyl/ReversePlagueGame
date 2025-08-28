import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

public class VaccineTopUpGUI extends JPanel{

    private static HashMap<String, Integer> vaccineProperties = new HashMap<>();
    private JPanel contentPanel;

    public VaccineTopUpGUI() {
        setLayout(new BorderLayout());
        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setPreferredSize(new Dimension(250, 600));

        add(scrollPane, BorderLayout.CENTER);
    }

    public void addTopUp(VaccineTopUp tp){
        contentPanel.add(tp.createVaccineTopUpSection());
    }

}

class VaccineTopUp{
    public static ArrayList<VaccineTopUp> vaccineTopUps = new ArrayList<>();
    private int price;
    private String title;
    private String description;
    private JButton buyButton;
    private JLabel priceLabel;
    private float value;
    public VaccineTopUp(String title, String description, int initialPrice){
        this.title = title;
        this.description = description;
        this.price = initialPrice;
        this.value = 0;
        vaccineTopUps.add(this);
    }
    public JPanel createVaccineTopUpSection() {
        JPanel topUpPanel = new JPanel();
        topUpPanel.setLayout(new BorderLayout());
        topUpPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        topUpPanel.setPreferredSize(new Dimension(200, 120));
        topUpPanel.setMaximumSize(new Dimension(200, 120));

        JLabel nameLabel = new JLabel(title, SwingConstants.CENTER);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        topUpPanel.add(nameLabel, BorderLayout.NORTH);

        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        topUpPanel.add(progressBar, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());

        buyButton = new JButton("Buy More");
        priceLabel = new JLabel("Price: " + price);
        buyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                value += 0.1;

                if (value >= 1.0) {
                    buyButton.setEnabled(false);
                    JOptionPane.showMessageDialog(topUpPanel, title + " is fully upgraded!");
                    progressBar.setValue( 100 );
                    priceLabel.setText("Full");

                } else {
                    progressBar.setValue( (int)(100 * value) );

                    GameWindow.money -= price;

                    price = (int)(1.1 * (float)price);
                    priceLabel.setText("Price: " + price);
                    GameWindow.gameScoreMenue.updateMoney(GameWindow.money);
                    VaccineTopUp.updateButtons();
                }

            }
        });
        buttonPanel.add(priceLabel);
        buttonPanel.add(buyButton);

        JButton infoButton = new JButton("i");
        infoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ImageIcon icon = new ImageIcon(ReversePlagueGame.imagePath + "info.png"); 
                JOptionPane.showMessageDialog(topUpPanel, description, "Info - " + title, JOptionPane.INFORMATION_MESSAGE, icon);
            }
        });
        buttonPanel.add(infoButton);

        topUpPanel.add(buttonPanel, BorderLayout.SOUTH);

        return topUpPanel;
    }
    static public void updateButtons(){
        for(VaccineTopUp vtu: vaccineTopUps){
            vtu.buyButton.setEnabled(vtu.getValue() < 1.0 && GameWindow.money >= vtu.price);
        }
    }

    public float getValue() {return value;}
}