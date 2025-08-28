import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MapCoin {
    private static ArrayList<MapCoin> allMapCoins = new ArrayList();
    public static int coinLifeTime = 5000;
    public static double spontaniousSpawnRate = 0.0001;
    public static int requiredCuredAmountToSpawn = 10000;
    private final static int clickMargin = 24;
    public static final int balance = 100;
    private Country country;
    private Image image;
    private int x,y;
    
    public MapCoin(Country country){
        this.country = country;
        this.x = (int) (country.getX() + (Math.random() * 2 - 1) * 100);
        this.y = (int) (country.getY() + (Math.random() * 2 - 1) * 100);
        try {
            this.image = ImageIO.read(new File(ReversePlagueGame.imagePath + "dollar.png"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        allMapCoins.add(this);
        summonCoin();
    }
    public int getX() {return x;}
    public int getY() {return y;}
    public Image getImage(){return image;}
    public int getBalance() {return balance;}
    public static void drawMapCoins(Graphics g, JPanel observer){
        for(MapCoin mp : (ArrayList<MapCoin>)allMapCoins.clone()){
            if(mp != null){ g.drawImage(mp.getImage(), mp.getX(), mp.getY(), observer); }
        }
    }
    public static MapCoin clickOnCoins(int clickX, int clickY){
        for(MapCoin mp : allMapCoins){
            if(clickX >= mp.getX() && clickX <= mp.getX() + clickMargin * GameWindow.mapPanel.getZoomFactor() && clickY >= mp.getY() && clickY <= mp.getY() + clickMargin * GameWindow.mapPanel.getZoomFactor()){
                allMapCoins.remove(mp);
                return mp;
            }
        }
        return null;
    }
    private void summonCoin(){
        Thread coinThread = new Thread(() -> {
            int waitedTime = 0;
            while (waitedTime <= coinLifeTime && GameWindow.isGameRunning){
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                waitedTime += 10;
            }
            allMapCoins.remove(this);
        });
        coinThread.start();
    }
}
