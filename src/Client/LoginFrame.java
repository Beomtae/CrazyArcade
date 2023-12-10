package Client;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class LoginFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    private JLabel contentPane;
    private JTextField txtUserName;
    private JTextField txtIpAddress;
    private JTextField txtPortNumber;
    private JButton startB;
    private JLabel nameL, ipL, portL;

    int width = 800, height = 800;

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    LoginFrame frame = new LoginFrame();
                    frame.setVisible(true);
                    frame.txtUserName.requestFocus();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public LoginFrame() {
        contentPane = new JLabel(new ImageIcon("images/로그인화면.jpg"));

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(0, 0, width, height);
        setContentPane(contentPane);
        contentPane.setLayout(null);
        setResizable(false);

        ipL = new JLabel("IP Address:");
        ipL.setBounds(10, 10, 100, 30);
        contentPane.add(ipL);
        txtIpAddress = new JTextField();
        txtIpAddress.setText("127.0.0.1");
        txtIpAddress.setHorizontalAlignment(SwingConstants.CENTER);
        txtIpAddress.setBounds(100, 10, 100, 30);
        contentPane.add(txtIpAddress);
        txtIpAddress.setColumns(10);

        portL = new JLabel("Port Number:");
        portL.setBounds(10, 50, 100, 30);
        contentPane.add(portL);
        txtPortNumber = new JTextField();
        txtPortNumber.setText("30000");
        txtPortNumber.setHorizontalAlignment(SwingConstants.CENTER);
        txtPortNumber.setBounds(100, 50, 100, 30);
        contentPane.add(txtPortNumber);
        txtPortNumber.setColumns(10);


        nameL = new JLabel(new ImageIcon("images/id.png"));
        nameL.setBounds(300, 500, 70, 30);
        nameL.setOpaque(true);
        nameL.setBackground(Color.blue);
        contentPane.add(nameL);
        txtUserName = new JTextField();
        txtUserName.setHorizontalAlignment(SwingConstants.CENTER);
        txtUserName.setBounds(380, 500, 120, 30);
        contentPane.add(txtUserName);
        txtUserName.setColumns(10);


        startB = new JButton(new ImageIcon("images/start.png"));
        startB.setBounds(310, 550, 180, 50);
        contentPane.add(startB);

        setVisible(true);
        Myaction action = new Myaction();
        startB.addActionListener(action);
        txtUserName.addActionListener(action);
        txtIpAddress.addActionListener(action);
        txtPortNumber.addActionListener(action);

    }

    class Myaction implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = txtUserName.getText().trim();
            String ip_addr = txtIpAddress.getText().trim();
            String port_no = txtPortNumber.getText().trim();
            new WaitingFrame(username, ip_addr, port_no);
            setVisible(false);
        }
    }
}