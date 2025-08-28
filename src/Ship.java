import java.awt.*;
import java.awt.image.BufferedImage;

public class Ship extends Transport{

    public Ship(Country from, Country to) {
        super(ReversePlagueGame.imagePath + "ship.png", new AStarPathfinding(GameWindow.mapPanel.getMapImage()){
            @Override
            public boolean isBlocked(BufferedImage map, int px, int py) {
                int d = 20;
                Color c = new Color(map.getRGB(px,py));
                return !((c.getRed() >= 255 - d && c.getRed() <= 255 + d) && (c.getGreen() >= 255 - d && c.getGreen() <= 255 + d) && (c.getBlue() >= 255 - d && c.getBlue() <= 255 + d));
            }
        },360, from, to);

    }
}
