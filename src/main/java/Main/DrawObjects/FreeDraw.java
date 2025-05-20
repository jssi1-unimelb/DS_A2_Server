package Main.DrawObjects;

import java.awt.image.BufferedImage;
import java.util.LinkedList;

public class FreeDraw extends Shape {
    LinkedList<Drawable> shapes;

    @Override
    public void draw(BufferedImage image) {
        for(Drawable shape: shapes) {
            shape.draw(image);
        }
    }
}
