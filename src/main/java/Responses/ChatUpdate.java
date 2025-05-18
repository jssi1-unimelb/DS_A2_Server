package Responses;

import Main.ChatItem;

public class ChatUpdate extends Response {
    public ChatItem item;

    public ChatUpdate(ChatItem chatItem) {
        super("chat");
        this.item = chatItem;
    }
}
