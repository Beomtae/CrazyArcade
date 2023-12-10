package Server;
import java.io.Serializable;

class ChatMsg implements Serializable {
    private static final long serialVersionUID = 1L;
    public String code; //
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

    public ChatMsg(String UserName, String code, String msg, String dm) {
        this.code = code;
        this.UserName = UserName;
        this.data = msg;
        this.dm = dm;
    }
    public void printChtMsg() {
//		System.out.println("UserName : "+UserName +", x: "+x+", y: "+y+", move:"+move+", code="+code);
    }
//	public String prt() {
//		String prt = "UserName : "+UserName +", x: "+x+", y: "+y+", move:"+move+", code="+code;
//		return prt;
//	}
}