package Main;

import java.util.ArrayList;

public class UsersList {
    private ArrayList<User> users;
    private Server server;

    public UsersList(Server server) {
        this.users = new ArrayList<>();
        this.server = server;
    }

    public synchronized void addUser(User user) {
        users.add(user);

        // Update everyone
        server.pushUserUpdate(users);
    }

    public synchronized void removeUser(User user) {
        users.removeIf(u -> u.id.equals(user.id));

        // Update everyone
        server.pushUserUpdate(users);
    }
}
