import DrawObjects.Drawable;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.concurrent.*;

public class Whiteboard {
    private final LinkedList<Drawable> changes = new LinkedList<>();;
    private final BufferedImage whiteboard;
    private final Graphics2D g2D;
    private final Server server;

    public Whiteboard(Server server) {
        whiteboard = new BufferedImage(700, 600, BufferedImage.TYPE_INT_ARGB);
        g2D = whiteboard.createGraphics();
        this.server = server;
    }

    public void processChanges(Drawable change) {
        // Add change
        change.draw(g2D);

        // Update everyone
        server.pushWhiteboardUpdate(whiteboard);
    }
}
