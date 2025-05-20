package Main.ServerMsg;

import Main.User;

public class UserJoinRequest extends ServerMsg {
    public User user;

    public UserJoinRequest(User user) {
        super("join request");
        this.user = user;
    }
}
