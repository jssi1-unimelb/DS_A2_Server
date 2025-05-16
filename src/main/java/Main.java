import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) {
        int port = Integer.parseInt(args[0]);
        Server server = new Server(port);
    }
}