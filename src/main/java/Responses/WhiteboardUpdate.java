package Responses;

import java.awt.image.BufferedImage;

public class WhiteboardUpdate extends Response {
    public BufferedImage whiteboard;

    public WhiteboardUpdate(BufferedImage whiteboard) {
        super("whiteboard update");
        this.whiteboard = whiteboard;
    }
}
