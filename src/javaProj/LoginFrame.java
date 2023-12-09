package javaProj;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

class LoginFrame extends JFrame {
	public LoginFrame() {
		setTitle("CrazyArcade");
		setSize(800, 800);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		Container c = getContentPane();
		c.add(new MyPanel());
		requestFocus();
		setVisible(true);
		new Sound(1);
	}

	public class MyPanel extends JPanel {
		private JLabel contentPane;
		private JButton startB;

		public MyPanel() {
			contentPane = new JLabel(new ImageIcon("images/로그인화면.jpg"));
			setTitle("CrazyArcade");
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 프레임 윈도우 닫으면 종료
			setContentPane(contentPane);
			contentPane.setLayout(null);
			setResizable(false); // 게임 창의 크기 변경 불가

			// 게임 시작 버튼
			startB = new JButton(new ImageIcon("images/start.png"));
			startB.setBounds(310, 550, 180, 50);
			contentPane.add(startB);

			setVisible(true); // 보여라
			Myaction action = new Myaction();
			startB.addActionListener(action);
		}

		class Myaction implements ActionListener // 내부클래스로 액션 이벤트 처리 클래스
		{
			@Override
			public void actionPerformed(ActionEvent e) {
				
				new Sound(2);
				new GameStart();
				dispose();
			}
		}
	}

	public static void main(String[] args) {
		new LoginFrame();
	}
}
