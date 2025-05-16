import Requests.ConnectionRequest;
import Requests.UserStatusUpdate;
import Requests.WhiteboardChangeRequest;
import Responses.ConnectionStatusResponse;
import Responses.User;
import Responses.UsersUpdate;
import Responses.WhiteboardUpdate;

import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

public class Worker extends Thread{
    private final ConnectionPool pool;
    private DataOutputStream output;
    private UsersList users;
    private Whiteboard whiteboard;
    private User currentUser;
    private boolean hasConnection = false;

    public Worker(ConnectionPool pool, UsersList users, Whiteboard whiteboard) {
        this.pool = pool;
        this.users = users;
        this.whiteboard = whiteboard;
    }

    public void run() {
        while (true) {
            try (
                Socket client = pool.getConnectionRequest();
                DataInputStream input = new DataInputStream(client.getInputStream());
            ) {
                output = new DataOutputStream(client.getOutputStream());
                hasConnection = true;

                boolean deadConnection = false;
                while(!deadConnection) {

                    String requestJson = input.readUTF(); // Wait for client requests
                    String[] terms = requestJson.split("\"");
                    int index = Arrays.asList(terms).indexOf("type");
                    String type = terms[index + 2];
                    switch (type) {
                        case "connection" -> { // User wants to connect
                            ConnectionRequest request = GsonUtil.gson.fromJson(requestJson, ConnectionRequest.class);

                            // Send an update to the manager for the new user wanting to join


                            // TEMP FOR TESTING
                            // Make new user
                            String id = UUID.randomUUID().toString();
                            String username = request.username;
                            String role = request.role;
                            User user = new User(id, username, role);
                            users.addUser(user);
                            this.currentUser = user;
                        }
                        case "user status" -> { // Manager's response to a user connection
                            UserStatusUpdate request = GsonUtil.gson.fromJson(requestJson, UserStatusUpdate.class);

                            // If rejected, close the connection of the corresponding user

                            // If accepted, update the user list for all users and send the whiteboard to the current user
                        }
                        case "whiteboard" -> { // Updates to the white board
                            WhiteboardChangeRequest request = GsonUtil.gson.fromJson(requestJson, WhiteboardChangeRequest.class);

                            // Queue up the changes
                            whiteboard.processChanges(request.obj);
                        }
                        case "disconnect" -> {
                            users.removeUser(currentUser);
                            pool.killConnection();
                            deadConnection = true;
                            hasConnection = false;
                        }
                    }

                    if(deadConnection) { // If client disconnects
                        output.close();
                        pool.killConnection();
                    }
                }
            } catch (IOException ioe) {
                System.out.println("IOException: " + ioe.getMessage());
                pool.killConnection();
            } catch (RuntimeException e) {
                System.out.println("Runtime Exception: " + e.getMessage());
                pool.killConnection();
            } catch (InterruptedException ie) {
                System.out.println("Interruption Exception: " + ie.getMessage());
                pool.killConnection();
            }
        }
    }

    public synchronized void updateWhiteboard(BufferedImage whiteboard) {
        if(hasConnection) {
            try {
                WhiteboardUpdate whiteboardUpdate = new WhiteboardUpdate(whiteboard);
                String response = GsonUtil.gson.toJson(whiteboardUpdate);
                output.writeUTF(response);
            } catch (IOException e) {
                System.out.println(e);
                throw new RuntimeException(e);
            }
        }
    }

    public synchronized void updateUsers(ArrayList<User> users) {
        if(hasConnection) {
            UsersUpdate usersUpdate = new UsersUpdate(users);
            String response = GsonUtil.gson.toJson(usersUpdate);

            try {
                output.writeUTF(response);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public synchronized void updateConnectionStatus(boolean isAlive) {
        if(hasConnection) {
            ConnectionStatusResponse connectionStatusResponse = new ConnectionStatusResponse(isAlive);
            String response = GsonUtil.gson.toJson(connectionStatusResponse);

            try {
                output.writeUTF(response);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
