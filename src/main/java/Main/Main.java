// Jiachen Si 1085839
package Main;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Main {
    public static void main(String[] args) {
        try {
            InetAddress host = InetAddress.getByName(args[0]);
            int port = Integer.parseInt(args[1]);
            Server server = new Server(host, port);
        } catch (UnknownHostException e) {
            System.out.print("Error: Unknown Host");
        }
    }
}