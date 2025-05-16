import Responses.User;

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
        users.remove(user);

        // Update everyone
        server.pushUserUpdate(users);
    }

    public User getManager() {
        for(User user : users) {
            if(user.role.equals("manager")) {
                return user;
            }
        }
        return null;
    }
}
