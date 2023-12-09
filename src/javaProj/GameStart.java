package javaProj;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Vector;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;



public class GameStart extends JFrame implements KeyListener, MouseListener, Runnable {

	Thread thread;

	// keyboard 제어를 위한 변수
	boolean keyU = false;
	boolean keyD = false;
	boolean keyL = false;
	boolean keyR = false;

	boolean keyUU = false;
	boolean keyDD = false;
	boolean keyLL = false;
	boolean keyRR = false;

	// 상태변수
	boolean die = false;
	int bombAvailable1 = 1; // 초기 최대 물풍선 개수는 1
	int bombAvailable2 = 1;
	final int maxBomb = 3;
	// boolean isBombExplode = false;
	private int speed = 10; // 80이 기본, 40이면 빠름, 테스트용이라 30고정
	private int MaxSpeed = 40;

	// 각각 스피드 조절을 위해 변수 선언. keyprocess에서 move 값 변경을 위함
	private int speed1 = 1; // 기본1
	private int speed2 = 1;
	private int maxMoveSpeed = 4; // 최대 3

	// 초기 폭발 범위
	private int bombPower1 = 1;
	private int bombPower2 = 1;
	private int maxBombPower = 5;

	// myMove, yourMove에 쓸 StringData
	final String UP = "up";
	final String DOWN = "down";
	final String RIGHT = "right";
	final String LEFT = "left";

	// 초기화시 캐릭터는 앞을 쳐다보고 있음
	String myMove = DOWN;
	String yourMove = DOWN;

	// 좌표 변수들
	int myX = 500, myY = 500;
	int yourX = 100, yourY = 100;
	int bombX, bombY;
	int bx, by;

	// map state
	final String BROWNBLOCK = "BROWNBLOCK";
	final String PINKBLOCK = "PINKBLOCK";
	// final String ITEM = "ITEM";
	final String FREE = "FREE";
	final String BOMB = "BOMB";
	final String BUP = "BUP";
	final String BDOWN = "BDOWN";
	final String BLEFT = "BLEFT";
	final String BRIGHT = "BRIGHT";
	final String BCENTER = "BCENTER";
	final String ITEMSPEED = "ITEMSPEED"; // 속력을 빠르게
	final String ITEMSTRONGBOMB = "ITEMSTRONGBOMB"; // 물풍선 길이를 길게
	final String ITEMPLUSBOMB = "ITEMPLUSBOMB"; // 물풍선 개수 증가
	final String ITEMPLUSPOWER = "ITEMPLUSPOWER";

	// 8개중 3개만아이템 (나올확률 1/8)
	private String[] itemArray = { ITEMPLUSBOMB, ITEMPLUSPOWER, ITEMSPEED, FREE, FREE, FREE, FREE, FREE };

	// 아이템 변수
//	   private Vector<JLabel> item = new Vector<JLabel>();
//	   private ArrayList<JLabel> itemlist = new ArrayList<JLabel>();	   
//	   private ImageIcon[] item2 = { new ImageIcon("images/speed.png"),null,null, null };
	ImageIcon item3;
	JLabel itemLabel;

	private JLabel contentPane;

	GameScreen gamescreen; // Canvas객체

	int gScreenWidth = 615;// 게임 화면 너비
	int gScreenHeight = 645;// 게임 화면 높이
	Image mapBG = new ImageIcon("Images/mapbg1.png").getImage(); // 게임1배경

	boolean roof = true;// 스레드 루프 정보
	public MapInfo mapInfo;
	public Main main;
	Random random = new Random();

	///////////////////////////////////////////////////////////////
	// 생성자 //
	///////////////////////////////////////////////////////////////
	public GameStart() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, gScreenWidth, gScreenHeight);
		setLocationRelativeTo(null);
		// contentPane = new JLabel(new ImageIcon("Images/mapbg1.png"));
		contentPane = new JLabel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);

		setContentPane(contentPane);
		setResizable(true); // 윈도우 크기 변경
		setVisible(true);
		addKeyListener(this);// 키 입력 이벤트 리스너 활성화

		gamescreen = new GameScreen(this);// 화면 묘화를 위한 캔버스 객체
		gamescreen.setBounds(0, 0, gScreenWidth, gScreenHeight);
		add(gamescreen);// Canvas 객체를 프레임에 올린다

		this.requestFocus();

		mapInfo = new MapInfo(15);
		mapSetting(mapInfo);
		initialize();

		thread = new Thread(this);
		thread.start();

		printMap(); // 디버깅용

	}// End of GameStart(생성자)

//////////////////////디버깅용///////////////////////////////   
	public void printMap() {
		for (int i = 0; i < 15; i++) {
			for (int j = 0; j < 15; j++) {
				// 좌표와 상태출력
				// System.out.printf("(%3d,%3d) %10s\t",
				// mapInfo.map[i][j].x,mapInfo.map[i][j].y,mapInfo.map[i][j].state);

				// 상태만출력
				System.out.printf("%10s\t", mapInfo.map[i][j].state);

				// 벽여부만 출력
				// System.out.printf("%3s\t", mapInfo.map[i][j].wasWall);
			}
			System.out.println();
		}
		System.out.println();
		System.out.println();
	}
