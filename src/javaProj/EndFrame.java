package javaProj;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

class EndFrame extends JFrame {
	int p;
	public EndFrame(int p) {
		this.p=p;
		setTitle("ending");
		setSize(800, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		Container c = getContentPane();
		c.add(new MyPanel());
		requestFocus();
		setVisible(true);
	}

	public class MyPanel extends JPanel {
		private JLabel exitcontentPane;
		private JButton exitB;

		public MyPanel() {
			if (p==1)
				exitcontentPane = new JLabel(new ImageIcon("images/우니win.png"));
			else if (p==2)
				exitcontentPane = new JLabel(new ImageIcon("images/배찌win.png"));

			setTitle("CrazyArcade");
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 프레임 윈도우 닫으면 종료
			setContentPane(exitcontentPane);
			exitcontentPane.setLayout(null);
			setResizable(false); // 게임 창의 크기 변경 불가

			// 게임 종료 버튼
			exitB = new JButton(new ImageIcon("images/종료.png"));
			exitB.setBounds(340, 500, 100, 40);
			exitcontentPane.add(exitB);

			setVisible(true); // 보여라
			Myaction action = new Myaction();
			exitB.addActionListener(action);
		}

		class Myaction implements ActionListener // 내부클래스로 액션 이벤트 처리 클래스
		{
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(DISPOSE_ON_CLOSE);

				dispose();
			}
		}
	}
//   public static void main(String[] args) {
//      new LoginFrame();
//   }
}
