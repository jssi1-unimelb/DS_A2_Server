package DrawObjects;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Triangle implements Drawable {
    private final int[] xPoints;
    private final int[] yPoints;
    private final Color colour;
    private final int size;

    public Triangle(int[] xPoints, int[] yPoints, Color colour, int size) {
        this.xPoints = xPoints;
        this.yPoints = yPoints;
        this.colour = colour;
        this.size = size;
    }

    public void draw(BufferedImage image) {
        Graphics2D g2D = image.createGraphics();
        g2D.setColor(colour);
        g2D.setStroke(new BasicStroke(size));
        g2D.drawPolygon(xPoints, yPoints, 3); // 3 for triangle
        g2D.dispose();
    }
}