//////////////////////디버깅용///////////////////////////////	   

	///////////////////////////////////////////////////////////////
	// initializing Function //
	///////////////////////////////////////////////////////////////

	public void mapSetting(MapInfo mapInfo) { // 맵 정보 초기화
		// 가생이 갈색 블록 추가
		for (int j = 0; j < 15; j++) {
			for (int i = 0; i < 15; i++) {
				if (i == 0 || i == 14 || j == 0 || j == 14) {
					mapInfo.map[i][j].state = BROWNBLOCK;
					mapInfo.map[i][j].wasWall = true;
				} // End of if
			} // End of inner for
		} // End of outer for

		// 가운데 하트 핑크블록 추가
		mapInfo.map[1][3].state = PINKBLOCK;
		mapInfo.map[1][4].state = PINKBLOCK;
		mapInfo.map[1][5].state = PINKBLOCK;
		mapInfo.map[1][9].state = PINKBLOCK;
		mapInfo.map[1][10].state = PINKBLOCK;
		mapInfo.map[1][11].state = PINKBLOCK;
		mapInfo.map[2][2].state = PINKBLOCK;
		mapInfo.map[2][6].state = PINKBLOCK;
		mapInfo.map[2][8].state = PINKBLOCK;
		mapInfo.map[2][12].state = PINKBLOCK;
		mapInfo.map[3][1].state = PINKBLOCK;
		mapInfo.map[3][7].state = PINKBLOCK;
		mapInfo.map[3][13].state = PINKBLOCK;
		mapInfo.map[4][1].state = PINKBLOCK;
		mapInfo.map[4][7].state = PINKBLOCK;
		mapInfo.map[4][13].state = PINKBLOCK;
		mapInfo.map[5][1].state = PINKBLOCK;
		mapInfo.map[5][13].state = PINKBLOCK;
		mapInfo.map[6][1].state = PINKBLOCK;
		mapInfo.map[6][13].state = PINKBLOCK;
		mapInfo.map[7][1].state = PINKBLOCK;
		mapInfo.map[7][13].state = PINKBLOCK;
		mapInfo.map[8][2].state = PINKBLOCK;
		mapInfo.map[8][12].state = PINKBLOCK;
		mapInfo.map[9][3].state = PINKBLOCK;
		mapInfo.map[9][11].state = PINKBLOCK;
		mapInfo.map[10][4].state = PINKBLOCK;
		mapInfo.map[10][10].state = PINKBLOCK;
		mapInfo.map[11][5].state = PINKBLOCK;
		mapInfo.map[11][9].state = PINKBLOCK;
		mapInfo.map[12][6].state = PINKBLOCK;
		mapInfo.map[12][8].state = PINKBLOCK;
		mapInfo.map[13][7].state = PINKBLOCK;
		
		// 핑크하트도 모두 벽처리
		mapInfo.map[1][3].wasWall = true;
		mapInfo.map[1][4].wasWall = true;
		mapInfo.map[1][5].wasWall = true;
		mapInfo.map[1][9].wasWall = true;
		mapInfo.map[1][10].wasWall = true;
		mapInfo.map[1][11].wasWall = true;
		mapInfo.map[2][2].wasWall = true;
		mapInfo.map[2][6].wasWall = true;
		mapInfo.map[2][8].wasWall = true;
		mapInfo.map[2][12].wasWall = true;
		mapInfo.map[3][1].wasWall = true;
		mapInfo.map[3][7].wasWall = true;
		mapInfo.map[3][13].wasWall = true;
		mapInfo.map[4][1].wasWall = true;
		mapInfo.map[4][7].wasWall = true;
		mapInfo.map[4][13].wasWall = true;
		mapInfo.map[5][1].wasWall = true;
		mapInfo.map[5][13].wasWall = true;
		mapInfo.map[6][1].wasWall = true;
		mapInfo.map[6][13].wasWall = true;
		mapInfo.map[7][1].wasWall = true;
		mapInfo.map[7][13].wasWall = true;
		mapInfo.map[8][2].wasWall = true;
		mapInfo.map[8][12].wasWall = true;
		mapInfo.map[9][3].wasWall = true;
		mapInfo.map[9][11].wasWall = true;
		mapInfo.map[10][4].wasWall = true;
		mapInfo.map[10][10].wasWall = true;
		mapInfo.map[11][5].wasWall = true;
		mapInfo.map[11][9].wasWall = true;
		mapInfo.map[12][6].wasWall = true;
		mapInfo.map[12][8].wasWall = true;
		mapInfo.map[13][7].wasWall = true;
	}// End of mapSetting(MapInfo mapInfo)

	public void initialize() { // 게임 초기화
		// 초기 캐릭터는 앞을 보고있음.
		myMove = DOWN;
		yourMove = DOWN;
		gamescreen.repaint();
	}// End of initialize()

	///////////////////////////////////////////////////////////////
	// 기능 함수들 모음 //
	///////////////////////////////////////////////////////////////

	public void colliderControl(MapInfo mapInfo) { // 충돌처리
		
		int collX1, collY1, collX2, collY2;
		collX1 = myX / 40;	collY1 = myY / 40;
		collX2 = yourX / 40;	collY2 = yourY / 40;
		
		//배찌 X
		if(myX % 40 < 20)
			collX1 += 0;
		else 
			collX1 += 1;
		 //배찌 Y
		if(myY % 40 < 20)
			collY1 += 0;
		else 
			collY1 += 1;
		
		//우니 X
		if(yourX % 40 < 25)
			collX2 += 0;
		else
			collX2 += 1;
		//우니 Y
		if(yourY % 40 < 15)
			collY2 += 0;
		else
			collY2 += 1;
		
		// 배찌 폭탄맞음 (나누기 한 몫 안맞음. 맞춰야함) 배열 좌표와 myX,Y 값의 대조
		  if ((mapInfo.map[collY1][collX1].state == BCENTER) || 
			  (mapInfo.map[collY1][collX1].state == BUP) || 
			  (mapInfo.map[collY1][collX1].state == BDOWN) || 
			  (mapInfo.map[collY1][collX1].state == BRIGHT) ||
			  (mapInfo.map[collY1][collX1].state == BLEFT)) {
			  dispose();
			  new EndFrame(1);
		  }
			  //System.exit(0);
		  
		  // 우니 폭탄맞음 (나누기 한 몫 안맞음. 맞춰야함) 
		  if ((mapInfo.map[collY2][collX2].state == BCENTER) || 
			  (mapInfo.map[collY2][collX2].state == BUP) ||
			  (mapInfo.map[collY2][collX2].state == BDOWN) || 
			  (mapInfo.map[collY2][collX2].state == BRIGHT) || 
			  (mapInfo.map[collY2][collX2].state == BLEFT)) { 
			  dispose();
			  new EndFrame(2);
		  }
			  //System.exit(0);
		 

		for (int i = 0; i < mapInfo.size; i++) {
			for (int j = 0; j < mapInfo.size; j++) {
				switch (mapInfo.map[i][j].state) {
				case BROWNBLOCK, PINKBLOCK:
					// 배찌
					if (((myX >= mapInfo.map[i][j].x - 40) && (myX <= mapInfo.map[i][j].x))
							&& ((myY < mapInfo.map[i][j].y + 5) && (myY > mapInfo.map[i][j].y - 35))) {
						keyR = false;
						continue;
					} else if (((myX >= mapInfo.map[i][j].x) && (myX <= mapInfo.map[i][j].x + 40))
							&& ((myY < mapInfo.map[i][j].y + 5) && (myY > mapInfo.map[i][j].y - 35))) {
						keyL = false;
						continue;
					} else if (((myX > mapInfo.map[i][j].x - 10) && (myX < mapInfo.map[i][j].x + 30))
							&& ((myY > mapInfo.map[i][j].y) && (myY < mapInfo.map[i][j].y + 40))) {
						keyU = false;
						continue;
					} else if (((myX >= mapInfo.map[i][j].x - 10) && (myX <= mapInfo.map[i][j].x + 30))
							&& ((myY + 46 >= mapInfo.map[i][j].y - 10) && (myY + 46 < mapInfo.map[i][j].y + 40))) {
						keyD = false;
						continue;
					}

					// 우니
					if (((yourX >= mapInfo.map[i][j].x - 40) && (yourX <= mapInfo.map[i][j].x))
							&& ((yourY < mapInfo.map[i][j].y + 5) && (yourY > mapInfo.map[i][j].y - 35))) {
						keyRR = false;
						continue;
					} else if (((yourX >= mapInfo.map[i][j].x) && (yourX <= mapInfo.map[i][j].x + 40))
							&& ((yourY < mapInfo.map[i][j].y + 5) && (yourY > mapInfo.map[i][j].y - 35))) {
						keyLL = false;
						continue;
					} else if (((yourX > mapInfo.map[i][j].x - 10) && (yourX < mapInfo.map[i][j].x + 30))
							&& ((yourY > mapInfo.map[i][j].y) && (yourY < mapInfo.map[i][j].y + 40))) {
						keyUU = false;
						continue;
					} else if (((yourX >= mapInfo.map[i][j].x - 10) && (yourX <= mapInfo.map[i][j].x + 30))
							&& ((yourY + 46 >= mapInfo.map[i][j].y - 10) && (yourY + 46 < mapInfo.map[i][j].y + 40))) {
						keyDD = false;
						continue;
					}

					break;

				case ITEMSPEED:
					// 배찌
					if (((myX >= mapInfo.map[i][j].x - 40) && (myX <= mapInfo.map[i][j].x))
							&& ((myY < mapInfo.map[i][j].y + 5) && (myY > mapInfo.map[i][j].y - 35))) {
						mapInfo.map[i][j].state = FREE;
						// itemSpeedUp1();
						speed1 = itemSpeedUP(speed1);
						continue;
					} else if (((myX >= mapInfo.map[i][j].x) && (myX <= mapInfo.map[i][j].x + 40))
							&& ((myY < mapInfo.map[i][j].y + 5) && (myY > mapInfo.map[i][j].y - 35))) {
						mapInfo.map[i][j].state = FREE;
						// itemSpeedUp1();
						speed1 = itemSpeedUP(speed1);
						continue;
					} else if (((myX > mapInfo.map[i][j].x - 10) && (myX < mapInfo.map[i][j].x + 30))
							&& ((myY > mapInfo.map[i][j].y) && (myY < mapInfo.map[i][j].y + 40))) {
						mapInfo.map[i][j].state = FREE;
						// itemSpeedUp1();
						speed1 = itemSpeedUP(speed1);
						continue;
					} else if (((myX >= mapInfo.map[i][j].x - 10) && (myX <= mapInfo.map[i][j].x + 30))
							&& ((myY + 46 >= mapInfo.map[i][j].y - 10) && (myY + 46 < mapInfo.map[i][j].y + 40))) {
						mapInfo.map[i][j].state = FREE;
						// itemSpeedUp1();
						speed1 = itemSpeedUP(speed1);
						continue;
					}

					// 우니
					if (((yourX >= mapInfo.map[i][j].x - 40) && (yourX <= mapInfo.map[i][j].x))
							&& ((yourY < mapInfo.map[i][j].y + 5) && (yourY > mapInfo.map[i][j].y - 35))) {
						mapInfo.map[i][j].state = FREE;
						// itemSpeedUp2();
						speed2 = itemSpeedUP(speed2);
						continue;
					} else if (((yourX >= mapInfo.map[i][j].x) && (yourX <= mapInfo.map[i][j].x + 40))
							&& ((yourY < mapInfo.map[i][j].y + 5) && (yourY > mapInfo.map[i][j].y - 35))) {
						mapInfo.map[i][j].state = FREE;
						// itemSpeedUp2();
						speed2 = itemSpeedUP(speed2);
						continue;
					} else if (((yourX > mapInfo.map[i][j].x - 10) && (yourX < mapInfo.map[i][j].x + 30))
							&& ((yourY > mapInfo.map[i][j].y) && (yourY < mapInfo.map[i][j].y + 40))) {
						mapInfo.map[i][j].state = FREE;
						// itemSpeedUp2();
						speed2 = itemSpeedUP(speed2);
						continue;
					} else if (((yourX >= mapInfo.map[i][j].x - 10) && (yourX <= mapInfo.map[i][j].x + 30))
							&& ((yourY + 46 >= mapInfo.map[i][j].y - 10) && (yourY + 46 < mapInfo.map[i][j].y + 40))) {
						mapInfo.map[i][j].state = FREE;
						// itemSpeedUp2();
						speed2 = itemSpeedUP(speed2);
						continue;
					}
					break;

				case ITEMPLUSBOMB:
					// 배찌
					if (((myX >= mapInfo.map[i][j].x - 40) && (myX <= mapInfo.map[i][j].x))
							&& ((myY < mapInfo.map[i][j].y + 5) && (myY > mapInfo.map[i][j].y - 35))) {
						mapInfo.map[i][j].state = FREE;
						bombAvailable1 = itemPlusBomb(bombAvailable1);
						continue;
					} else if (((myX >= mapInfo.map[i][j].x) && (myX <= mapInfo.map[i][j].x + 40))
							&& ((myY < mapInfo.map[i][j].y + 5) && (myY > mapInfo.map[i][j].y - 35))) {
						mapInfo.map[i][j].state = FREE;
						bombAvailable1 = itemPlusBomb(bombAvailable1);
						continue;
					} else if (((myX > mapInfo.map[i][j].x - 10) && (myX < mapInfo.map[i][j].x + 30))
							&& ((myY > mapInfo.map[i][j].y) && (myY < mapInfo.map[i][j].y + 40))) {
						mapInfo.map[i][j].state = FREE;
						bombAvailable1 = itemPlusBomb(bombAvailable1);
						continue;
					} else if (((myX >= mapInfo.map[i][j].x - 10) && (myX <= mapInfo.map[i][j].x + 30))
							&& ((myY + 46 >= mapInfo.map[i][j].y - 10) && (myY + 46 < mapInfo.map[i][j].y + 40))) {
						mapInfo.map[i][j].state = FREE;
						bombAvailable1 = itemPlusBomb(bombAvailable1);
						continue;
					}

					// 우니
					if (((yourX >= mapInfo.map[i][j].x - 40) && (yourX <= mapInfo.map[i][j].x))
							&& ((yourY < mapInfo.map[i][j].y + 5) && (yourY > mapInfo.map[i][j].y - 35))) {
						mapInfo.map[i][j].state = FREE;
						bombAvailable2 = itemPlusBomb(bombAvailable2);
						continue;
					} else if (((yourX >= mapInfo.map[i][j].x) && (yourX <= mapInfo.map[i][j].x + 40))
							&& ((yourY < mapInfo.map[i][j].y + 5) && (yourY > mapInfo.map[i][j].y - 35))) {
						mapInfo.map[i][j].state = FREE;
						bombAvailable2 = itemPlusBomb(bombAvailable2);
						continue;
					} else if (((yourX > mapInfo.map[i][j].x - 10) && (yourX < mapInfo.map[i][j].x + 30))
							&& ((yourY > mapInfo.map[i][j].y) && (yourY < mapInfo.map[i][j].y + 40))) {
						mapInfo.map[i][j].state = FREE;
						bombAvailable2 = itemPlusBomb(bombAvailable2);
						continue;
					} else if (((yourX >= mapInfo.map[i][j].x - 10) && (yourX <= mapInfo.map[i][j].x + 30))
							&& ((yourY + 46 >= mapInfo.map[i][j].y - 10) && (yourY + 46 < mapInfo.map[i][j].y + 40))) {
						mapInfo.map[i][j].state = FREE;
						bombAvailable2 = itemPlusBomb(bombAvailable2);
						continue;
					}
					break;

				case ITEMPLUSPOWER:
					// 배찌
					if (((myX >= mapInfo.map[i][j].x - 40) && (myX <= mapInfo.map[i][j].x))
							&& ((myY < mapInfo.map[i][j].y + 5) && (myY > mapInfo.map[i][j].y - 35))) {
						mapInfo.map[i][j].state = FREE;
						bombPower1 = itemPlusPower(bombPower1);
						continue;
					} else if (((myX >= mapInfo.map[i][j].x) && (myX <= mapInfo.map[i][j].x + 40))
							&& ((myY < mapInfo.map[i][j].y + 5) && (myY > mapInfo.map[i][j].y - 35))) {
						mapInfo.map[i][j].state = FREE;
						bombPower1 = itemPlusPower(bombPower1);
						continue;
					} else if (((myX > mapInfo.map[i][j].x - 10) && (myX < mapInfo.map[i][j].x + 30))
							&& ((myY > mapInfo.map[i][j].y) && (myY < mapInfo.map[i][j].y + 40))) {
						mapInfo.map[i][j].state = FREE;
						bombPower1 = itemPlusPower(bombPower1);
						continue;
					} else if (((myX >= mapInfo.map[i][j].x - 10) && (myX <= mapInfo.map[i][j].x + 30))
							&& ((myY + 46 >= mapInfo.map[i][j].y - 10) && (myY + 46 < mapInfo.map[i][j].y + 40))) {
						mapInfo.map[i][j].state = FREE;
						bombPower1 = itemPlusPower(bombPower1);
						continue;
					}

					// 우니
					if (((yourX >= mapInfo.map[i][j].x - 40) && (yourX <= mapInfo.map[i][j].x))
							&& ((yourY < mapInfo.map[i][j].y + 5) && (yourY > mapInfo.map[i][j].y - 35))) {
						mapInfo.map[i][j].state = FREE;
						bombPower2 = itemPlusPower(bombPower2);
						continue;
					} else if (((yourX >= mapInfo.map[i][j].x) && (yourX <= mapInfo.map[i][j].x + 40))
							&& ((yourY < mapInfo.map[i][j].y + 5) && (yourY > mapInfo.map[i][j].y - 35))) {
						mapInfo.map[i][j].state = FREE;
						bombPower2 = itemPlusPower(bombPower2);
						continue;
					} else if (((yourX > mapInfo.map[i][j].x - 10) && (yourX < mapInfo.map[i][j].x + 30))
							&& ((yourY > mapInfo.map[i][j].y) && (yourY < mapInfo.map[i][j].y + 40))) {
						mapInfo.map[i][j].state = FREE;
						bombPower2 = itemPlusPower(bombPower2);
						continue;
					} else if (((yourX >= mapInfo.map[i][j].x - 10) && (yourX <= mapInfo.map[i][j].x + 30))
							&& ((yourY + 46 >= mapInfo.map[i][j].y - 10) && (yourY + 46 < mapInfo.map[i][j].y + 40))) {
						mapInfo.map[i][j].state = FREE;
						bombPower2 = itemPlusPower(bombPower2);
						continue;
					}
					break;

				case BOMB:
					// 배찌
					if (((myX >= mapInfo.map[i][j].x - 40) && (myX <= mapInfo.map[i][j].x))
							&& ((myY < mapInfo.map[i][j].y + 5) && (myY > mapInfo.map[i][j].y - 35))) {
						keyR = false;
						continue;
					} else if (((myX >= mapInfo.map[i][j].x) && (myX <= mapInfo.map[i][j].x + 40))
							&& ((myY < mapInfo.map[i][j].y + 5) && (myY > mapInfo.map[i][j].y - 35))) {
						keyL = false;
						continue;
					} else if (((myX > mapInfo.map[i][j].x - 10) && (myX < mapInfo.map[i][j].x + 30))
							&& ((myY > mapInfo.map[i][j].y) && (myY < mapInfo.map[i][j].y + 40))) {
						keyU = false;
						continue;
					} else if (((myX >= mapInfo.map[i][j].x - 10) && (myX <= mapInfo.map[i][j].x + 30))
							&& ((myY + 46 >= mapInfo.map[i][j].y - 10) && (myY + 46 < mapInfo.map[i][j].y + 40))) {
						keyD = false;
						continue;
					}

					// 우니
					if (((yourX >= mapInfo.map[i][j].x - 40) && (yourX <= mapInfo.map[i][j].x))
							&& ((yourY < mapInfo.map[i][j].y + 5) && (yourY > mapInfo.map[i][j].y - 35))) {
						keyRR = false;
						continue;
					} else if (((yourX >= mapInfo.map[i][j].x) && (yourX <= mapInfo.map[i][j].x + 40))
							&& ((yourY < mapInfo.map[i][j].y + 5) && (yourY > mapInfo.map[i][j].y - 35))) {
						keyLL = false;
						continue;
					} else if (((yourX > mapInfo.map[i][j].x - 10) && (yourX < mapInfo.map[i][j].x + 30))
							&& ((yourY > mapInfo.map[i][j].y) && (yourY < mapInfo.map[i][j].y + 40))) {
						keyUU = false;
						continue;
					} else if (((yourX >= mapInfo.map[i][j].x - 10) && (yourX <= mapInfo.map[i][j].x + 30))
							&& ((yourY + 46 >= mapInfo.map[i][j].y - 10) && (yourY + 46 < mapInfo.map[i][j].y + 40))) {
						keyDD = false;
						continue;
					}
					break;

				}// end of switch
			} // end of inner for
		} // end of outer for
	}// End of colliderControl(MapInfo mapInfo) //충돌처리함수 끝


	/// 테스트용. 스피드1,2 합침
	public int itemSpeedUP(int testSpeed) {
		testSpeed += 1;
		if (testSpeed > maxMoveSpeed)
			testSpeed = maxMoveSpeed;

		return testSpeed;
	}

	public int itemPlusBomb(int bombCount) { // 한 번에 가능한 물풍선 개수 증가 (최대 10)
		bombCount += 1;
		if (bombCount > maxBomb)
			bombCount = maxBomb;

		return bombCount;
	}

	public int itemPlusPower(int bombPower) { ///////////////////////////////////// 폭발 범위 증가/////
		bombPower += 1;
		if (bombPower > maxBombPower)
			bombPower = maxBombPower;

		return bombPower;
	}

	public class BombThread implements Runnable {
		int bombX, bombY;
		int who; // 1은 배찌, 2는 우니

		public BombThread(int X, int Y, int who) {
			this.bombX = X;
			this.bombY = Y;
			this.who = who;
			System.out.println("1 >>>" + bombX + bombY);
			bombX /= 40;
			bombY /= 40;
			
			//myXY와 bomb가 설치될 배열 값을 맞추기 위한 작업
			if (who == 1) {
				if (myX != 0) {
					if(myX % 40 < 20)
						bombX += 0;
					else
						bombX += 1;
				}
				if (myY != 0) {
					if (myY % 40 < 20)
						bombY += 1;
					else
						bombY += 1;
				}
			} else if (who == 2) {
				if (yourX != 0) {
					if(yourX % 40 < 20)
						bombX += 0;
					else
						bombX += 1;
				}
				if (yourY != 0) {
					if (yourY % 40 < 20)
						bombY += 1;
					else
						bombY += 1;
				}
			}

			System.out.println("배열좌표>>>" + bombX + "," + bombY);
			System.out.println("폭탄 스레드");
		}// End of 생성자

		@Override
		public void run() {
			if (who == 1) {
				if (bombAvailable1 > 0) {
					dropBomb(bombX, bombY, who);
					try {
						Thread.sleep(3000);
						explodeBomb(bombX, bombY, who, bombPower1);
						Thread.sleep(400);
						freeBomb(bombX, bombY, who, bombPower1);
						
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} // End of if
			} // End of who if
			else if (who == 2) {
				if (bombAvailable2 > 0) {
					dropBomb(bombX, bombY, who);
					try {
						Thread.sleep(3000);
						explodeBomb(bombX, bombY, who, bombPower2);
						Thread.sleep(400);
						freeBomb(bombX, bombY, who, bombPower2);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} // End of if
			}
		}// End of run()
	}// End of BombThread

	public void freeBomb(int bombX, int bombY, int who, int power) {
		int randomNum;
		int i;
		System.out.println("FreeBomb");
		mapInfo.map[bombY][bombX].state = FREE;
		if (bombY - 1 >= 0) {
			for (i = 1; i <= power; i++) {
				if (bombY - i < 0) // 예외: 배열 밖으로 벗어나는 것 방지
					break;

				// 폭발이 벽 통과하지 못하도록 하기 위한 조건
				if (mapInfo.map[bombY - i][bombX].wasWall == true) {
					randomNum = random.nextInt(8);
					mapInfo.map[bombY - i][bombX].wasWall = false; // 벽 취소
					mapInfo.map[bombY - i][bombX].state = itemArray[randomNum];
					break;
				} else {
					mapInfo.map[bombY - i][bombX].state = FREE;
				}
			}

		}

		if (bombY + 1 < 15) {
			for (i = 1; i <= power; i++) {
				if (bombY + i >= 15) // 예외: 배열 밖으로 벗어나는 것 방지
					break;

				// 폭발이 벽 통과하지 못하도록 하기 위한 조건
				if (mapInfo.map[bombY + i][bombX].wasWall == true) {
					randomNum = random.nextInt(8);
					mapInfo.map[bombY + i][bombX].wasWall = false; // 벽 취소
					mapInfo.map[bombY + i][bombX].state = itemArray[randomNum];
					break;
				} else {
					mapInfo.map[bombY + i][bombX].state = FREE;
				}
			}
		}

		if (bombX + 1 < 15) {
			for (i = 1; i <= power; i++) {
				if (bombX + i >= 15) // 예외: 배열 밖으로 벗어나는 것 방지
					break;

				// 폭발이 벽 통과하지 못하도록 하기 위한 조건
				if (mapInfo.map[bombY][bombX + i].wasWall == true) {
					randomNum = random.nextInt(8);
					mapInfo.map[bombY][bombX + i].wasWall = false; // 벽 취소
					mapInfo.map[bombY][bombX + i].state = itemArray[randomNum];
					break;
				} else {
					mapInfo.map[bombY][bombX + i].state = FREE;
				}
			}
		}

		if (bombX - 1 >= 0) {
			for (i = 1; i <= power; i++) {
				if (bombX - i < 0) // 예외: 배열 밖으로 벗어나는 것 방지
					break;

				// 폭발이 벽 통과하지 못하도록 하기 위한 조건
				if (mapInfo.map[bombY][bombX - i].wasWall == true) {
					randomNum = random.nextInt(8);
					mapInfo.map[bombY][bombX - i].wasWall = false; // 벽 취소
					mapInfo.map[bombY][bombX - i].state = itemArray[randomNum];
					break;
				} else {
					mapInfo.map[bombY][bombX - i].state = FREE;
				}
			}
		}
		if (who == 1)
			bombAvailable1 += 1;
		else if (who == 2)
			bombAvailable2 += 1;
		printMap();
	}

	public void explodeBomb(int bombX, int bombY, int who, int power) {
		int i = 1;
		System.out.println("explodeBomb");
		printMap();
		mapInfo.map[bombY][bombX].state = BCENTER;
		if (bombY - 1 >= 0)
			for (i = 1; i <= power; i++) {
				if (bombY - i < 0) // 배열 밖으로 벗어나는 것 방지
					break;
				if (mapInfo.map[bombY - i][bombX].wasWall == true)
					break;

				else
					mapInfo.map[bombY - i][bombX].state = BUP;

			}
		if (bombY + 1 < 15)
			for (i = 1; i <= power; i++) {
				if (bombY + i >= 15) // 배열 밖으로 벗어나는 것 방지
					break;
				if (mapInfo.map[bombY + i][bombX].wasWall == true)
					break;

				else
					mapInfo.map[bombY + i][bombX].state = BDOWN;

			}
		if (bombX + 1 < 15)
			for (i = 1; i <= power; i++) {							
				if (bombX + i >= 15) // 배열 밖으로 벗어나는 것 방지
					break;
				if (mapInfo.map[bombY][bombX + i].wasWall == true)
					break;

				else
					mapInfo.map[bombY][bombX + i].state = BRIGHT;

			}
		if (bombX - 1 >= 0)
			for (i = 1; i <= power; i++) {
				if (bombX - i < 0) // 배열 밖으로 벗어나는 것 방지
					break;
				if (mapInfo.map[bombY][bombX - i].wasWall == true)
					break;

				else
					mapInfo.map[bombY][bombX - i].state = BLEFT;

			}
	}

	public void dropBomb(int bombX, int bombY, int who) {

		System.out.println("배열좌표>>>" + bombX + "," + bombY);
		mapInfo.map[bombY][bombX].state = BOMB;
		printMap();

		System.out.println(">>>" + bombX + "," + bombY);
		if (who == 1)
			bombAvailable1 -= 1;
		else if (who == 2)
			bombAvailable2 -= 1;
	}// End of dropBomb()

	///////////////////////////////////////////////////////////////
	// KeyBoard 이벤트 처리 + process //
	///////////////////////////////////////////////////////////////
	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		// System.out.println(e.getKeyCode());
		switch (e.getKeyCode()) {
		case KeyEvent.VK_RIGHT:
			keyR = true;
			System.out.println("Right");
			break;
		case KeyEvent.VK_LEFT:
			keyL = true;
			System.out.println("Left");
			break;
		case KeyEvent.VK_UP:
			keyU = true;
			System.out.println("Up");
			break;
		case KeyEvent.VK_DOWN:
			keyD = true;
			System.out.println("Down");
			break;
		case KeyEvent.VK_SPACE:
			Runnable BombThread1 = new BombThread(myX, myY, 1);
			new Thread(BombThread1).start();
			break;

		case KeyEvent.VK_D:
			keyRR = true;
			break;
		case KeyEvent.VK_A:
			keyLL = true;
			break;
		case KeyEvent.VK_W:
			keyUU = true;
			break;
		case KeyEvent.VK_S:
			keyDD = true;
			break;
		case KeyEvent.VK_Q:
			Runnable BombThread2 = new BombThread(yourX, yourY, 2);
			new Thread(BombThread2).start();
			break;

		// new Thread(bombThread).start();
		}// End of switch
	}// End of KeyPressed(KeyEvent e)

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		switch (e.getKeyCode()) {
		case KeyEvent.VK_RIGHT:
			System.out.println("Right False");
			System.out.println("(" + myX + "," + myY + ")");
			keyR = false;
			break;
		case KeyEvent.VK_LEFT:
			System.out.println("Left False");
			System.out.println("(" + myX + "," + myY + ")");
			keyL = false;
			break;
		case KeyEvent.VK_UP:
			System.out.println("UP False");
			System.out.println("(" + myX + "," + myY + ")");
			keyU = false;
			break;
		case KeyEvent.VK_DOWN:
			System.out.println("Down False");
			System.out.println("(" + myX + "," + myY + ")");
			keyD = false;
			break;

		case KeyEvent.VK_D:
			System.out.println("(" + yourX + "," + yourY + ")");
			keyRR = false;
			break;
		case KeyEvent.VK_A:
			System.out.println("(" + yourX + "," + yourY + ")");
			keyLL = false;
			break;
		case KeyEvent.VK_W:
			System.out.println("(" + yourX + "," + yourY + ")");
			keyUU = false;
			break;
		case KeyEvent.VK_S:
			System.out.println("(" + yourX + "," + yourY + ")");
			keyDD = false;
			break;
		}// End of switch
	}// End of KeyRealesed(KeyEvent e)

	// 테스트를 위해 우니도 배찌와 같은방향으로 보게 설정해놓았음
	public void keyProcess() {
		// System.out.println("KeyProcess()"+myMove);
		if (keyU == true) {
			System.out.println("myMove" + myMove);
			myMove = UP; // bazziCurrent("images/bazzi_back.png");
			// yourMove = UP;
			myY -= speed1;
			// gt.send(username + ":" + "MOVE:" + move);
			if (myY < 0) {
				myY = 0;
			}
		}
		if (keyD == true) {
			myMove = DOWN; // bazziCurrent("images/bazzi_front.png");
			myY += speed1;
			// yourMove = DOWN;

			if (myY > 550) {
				myY = 550;
			}
		}
		if (keyL == true) {
			myMove = LEFT; // bazziCurrent("images/bazzi_left.png");
			myX -= speed1;
			// yourMove = LEFT;

			if (myX < 16) {
				myX = 16;
			}
		}
		if (keyR == true) {
			myMove = RIGHT;
			myX += speed1;
			// yourMove = RIGHT;

			if (myX > 580) {
				myX = 580;
			}
		}
		///////////////////////////////////////// 우니
		///////////////////////////////////////// 움직이기////////////////////////////////
		if (keyUU == true) {
			// System.out.println("myMove"+myMove);
			yourMove = UP; // bazziCurrent("images/bazzi_back.png");
			// yourMove = UP;
			yourY -= speed2;

			if (yourY < 0) {
				yourY = 0;
			}
		}
		if (keyDD == true) {
			yourMove = DOWN; // bazziCurrent("images/bazzi_front.png");
			yourY += speed2;
			// yourMove = DOWN;

			if (yourY > 550) {
				yourY = 550;
			}
		}
		if (keyLL == true) {
			yourMove = LEFT; // bazziCurrent("images/bazzi_left.png");
			yourX -= speed2;
			// yourMove = LEFT;

			if (yourX < 16) {
				yourX = 16;
			}
		}
		if (keyRR == true) {
			yourMove = RIGHT;
			yourX += speed2;
			// yourMove = RIGHT;

			if (yourX > 560) { // 이상하게 추가된 우니는 원래 있던 배찌의 최대 x값 580이 아님
				yourX = 560;
			}
		}
	}// End of keyProcess()

	///////////////////////////////////////////////////////////////
	// OnClickListener(focus) //
	///////////////////////////////////////////////////////////////

	// 이게안됨 ㅠㅠ
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
		contentPane.requestFocus();
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	///////////////////////////////////////////////////////////////
	// GameCanvas //
	///////////////////////////////////////////////////////////////

	class GameScreen extends Canvas {
		GameStart main;

		Image dblbuff;
		Graphics gc;

		Image bg; // 배경화면

		Image bazziUp = new ImageIcon("images/bazzi_back.png").getImage();
		Image bazziDown = new ImageIcon("images/bazzi_front.png").getImage();
		Image bazziLeft = new ImageIcon("images/bazzi_left.png").getImage();
		Image bazziRight = new ImageIcon("images/bazzi_right.png").getImage();

		Image uniUp = new ImageIcon("images/woonie_back.png").getImage();
		Image uniDown = new ImageIcon("images/woonie_front.png").getImage();
		Image uniLeft = new ImageIcon("images/woonie_left.png").getImage();
		Image uniRight = new ImageIcon("images/woonie_right.png").getImage();

		// 박스
		Image iconBoxBrown = new ImageIcon("images/cookie.png").getImage();
		Image iconBoxPink = new ImageIcon("images/cookie2.png").getImage();
		Image iconItemSpeed = new ImageIcon("images/speed.png").getImage();
		Image iconBomb = new ImageIcon("images/bomb.png").getImage();

		// 물풍선
		Image iconBumb = new ImageIcon("images/bomb.png").getImage();
		Image iconBup = new ImageIcon("images/bup.png").getImage();
		Image iconBdown = new ImageIcon("images/bdown.png").getImage();
		Image iconBleft = new ImageIcon("images/bleft.png").getImage();
		Image iconBright = new ImageIcon("images/bright.png").getImage();
		Image iconBcenter = new ImageIcon("images/bcenter.png").getImage();

		//////////////////////////////////////////////////////////////////////////////////////////

		GameScreen(GameStart main) {
			this.main = main;
		}// End of GameScreen(생성자)

		public void paint(Graphics g) {
			if (gc == null) {
				dblbuff = createImage(main.gScreenWidth, main.gScreenHeight);// 더블 버퍼링용 오프스크린 버퍼 생성. 필히 paint 함수 내에서 해
																				// 줘야 한다. 그렇지 않으면 null이 반환된다.
				if (dblbuff == null)
					System.out.println("오프스크린 버퍼 생성 실패");
				else
					gc = dblbuff.getGraphics();// 오프스크린 버퍼에 그리기 위한 그래픽 컨텍스트 획득
				return;
			}
			update(g);
		}// End of paint(Graphics g)

		public void update(Graphics g) {// 화면 깜박거림을 줄이기 위해, paint에서 화면을 바로 묘화하지 않고 update 메소드를 호출하게 한다.

			if (gc == null)
				return;
			dblpaint();// 오프스크린 버퍼에 그리기
			g.drawImage(dblbuff, 0, 0, this);// 오프스크린 버퍼를 메인화면에 그린다.
		}// End of update(Graphics g)

		public void dblpaint() {
			// 실제 그리는 동작은 이 함수에서 모두 행한다.
			Draw_BG(); // 맵 배경화면 (핑크) 그리기
			Draw_Blocks(); // 블록 그리기
			Draw_myChracter(); // 내 캐릭터 (배찌)그리기
			Draw_yourChracter(); // 상대 캐릭터(우니) 그리기
			// DrawBomb(); //물풍선그리기
			// ExplodeBomb(); //물풍선 폭발 그리기
		}// End of dblpaint()

		public void Draw_BG() {
			gc.drawImage(mapBG, 0, 0, this);
		}// End of Draw_BG()

		public void Draw_Blocks() {

			for (int i = 0; i < 15; i++) {
				for (int j = 0; j < 15; j++) {
					switch (mapInfo.map[i][j].state) {

					// WALL
					case BROWNBLOCK:
						gc.drawImage(iconBoxBrown, mapInfo.map[i][j].x, mapInfo.map[i][j].y, this);
						break;
					case PINKBLOCK:
						gc.drawImage(iconBoxPink, mapInfo.map[i][j].x, mapInfo.map[i][j].y, this);
						break;

					// BOMB
					case BOMB:
						gc.drawImage(iconBomb, mapInfo.map[i][j].x, mapInfo.map[i][j].y, this);
						break;
					case BUP:
						gc.drawImage(iconBup, mapInfo.map[i][j].x, mapInfo.map[i][j].y, this);
						break;
					case BDOWN:
						gc.drawImage(iconBdown, mapInfo.map[i][j].x, mapInfo.map[i][j].y, this);
						break;
					case BLEFT:
						gc.drawImage(iconBleft, mapInfo.map[i][j].x, mapInfo.map[i][j].y, this);
						break;
					case BRIGHT:
						gc.drawImage(iconBright, mapInfo.map[i][j].x, mapInfo.map[i][j].y, this);
						break;
					case BCENTER:
						gc.drawImage(iconBcenter, mapInfo.map[i][j].x, mapInfo.map[i][j].y, this);
						break;

					// ITEM
					case ITEMSPEED:
						gc.drawImage(new ImageIcon("images/speed.png").getImage(), mapInfo.map[i][j].x,
								mapInfo.map[i][j].y, this);
						break;
					case ITEMSTRONGBOMB:
						gc.drawImage(new ImageIcon("images/speed.png").getImage(), mapInfo.map[i][j].x,
								mapInfo.map[i][j].y, this);
						break;
					case ITEMPLUSBOMB:
						gc.drawImage(new ImageIcon("images/plusBomb3.png").getImage(), mapInfo.map[i][j].x,
								mapInfo.map[i][j].y, this);
						break;

					case ITEMPLUSPOWER:
						gc.drawImage(new ImageIcon("images/pluspower.png").getImage(), mapInfo.map[i][j].x,
								mapInfo.map[i][j].y, this);

					case FREE:
						gc.drawImage(null, mapInfo.map[i][j].x, mapInfo.map[i][j].y, this);
						break;

					}// End of switch
				} // End of inner for
			} // End of outer for
		}// End of Draw_Blocks()

		public void Draw_myChracter() {
			switch (myMove) {
			case UP:
				gc.drawImage(bazziUp, myX, myY, this);
				break;
			case DOWN:
				gc.drawImage(bazziDown, myX, myY, this);
				break;
			case LEFT:
				gc.drawImage(bazziLeft, myX, myY, this);
				break;
			case RIGHT:
				gc.drawImage(bazziRight, myX, myY, this);
				break;
			}// End of switch
		}// End of Draw_myCharcter()

		public void Draw_yourChracter() {
			switch (yourMove) {
			case UP:
				gc.drawImage(uniUp, yourX, yourY, this);
				break;
			case DOWN:
				gc.drawImage(uniDown, yourX, yourY, this);
				break;
			case LEFT:
				gc.drawImage(uniLeft, yourX, yourY, this);
				break;
			case RIGHT:
				gc.drawImage(uniRight, yourX, yourY, this);
				break;
			}// End of switch
		}// End of Draw_myCharcter()



	}// End of GameCanvas(class)
		///////////////////////////////////////////////////////////////
		// GameCanvas 끝 //
		///////////////////////////////////////////////////////////////

	///////////////////////////////////////////////////////////////
	// 메인 스레드, 메인 함수 //
	///////////////////////////////////////////////////////////////
	@Override
	public void run() {// 메인 스레드
		// TODO Auto-generated method stub

		while (roof) {
			gamescreen.repaint();// 화면 리페인트
			colliderControl(mapInfo); // 충돌처리
			keyProcess(); // 키보드 처리

			try {
				Thread.sleep(speed);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	} // End of run()

	//public static void main(String[] args) {
		//new GameStart().run();
	//}
} // End of Class