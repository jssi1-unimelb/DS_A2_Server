// Jiachen Si 1085839
package Main.ServerMsg;

import Main.User;

import java.util.ArrayList;

public class UsersUpdate extends ServerMsg {
    public ArrayList<User> users;

    public UsersUpdate(ArrayList<User> users) {
        super("users update");
        this.users = users;
    }
}
