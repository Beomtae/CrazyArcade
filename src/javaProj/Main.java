package javaProj;
import java.awt.EventQueue;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;

import javax.management.RuntimeErrorException;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.border.EmptyBorder;

import javaProj.Cookie;
import javaProj.GameStart;

public class Main extends JFrame{
	
	private JLabel contentPane;
	private Vector<JLabel> item = new Vector<JLabel>();
	 private ArrayList<JLabel> itemlist = new ArrayList<JLabel>();
	   
	 ImageIcon item3;
	  JLabel itemLabel;
	  Random random = new Random();
	  
	   int myX = 500;
	   int myY = 500;
	   
	   int x;
	   int y;
	   
	   int bx, by;
	   private ImageIcon[] item2 = { new ImageIcon("images/speed.png"),null,null, null };
	   
	   boolean die = false;
	   boolean check=true;
	   
	   boolean keyU = false;
	   boolean keyD = false;
	   boolean keyL = false;
	   boolean keyR = false;
	   
	   private int speed = 80;
	   String move;
	   
	
JLabel bazzi = new JLabel(new ImageIcon("images/bazzi_front.png"));
 
public void DropBomb() {
    this.x = myX;
    this.y = myY;

    Runnable runnable = new Runnable() {

       @Override
       public void run() {
          ImageIcon bubble = new ImageIcon("images/bomb.png");
          JLabel bu = new JLabel(bubble);
          x /= 40;
          y /= 40;
          x *= 40;
          y *= 40;

          bu.setSize(40, 40);
          bu.setLocation(x + 16, y + 5);
          bu.setVisible(true);
          contentPane.add(bu);

          bx = x + 16;
          by = y + 5;
          try {
             Thread.sleep(2000);
//             bu.setVisible(false);

             ImageIcon bup = new ImageIcon("images/bup.png");
             JLabel bupp = new JLabel(bup);
             ImageIcon bright = new ImageIcon("images/bright.png");
             JLabel br = new JLabel(bright);
             ImageIcon bdown = new ImageIcon("images/bdown.png");
             JLabel bd = new JLabel(bdown);
             ImageIcon bleft = new ImageIcon("images/bleft.png");
             JLabel bl = new JLabel(bleft);

             bupp.setSize(40, 40);
             bupp.setLocation(bu.getLocation().x, bu.getLocation().y - 40);
             bupp.setVisible(true);
             contentPane.add(bupp);
             br.setSize(40, 40);
             br.setLocation(bu.getLocation().x + 40, bu.getLocation().y);
             br.setVisible(true);
             contentPane.add(br);
             bd.setSize(40, 40);
             bd.setLocation(bu.getLocation().x, bu.getLocation().y + 40);
             bd.setVisible(true);
             contentPane.add(bd);
             bl.setSize(40, 40);
             bl.setLocation(bu.getLocation().x - 40, bu.getLocation().y);
             bl.setVisible(true);
             contentPane.add(bl);
             bu.setIcon(new ImageIcon("images/bcenter.png"));
             Thread.sleep(1000);
             bupp.setVisible(false);
             br.setVisible(false);
             bd.setVisible(false);
             bl.setVisible(false);
             bu.setVisible(false);

             checkLocation();
             bx = bu.getLocation().x;
             by = bu.getLocation().y;
          } catch (InterruptedException e) {
             e.printStackTrace();
          }

       }
    };
    new Thread(runnable).start();
 }
public void checkLocation() {

    if((myX>bx-65 && myX<bx+60) &&(myY>by-40 &&myY<by+20)) {
       die = true;
    }
    else if((myX>bx-10 && myX<bx+50) &&(myY>by-80 &&myY<by+45)) {
       die = true;
    }

    for (int i = 0; i < item.size(); i++) {


       y -= 40;
       if ((bx + 40 >= item.get(i).getX() && bx + 40 <= item.get(i).getX() + 16)
             && (by >= item.get(i).getY() && by <= item.get(i).getY() + 5)) {
          item.get(i).setIcon(null);

          item3 = item2[random.nextInt(3)];
          itemLabel = new JLabel(item3);
          itemLabel.setLocation(item.get(i).getX(), item.get(i).getY());
          itemLabel.setSize(40, 40);
          contentPane.add(itemLabel);
          itemlist.add(itemLabel);
          item.remove(i);

       } else if ((bx >= item.get(i).getX() && bx <= item.get(i).getX() + 16)
             && (by + 40 >= item.get(i).getY() && by + 40 <= item.get(i).getY() + 5)) {
          item.get(i).setIcon(null);

          item3 = item2[random.nextInt(3)];
          itemLabel = new JLabel(item3);
          itemLabel.setLocation(item.get(i).getX(), item.get(i).getY());
          itemLabel.setSize(40, 40);
          contentPane.add(itemLabel);
          itemlist.add(itemLabel);
          item.remove(i);

       } else if ((bx - 40 >= item.get(i).getX() && bx - 40 <= item.get(i).getX() + 16)
             && (by >= item.get(i).getY() && by <= item.get(i).getY() + 5)) {

          item.get(i).setIcon(null);
          item3 = item2[random.nextInt(3)];
          itemLabel = new JLabel(item3);
          itemLabel.setLocation(item.get(i).getX(), item.get(i).getY());
          itemLabel.setSize(40, 40);
          contentPane.add(itemLabel);
          itemlist.add(itemLabel);
          item.remove(i);

       } else if ((bx >= item.get(i).getX() && bx <= item.get(i).getX() + 16)
             && (by - 40 >= item.get(i).getY() && by - 40 <= item.get(i).getY() + 5)) {
          item.get(i).setIcon(null);

          item3 = item2[random.nextInt(3)];
          itemLabel = new JLabel(item3);
          itemLabel.setLocation(item.get(i).getX(), item.get(i).getY());
          itemLabel.setSize(40, 40);
          contentPane.add(itemLabel);
          itemlist.add(itemLabel);
          item.remove(i);

       }

       // item.get(i).setIcon(null); //23
    }

    
 }


public void bazziCurrent(String imageLocation) {
    bazzi.setIcon(new ImageIcon(imageLocation));
 }


private void firstLocation() {
    
    contentPane.add(bazzi);
    bazzi.setSize(44, 56);
    bazzi.setLocation(myX, myY);


 }

public void keyProcess() {
    if (keyU == true) {
       bazziCurrent("images/bazzi_back.png");
       myY -= 10;
       bazzi.setLocation(myX, myY);
       move = "U";
       if (myY < 0) {
          myY = 0;
       }
    }
    if (keyD == true) {
       bazziCurrent("images/bazzi_front.png");
       myY += 10;
       bazzi.setLocation(myX, myY);
       move = "D";
       if (myY > 550) {
          myY = 550;
       }
    }
    if (keyL == true) {
       bazziCurrent("images/bazzi_left.png");
       myX -= 10;
       bazzi.setLocation(myX, myY);
       move = "L";
       if (myX < 16) {
          myX = 16;
       }
    }
    if (keyR == true) {
       bazziCurrent("images/bazzi_right.png");
       myX += 10;
       bazzi.setLocation(myX, myY);
       move = "R";
       if (myX > 580) {
          myX = 580;
       }
    }
 }



Runnable runnable = new Runnable() {

    @Override
    public void run() {
       while (true) {
          keyProcess();
         // ItemSpeed();
          
          repaint();
          try {
             Thread.sleep(speed);
          } catch (InterruptedException e) {
             e.printStackTrace();
          }
       }
    }
 };
 public Main(){
 
	 setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
     setBounds(100, 100, 650, 650);
     setLocationRelativeTo(null);
     contentPane = new JLabel(new ImageIcon("Images/mapbg1.png"));
     contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
     contentPane.setLayout(null);

     setContentPane(contentPane);

     firstLocation();
     new Thread(runnable).start();

     
     setVisible(true); 
     
     
     addKeyListener(new KeyAdapter() {
         @Override
         public void keyPressed(KeyEvent e) {
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
           	new Thread(one).start();
            }

         }
         
         Runnable one = new Runnable() {
             
             @Override
             public void run() {
                if(check) {
                   DropBomb();
                         check=false;
                         try {
                      Thread.sleep(3000);
                      check=true;
                   } catch (InterruptedException e) {
                      // TODO Auto-generated catch block
                      e.printStackTrace();
                   }
                }
                
             }
          };

         @Override
         public void keyReleased(KeyEvent e) {
            switch (e.getKeyCode()) {
            case KeyEvent.VK_RIGHT:
            	 System.out.println("Right False");
               keyR = false;
               break;
            case KeyEvent.VK_LEFT:
            	 System.out.println("Left False");
               keyL = false;
               break;
            case KeyEvent.VK_UP:
            	 System.out.println("UP False");
               keyU = false;
               break;
            case KeyEvent.VK_DOWN:
            	 System.out.println("Down False");
               keyD = false;
               break;
            }
         }
      });
     
