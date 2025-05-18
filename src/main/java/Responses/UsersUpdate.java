package Responses;

import Main.User;

import java.util.ArrayList;

public class UsersUpdate extends Response {
    public ArrayList<User> users;

    public UsersUpdate(ArrayList<User> users) {
        super("users update");
        this.users = users;
    }
}
