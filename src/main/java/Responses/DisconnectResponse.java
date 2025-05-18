package Responses;

public class DisconnectResponse extends Response{
    public String msg;

    public DisconnectResponse(String msg) {
        super("disconnect");
        this.msg = msg;
    }
}