     Cookie cookie = new Cookie(15);
     int [][] HeartHead = {{1,4},{2,3},{3,2},{4,1},{5,2},{6,3},{7,4},{8,3},{9,2},{10,1},{11,2},{12,3},{13,4}};
     
     for (int i = 0; i < cookie.size; i++) {
        for (int j = 0; j < cookie.size; j++) {
           cookie.map[i][j] = "1";
           String block = cookie.map[i][j];
           if (i == 0 || j == 14 || i == 14 || j == 0) {
        	   //갈색 테두리  
              JLabel cookie2 = new JLabel(new ImageIcon("images/cookie.png"));
              item.add(cookie2);
              this.add(cookie2);
              cookie2.setBounds(i * 40 + 15, j * 40, 45, 45);
           } else if ( i + j == 20 || i == j - 6) {
             //안에 모양   
        	   JLabel cookie3 = new JLabel(new ImageIcon("images/cookie2.png"));
              item.add(cookie3);
              this.add(cookie3);
              cookie3.setBounds(i * 40 + 15, j * 40, 45, 45);
           }
           else if ((i ==1) || (i ==13)) {
        	   if ((j ==4) || (j==5) || (j==6)){
        		   //일직선부분  
        	   JLabel cookie3 = new JLabel(new ImageIcon("images/cookie2.png"));
               item.add(cookie3);
               this.add(cookie3);
               cookie3.setBounds(i * 40 + 15, j * 40, 45, 45);
        	   }
           }
           
           for (int q =0; q<13; q++) {
    		   int ii = HeartHead[q][0];
    		   int jj = HeartHead[q][1];
    		   
    		   JLabel cookie3 = new JLabel(new ImageIcon("images/cookie2.png"));
               item.add(cookie3);
               this.add(cookie3);
               cookie3.setBounds(ii * 40 + 15, jj * 40, 45, 45);	   
       }
           }
        
        
        }
     

	 
	 
	 
 }
// public static void main(String[] args) {
 // new Main();
  
 //}
}