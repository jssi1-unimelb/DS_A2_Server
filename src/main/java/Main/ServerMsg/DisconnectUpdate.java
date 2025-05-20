// Jiachen Si 1085839
package Main.ServerMsg;

public class DisconnectUpdate extends ServerMsg {
    public String msg;

    public DisconnectUpdate(String msg) {
        super("disconnect");
        this.msg = msg;
    }
}
