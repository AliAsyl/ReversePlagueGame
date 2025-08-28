import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

class GameWindow extends JFrame {

    private String difficulty;
    static public int money = 0;
    static public int points = 0;
    public static boolean isGameRunning;
    static public DraggableMapPanel mapPanel;
    private CountryStatisticsWidjet countryStatisticsWidjet;
    private GameProgressSaveMenue gameProgressSaveMenue;
    public static VaccineTopUpGUI vaccineTopUpGUI;
    public static GameScoreMenue gameScoreMenue;
    private Thread gameThread;


    public GameWindow(String difficulty) throws IOException {
        isGameRunning = true;
        this.difficulty = difficulty;

        setTitle("Reverse Plague Game");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        int gameSpeed = 0;
        switch (difficulty) {
            case "Easy":
                Virus.virusSpreadRate = 1000;
                Virus.baseSpreadRate = 0.05;
                Virus.baseCureRate = 0.0005; 
                Virus.baseImmunity = 0.00005; 
                Virus.coverageOfVaccine = 0.0000001; 
                Virus.effectivenessOfVaccine = 0.7; 
                Virus.deadRate = 0.00005; 
                Transport.appearanceChance = 0.999;
                Transport.withVaccineChance = 0.5;
                MapCoin.coinLifeTime = 5000;
                MapCoin.spontaniousSpawnRate = 0.9999;
                MapCoin.requiredCuredAmountToSpawn = 100000;
                break;
            case "Medium":
                Virus.virusSpreadRate = 500;
                Virus.baseSpreadRate = 0.08;
                Virus.baseCureRate = 0.00025;
                Virus.baseImmunity = 0.000025;
                Virus.coverageOfVaccine = 0.00000005;
                Virus.effectivenessOfVaccine = 0.5; 
                Virus.deadRate = 0.0001; 
                Transport.appearanceChance = 0.9999;
                Transport.withVaccineChance = 0.9;
                MapCoin.coinLifeTime = 2000;
                MapCoin.spontaniousSpawnRate = 0.99999;
                MapCoin.requiredCuredAmountToSpawn = 250000;
                break;
            case "Hard":
                Virus.virusSpreadRate = 250;
                Virus.baseSpreadRate = 0.1; 
                Virus.baseCureRate = 0.001; 
                Virus.baseImmunity = 0.000001;
                Virus.coverageOfVaccine = 0.00000001; 
                Virus.effectivenessOfVaccine = 0.2; 
                Virus.deadRate = 0.0001; 
                Transport.appearanceChance = 0.99;
                Transport.withVaccineChance = 0.999;
                MapCoin.coinLifeTime = 1000;
                MapCoin.spontaniousSpawnRate = 0.9999;
                MapCoin.requiredCuredAmountToSpawn = 10000000;
                break;
        }

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());

        gameScoreMenue = new GameScoreMenue();
        gameScoreMenue.updateDifficulty(difficulty);
        topPanel.add(gameScoreMenue, BorderLayout.WEST);

        vaccineTopUpGUI = new VaccineTopUpGUI();
        add(vaccineTopUpGUI, BorderLayout.WEST);
        new Virus();

        countryStatisticsWidjet = new CountryStatisticsWidjet();
        topPanel.add(countryStatisticsWidjet, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        GameWindow.mapPanel = new DraggableMapPanel(ReversePlagueGame.imagePath + "map1.jpg") {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Country.drawCountries(g, this);
                Country.drawHubej(g, this);
                Transport.drawTransports(g, this);
                MapCoin.drawMapCoins(g, this);

            }
            @Override
            public void mapCoinClicked(MapCoin mapCoin){
                GameWindow.money += mapCoin.getBalance();
                VaccineTopUp.updateButtons();
            }
            @Override
            public void mapCountryClicked(Country country){
                countryStatisticsWidjet.displayCountryStatistics(country);
            }

        };


        InputMap inputMap = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getRootPane().getActionMap();

