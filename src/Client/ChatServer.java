package Client;

import javaProj.GameStart;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class ChatServer extends JFrame {
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    JTextArea textArea;
    private JTextField txtPortNumber;

    private boolean player1Ready = false;
    private boolean player2Ready = false;

    private ServerSocket socket; // 서버소켓
    private Socket client_socket; // accept()에서 생성된 client 소켓
    private Vector<UserService> UserVec = new Vector<>(); // 연결된 사용자를 저장할 벡터
    private static final int BUF_LEN = 128; // Windows 처럼 BUF_LEN을 정의

    public static void main(String[] args) {

        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    ChatServer frame = new ChatServer();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public ChatServer() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 338, 386);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(12, 10, 300, 244);
        contentPane.add(scrollPane);

        textArea = new JTextArea();
        textArea.setEditable(false);
        scrollPane.setViewportView(textArea);

        JLabel lblNewLabel = new JLabel("Port Number");
        lblNewLabel.setBounds(12, 264, 87, 26);
        contentPane.add(lblNewLabel);

        txtPortNumber = new JTextField();
        txtPortNumber.setHorizontalAlignment(SwingConstants.CENTER);
        txtPortNumber.setText("30000");
        txtPortNumber.setBounds(111, 264, 199, 26);
        contentPane.add(txtPortNumber);
        txtPortNumber.setColumns(10);

        JButton btnServerStart = new JButton("Server Start");
        btnServerStart.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    socket = new ServerSocket(Integer.parseInt(txtPortNumber.getText()));
                } catch (NumberFormatException | IOException e1) {
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
        btnServerStart.setBounds(12, 300, 300, 35);
        contentPane.add(btnServerStart);
    }

    public synchronized void playerReady(String playerName) {
        if (playerName.equals("Player1")) {
            player1Ready = true;
            AppendText("Player1 is ready.");
        } else if (playerName.equals("Player2")) {
            player2Ready = true;
            AppendText("Player2 is ready.");
        }

        if (player1Ready && player2Ready) {
            AppendText("Both players are ready. Starting the game...");

            // 게임 시작 메시지를 두 플레이어에게 보내기
            for (UserService user : UserVec) {
                user.WriteOne("/startGame");
            }

            // 게임 시작 후 준비 상태 초기화
            player1Ready = false;
            player2Ready = false;
        }
    }



    class AcceptServer extends Thread {
        @SuppressWarnings("unchecked")
        public void run() {
            while (true) {
                try {
                    AppendText("Waiting clients ...");
                    client_socket = socket.accept();
                    AppendText("New participant from " + client_socket);
                    UserService new_user = new UserService(client_socket);
                    UserVec.add(new_user);
                    AppendText("User joined. Current participants: " + UserVec.size());
                    new_user.start();

                } catch (IOException e) {
                    AppendText("!!!! Accept error occurred... !!!!");
                }
            }
        }
    }

    public void AppendText(String str) {
        textArea.append(str + "\n");
        textArea.setCaretPosition(textArea.getText().length());
    }

    public void WriteAll(String str) {
        for (int i = 0; i < UserVec.size(); i++) {
            UserService user = UserVec.get(i);
            user.WriteOne(str);
        }
    }

    class UserService extends Thread {
        private InputStream is;
        private OutputStream os;
        private DataInputStream dis;
        private DataOutputStream dos;
        private Socket client_socket;
        private Vector<UserService> user_vc;
        private String UserName = "";

        public UserService(Socket client_socket) {
            this.client_socket = client_socket;
            this.user_vc = UserVec;
            try {
                is = client_socket.getInputStream();
                dis = new DataInputStream(is);
                os = client_socket.getOutputStream();
                dos = new DataOutputStream(os);
                String line1 = dis.readUTF();
                String[] msg = line1.split(" ");
                UserName = msg[1].trim();
                AppendText(UserName + " joined the chat.");
                WriteOne("Welcome to the chat, " + UserName + "!\n");
            } catch (Exception e) {
                AppendText("UserService error");
            }
        }

        public void run() {
            while (true) {
                try {
                    String msg = dis.readUTF().trim();
                    AppendText(msg);

                    if (msg.startsWith("/ready")) {
                        handlePlayerReady(msg);
                    } else {
                        WriteAll(msg + "\n");
                    }
            } catch (IOException e) {
                    // ... 이전 코드 ...
                }
            }
        }
        private void handlePlayerReady(String readyMessage) {
            String[] tokens = readyMessage.split(" ");
            if (tokens.length == 2) {
                String playerName = tokens[1];

                // 사용자의 이름을 동적으로 가져오기
                String currentUserName = getUserName();
                System.out.print(currentUserName);

                if (playerName.equals(currentUserName)) {
                    setPlayerReadyStatus(true);

                    if (areAllPlayersReady()) {
                        AppendText("Both players are ready. Starting the game...");

                        // 게임 시작 화면으로 전환 또는 다른 처리를 수행할 부분
                        // 예시: GameStart 클래스의 인스턴스를 생성하여 보여주고 현재 프레임을 숨김
                        GameStart gameStart = new GameStart();
                        gameStart.setVisible(true);

                        // 게임 시작 후 준비 상태 초기화
                        setPlayerReadyStatus(false);
                    }
                }
            }
        }

        private String getUserName() {
            return UserName;
        }

        private void setPlayerReadyStatus(boolean status) {
            // 플레이어의 준비 상태를 동적으로 설정하는 코드
            if (player1Ready==false){
                player1Ready = status;
            }
            else player2Ready = status;

        }

        private boolean areAllPlayersReady() {
            // 모든 플레이어가 준비되었는지 여부를 확인하는 코드
            return player1Ready && player2Ready;
        }

        public void WriteOne(String msg) {
            try {
                dos.writeUTF(msg);
            } catch (IOException e) {
                AppendText("dos.write() error");
                try {
                    dos.close();
                    dis.close();
                    client_socket.close();
                    UserVec.removeElement(this);
                    AppendText(UserName + " left the chat. Remaining participants: " + UserVec.size());
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }
}
