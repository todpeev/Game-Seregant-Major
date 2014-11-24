package client_server;

import javax.swing.*;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;



public class GameFrame  extends JFrame implements ActionListener{
	
	private static final long serialVersionUID = 1L;
	private Container c = null;
	private  JLayeredPane playerPaneN;
	private  JLayeredPane playerPaneS;
	private  JLayeredPane playerPaneE;
	private JLayeredPane  spareHandPane;
	private JLayeredPane  handPane;
	private JButton startButton;
	private JButton cardBtnN, cardBtnS, cardBtnE, cardOnHand, cardSpare;
	private final int playerCard = 16;
	private final int hand = 3;
	private final int spareHand = 4;
	int offset = 30;
	Point origin = new Point(350, 5);
	Point origin1 = new Point(5, 5);
	Point origin2 = new Point(45, 45);
    Point origin3 = new Point(360, 80);
    



	public GameFrame()   {
		createGUI();
	}
	
	public void createGUI()  {
		setTitle("Playing Cards");
		setIconImage(new ImageIcon(getClass().getResource("images/6.gif")).getImage());
		setContentPane(new JLabel(new ImageIcon(getClass().getResource("images/o.jpg"))));
	    c = getContentPane();
	    c.setLayout(new BorderLayout());
		
	    playerPaneN = new JLayeredPane();
	    playerPaneN.setPreferredSize(new Dimension(60, 80));
	    
	    playerPaneS = new JLayeredPane();
	    playerPaneS.setPreferredSize(new Dimension(60, 80));
	    
	    playerPaneE = new JLayeredPane();
	    playerPaneE.setPreferredSize(new Dimension(120, 50));
	    
	    spareHandPane = new JLayeredPane();
	    spareHandPane.setPreferredSize(new Dimension(120, 50));
	   
	    handPane = new JLayeredPane();
	    handPane.setPreferredSize(new Dimension(120, 50));
	    
	    Icon imgH = new ImageIcon(getClass().getResource("cards/b2fv.png"));
	    Icon imgV = new ImageIcon(getClass().getResource("cards/5 kypa.gif"));
	    
	    startButton = createColoredButton("Start", null, new Point(0,0));
	    startButton.setBounds(10, 400, 100, 40);
	    spareHandPane.add(startButton);
	    

	    
	   
	    
	    for (int i = 0; i < playerCard; i++) {
	           cardBtnN = createColoredButton("", imgH, origin);
	           cardBtnS = createColoredButton("", imgV, origin);
	           playerPaneN.add(cardBtnN, new Integer(i));
	           playerPaneS.add(cardBtnS, new Integer(i));
	          
	           origin.x += offset;
	         
	    }
	    
	    for (int i = 0; i < playerCard; i++) {
	           cardBtnE = createColoredButton("", imgH, origin1);
	           cardBtnE.setBounds(origin1.x, origin1.y , 70, 44);
	           playerPaneE.add(cardBtnE, new Integer(i));
	           offset = 28;
	          
	           origin1.y += offset;
	         
	    }
	    
	    for (int i = 0; i < spareHand; i++) {
	    	offset = 80;
	    	cardSpare = new JButton("", imgV);
	    	cardSpare.setBounds(origin2.x, origin2.y , 50, 70);
	    	cardSpare.setBackground(Color.white);
	    	spareHandPane.add(cardSpare, new Integer(i));
	    	
	    	origin2.y += offset;
	    }
	    
	   
	    for (int i = 0; i < hand; i++) {
	    	offset = 120;
	    	cardOnHand =  new JButton("", imgV);
	    	cardOnHand.setBounds(origin3.x, origin3.y , 50,70);
	    	cardOnHand.setBackground(Color.white);
	    	handPane.add(cardOnHand, new Integer(i));
	    	
	    	if(i == 1) {
	    	  origin3.x += offset - 240;
	    	  origin3.y += offset ;
	    	  
	    	}
	    	else {
	    		origin3.x += offset;
	    	    origin3.y += offset;
	    	}
	    }
	    
	    
	   
	    
	    add(Box.createRigidArea(new Dimension(0, 10)));
	    c.add(playerPaneN, BorderLayout.NORTH);
	    c.add(playerPaneS, BorderLayout.SOUTH);
	    c.add(playerPaneE, BorderLayout.EAST);
	    c.add(spareHandPane, BorderLayout.WEST);
        c.add(handPane, BorderLayout.CENTER);
        
		setJMenuBar(createMenuBar());
		
		setSize(1200, 700);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(javax.swing. WindowConstants.DISPOSE_ON_CLOSE);
		setVisible(true);
	}

	
	private JButton createColoredButton(String text, Icon img, Point origin) {
	       JButton cardBtn = new JButton(text, img);
	       cardBtn.setVerticalAlignment(JLabel.CENTER);
	       cardBtn.setHorizontalAlignment(JLabel.CENTER);
	       cardBtn.setOpaque(true);
	       cardBtn.setBackground(Color.white);
	       cardBtn.setForeground(Color.black);
	       cardBtn.setBorder(BorderFactory.createLineBorder(Color.black));
	       cardBtn.setBounds(origin.x, origin.y, 50, 67);
	       return  cardBtn;
	    }
	

	
	private JMenuBar createMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		
		JMenu gameMenu = new JMenu("New game");
		JMenu statisticMenu = new JMenu("Statistics");
		JMenu helpMenu = new JMenu("Help");
		
		final JMenuItem connectToServerItem = new JMenuItem("Connect to server");
		final JMenuItem exitItem = new JMenuItem("Exit");
		final JMenuItem aboutAuthorItem = new JMenuItem("About author...");
		final JMenuItem aboutGameItem = new JMenuItem("About game...");
		final JMenuItem helpItem = new JMenuItem("Help");
		final JMenuItem gameRulesItem = new JMenuItem("Game Rules");
		
		gameMenu.add(connectToServerItem);
		gameMenu.addSeparator();
		gameMenu.add(exitItem);
		
		statisticMenu.add(aboutAuthorItem);
		statisticMenu.add(aboutGameItem);
		
		helpMenu.add(helpItem);
		helpMenu.add(gameRulesItem);
		
		menuBar.add(gameMenu);
		menuBar.add(statisticMenu);
		menuBar.add(helpMenu);
		
		gameMenu.setMnemonic(KeyEvent.VK_F);
		exitItem.setMnemonic(KeyEvent.VK_X);
		
		connectToServerItem.addActionListener(new ActionListener() {
			
			@SuppressWarnings("unused")
			@Override
			public void actionPerformed(ActionEvent event) {
				if (event.getSource() == connectToServerItem) {
					ConnectToServerFrame startServer = new ConnectToServerFrame();
					
				}
				
			}
		});
		
		exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
        exitItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				int action = JOptionPane.showConfirmDialog(GameFrame.this, "Do you really want to exit the game?",
						"Confirm Exit", JOptionPane.OK_CANCEL_OPTION);
				
				if (action == JOptionPane.OK_OPTION) {
					System.exit(0);
				}
			}
		});
        return menuBar;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}

	
	
}

	
	



	


 

 

	 
 

	

