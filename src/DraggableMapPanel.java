import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

class DraggableMapPanel extends JPanel {
    private static Graphics currentFrame;
    private Thread graphicsUpdatingThread;
    private BufferedImage mapImage;
    private double zoomFactor = 1.0;  
    private int offsetX = 0;
    private int offsetY = 0;
    private Point lastDragPoint;

    int dx = 0;
    int dy = 0;

    private static final double MIN_ZOOM = 0.2;  
    private static final double MAX_ZOOM = 3.0;  
    DraggableMapPanel leftPanel;

    public int getDx() {
        return dx;
    }

    public int getDy() {
        return dy;
    }

    public int getOffsetX() {
        return offsetX;
    }

    public int getOffsetY() {
        return offsetY;
    }

    public double getZoomFactor() {
        return zoomFactor;
    }

    public static Graphics getCurrentFrame(){return currentFrame;}
    public DraggableMapPanel(String imagePath) {
        try {
            mapImage = ImageIO.read(new File(imagePath));
        } catch (IOException e) {
            e.printStackTrace();
        }


        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                lastDragPoint = e.getPoint();
                try {
                    handleCountryClick(e.getX(), e.getY());
                } catch (AWTException ex) {
                    throw new RuntimeException(ex);
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (lastDragPoint != null) {
                    dx = e.getX() - lastDragPoint.x;
                    dy = e.getY() - lastDragPoint.y;
                    offsetX += dx;
                    offsetY += dy;


                    enforceDraggingLimits();

                    lastDragPoint = e.getPoint();
                    repaint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                lastDragPoint = null;
            }

        };


        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);
        addMouseWheelListener(e -> handleMouseWheelEvent(e));

    }

    public void startDragMapLoop(){
        graphicsUpdatingThread = new Thread(() -> {
            while(GameWindow.isGameRunning){
                repaint();
                try {
                    Thread.sleep(16);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        graphicsUpdatingThread.start();
    }
    private void handleMouseWheelEvent(MouseWheelEvent e) {
        int notches = e.getWheelRotation();
        if (notches < 0) {
            zoomIn();
        } else {
            zoomOut();
        }
    }

    private void zoomIn() {
        zoomFactor *= 1.1; 
        zoomFactor = Math.min(MAX_ZOOM, zoomFactor);
    }
    private void zoomOut() {
        zoomFactor /= 1.1; 
        zoomFactor = Math.max(MIN_ZOOM, zoomFactor);
    }

    private void enforceDraggingLimits() {
        int mapWidth = (int) (mapImage.getWidth(this) * zoomFactor);
        int mapHeight = (int) (mapImage.getHeight(this) * zoomFactor);

        offsetX = Math.max(Math.min(offsetX, 0), getWidth() - mapWidth);
        offsetY = Math.max(Math.min(offsetY, 0), getHeight() - mapHeight);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (mapImage != null) {
            Graphics2D g2d = (Graphics2D) g;

            g2d.translate(offsetX, offsetY);
            g2d.scale(zoomFactor, zoomFactor);

            g2d.drawImage(mapImage, 0, 0, this);
            currentFrame = g;
        } 
    }

    @Override
    public Dimension getPreferredSize() {
        int newWidth = (int) (mapImage.getWidth(this) * zoomFactor);
        int newHeight = (int) (mapImage.getHeight(this) * zoomFactor);
        return new Dimension(newWidth, newHeight);
    }


    public void mapCoinClicked(MapCoin mapCoin){};
    public void mapCountryClicked(Country country){};

    void handleCountryClick(int x, int y) throws AWTException {
        int imgX = (int) ((x - offsetX) / zoomFactor);
        int imgY = (int) ((y - offsetY) / zoomFactor);

        MapCoin clickedCoin = MapCoin.clickOnCoins(imgX, imgY);
        if(clickedCoin != null){mapCoinClicked(clickedCoin);}

        Country clickedCountry = Country.getCountryByColor(new Color(mapImage.getRGB(imgX, imgY)));
        if(clickedCountry != null){mapCountryClicked(clickedCountry);}
    }

    public BufferedImage getMapImage(){
        return mapImage;
    }

}



