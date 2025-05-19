package Main;

import Main.Responses.ConnectionResponse;

import java.awt.image.BufferedImage;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Server {
    private final static int MAX_THREADS = 10;
    private final static int TIMEOUT = 5 * 60 * 1000; // 5 minute timeout
//    private final WorkerPool workers;
    private final Worker[] workers;
    private final ConnectionPool connections;
    private final UsersList usersList;
    private final Whiteboard whiteboard;
    private DataOutputStream dos = null;

    public Server(int port) {
        connections = new ConnectionPool(MAX_THREADS);
        usersList = new UsersList(this);
        whiteboard = new Whiteboard(this);
        workers = new Worker[MAX_THREADS];
        for(int i = 0; i < MAX_THREADS; i++) {
            Worker newWorker = new Worker(this, connections, usersList, whiteboard);
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
                    dos = new DataOutputStream(socket.getOutputStream());
                    ConnectionResponse res = new ConnectionResponse("Error: Server is busy");
                    String resJson = GsonUtil.gson.toJson(res);
                    write(resJson);
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
            if(worker.currentUser != null) {
                worker.updateUsers(users);
            }
        }
    }

    // Propogate chat update to all users
    public void pushChatUpdate(ChatItem chatItem) {
        for(Worker worker: workers) {
            if(worker.currentUser != null) {
                worker.updateChat(chatItem);
            }
        }
    }

    // Propagate new whiteboard to all users
    public void pushWhiteboardUpdate(BufferedImage whiteboard, String type) {
        for(Worker worker: workers) {
            if(worker.currentUser != null) {
                worker.updateWhiteboard(whiteboard, type);
            }
        }
    }

    // Disconnect all users when manager disconnects
    public void disconnectAll() {
        for(Worker worker: workers) {
            if(worker.currentUser != null) {
                worker.sendDisconnectResponse("Error: Session ended, manager disconnected");
            }
        }
    }

    public Worker getManagerThread() {
        for(Worker worker : workers) {
            if(worker.currentUser != null) {
                if(worker.currentUser.role.equals("manager")) {
                    return worker;
                }
            }
        }
        return null;
    }

    public Worker getUserThread(User user) {
        for(Worker worker : workers) {
            if(worker.currentUser != null) {
                if(worker.currentUser.id.equals(user.id)) {
                    return worker;
                }
            }
        }
        return null;
    }

    // Helper function that converts strings to byte arrays to be sent
    public void write(String req) {
        try {
            byte[] reqBytes = req.getBytes(StandardCharsets.UTF_8);
            dos.writeInt(reqBytes.length); // Send length of byte array
            dos.write(reqBytes);           // Send the request
            dos.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
