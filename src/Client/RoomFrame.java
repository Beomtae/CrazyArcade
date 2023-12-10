package Client;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.border.Border;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

class RoomFrame extends JFrame {

    private static final long serialVersionUID = 1L;
    public JLabel contentPane2;
    private JTextField txtInput2;
    private String UserName;
    public JButton startGame;
    public JButton exitRoom;
    private JButton btnSend2;
    public JLabel waitGame;
    private JTextPane chatArea;

    public JLabel player1;
    public JLabel player2;
    public JLabel player1name;
    public JLabel player2name;

    private String RoomName;

    static WaitingFrame wf;
    static RoomFrame rf;

    int master;

    static int status = 0;

    void setRoomName(String RoomName) {
        this.RoomName = RoomName;
    }

    String getRoomName() {
        return this.RoomName;
    }

    public RoomFrame(String username, int master, String ip_addr, String port_no, WaitingFrame wf) {
        RoomFrame.wf = wf;
        this.master = master;
        this.UserName = username;
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(0, 0, 400, 400);
        contentPane2 = new JLabel(new ImageIcon("images/배경화면2.png"));
        setContentPane(contentPane2);
        contentPane2.setLayout(null);

        chatArea = new JTextPane();
        chatArea.setEditable(true);
        chatArea.setFont(new Font("굴림", Font.PLAIN, 12));

        player1 = new JLabel(new ImageIcon("images/bazzi_front.png"));
        player1.setBounds(110, 50, 48, 56);
        contentPane2.add(player1);

        player2 = new JLabel(new ImageIcon("images/woonie_front.png"));
        player2.setBounds(242, 50, 48, 56);
        setVisible(true);

        if (master == 1) {
            player1name = new JLabel(UserName);
            player1name.setForeground(Color.white);
            player1name.setHorizontalAlignment(JLabel.CENTER);
            player1name.setBounds(110, 110, 48, 30);
            contentPane2.add(player1name);

            startGame = new JButton(new ImageIcon("images/start.png"));
            startGame.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (status == 0)
                        wf.SendMessage3("상대가 없습니다..");
                    else if (status == 1) {
                        wf.SendMessage3("게임시작 !!");
                        ChatMsg start = new ChatMsg("rf.getRoomName()", "600", "game start");
                        wf.SendObject(start);
                    }
                }
            });
            startGame.setBounds(110, 150, 180, 50);
            contentPane2.add(startGame);

            exitRoom = new JButton("test");
            exitRoom.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    ChatMsg out = new ChatMsg(UserName,"510", getRoomName());
                    wf.SendObject(out);
                }
            });
            exitRoom.setBounds(320, 150, 50, 50);
            contentPane2.add(exitRoom);

        } else if (master == 2) {
            waitGame = new JLabel("대기중 입니다..");
            waitGame.setFont(new Font("맑은 고딕", Font.BOLD, 14));
            waitGame.setHorizontalAlignment(JLabel.CENTER);
            waitGame.setOpaque(true);
            waitGame.setBackground(new Color(255,153,000));
            waitGame.setForeground(Color.white);
            Border border = BorderFactory.createLineBorder(Color.BLACK, 1);
            waitGame.setBorder(border);
            waitGame.setBounds(110, 150, 180, 50);
            contentPane2.add(waitGame);


            player1name = new JLabel(UserName);
            player1name.setForeground(Color.white);
            player1name.setHorizontalAlignment(JLabel.CENTER);
            player1name.setBounds(110, 110, 48, 30);
            contentPane2.add(player1name);
        }


        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(44, 210, 300, 110);
        contentPane2.add(scrollPane);
        scrollPane.setViewportView(chatArea);


        txtInput2 = new JTextField();
        txtInput2.setBounds(44, 325, 230, 35);
        contentPane2.add(txtInput2);
        txtInput2.setColumns(10);


        btnSend2 = new JButton("보내기");
        btnSend2.setBackground(new Color(255,153,000));
        btnSend2.setForeground(Color.white);
        btnSend2.setFont(new Font("맑은 고딕", Font.BOLD, 9));
        btnSend2.setBounds(280, 325, 64, 35);
        contentPane2.add(btnSend2);

        try {
            TextSendActionInRoom action = new TextSendActionInRoom();
            btnSend2.addActionListener(action);
            txtInput2.addActionListener(action);
            txtInput2.requestFocus();
        } catch (NumberFormatException e) {
            e.printStackTrace();
            wf.AppendText("connect error");
        }
    }


    class TextSendActionInRoom implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {

            if (e.getSource() == btnSend2 || e.getSource() == txtInput2) {
                String msg = null;
                msg = txtInput2.getText();
                wf.SendMessage2(msg);
                txtInput2.setText("");
                txtInput2.requestFocus();
                if (msg.contains("/exit"))
                    System.exit(0);
            }
        }
    }

    public void AppendText2(String msg) {
        StyledDocument doc = chatArea.getStyledDocument();
        SimpleAttributeSet left = new SimpleAttributeSet();
        StyleConstants.setAlignment(left, StyleConstants.ALIGN_LEFT);
        StyleConstants.setForeground(left, Color.BLACK);

        doc.setParagraphAttributes(doc.getLength(), 1, left, false);
        try {
            doc.insertString(doc.getLength(), msg+"\n", left );
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        int len = chatArea.getDocument().getLength();
        chatArea.setCaretPosition(len);
    }
}