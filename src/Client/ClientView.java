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
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class ClientView extends JFrame {
    private JPanel contentPane;
    private JTextField txtInput;
    private String UserName;
    private JButton btnSend;
    private JButton btnGameReady;
    private JTextArea textArea;

    private JLabel lblStatus;
    private boolean playerReady = false;
    private static final int BUF_LEN = 128;
    private Socket socket;
    private InputStream is;
    private OutputStream os;
    private DataInputStream dis;
    private DataOutputStream dos;
    private JLabel lblUserName;

    public ClientView(String username, String ip_addr, String port_no) {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 392, 500);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(12, 10, 352, 340);
        contentPane.add(scrollPane);

        textArea = new JTextArea();
        textArea.setEditable(false);
        scrollPane.setViewportView(textArea);

        txtInput = new JTextField();
        txtInput.setBounds(91, 365, 185, 40);
        contentPane.add(txtInput);
        txtInput.setColumns(10);

        btnSend = new JButton("Send");
        btnSend.setBounds(288, 364, 76, 40);
        contentPane.add(btnSend);

        btnGameReady = new JButton("게임 준비");
        btnGameReady.setBounds(12, 415, 352, 40);
        contentPane.add(btnGameReady);
        btnGameReady.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showWaitingMessage();
                SendMessage("/ready " + UserName);
            }
        });


        lblUserName = new JLabel("Name");
        lblUserName.setHorizontalAlignment(SwingConstants.CENTER);
        lblUserName.setBounds(12, 364, 67, 40);
        contentPane.add(lblUserName);
        setVisible(true);
        lblStatus = new JLabel("");
        lblStatus.setHorizontalAlignment(SwingConstants.CENTER);
        lblStatus.setBounds(12, 350, 352, 20);
        contentPane.add(lblStatus);

        AppendText("User " + username + " connecting " + ip_addr + " " + port_no + "\n");
        UserName = username;
        lblUserName.setText(username + ">");

        try {
            socket = new Socket(ip_addr, Integer.parseInt(port_no));
            is = socket.getInputStream();
            dis = new DataInputStream(is);
            os = socket.getOutputStream();
            dos = new DataOutputStream(os);

            SendMessage("/login " + UserName);
            ListenNetwork net = new ListenNetwork();
            net.start();
            Myaction action = new Myaction();
            btnSend.addActionListener(action);
            txtInput.addActionListener(action);
            txtInput.requestFocus();
        } catch (NumberFormatException | IOException e) {
            e.printStackTrace();
            AppendText("connect error");
        }
    }

    class ListenNetwork extends Thread {
        public void run() {
            while (true) {
                try {
                    String msg = dis.readUTF();
                    if (msg.equals("/startGame")) {
                        // 게임 시작 메시지를 받으면 게임 화면으로 전환
                        showGameStartScreen();
                    } else if (msg.endsWith(" is ready")) {
                        // 사용자가 준비 완료 메시지를 받으면 어떤 사용자가 준비 완료했는지 표시
                        AppendText(msg);
                    } else {
                        AppendText(msg);
                    }
                } catch (IOException e) {
                    AppendText("dis.read() error");
                    try {
                        dos.close();
                        dis.close();
                        socket.close();
                        break;
                    } catch (Exception ee) {
                        break;
                    }
                }
            }
        }
    }



    class Myaction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == btnSend || e.getSource() == txtInput) {
                String msg = String.format("[%s] %s\n", UserName, txtInput.getText());
                SendMessage(msg);
                txtInput.setText("");
                txtInput.requestFocus();
                if (msg.contains("/exit"))
                    System.exit(0);
            }
        }
    }

    public void AppendText(String msg) {
        textArea.append(msg);
        textArea.setCaretPosition(textArea.getText().length());
    }

    public void SendMessage(String msg) {
        try {
            dos.writeUTF(msg);
        } catch (IOException e) {
            AppendText("dos.write() error");
            try {
                dos.close();
                dis.close();
                socket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
                System.exit(0);
            }
        }
    }

    private void showGameStartScreen() {
        GameStart gameStartView = new GameStart();
        gameStartView.setVisible(true);
        setVisible(false);
    }

    private void showWaitingMessage() {
        lblStatus.setText("상대방을 기다리는 중입니다. 잠시만 기다려주세요.");
    }
}
