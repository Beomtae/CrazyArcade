package Client;
import java.io.Serializable;

class ChatMsg implements Serializable {
    private static final long serialVersionUID = 1L;
    public String code;
    public String UserName;
    public String data;
    public String dm;
    public int x;
    public int y;
    public String move;

    public ChatMsg(String UserName, String code, String msg) {
        this.code = code;
        this.UserName = UserName;
        this.data = msg;
        this.dm = null;
    }

    public void update(int x, int y, String move) {
        this.x = x;
        this.y = y;
        this.move = move;
    }
}