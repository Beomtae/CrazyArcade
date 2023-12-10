package Client;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import javax.swing.JOptionPane;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class WaitingFrame extends JFrame implements MouseListener {

    private static final long serialVersionUID = 1L;
    int width = 800, height = 800;

    private JLabel contentPane;
    private JTextField txtInput;
    public String UserName;
    private JButton btnSend;

    private Socket socket;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;

    Socket getSocket() {
        return this.socket;
    }

    ObjectInputStream getOis() {
        return this.ois;
    }

    ObjectOutputStream getOos() {
        return this.oos;
    }

    private JLabel lblUserName;
    private JTextPane textArea;
    private JTextPane textArea2;

    private JButton btnRoom;
    private JButton btnExit;

    private JScrollPane scrollPane2;
    private DefaultListModel<String> demoList = new DefaultListModel<String>();
    private JList<String> roomJList = new JList<String>(demoList);

    private String ip_addr;
    private String port_no;

    static WaitingFrame wf;
    private RoomFrame myRoom;
    static GameStart gs;

    ChatMsg movingInfo;


    public WaitingFrame(String username, String ip_addr, String port_no) {
        WaitingFrame wf = this;
        this.ip_addr = ip_addr;
        this.port_no = port_no;

        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(0, 0, width, height);
        contentPane = new JLabel(new ImageIcon("images/배경화면.png"));
        setContentPane(contentPane);
        contentPane.setLayout(null);


        roomJList.setModel(demoList);
        roomJList.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        roomJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        roomJList.addMouseListener(this);

        scrollPane2 = new JScrollPane(roomJList);
        scrollPane2.setBounds(25, 45, 350, 280);
        contentPane.add(scrollPane2);

        btnRoom = new JButton("방 생성");
        btnRoom.setBackground(new Color(255, 153, 000));
        btnRoom.setForeground(Color.white);
        btnRoom.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        btnRoom.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String roomName = String.format("%s의 방", UserName);
                ChatMsg msg = new ChatMsg(UserName, "500", roomName);
                SendObject(msg);
                myRoom = new RoomFrame(username, 1, ip_addr, port_no, wf);
                myRoom.setRoomName(roomName);
                setVisible(false); // WaitingFrame
            }
        });
        btnRoom.setBounds(25, 360, 160, 40);
        contentPane.add(btnRoom);

        btnExit = new JButton("종료");
        btnExit.setBackground(new Color(255, 153, 000));
        btnExit.setForeground(Color.white);
        btnExit.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        btnExit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ChatMsg msg = new ChatMsg(UserName, "400", "Bye");
                SendObject(msg);
                System.exit(0);
            }
        });
        btnExit.setBounds(215, 360, 160, 40);
        contentPane.add(btnExit);

        JScrollPane scrollPane3 = new JScrollPane();
        scrollPane3.setBounds(25, 455, 350, 285);
        contentPane.add(scrollPane3);

        textArea2 = new JTextPane();
        textArea2.insertIcon(new ImageIcon("images/배경화면"));
        textArea2.setEditable(true);
        textArea2.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        scrollPane3.setViewportView(textArea2);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(405, 45, 350, 650);
        contentPane.add(scrollPane);

        textArea = new JTextPane();
        textArea.setEditable(true);
        textArea.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        scrollPane.setViewportView(textArea);

        txtInput = new JTextField();
        txtInput.setBounds(470, 705, 210, 40);
        contentPane.add(txtInput);
        txtInput.setColumns(10);

        btnSend = new JButton("보내기");
        btnSend.setBackground(new Color(255, 153, 000));
        btnSend.setForeground(Color.white);
        btnSend.setFont(new Font("맑은 고딕", Font.BOLD, 12));
        btnSend.setBounds(685, 705, 70, 40);
        contentPane.add(btnSend);

        lblUserName = new JLabel("Name");
        lblUserName.setOpaque(true);
        lblUserName.setForeground(Color.WHITE);
        lblUserName.setBackground(new Color(255, 153, 000));
        lblUserName.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        lblUserName.setHorizontalAlignment(SwingConstants.CENTER);
        lblUserName.setBounds(405, 705, 60, 40);
        contentPane.add(lblUserName);

        AppendText("User " + username + " connecting " + ip_addr + " " + port_no);
        UserName = username;
        lblUserName.setText(username);
        setVisible(true);

        try {
            socket = new Socket(ip_addr, Integer.parseInt(port_no));

            oos = new ObjectOutputStream(socket.getOutputStream());
            oos.flush();
            ois = new ObjectInputStream(socket.getInputStream());

            ChatMsg obcm = new ChatMsg(UserName, "100", "Hello");
            SendObject(obcm);

            new ListenNetwork(wf).start();

            TextSendAction action = new TextSendAction();
            btnSend.addActionListener(action);
            txtInput.addActionListener(action);
            txtInput.requestFocus();

        } catch (NumberFormatException | IOException e) {
            e.printStackTrace();
            AppendText("connect error");
        }

    }

    public void mouseClicked(MouseEvent click) {
        if (click.getClickCount() == 2) {
            String name = (String) roomJList.getSelectedValue();
            ChatMsg obcm = new ChatMsg(UserName, "501", name);
            SendObject(obcm);
        }
    }



    public void AppendText(String msg) {
        msg = msg.trim();
        StyledDocument doc = textArea.getStyledDocument();
        SimpleAttributeSet left = new SimpleAttributeSet();
        StyleConstants.setAlignment(left, StyleConstants.ALIGN_LEFT);
        StyleConstants.setForeground(left, Color.BLACK);
        doc.setParagraphAttributes(doc.getLength(), 1, left, false);
        try {
            doc.insertString(doc.getLength(), msg + "\n", left);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        int len = textArea.getDocument().getLength();
        textArea.setCaretPosition(len);
    }

    public void AppendTextR(String msg) {
        msg = msg.trim();

        StyledDocument doc = textArea.getStyledDocument();
        SimpleAttributeSet right = new SimpleAttributeSet();
        StyleConstants.setAlignment(right, StyleConstants.ALIGN_RIGHT);
        StyleConstants.setForeground(right, Color.BLUE);

        doc.setParagraphAttributes(doc.getLength(), 1, right, false);
        try {
            doc.insertString(doc.getLength(), msg + "\n", right);
        } catch (BadLocationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        int len = textArea.getDocument().getLength();
        textArea.setCaretPosition(len);
    }

    public void AppendUser(String msg) {
        msg = msg.trim();

        textArea2.setText("");
        StyledDocument doc = textArea2.getStyledDocument();
        SimpleAttributeSet right = new SimpleAttributeSet();
        try {
            doc.insertString(doc.getLength(), msg, right);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        int len = textArea2.getDocument().getLength();
        textArea2.setCaretPosition(len);
    }

    class ListenNetwork extends Thread {
        private WaitingFrame wf;

        public ListenNetwork(WaitingFrame wf) {
            this.wf = wf;
        }

        public void run() {
            while (true) {
                try {
                    Object obcm = null;
                    String msg = null;
                    ChatMsg cm;
                    try {
                        obcm = ois.readObject();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                        break;
                    }
                    if (obcm == null) {
                        break;
                    }

                    if (obcm instanceof ChatMsg) {
                        cm = (ChatMsg) obcm;
                        msg = String.format("[%s]\n%s", cm.UserName, cm.data);
                    } else
                        continue;
                    switch (cm.code) {
                        case "200":
                            if (cm.UserName.equals(UserName))
                                AppendTextR(msg);
                            else
                                AppendText(msg);
                            break;
                        case "300":
                            AppendUser(cm.data);
                            break;
                        case "400":
                            JOptionPane.showMessageDialog(contentPane, "상대방이 나갔습니다.");
                            myRoom.setVisible(false);
                            wf.setVisible(true);
                            break;
                        case "503":
                            msg = String.format("[%s] %s", cm.UserName, cm.data);
                            myRoom.AppendText2(msg);
                            break;
                        case "504":
                            demoList.clear();
                            String[] RoomNameList = cm.dm.split(("\\$"));
                            for (String RoomName : RoomNameList) {
                                demoList.addElement(RoomName);
                            }
                            break;
                        case "505":
                            JOptionPane.showMessageDialog(contentPane, cm.data);
                            break;
                        case "506":
                            RoomFrame.status = 1;

                            myRoom.player2name = new JLabel(cm.UserName);
                            myRoom.player2name.setForeground(Color.white);
                            myRoom.player2name.setHorizontalAlignment(JLabel.CENTER);
                            myRoom.player2name.setBounds(242, 110, 48, 30);
                            myRoom.contentPane2.add(myRoom.player2name);

                            myRoom.player2 = new JLabel(new ImageIcon("images/woonie_front.png"));
                            myRoom.player2.setBounds(242, 50, 48, 56);
                            myRoom.contentPane2.add(myRoom.player2);
                            myRoom.repaint();
                            myRoom.AppendText2("상대가 입장하였습니다.");
                            break;
                        case "507":
                            setVisible(false);
                            myRoom = new RoomFrame(UserName, 2, ip_addr, port_no, wf);

                            myRoom.player2name = new JLabel(cm.data);
                            myRoom.player2name.setForeground(Color.white);
                            myRoom.player2name.setHorizontalAlignment(JLabel.CENTER);
                            String RoomName = String.format("%s의 방", cm.data);
                            myRoom.setRoomName(RoomName);
                            myRoom.player2name.setBounds(242, 110, 48, 30);
                            myRoom.contentPane2.add(myRoom.player2name);

                            myRoom.player2 = new JLabel(new ImageIcon("images/woonie_front.png"));
                            myRoom.player2.setBounds(242, 50, 48, 56);
                            myRoom.contentPane2.add(myRoom.player2);
                            myRoom.repaint();
                            myRoom.AppendText2("방에 입장하였습니다.");
                            break;
                        case "508":
                            msg = String.format("%s", cm.data);
                            myRoom.AppendText2(msg);
                            break;
                        case "509":
                            myRoom.setVisible(false);
                            wf.setVisible(true);
                            break;
                        case "600":
                            myRoom.setVisible(false);
                            gs = new GameStart(wf, UserName, myRoom);
                            break;
                    }
                } catch (IOException e) {
                    AppendText("ois.readObject() error");
                    try {
                        ois.close();
                        oos.close();
                        socket.close();
                        break;
                    } catch (Exception ee) {
                        break;
                    }
                }
            }
        }
    }

    class TextSendAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == btnSend || e.getSource() == txtInput) {
                String msg = null;
                msg = txtInput.getText();
                SendMessage(msg);
                txtInput.setText("");
                txtInput.requestFocus();
                if (msg.contains("/exit"))
                    System.exit(0);
            }

        }
    }

    public void SendMessage(String msg) {
        try {
            ChatMsg obcm = new ChatMsg(UserName, "200", msg);
            oos.writeObject(obcm);
        } catch (IOException e) {
            AppendText("oos.writeObject() error");
            try {
                ois.close();
                oos.close();
                socket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
                System.exit(0);
            }
        }
    }

    public void SendObject(ChatMsg ob) {
        try {
            oos.writeObject(ob);

        } catch (IOException e) {
            AppendText("SendObject Error");
        }
    }

    public void SendMessage2(String msg) {
        try {
            ChatMsg obcm = new ChatMsg(UserName, "503", msg);
            oos.writeObject(obcm);
        } catch (IOException e) {
            myRoom.AppendText2("oos.writeObject() error");
            try {
                ois.close();
                oos.close();
                socket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
                System.exit(0);
            }
        }
    }

    public void SendMessage3(String msg) {
        try {

            ChatMsg obcm = new ChatMsg(UserName, "508", msg);
            oos.writeObject(obcm);
        } catch (IOException e) {
            myRoom.AppendText2("oos.writeObject() error");
            try {
                ois.close();
                oos.close();
                socket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
                System.exit(0);
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub

    }
}