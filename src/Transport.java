import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;

public class Transport {
    public static double appearanceChance = 0.001;
    public static double withVaccineChance = 0.5;
    private static ArrayList<Transport> allTransports = new ArrayList<>();
    private Image image;
    private ArrayList<Node> road;
    private AStarPathfinding pathFinder;
    private int amountOfPeople;
    private double x, y;
    private Thread driveThread;
    private boolean isDriving = false;
    private Country from, to;
    private double speed = 0.001;
    private double pathDriven = 0;
    private int curentPathIndex = 0;
    private int framerate = 4;
    private String imagePath;
    private Image vaccine;

    public Transport(String imagePath, AStarPathfinding pathFinder, int amountOfPeople, Country from, Country to){
        this.from = from;
        this.pathFinder = pathFinder;
        this.amountOfPeople = amountOfPeople;
        this.imagePath = imagePath;
        this.to = to;
        if(Math.random() > withVaccineChance){
            try {
                this.vaccine = ImageIO.read(new File(ReversePlagueGame.imagePath + "vaccine.png"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        startDriving();
    }
    public Transport(String imagePath, int amountOfPeople, Country from, Country to) {

        this.from = from;
        this.amountOfPeople = amountOfPeople;
        this.to = to;
        this.imagePath = imagePath;
        if(Math.random() > withVaccineChance){
            try {
                this.vaccine = ImageIO.read(new File(ReversePlagueGame.imagePath + "vaccine.png"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        startDriving();
    }
    public void startDriving(){
        this.isDriving = true;
        Transport.allTransports.add(this);
        this.driveThread = new Thread(() -> {
            try {

                    this.x = from.getX();
                    this.y = from.getY();

                    double infectedPercentage = from.getInfected() / from.getPopulation();
                    double peopleInTransport =  (Math.random()/2 + 0.5) * (double)amountOfPeople;

                    double infectedPeople = infectedPercentage * peopleInTransport;
                    double healthyCuredPeople = peopleInTransport - infectedPeople;

                    if(healthyCuredPeople > from.getHealthy()){
                        if(infectedPeople > from.getInfected()){
                            isDriving = false;
                        }else{
                            healthyCuredPeople = 0;
                        }
                    }
                    if(infectedPeople > from.getInfected()){
                        if(healthyCuredPeople > from.getHealthy()){
                            isDriving = false;
                        }else{
                            infectedPeople = 0;
                        }
                    }
                    if(isDriving){

                        if(getClass() == Ship.class){
                            this.framerate = 20;
                            this.x = from.getPortX();
                            this.y = from.getPortY();


                            if(from.getShipsToCountries().containsKey(to.getName())) {
                                road = from.getShipsToCountries().get(to.getName());
                            }else{
                                road = pathFinder.aStar(new Node(from.getPortX(), from.getPortY()), new Node(to.getPortX(), to.getPortY()));
                                AStarPathfinding.compressPath(road, 2);
                                from.getShipsToCountries().put(to.getName(), road);
                                to.getShipsToCountries().put(from.getName(), road);
                            }
                        }
                        if (getClass() == Car.class) {
                            this.framerate = 20;
                            if(from.getRoadsToCountries().containsKey(to.getName())){
                                road = from.getRoadsToCountries().get(from.getName());
                            }else{
                                road = pathFinder.aStar(new Node(from.getX(), from.getY()), new Node(to.getX(), to.getY()));
                                AStarPathfinding.compressPath(road, 4);
                                from.getRoadsToCountries().put(to.getName(), road);
                                to.getRoadsToCountries().put(from.getName(), road);
                            }
                        }
                        try {
                            image = ImageIO.read(new File(imagePath));
                        } catch (IOException ex) {
                            ex.printStackTrace();
                            isDriving = false;
                        }
                        if(road != null){
                            if(road.size() <= 1){
                                isDriving = false;
                            }
                        }
                    }

                    if(isDriving){
                        while (isDriving && GameWindow.isGameRunning) {
                            drive();
                            Thread.sleep(framerate);
                        }
                        to.setInfected(to.getInfected() + infectedPeople);
                        to.setCured(to.getCured() + healthyCuredPeople);
                        to.showPeopleIncrease((int)infectedPeople, (int)healthyCuredPeople);
                        if(isWithVaccine()){
                            double newVaccine = to.getInfected() * 0.01;
                            to.setInfected(to.getInfected() - newVaccine);
                            to.setVaccinated(to.getVaccinated() + newVaccine);
                        }

                    }
                    allTransports.remove(this);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {
                    allTransports.remove(this);
                }
            });
            this.driveThread.start();

        }
    public void drive(){
        if (road != null){
            x = road.get(curentPathIndex).x;
            y = road.get(curentPathIndex).y;
            curentPathIndex ++;
            if(curentPathIndex >= road.size()){
                isDriving = false;
            }
        }else {
            x = from.getX() + (double)(to.getX() - from.getX()) * pathDriven;
            y = from.getY() + (double)(to.getY() - from.getY()) * pathDriven;

            pathDriven += speed;
            if(pathDriven >= 1.0){
                isDriving = false;
            }
        }
    }

    public int getX(){return (int)x;}
    public int getY(){return (int)y;}
    public boolean isWithVaccine(){return vaccine != null;}
    public static void drawTransports(Graphics g, JPanel observer){
        for(Transport t : (ArrayList<Transport>)allTransports.clone()){
            if(t != null){
                g.drawImage(t.image, t.getX(), t.getY(), observer);
                if(t.isWithVaccine()){
                    g.drawImage(t.vaccine, t.getX(), t.getY() - 24, observer);
                }
            }
        }
    }

    }

