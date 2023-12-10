package Server;
import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;
import java.awt.event.ActionEvent;
import javax.swing.SwingConstants;

public class GameServer extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    JTextArea textArea;
    private JTextField txtPortNumber;
    private ServerSocket socket;
    private Socket client_socket;
    private Vector<UserService> UserVec = new Vector<UserService>();
    private Vector<UserService> WaitVec = new Vector<UserService>();
    private Vector<GameRoom> RoomVec = new Vector<GameRoom>();
    private static final int BUF_LEN = 128;

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    GameServer frame = new GameServer();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public GameServer() {
        new DefaultListModel<String>();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 338, 440);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(12, 10, 300, 298);
        contentPane.add(scrollPane);

        textArea = new JTextArea();
        textArea.setEditable(false);
        scrollPane.setViewportView(textArea);

        JLabel lblNewLabel = new JLabel("Port Number");
        lblNewLabel.setBounds(13, 318, 87, 26);
        contentPane.add(lblNewLabel);

        txtPortNumber = new JTextField();
        txtPortNumber.setHorizontalAlignment(SwingConstants.CENTER);
        txtPortNumber.setText("30000");
        txtPortNumber.setBounds(112, 318, 199, 26);
        contentPane.add(txtPortNumber);
        txtPortNumber.setColumns(10);

        JButton btnServerStart = new JButton("Server Start");
        btnServerStart.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    socket = new ServerSocket(Integer.parseInt(txtPortNumber.getText()));
                } catch (NumberFormatException | IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                AppendText("Chat Server Running..");
                btnServerStart.setText("Chat Server Running..");
                btnServerStart.setEnabled(false);
                txtPortNumber.setEnabled(false);
                AcceptServer accept_server = new AcceptServer();
                accept_server.start();
            }
        });
        btnServerStart.setBounds(12, 356, 300, 35);
        contentPane.add(btnServerStart);
    }
    class AcceptServer extends Thread {
        public void run() {
            while (true) {
                try {
                    AppendText("Waiting new clients ...");
                    client_socket = socket.accept();
                    AppendText("새로운 참가자 from " + client_socket);
                    UserService new_user = new UserService(client_socket, socket);
                    UserVec.add(new_user);
                    WaitVec.add(new_user);
                    new_user.start();

                    AppendText("현재 참가자 수 " + UserVec.size());
                } catch (IOException e) {
                    AppendText("accept() error");
                }
            }
        }
    }


    public void AppendText(String str) {
        textArea.append(str + "\n");
        textArea.setCaretPosition(textArea.getText().length());
    }


    public void AppendObject(ChatMsg msg) {
        textArea.append("code = " + msg.code + "\n");
        textArea.append("id = " + msg.UserName + "\n");
        textArea.append("data = " + msg.data + "\n");
        textArea.append("x = " + msg.x);
        textArea.append("    y = " + msg.y + "\n");
        textArea.append("move = " + msg.move + "\n" + "\n");
        textArea.setCaretPosition(textArea.getText().length());
    }

    class UserService extends Thread {

        private ObjectInputStream ois;
        private ObjectOutputStream oos;
        private Socket client_socket;

        public String UserName = "";
        public String UserStatus;
        public String RoomNameList = "";

        GameRoom myRoom;

        public UserService(Socket client_socket, ServerSocket socket) {
            this.client_socket = client_socket;

            try {
                oos = new ObjectOutputStream(client_socket.getOutputStream());
                oos.flush();
                ois = new ObjectInputStream(client_socket.getInputStream());
            } catch (Exception e) {
                AppendText("userService error");
            }
        }

        public void Login() {
            AppendText("새로운 참가자" + UserName + " 입장.");
            WriteOne("Welcome to Java chat server\n");
            WriteOne(UserName + "님 환영합니다.\n");
            String msg = "[" + UserName + "]님이 입장 하였습니다..\n";
            WriteOthers(msg);
            RoomNameList = "";
            for (int i = 0; i < RoomVec.size(); i++) {
                RoomNameList += (RoomVec.elementAt(i).RoomName + "$");
            }
            ChatMsg cm = new ChatMsg(UserName, "504", "방 목록 갱신", RoomNameList);
            WriteOneObject(cm);
        }

        public void Logout() {
            String msg = "[" + UserName + "]님이 퇴장 하였습니다.\n";
            UserVec.removeElement(this);
            WaitVec.removeElement(this);
            WriteAll(msg);
            AppendText("사용자 " + "[" + UserName + "] 퇴장. 현재 참가자 수 " + UserVec.size());
            SendUserListAll();
        }

        public void WriteAll(String str) {
            for (int i = 0; i < UserVec.size(); i++) {
                UserService user = (UserService) UserVec.elementAt(i);
                if (user.UserStatus == "O")
                    user.WriteOne(str);
            }
        }

        public void WriteAllObject(Object ob) {
            for (int i = 0; i < UserVec.size(); i++) {
                UserService user = (UserService) UserVec.elementAt(i);
                if (user.UserStatus == "O")
                    user.WriteOneObject(ob);
            }
        }

        public void WriteRoomObject(Object ob) {
            for (int i = 0; i < myRoom.InRoomUser.size(); i++) {
                UserService user = (UserService) myRoom.InRoomUser.elementAt(i);
                user.WriteOneObject(ob);
            }
        }

        public void InformElseName(Object ob) {
            UserService user = (UserService) myRoom.InRoomUser.elementAt(0);
            user.WriteOneObject(ob);
            String owner = user.UserName;
            user = (UserService) myRoom.InRoomUser.elementAt(1);
            ChatMsg player2m = new ChatMsg(user.UserName, "507", owner);
            user.WriteOneObject(player2m);
        }

        public void WriteOthers(String str) {
            for (int i = 0; i < UserVec.size(); i++) {
                UserService user = (UserService) UserVec.elementAt(i);
                if (user != this && user.UserStatus == "O")
                    user.WriteOne(str);
            }
        }

        public byte[] MakePacket(String msg) {
            byte[] packet = new byte[BUF_LEN];
            byte[] bb = null;
            int i;
            for (i = 0; i < BUF_LEN; i++)
                packet[i] = 0;
            try {
                bb = msg.getBytes("euc-kr");
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            for (i = 0; i < bb.length; i++)
                packet[i] = bb[i];
            return packet;
        }

        public void WriteOne(String msg) {
            try {
                ChatMsg obcm = new ChatMsg("SERVER", "200", msg);
                oos.writeObject(obcm);
            } catch (IOException e) {
                AppendText("dos.writeObject() error");
                try {
                    ois.close();
                    oos.close();
                    client_socket.close();
                    client_socket = null;
                    ois = null;
                    oos = null;
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                Logout();
            }
        }

        public void SendUserListAll() {
            for (int i = 0; i < UserVec.size(); i++) {
                UserService user = (UserService) UserVec.elementAt(i);
                user.SendUserList();
            }
        }

        public void SendUserList() {
            String users = "";
            for (int i = 0; i < WaitVec.size(); i++) {
                UserService user = (UserService) WaitVec.elementAt(i);
                users += (user.UserName + "\n");
            }
            try {
                ChatMsg obcm = new ChatMsg("SERVER", "300", users);
                oos.writeObject(obcm);
            } catch (IOException e) {
                AppendText("dos.writeObject() error");
                try {
                    ois.close();
                    oos.close();
                    client_socket.close();
                    client_socket = null;
                    ois = null;
                    oos = null;
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                Logout();
            }
        }

        public void WriteWaitObject(Object ob) {
            for (int i = 0; i < WaitVec.size(); i++) {
                UserService user = (UserService) WaitVec.elementAt(i);
                user.WriteOneObject(ob);
            }
        }
        public void WritePrivate(String msg) {
            try {
                ChatMsg obcm = new ChatMsg("귓속말", "200", msg);
                oos.writeObject(obcm);
            } catch (IOException e) {
                AppendText("dos.writeObject() error");
                try {
                    oos.close();
                    client_socket.close();
                    client_socket = null;
                    ois = null;
                    oos = null;
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                Logout();
            }
        }

        public void WriteOneObject(Object ob) {
            try {
                oos.writeObject(ob);
            } catch (IOException e) {
                AppendText("oos.writeObject(ob) error");
                try {
                    ois.close();
                    oos.close();
                    client_socket.close();
                    client_socket = null;
                    ois = null;
                    oos = null;
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                Logout();
            }
        }

        public void run() {
            while (true) {
                try {
                    Object obcm = null;
                    String msg = null;
                    ChatMsg cm = null;
                    if (socket == null)
                        break;
                    try {
                        obcm = ois.readObject();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                        return;
                    }
                    if (obcm == null)
                        break;
                    if (obcm instanceof ChatMsg) {
                        cm = (ChatMsg) obcm;
                        AppendObject(cm);
                    } else
                        continue;
                    if (cm.code.matches("100")) {
                        UserName = cm.UserName;
                        UserStatus = "O";
                        Login();

                        SendUserListAll();
                    }

                    else if (cm.code.matches("200")) {
                        msg = String.format("[%s] %s", cm.UserName, cm.data);
                        AppendText(msg);
                        String[] args = msg.split(" ");
                        if (args.length == 1) {
                            UserStatus = "O";
                        } else if (args[1].matches("/exit")) {
                            Logout();
                            break;
                        } else if (args[1].matches("/list")) {
                            WriteOne("User list\n");
                            WriteOne("Name\tStatus\n");
                            WriteOne("-----------------------------\n");
                            for (int i = 0; i < UserVec.size(); i++) {
                                UserService user = (UserService) UserVec.elementAt(i);
                                WriteOne(user.UserName + "\t" + user.UserStatus + "\n");
                            }
                            WriteOne("-----------------------------\n");
                        } else if (args[1].matches("/sleep")) {
                            UserStatus = "S";
                        } else if (args[1].matches("/wakeup")) {
                            UserStatus = "O";
                        } else if (args[1].matches("/to")) {
                            for (int i = 0; i < UserVec.size(); i++) {
                                UserService user = (UserService) UserVec.elementAt(i);
                                if (user.UserName.matches(args[2]) && user.UserStatus.matches("O")) {
                                    String msg2 = "";
                                    for (int j = 3; j < args.length; j++) {
                                        msg2 += args[j];
                                        if (j < args.length - 1)
                                            msg2 += " ";
                                    }
                                    user.WritePrivate(args[0] + " " + msg2 + "\n");
                                    break;
                                }
                            }
                        } else {
                            UserStatus = "O";
                            // WriteAll(msg + "\n"); // Write All
                            WriteAllObject(cm);
                        }
                    } else if (cm.code.matches("400")) {
                        Logout();
                    }

                    else if (cm.code.matches("500")) {
                        myRoom = new GameRoom();
                        myRoom.setRoomName(cm.data);
                        myRoom.count++;
                        RoomVec.add(myRoom);
                        myRoom.InRoomUser.add(this);
                        WaitVec.remove(this);
                        SendUserListAll();
                        cm.code = "504";
                        RoomNameList = "";
                        for (int i = 0; i < RoomVec.size(); i++) {
                            RoomNameList += (RoomVec.elementAt(i).RoomName + "$");
                        }
                        cm.data = myRoom.RoomName;
                        cm.dm = RoomNameList;
                        WriteAllObject(cm);
                    }

                    else if (cm.code.matches("501")) {
                        for (int i = 0; i < RoomVec.size(); i++) {
                            GameRoom r = RoomVec.get(i);
                            if (r.RoomName.equals(cm.data)) {
                                if (r.count < 2) {
                                    myRoom = RoomVec.get(i);
                                    myRoom.count++;
                                    WaitVec.remove(this);
                                    myRoom.InRoomUser.add(this);
                                    cm.code = "506";
                                    cm.data = "방에 입장";
                                    InformElseName(cm);
                                    SendUserListAll();
                                } else {
                                    cm.code = "505";
                                    cm.data = "선택한 방 인원이 꽉 찼습니다.";
                                    WriteOneObject(cm);
                                }
                            }
                        }
                    }
                    else if (cm.code.matches("503")) {
                        msg = String.format("[%s] %s", cm.UserName, cm.data);
                        AppendObject(cm);
                        WriteOneObject(cm);
                    }

                    else if (cm.code.matches("508")) {
                        msg = String.format("[%s] %s", cm.UserName, cm.data);
                        AppendObject(cm);
                        WriteRoomObject(cm);
                    }

                    else if (cm.code.matches("510")) {
                        AppendObject(cm);
                        for (int i = 0; i < RoomVec.size(); i++) {
                            GameRoom r = RoomVec.get(i);
                            if (r.RoomName.equals(cm.data)) {
                                AppendText("방 인원" + r.count);
                                if (r.count == 2) {
                                    for (int j = 0; j < r.InRoomUser.size(); j++) {
                                        ChatMsg out = new ChatMsg(UserName, "509", "back");
                                        WriteRoomObject(out);
                                        WaitVec.add(r.InRoomUser.get(j));
                                    }
                                    RoomVec.removeElement(r);

                                    RoomNameList = "";
                                    for (int j = 0; j < RoomVec.size(); j++) {
                                        RoomNameList += (RoomVec.elementAt(j).RoomName + "$");
                                    }
                                    System.out.println("나가시 처리 후 list" + RoomNameList);
                                    cm.code = "504";
                                    cm.data = myRoom.RoomName;
                                    cm.dm = RoomNameList;
                                    WriteAllObject(cm);
                                    SendUserListAll();
                                }
                            }
                        }
                    }

                    else if (cm.code.matches("600")) {
                        if (myRoom.count == 2) {
                            for (int i = 0; i < myRoom.InRoomUser.size(); i++) {
                                ChatMsg start = new ChatMsg(myRoom.InRoomUser.get(i).UserName, "600", myRoom.RoomName);
                                UserService user = myRoom.InRoomUser.get(i);
                                user.WriteOneObject(start);
                                AppendText(user.UserName + "에게 보냄");
                            }
                        }
                    }

                    else if (cm.code.matches("601")) {
                        AppendText("신호 확인");
                        for (int i = 0; i < myRoom.InRoomUser.size(); i++) {
                            if (!(myRoom.InRoomUser.get(i).UserName.equals(UserName))) {
                                AppendText(UserName + "가" + myRoom.InRoomUser.get(i).UserName + "에게 보냄");
                                AppendText(cm.code + cm.move + cm.x + cm.y);
                                UserService user = myRoom.InRoomUser.get(i);
                                user.WriteOneObject(cm);
                            }
                        }
                    }

                    else if (cm.code.matches("602")) {
                        for (int i = 0; i < myRoom.InRoomUser.size(); i++) {
                            if (!(myRoom.InRoomUser.get(i).UserName.equals(UserName))) {
                                AppendText(UserName + "가" + myRoom.InRoomUser.get(i).UserName + "에게 보냄");
                                AppendText(cm.code + cm.move + cm.x + cm.y);
                                UserService user = myRoom.InRoomUser.get(i);
                                user.WriteOneObject(cm);
                            }
                        }
                    }

                    else if (cm.code.matches("400")) {
                        Logout();
                        break;
                    } else {
                        WriteAllObject(cm);
                    }

                } catch (IOException e) {
                    AppendText("server) ois.readObject() error");
                    try {
                        RoomVec.removeElement(myRoom);
                        UserVec.removeElement(this);
                        RoomNameList = "";
                        for (int j = 0; j < RoomVec.size(); j++) {
                            RoomNameList += (RoomVec.elementAt(j).RoomName + "$");
                        }
                        ChatMsg cm = new ChatMsg(UserName, "504", "방 목록 갱신", RoomNameList);
                        if (myRoom != null) {
                            if (myRoom.count == 2) {
                                for (int j = 0; j < myRoom.InRoomUser.size(); j++) {
                                    UserService user = myRoom.InRoomUser.get(j);
                                    if (!(UserName.equals(user.UserName))) {
                                        ChatMsg error = new ChatMsg(user.UserName, "400", "에러");
                                        WaitVec.add(user);
                                        SendUserListAll();
                                        user.WriteOneObject(error);
                                    }
                                }
                            }
                        }
                        WriteAllObject(cm);
                        ois.close();
                        oos.close();
                        client_socket.close();
                        Logout();
                        break;
                    } catch (Exception ee) {
                        break;
                    }
                }
            }
        }
    }

    public class GameRoom {
        public String RoomName;
        Vector<UserService> InRoomUser;
        int count = 0;

        GameRoom() {
            InRoomUser = new Vector<>();
        }


        void setRoomName(String username) {
            this.RoomName = username;
        }

        String getRoomName() {
            return this.RoomName;
        }
    }
}