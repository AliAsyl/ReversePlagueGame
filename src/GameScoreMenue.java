import javax.swing.*;
import java.awt.*;

public class GameScoreMenue extends JPanel{
    private JLabel pointsLabel;
    private JLabel difficultyLabel;
    private JLabel timeLabel;
    private JLabel moneyLabel;
    private int hours;
    private int minutes;
    private int seconds;
    private Thread gameTimerThread;
    private boolean counting;
    public GameScoreMenue() {
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(Color.DARK_GRAY);
        JLabel titleLabel = new JLabel("Game Scoreboard");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        JPanel descriptionPanel = new JPanel();
        descriptionPanel.setLayout(new GridLayout( 4, 2, 10, 10));
        descriptionPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel pointsTextLabel = new JLabel("Points:");
        pointsTextLabel.setFont(new Font("Arial", Font.BOLD, 16));
        pointsLabel = new JLabel("0");
        pointsLabel.setFont(new Font("Arial", Font.PLAIN, 16));

        JLabel moneyTextLabel = new JLabel("Money:");
        moneyTextLabel.setFont(new Font("Arial", Font.BOLD, 16));
        moneyLabel = new JLabel("0");
        moneyLabel.setFont(new Font("Arial", Font.PLAIN, 16));

        JLabel difficultyTextLabel = new JLabel("Difficulty:");
        difficultyTextLabel.setFont(new Font("Arial", Font.BOLD, 16));
        difficultyLabel = new JLabel("Easy");
        difficultyLabel.setFont(new Font("Arial", Font.PLAIN, 16));

        JLabel timeTextLabel = new JLabel("Time Elapsed:");
        timeTextLabel.setFont(new Font("Arial", Font.BOLD, 16));
        timeLabel = new JLabel("00:00");
        timeLabel.setFont(new Font("Arial", Font.PLAIN, 16));

        descriptionPanel.add(pointsTextLabel);
        descriptionPanel.add(pointsLabel);
        descriptionPanel.add(moneyTextLabel);
        descriptionPanel.add(moneyLabel);
        descriptionPanel.add(difficultyTextLabel);
        descriptionPanel.add(difficultyLabel);
        descriptionPanel.add(timeTextLabel);
        descriptionPanel.add(timeLabel);
        add(descriptionPanel, BorderLayout.SOUTH);
        resetCounter();

    }

    public void updatePoints(int points) {
        pointsLabel.setText(String.valueOf(points));
    }
    public void updateMoney(int money) {
        moneyLabel.setText(String.valueOf(money));
    }

    public void updateDifficulty(String difficulty) {
        difficultyLabel.setText(difficulty);
    }

    public void updateTime(String time) {
        timeLabel.setText(time);
    }

    public void startCountingTime(){
        counting = true;
        gameTimerThread = new Thread(() -> {
            boolean flipper = false;
            while(counting && GameWindow.isGameRunning){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                seconds ++;
                if(seconds >= 60){
                    seconds = 0;
                    minutes ++;
                }
                if(minutes >= 60){
                    minutes = 0;
                    hours ++;
                }
                updateMoney(GameWindow.money);
                updatePoints(GameWindow.points);
                if(flipper){
                    updateTime(hours + "h - " + minutes + "m : "+seconds+"s");
                }else{
                    updateTime(hours + "h - " + minutes + "m   "+seconds+"s");
                }
                flipper = !flipper;
            }
        });
        gameTimerThread.start();
    }
    public void stopCounting(){
        counting = false;
    }
    public void resetCounter(){
        hours = 0;
        minutes = 0;
        seconds = 0;
    }

}




