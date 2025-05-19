package Main.DrawObjects;

import java.awt.image.BufferedImage;
import java.util.LinkedList;

public class FreeDraw implements Drawable{
    LinkedList<Drawable> shapes;

    @Override
    public void draw(BufferedImage image) {
        for(Drawable shape: shapes) {
            shape.draw(image);
        }
    }
}
