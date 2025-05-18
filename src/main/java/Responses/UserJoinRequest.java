package Responses;

import Main.User;

public class UserJoinRequest extends Response {
    public User user;

    public UserJoinRequest(User user) {
        super("join request");
        this.user = user;
    }
}
