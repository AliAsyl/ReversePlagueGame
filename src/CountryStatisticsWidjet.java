import javax.swing.*;
import java.awt.*;

public class CountryStatisticsWidjet extends JPanel {
    private Country monitoringCountry;
    private Thread countryStatisticsThread;
    private JProgressBar healthyBar;
    private JProgressBar infectedBar;
    private JProgressBar curedBar;
    private JProgressBar deadBar;
    private JProgressBar vaccinatedBar;
    private JLabel titleLabel;
    private JLabel healthyLabel;
    private JLabel infectedLabel;
    private JLabel curedLabel;
    private JLabel deadLabel;
    private JLabel vaccinatedLabel;

    public CountryStatisticsWidjet() {
        super(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        titleLabel = new JLabel("Select country to view statistics!", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(titleLabel, BorderLayout.NORTH);

        JPanel statsPanel = new JPanel(new GridLayout(5, 2, 10, 10));

        healthyLabel = new JLabel("Healthy People:", JLabel.RIGHT);
        healthyLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        healthyBar = createCustomProgressBar(Color.GREEN);
        statsPanel.add(healthyLabel);
        statsPanel.add(healthyBar);

        infectedLabel = new JLabel("Infected People:", JLabel.RIGHT);
        infectedLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        infectedBar = createCustomProgressBar(Color.RED);
        statsPanel.add(infectedLabel);
        statsPanel.add(infectedBar);

        vaccinatedLabel = new JLabel("Vaccinated People:", JLabel.RIGHT);
        vaccinatedLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        vaccinatedBar = createCustomProgressBar(Color.CYAN);
        statsPanel.add(vaccinatedLabel);
        statsPanel.add(vaccinatedBar);

        curedLabel = new JLabel("Cured People:", JLabel.RIGHT);
        curedLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        curedBar = createCustomProgressBar(Color.BLUE);
        statsPanel.add(curedLabel);
        statsPanel.add(curedBar);

        deadLabel = new JLabel("Dead People:", JLabel.RIGHT);
        deadLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        deadBar = createCustomProgressBar(Color.BLACK);
        statsPanel.add(deadLabel);
        statsPanel.add(deadBar);

        add(statsPanel, BorderLayout.CENTER);

        countryStatisticsThread = new Thread(() -> {
            while (GameWindow.isGameRunning) {
                if (monitoringCountry != null) {
                    titleLabel.setText("<html>Statistics of <b>" + monitoringCountry.getName() + "</b><br>Population: " + (int)monitoringCountry.getPopulation() + "</html>");

                    healthyLabel.setText("Healthy People: " + (int) Math.round(monitoringCountry.getHealthy()));
                    infectedLabel.setText("Infected People: " + (int) Math.round(monitoringCountry.getInfected()));
                    curedLabel.setText("Cured People: " + (int) Math.round(monitoringCountry.getCured()));
                    vaccinatedLabel.setText("Vaccinated People: " + (int) Math.round(monitoringCountry.getVaccinated()));
                    deadLabel.setText("Dead People: " + (int) Math.round(monitoringCountry.getDead()));

                    healthyBar.setValue((int) Math.round(100.0 * monitoringCountry.getHealthy() / monitoringCountry.getPopulation()));
                    infectedBar.setValue((int) Math.round(100.0 * monitoringCountry.getInfected() / monitoringCountry.getPopulation()));
                    curedBar.setValue((int) Math.round(100.0 * monitoringCountry.getCured() / monitoringCountry.getPopulation()));
                    deadBar.setValue((int) Math.round(100.0 * monitoringCountry.getDead() / monitoringCountry.getPopulation()));
                    vaccinatedBar.setValue((int) Math.round(100.0 * monitoringCountry.getVaccinated() / monitoringCountry.getPopulation()));
                }
                try {
                    Thread.sleep(16);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        countryStatisticsThread.start();
    }

    private JProgressBar createCustomProgressBar(Color color) {
        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        progressBar.setForeground(color);
        progressBar.setPreferredSize(new Dimension(200, 20)); 
        return progressBar;
    }

    public void displayCountryStatistics(Country c) {
        monitoringCountry = c;
    }
}
