// Jiachen Si 1085839
package Main;

import Main.DrawObjects.Drawable;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Whiteboard {
    private BufferedImage whiteboard;
    private final Server server;

    public Whiteboard(Server server) {
        whiteboard = new BufferedImage(700, 600, BufferedImage.TYPE_INT_ARGB);
        this.server = server;
    }

    public void processChanges(Drawable change) {
        // Add change
        change.draw(whiteboard);

        // Update everyone
        server.pushWhiteboardUpdate(whiteboard, "change");
    }

    public void newWhiteboard(BufferedImage whiteboard) {
        this.whiteboard = whiteboard;
        server.pushWhiteboardUpdate(whiteboard, "new");
    }

    public void closeWhiteboard() {
        this.whiteboard = new BufferedImage(700, 600, BufferedImage.TYPE_INT_ARGB);
        server.pushWhiteboardUpdate(whiteboard, "close");
    }

    public BufferedImage getWhiteboard() {
        return whiteboard;
    }
}
