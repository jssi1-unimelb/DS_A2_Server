package Responses;

import java.awt.image.BufferedImage;

public class WhiteboardUpdate extends Response {
    public BufferedImage whiteboard;
    public String whiteboardUpdateType;

    public WhiteboardUpdate(BufferedImage whiteboard, String type) {
        super("whiteboard update");
        this.whiteboard = whiteboard;
        this.whiteboardUpdateType = type;
    }
}
