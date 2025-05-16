package Requests;

import DrawObjects.Drawable;

public class WhiteboardChangeRequest extends Request {
    public Drawable obj;

    public WhiteboardChangeRequest(Drawable obj) {
        super("whiteboard");
        this.obj = obj;
    }
}
