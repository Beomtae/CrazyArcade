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

    /**
     * Launch the application.
     */
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

    // 수정된 부분: playerReady 메서드 추가

    public synchronized void playerReady(String playerName) {
        if (playerName.equals("Player1")) {
            player1Ready = true;
            System.out.println("Player1 is ready.");
        } else if (playerName.equals("Player2")) {
            player2Ready = true;
            System.out.println("Player2 is ready.");
        }

        if (player1Ready && player2Ready) {
            startGame();
        }
    }


    // 수정된 부분: startGame 메서드 추가
    private void startGame() {
        WriteAll("Both players are ready. Starting the game..");

        // 게임 시작 후 초기화 등 필요한 작업 수행
        GameStart gameStart = new GameStart();
        gameStart.setVisible(true);
        dispose();

        // 게임 시작 여부 초기화
        player1Ready = false;
        player2Ready = false;
    }

    // 사용자가 참여하는 서버 스레드
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

    // JTextArea에 문자열을 출력하는 메소드
    public void AppendText(String str) {
        textArea.append(str + "\n");
        textArea.setCaretPosition(textArea.getText().length());
    }

    // 전체 사용자에게 메시지를 전송하는 메소드
    public void WriteAll(String str) {
        for (int i = 0; i < UserVec.size(); i++) {
            UserService user = UserVec.get(i);
            user.WriteOne(str);
        }
    }

    // 사용자당 생성되는 스레드
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
                AppendText(UserName + " 님이 입장하였습니다.");
                WriteOne("크레이지 아케이드 대기방입니다.\n");
                WriteOne(UserName + ", 환엽합니다!\n");
            } catch (Exception e) {
                AppendText("UserService error");
            }
        }

        // 수정된 부분: 게임 시작 메시지를 받았을 때 처리 추가
        public void run() {
            while (true) {
                try {
                    String msg = dis.readUTF().trim();
                    AppendText(msg);

                    // 수정된 부분: 게임 시작 요청을 받았을 때 playerReady 메서드 호출
                    if (msg.equals("/startGame")) {
                        playerReady(UserName);
                    } else {
                        // 게임 시작이 아닌 경우 메시지를 모든 클라이언트에게 전송
                        WriteAll(msg + "\n");
                    }
                } catch (IOException e) {
                    AppendText("dis.readUTF() error");
                    try {
                        dos.close();
                        dis.close();
                        client_socket.close();
                        UserVec.removeElement(this);
                        AppendText("User left. Remaining participants: " + UserVec.size());
                        break;
                    } catch (Exception ee) {
                        break;
                    }
                }
            }
        }

        // 사용자에게 메시지를 전송하는 메소드
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
                    AppendText("User left. Remaining participants: " + UserVec.size());
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }
}
