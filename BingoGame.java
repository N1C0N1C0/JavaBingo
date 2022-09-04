package AwtSwing;

import java.awt.*;
import java.awt.event.*;
import java.util.Random;


import javax.swing.*;
import javax.swing.Timer;
import javax.sound.sampled.*;
import java.io.File;

public class BingoGame {
	static JPanel panelNoth;	// Top View
	static JPanel panelCenter;	// Game View
	static JLabel labelMessage;
	static JButton[] buttons=new JButton[16];
	static String[] images= {
		"fruit01.png","fruit02.png","fruit03.png","fruit04.png",
		"fruit05.png","fruit06.png","fruit07.png","fruit08.png",
		"fruit01.png","fruit02.png","fruit03.png","fruit04.png",
		"fruit05.png","fruit06.png","fruit07.png","fruit08.png"
	};
	static int openCount=0;		// Opened Card Count: 0, 1, 2 
	static int buttonIndexSave1=0;		// First Opened Card Index 0~15
	static int buttonIndexSave2=0;		// Second Opened Card Index 0~15
	static Timer timer;
	static int tryCount =0;		// Try Count
	static int successCount=0;		// Bingo Count : 0~8 
	
	static class MyFrame extends JFrame implements ActionListener{
		public MyFrame(String title) {
			super(title);
			this.setLayout(new BorderLayout());
			this.setSize(400,500);
			this.setVisible(true);
			this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			
			initUI(this);		// Screen UI Set.
			mixCard();		// Mix Fruit Card.
			
			this.pack();		// Pack Empty Space.
		}
		
		static void playSound(String filename) {
			File file =new File("./sound/"+filename);
			if(file.exists()) {
				try {
					AudioInputStream stream = AudioSystem.getAudioInputStream(file);
					Clip clip =AudioSystem.getClip();
					clip.open(stream);
					clip.start();
				}catch (Exception e) {
					e.printStackTrace();
				}
			}else {
				System.out.println("File Not Found!");
			}
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			// System.out.println("Button Clicked!");
			
			if(openCount == 2) {
				return;
			}
			
			JButton btn=(JButton)e.getSource();
			int index = getButtonIndex(btn);
			// System.out.println("Button Index:"+index);
			btn.setIcon(changeImage(images[index]));
			
			openCount++;
			if(openCount==1) {		// First Card?
				buttonIndexSave1 = index;
			}
			else if (openCount==2) {	// Second Card?
				buttonIndexSave2=index;
				tryCount++;
				labelMessage.setText("Find Same Fruit! "+"Try "+tryCount);
				
				// Judge Logic
				boolean isBingo=checkCard(buttonIndexSave1,buttonIndexSave2);
				if(isBingo==true) {
					playSound("bingo.wav");
					openCount=0;
					successCount++;
					if(successCount==8) {
						labelMessage.setText("Game Over "+"Try "+tryCount);
					}
				}else {
					backToQuestion();
				}
			}
		}
		
		public void backToQuestion() {
			// Time 1Second 
			timer=new Timer(1000, new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					System.out.println("Timer.");
					
					playSound("fail.wav");
					openCount=0;
					buttons[buttonIndexSave1].setIcon(changeImage("question.png"));
					buttons[buttonIndexSave2].setIcon(changeImage("question.png"));
					timer.stop();
				}
				
			});
			timer.start();
		}
		
		public boolean checkCard(int index1, int index2) {
			if(index1==index2) {
				return false;
			}
			if(images[index1].equals(images[index2])) {
				return true;
			}else {
				return false;
			}
			
		}
		
		public int getButtonIndex(JButton btn) {
			int index=0;
			for(int i=0;i<16;i++) {
				if(buttons[i]==btn) { //Same instance?
					index=i;
				}
			}
			return index;
		}
	}
	static void mixCard() {
		Random rand=new Random();
		for(int i=0;i<1000; i++) {
			int random=rand.nextInt(15)+1;	// 1~15
			// swap
			String temp=images[0];
			images[0]=images[random];
			images[random]=temp;
		}
	}
	
	static void initUI(MyFrame myFrame) {
		panelNoth=new JPanel();
		panelNoth.setPreferredSize(new Dimension(400,100));
		panelNoth.setBackground(Color.BLUE);
		labelMessage=new JLabel("Find Same Fruit! "+"Try 0");
		labelMessage.setPreferredSize(new Dimension(400,100));
		labelMessage.setForeground(Color.WHITE);
		labelMessage.setFont(new Font("MONACO",Font.BOLD,20));
		labelMessage.setHorizontalAlignment(JLabel.CENTER);
		panelNoth.add(labelMessage);
		myFrame.add("North",panelNoth);
		
		panelCenter=new JPanel();
		panelCenter.setLayout(new GridLayout(4,4));
		panelCenter.setPreferredSize(new Dimension(400,400));
		for(int i=0;i<16;i++) {
			buttons[i]=new JButton();
			buttons[i].setPreferredSize(new Dimension(100,100));
			buttons[i].setIcon(changeImage("question.png"));
			buttons[i].addActionListener(myFrame);
			panelCenter.add(buttons[i]);
		}
		myFrame.add("Center",panelCenter);
	}
	
	static ImageIcon changeImage(String filename) {
		ImageIcon icon=new ImageIcon("./img/"+filename);
		Image originImage=icon.getImage();
		Image changedImage=originImage.getScaledInstance(80, 80, Image.SCALE_SMOOTH);
		ImageIcon icon_new=new ImageIcon(changedImage);
		return icon_new;
		
	}
	
	public static void main(String[] args) {
		new MyFrame("Bingo Game");
	}

}
