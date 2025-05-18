package Requests;

/*
* THIS REQUEST CAN ONLY BE SENT BY THE MANAGER TYPE / DELETE THIS IN THE CLIENT AFTER WORK IS COMPLETE
*  */

import Main.User;

public class UserStatusUpdate extends Request {
    public boolean approved;
    public User user;
}
