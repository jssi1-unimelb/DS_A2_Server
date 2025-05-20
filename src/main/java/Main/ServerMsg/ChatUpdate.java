package Main.ServerMsg;

import Main.ChatItem;

public class ChatUpdate extends ServerMsg {
    public ChatItem item;

    public ChatUpdate(ChatItem chatItem) {
        super("chat");
        this.item = chatItem;
    }
}
