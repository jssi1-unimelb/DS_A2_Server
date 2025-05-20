// Jiachen Si 1085839
package Main.ServerMsg;

import java.awt.image.BufferedImage;

public class WhiteboardUpdate extends ServerMsg {
    public BufferedImage whiteboard;
    public String whiteboardUpdateType;

    public WhiteboardUpdate(BufferedImage whiteboard, String type) {
        super("whiteboard update");
        this.whiteboard = whiteboard;
        this.whiteboardUpdateType = type;
    }
}
