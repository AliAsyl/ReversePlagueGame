import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.IOException;
import java.io.File;
import javax.imageio.ImageIO;

public class Country {

    private HashMap<String, ArrayList<Node>> roadsToCountries = new HashMap<>();
    private HashMap<String, ArrayList<Node>> shipsToCountries = new HashMap<>();
    private String name;
    private double population;
    private double infected;
    private double vaccinated;
    private double cured;
    private double dead;
    private CountryRules countryRules;
    private String motherland;
    private int x,y;
    private int portX, portY;
    private boolean withPort;
    static public ArrayList<Country> allCountries = new ArrayList<>();
    private Thread countryThread;
    private boolean borderClosed;

    private BufferedImage closedBordersImage;
    private Color color;
    private int amountOfPeopleTimesPassed = 40;
    private int amountOfPeopleInfected = 0;
    private int amountOfPeopleCured = 0;

    public Country(String name, Color color, CountryRules countryRules, int population, String motherland, int x, int y, int portX, int portY){
        this.name = name;
        this.color = color;
        this.countryRules = countryRules;
        this.x = x;
        this.y = y;
        this.portX = portX;
        this.portY = portY;
        this.population = population;
        this.infected = 0;
        this.cured = 0;
        this.motherland = motherland;
        this.withPort = true;
        this.borderClosed = false;
        try {
            closedBordersImage = ImageIO.read(new File(ReversePlagueGame.imagePath + "no-car.png"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        Country.allCountries.add(this);
        startCountryThread();
    }
    public Country(String name, Color color, CountryRules countryRules, int population, String motherland, int x, int y) {
        this.name = name;
        this.color = color;
        this.countryRules = countryRules;
        this.x = x;
        this.y = y;
        this.population = population;
        this.infected = 0;
        this.cured = 0;
        this.motherland = motherland;
        this.withPort = false;
        this.borderClosed = false;
        try {
            closedBordersImage = ImageIO.read(new File(ReversePlagueGame.imagePath + "no-car.png"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        Country.allCountries.add(this);
        startCountryThread();
    }



    public void startCountryThread(){
        countryThread = new Thread(() -> {
            try {
                while (GameWindow.isGameRunning) {
                    if(Math.random() > Transport.appearanceChance){
                        Country c = Country.getRandomCountry();
                        if(c.getName() != name && !isBorderClosed() && !c.isBorderClosed()){
                            travelTo(c);
                        }
                    }
                    if(Math.random() > MapCoin.spontaniousSpawnRate ){
                        new MapCoin(this);
                    }

                    Thread.sleep(20);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        countryThread.start();
    }


    public void travelTo(Country country){
            if(country.isWithPort() && this.isWithPort()){
                switch ((int) Math.round(Math.random() * 2)){
                    case 0:
                        new Car(this, country);
                        break;
                    case 1:
                        new Airplane(this, country);
                        break;
                    case 2:
                        new Ship(this, country);
                        break;

            }
            }else{
                switch ((int) Math.round(Math.random())){
                    case 0:
                        new Car(this, country);
                        break;
                    case 1:
                        new Airplane(this, country);
                        break;
                }
            }
        }

    public String getName() {
        return name;
    }
    public void setPopulation(double population) {this.population = population;}
    public double getPopulation() {return population;}

    public double getInfected() {return infected;}
    public void setInfected(double infected){this.infected = infected;}

    public double getCured() {return cured;}
    public void setCured(double cured) {this.cured = cured;}

    public double getVaccinated() {return vaccinated;}
    public void setVaccinated(double vaccinated) {this.vaccinated = vaccinated;}

    public double getDead() {return dead;}
    public void setDead(double dead) {this.dead = dead;}

    public double getHealthy(){return population - dead - infected;}
    public int getX(){return x;}
    public int getY(){return y;}

    public int getPortX() {return portX;}

    public int getPortY() {return portY;}

    public boolean isWithPort() {return withPort;}

    public BufferedImage getBorderImage() {return closedBordersImage;}
    static public Country getCountryByName(String name){
        for(Country c : Country.allCountries){
            if(c.getName().equals(name)){
                return c;
            }
        }
        return null;
    }
    static public Country getRandomCountry(){
        return Country.allCountries.get((int) Math.round((Math.random()*(Country.allCountries.size() - 1))));
    }
    public String getMotherland() {
        return motherland;
    }
    public HashMap<String, ArrayList<Node>> getRoadsToCountries() {
        return roadsToCountries;
    }
    public HashMap<String, ArrayList<Node>> getShipsToCountries() {
        return shipsToCountries;
    }
    public void setBorderClosed(boolean to){borderClosed = to;}
    public boolean isBorderClosed(){return borderClosed;}
    public void showPeopleIncrease(int ill, int cured){
        amountOfPeopleTimesPassed = 0;
        amountOfPeopleInfected = ill;
        amountOfPeopleCured = cured;
    }
    public static void drawCountries(Graphics g, JPanel observer){
        for(Country c : allCountries){
            BufferedImage img = c.getBorderImage();
            if(c.isBorderClosed()){
                g.drawImage(img, c.getX() - img.getWidth()/2, c.getY()- img.getHeight()/2, observer);
            }
            if(c.amountOfPeopleTimesPassed < 40){
                g.drawString("+" + c.amountOfPeopleCured + " cured, +" + c.amountOfPeopleInfected + "infected", c.getX(), c.getY());
                c.amountOfPeopleTimesPassed ++;
            }
            c.setBorderClosed(c.countryRules.needToCloseBorders(c));
        }
    }
    public static void drawHubej(Graphics g, JPanel observer){
        g.drawString("Hubej", 742, 290);
    }

    public static Country getCountryByColor(Color color){
        Integer d = 10;
        for(Country c: allCountries){
            if((c.color.getRed() >= color.getRed() - d && c.color.getRed() <= color.getRed() + d) &&
                    (c.color.getGreen() >= color.getGreen() - d && c.color.getGreen() <= color.getGreen() + d) &&
                    (c.color.getBlue() >= color.getBlue() - d && c.color.getBlue() <= color.getBlue() + d)){return c;}
        }
        return null;
    }

    public static long getTotalInfected(){
        long total = 0;
        for(Country c : allCountries){
            total += (long)c.getInfected();
        }
        return total;
    }
    public static long getTotalPopulation(){
        long total = 0;
        for(Country c : allCountries){
            total += (long)c.getPopulation();
        }
        return total;
    }
}
