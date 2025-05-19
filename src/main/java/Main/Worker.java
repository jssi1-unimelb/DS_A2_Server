package Main;

import Main.Requests.*;
import Main.Responses.*;

import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

public class Worker extends Thread{
    private final ConnectionPool pool;
    private DataOutputStream output;
    private UsersList users;
    private Whiteboard whiteboard;
    User currentUser = null;
    private boolean hasConnection = false;
    private Server server;

    // For the manager thread
    private boolean IsJoinRequestAlreadyQueued = false;

    public Worker(Server server, ConnectionPool pool, UsersList users, Whiteboard whiteboard) {
        this.server = server;
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

                while(hasConnection) {
                    int length = input.readInt(); // Get length of incoming byte array
                    byte[] reqBytes = new byte[length];
                    input.readFully(reqBytes);    // Read the number of bytes specified by "reqBytes"
                    String requestJson = new String(reqBytes, StandardCharsets.UTF_8); // Convert byte[] to String
                    String[] terms = requestJson.split("\"");
                    int index = Arrays.asList(terms).indexOf("type");
                    String type = terms[index + 2];
                    switch (type) {
                        case "connection" -> { // User wants to connect
                            ConnectionRequest request = GsonUtil.gson.fromJson(requestJson, ConnectionRequest.class);
                            // Make user but don't add to user list until manager approves
                            this.currentUser = new User(UUID.randomUUID().toString(), request.username, request.role);
                            if(request.role.equals("manager")) { // Allow the manager to connect without approval
                                addUser(currentUser);
                            } else { // Send an update to the manager for the new user wanting to join
                                Worker manager = server.getManagerThread();
                                if(manager != null) {
                                    manager.newUserJoinRequest(currentUser);  // Send request to join
                                    manager.sleepUserThreadWaitingApproval(); // Sleep thread until request is processed
                                } else { // Manager hasn't connected yet
                                    sendDisconnectResponse("Error: The manager hasn't connected yet");
                                }
                            }
                        }
                        case "user status" -> { // Manager's response to a user connection
                            UserStatusUpdate request = GsonUtil.gson.fromJson(requestJson, UserStatusUpdate.class);
                            if(request.approved) { // Accepted
                                addUser(request.user);
                            } else { // Rejected
                                try {
                                    // If rejected, manager closes the connection of the corresponding user
                                    Worker userThread = server.getUserThread(request.user);
                                    userThread.sendDisconnectResponse("Manager has denied your join request");

                                } catch (NullPointerException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                            IsJoinRequestAlreadyQueued = false;
                            wakeUpWaitingThreads(); // Wake up thread that was waiting for request to be processed
                        }
                        case "whiteboard update" -> { // New shape drawn on the white board
                            WhiteboardChangeRequest request = GsonUtil.gson.fromJson(requestJson, WhiteboardChangeRequest.class);

                            // Queue up the changes
                            whiteboard.processChanges(request.obj);
                        }
                        case "new whiteboard" -> { // Manager changed the whiteboard being used
                            NewWhiteboardRequest request = GsonUtil.gson.fromJson(requestJson, NewWhiteboardRequest.class);
                            whiteboard.newWhiteboard(request.whiteboard);
                        }
                        case "close whiteboard" -> {
                            whiteboard.closeWhiteboard();
                        }
                        case "disconnect" -> {
                            hasConnection = false;
                            users.removeUser(currentUser);
                            if(currentUser.role.equals("manager")) { // Kick all users when manager disconnects
                                server.disconnectAll();
                            }
                        }
                        case "kick" -> {
                            KickUserRequest kickUserRequest = GsonUtil.gson.fromJson(requestJson, KickUserRequest.class);
                            Worker targetThread = server.getUserThread(kickUserRequest.user);
                            targetThread.sendDisconnectResponse("You have been kicked");
                        }
                        case "chat" -> {
                            UpdateChatRequest request = GsonUtil.gson.fromJson(requestJson, UpdateChatRequest.class);
                            server.pushChatUpdate(request.item);
                        }
                    }
                }
                // Client disconnected
                cleanUp();
            } catch (IOException ioe) { // Client terminated program
                hasConnection = false;
                users.removeUser(currentUser);
                if(currentUser.role.equals("manager")) { // Kick all users when manager disconnects
                    server.disconnectAll();
                }
                cleanUp();
            } catch (RuntimeException e) {
                hasConnection = false;
                System.out.println("Runtime Exception: " + e.getMessage());
                cleanUp();
            } catch (InterruptedException ie) {
                hasConnection = false;
                System.out.println("Interruption Exception: " + ie.getMessage());
                cleanUp();
            }
        }
    }

    private void cleanUp() {
        try {
            pool.killConnection();
            output.close();
            currentUser = null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void addUser(User user) {
        users.addUser(user);
        // Send the user the whiteboard
        updateWhiteboard(whiteboard.getWhiteboard(), "new");
    }

    public synchronized void updateWhiteboard(BufferedImage whiteboard, String type) {
        if(hasConnection) {
            WhiteboardUpdate whiteboardUpdate = new WhiteboardUpdate(whiteboard, type);
            String response = GsonUtil.gson.toJson(whiteboardUpdate);
            write(response);
        }
    }

    public synchronized void updateUsers(ArrayList<User> users) {
        if(hasConnection) {
            UsersUpdate usersUpdate = new UsersUpdate(users);
            String response = GsonUtil.gson.toJson(usersUpdate);
            write(response);
        }
    }

    public synchronized void sleepUserThreadWaitingApproval() {
        try {
            wait();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized void wakeUpWaitingThreads() {
        notifyAll();
    }

    public synchronized void newUserJoinRequest(User user) {
        if(hasConnection) {
            // Allows multiple users to queue up to be approved
            while(IsJoinRequestAlreadyQueued) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            IsJoinRequestAlreadyQueued = true;
            UserJoinRequest joinRequest = new UserJoinRequest(user);
            String joinRequestStr = GsonUtil.gson.toJson(joinRequest);
            write(joinRequestStr);
        }
    }

    public synchronized void sendDisconnectResponse(String msg) {
        if(hasConnection) {
            DisconnectResponse disconnectResponse = new DisconnectResponse(msg);
            String response = GsonUtil.gson.toJson(disconnectResponse);
            write(response);
        }
    }

    public void updateChat(ChatItem chatItem) {
        if(hasConnection) {
            ChatUpdate chatUpdate = new ChatUpdate(chatItem);
            String update = GsonUtil.gson.toJson(chatUpdate);
            write(update);
        }
    }

    // Helper function that converts strings to byte arrays to be sent
    private void write(String req) {
        try {
            byte[] reqBytes = req.getBytes(StandardCharsets.UTF_8);
            output.writeInt(reqBytes.length); // Send length of byte array
            output.write(reqBytes);           // Send the request
            output.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
