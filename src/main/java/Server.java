import Responses.User;

import java.awt.image.BufferedImage;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
    private final static int MAX_THREADS = 10;
    private final static int TIMEOUT = 5 * 60 * 1000; // 5 minute timeout
//    private final WorkerPool workers;
    private final Worker[] workers;
    private final ConnectionPool connections;
    private final UsersList usersList;
    private final Whiteboard whiteboard;

    public Server(int port) {
        connections = new ConnectionPool(MAX_THREADS);
        usersList = new UsersList(this);
        whiteboard = new Whiteboard(this);
        workers = new Worker[MAX_THREADS];
        for(int i = 0; i < MAX_THREADS; i++) {
            Worker newWorker = new Worker(connections, usersList, whiteboard);
            newWorker.start();
            workers[i] = newWorker;
        }

        System.out.println("Server is online\n");
        try ( ServerSocket server = new ServerSocket(port)) {
            while(true) {
                Socket socket = server.accept();
                socket.setSoTimeout(TIMEOUT);

                // Add connection to connection pool to be served
                boolean connected = connections.connect(socket);

                if(!connected) { // Server is busy
                    DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                    //ConnectionResponse res = new ConnectionResponse("Error: Server is busy");
                    dos.writeUTF("WE ARE BUSY FUCK OFF");
                    dos.close();
                    socket.close();
                }
            }
        } catch(IOException ioe) {
            throw new RuntimeException(ioe);
        } catch(SecurityException se) {
            System.out.println("Security exception: " + se.getMessage());
        } catch (RuntimeException e) {
            System.out.println("Runtime Exception: " + e.getMessage());
        }
    }

    // Propagate new user list to all users
    public void pushUserUpdate(ArrayList<User> users) {
        for(Worker worker: workers) {
            worker.updateUsers(users);
        }
    }

    // Propagate new whiteboard to all users
    public void pushWhiteboardUpdate(BufferedImage whiteboard) {
        for(Worker worker: workers) {
            worker.updateWhiteboard(whiteboard);
        }
    }
}