        inputMap.put(KeyStroke.getKeyStroke("shift ctrl Q"), "closeWindow");
        actionMap.put("closeWindow", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isGameRunning = false;
                MainMenu.mainMenue.setVisible(true);
                setVisible(false);
            }
        });

        initializeCountries();
        JScrollPane scrollPanel = new JScrollPane(GameWindow.mapPanel);
        add(scrollPanel);

        add(GameWindow.mapPanel, BorderLayout.CENTER);

        JButton stopButton = new JButton("Stop Game");
        stopButton.addActionListener(e -> {
            saveGame();
        });
        add(stopButton, BorderLayout.SOUTH);

        startGameLoop();
    }
    private void initializeCountries() {
        
        new Country("China", new Color(163, 73, 164), new CountryRules() {
            @Override
            public boolean needToCloseBorders(Country c) {
                return c.getDead() > 10000;
            }
        }, 1400000000, "Eurasia", 784, 307, 801, 282);

        new Country("USA",  new Color(0, 162, 232), new CountryRules() {
            @Override
            public boolean needToCloseBorders(Country c) {
                return c.getInfected() > 1000000;
            }
        },331000000, "North America", 183,268, 240, 301);

        new Country("India", new Color(142, 64, 58),new CountryRules() {
            @Override
            public boolean needToCloseBorders(Country c) {
                return c.getDead() > c.getPopulation()/2;
            }
        }, 1380000000, "Eurasia", 688, 327, 714,337);

        new Country("Russia",new Color(255, 0, 0),new CountryRules() {
            @Override
            public boolean needToCloseBorders(Country c) {
                return c.getVaccinated() < c.getInfected() * 2;
            }
        },  146000000, "Eurasia", 575, 199, 525,196);

        new Country("Brazil", new Color(247, 209, 72),new CountryRules() {
            @Override
            public boolean needToCloseBorders(Country c) {
                return c.getInfected() > c.getPopulation()/2;
            }
        }, 213000000, "South America", 308, 420, 356,437);

        new Country("Ukraine", new Color(255, 242, 0),new CountryRules() {
            @Override
            public boolean needToCloseBorders(Country c) {
                return false;
            }
        }, 300000000, "Eurasia", 544, 238);

        new Country("Poland", new Color(34, 177, 76),new CountryRules() {
            @Override
            public boolean needToCloseBorders(Country c) {
                return c.getDead() * 2 > c.getInfected();
            }
        },370000000, "Eurasia", 509, 231, 509, 216);

        new Country("Germany", new Color(255, 94, 252),new CountryRules() {
            @Override
            public boolean needToCloseBorders(Country c) {
                return c.getVaccinated() + c.getInfected() < 2 * c.getDead();
            }
        },850000000, "Eurasia", 486, 233,487,219);

        new Country("Qazaqstan",new Color(0, 253, 189),new CountryRules() {
            @Override
            public boolean needToCloseBorders(Country c) {
                return c.getDead() > c.getVaccinated();
            }
        }, 200000000, "Eurasia", 644, 237);

        new Country("Niger", new Color(255, 174, 201),new CountryRules() {
            @Override
            public boolean needToCloseBorders(Country c) {
                return c.getDead() > c.getVaccinated() * 2;
            }
        }, 280000000, "Africa", 489, 343, 467, 381);

        Country.getCountryByName("China").setInfected(40);

    }

    private void startGameLoop() {

        gameScoreMenue.startCountingTime();
        mapPanel.startDragMapLoop();
        Virus.startVirusThread();

        setVisible(true);
        gameThread = new Thread(() ->{
            while(GameWindow.isGameRunning){
                if(Country.getTotalInfected() == 0){
                    saveGame();
                    JOptionPane.showMessageDialog(this, "Congratulations! You won the game!"); 
                }
                if(Country.getTotalPopulation() == 0){
                    saveGame();
                    JOptionPane.showMessageDialog(this, "Oh no, You lost tha game"); 
                }
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        gameThread.start();
    }

    private void saveGame() {
        processStop(); 
          
        gameProgressSaveMenue = new GameProgressSaveMenue(){
            @Override
            public void onPlayerNameEntered(String name){
                
                try {
                    File file = new File("highscores.txt");
                    if (!file.exists()) {
                        file.createNewFile();
                    }

                    try (FileWriter writer = new FileWriter(file, true)) {
                        writer.write(name + "\t" + GameWindow.points + "\n"); 
                        gameProgressSaveMenue.dispose();
                    }
                } catch (IOException e) {
                    e.printStackTrace();

                }finally{
                    gameProgressSaveMenue.dispose();
                }


                
            }
        };
        //
        //

//

    }
    public void processStop(){
        isGameRunning = false;
        MainMenu.mainMenue.setVisible(true);
        setVisible(false);
        dispose();
    }
}


