package Responses;

public class ConnectionStatusResponse extends Response{
    public boolean isAlive;

    public ConnectionStatusResponse(boolean isAlive) {
        super("connection status");
        this.isAlive = isAlive;
    }
}
