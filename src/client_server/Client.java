package client_server;

//Client side of client/server 3-5-8 program.
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.net.Socket;
import java.net.InetAddress;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

public class Client extends JFrame implements Runnable, ActionListener
{
	
private static final long serialVersionUID = 1L;
private JTextArea displayArea; // JTextArea to display output
private Socket connection; // connection to server
private ObjectOutputStream output; // output stream to server
private ObjectInputStream input; // input stream from server
private String gameHost; // host name for server
private ArrayList<Card> playerCards;
private Container c = null;
private  JLayeredPane playerPaneN;
private  JLayeredPane playerPaneS;
private  JLayeredPane playerPaneE;
private JLayeredPane  spareHandPane;
private JLayeredPane  handPane;
private JButton startButton;
private JButton cardBtnN, cardBtnE, cardOnHand1,cardOnHand2,cardOnHand3,cardSpare;
private final int playerCard = 16;
private final int spareHand = 4;
int offset = 35;
Point origin = new Point(150, 5);
Point origin1 = new Point(5, 5);
Point origin2 = new Point(45, 0);
Point origin3 = new Point(360, 80);
private LinkedHashMap<JButton,Card> map;
private ArrayList<JButton> buttons;
@SuppressWarnings("unused")
private int playerNumber;
private boolean myTurn =false;
@SuppressWarnings("unused")
private int dummy=0;
private String trump="";
private ArrayList<String> Trumps;
private ArrayList<Card> spareCards;
private LinkedHashMap<JButton, Card> spareAll;
private ArrayList<JButton> spareButtons;
private h listener = new h();
private Icon imgH;
private ArrayList<Card> cardToChange;
private ArrayList<Card> spareCardToChange;
private JTextArea scoreArea;

public Client( String host )
{ 
   
	JLabel label = new JLabel(new ImageIcon(getClass().getResource("images/Table.jpg")));
	setContentPane(label);
	c = getContentPane();
	
   gameHost = host; // set name of server
   playerCards = new ArrayList<Card>();
   spareCards = new ArrayList<Card>();
   spareAll = new LinkedHashMap<JButton, Card>();
   spareButtons = new ArrayList<JButton>();
   cardToChange = new ArrayList<Card>();
   spareCardToChange = new ArrayList<Card>();
   
   Trumps = new ArrayList<String>();
   Trumps.add("pika");
   Trumps.add("kypa");
   Trumps.add("karo");
   Trumps.add("spatia");
   Trumps.add("pass");
   
   map = new LinkedHashMap<JButton,Card>();
   buttons = new ArrayList<JButton>();
   displayArea = new JTextArea( 4, 38 ); // set up JTextArea to display system messages
   displayArea.setEditable( false );  
   setTitle("Playing Cards");
   setIconImage(new ImageIcon(getClass().getResource("images/6.gif")).getImage());

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
   
   imgH = new ImageIcon(getClass().getResource("cards/b2fv.png"));
   Icon imgV = new ImageIcon(getClass().getResource("cards/b2fv.png"));
   Icon imgC = new ImageIcon(getClass().getResource("cards/b2fh.png"));

   for (int i = 0; i < playerCard; i++) 
   {
          cardBtnN = createColoredButton("", imgH, origin);
          buttons.add(createColoredButton("", imgH, origin));
          playerPaneN.add(cardBtnN, new Integer(i));
          playerPaneS.add(buttons.get(i), new Integer(i));
          origin.x += offset;        
   } 
      
   for (int i = 0; i < playerCard; i++) 
   {
          cardBtnE = createColoredButton("", imgC, origin1);
          cardBtnE.setBounds(origin1.x, origin1.y , 70, 44);
          playerPaneE.add(cardBtnE, new Integer(i));
          offset = 28;         
          origin1.y += offset;        
   }
   
   for (int i = 0; i < spareHand; i++) 
   {
   		offset = 80;
   		cardSpare = new JButton("", imgV);
   		cardSpare.setBounds(origin2.x, origin2.y , 50, 70);
   		cardSpare.setBackground(Color.white);
   		spareHandPane.add(cardSpare, new Integer(i));
   	   	origin2.y += offset;
   }

   handButtons();
   JPanel mainPanel = new JPanel();
   mainPanel.setLayout(new BorderLayout());
   mainPanel.add(playerPaneN, BorderLayout.NORTH);
   mainPanel.add(playerPaneS, BorderLayout.SOUTH);
   mainPanel.add(playerPaneE, BorderLayout.EAST);
   mainPanel.add(spareHandPane, BorderLayout.WEST);
   mainPanel.add(handPane, BorderLayout.CENTER);
   mainPanel.setOpaque(false);
   
   JPanel textPanel = new JPanel();
   textPanel.setLayout(new FlowLayout());
   textPanel.add(new JScrollPane( displayArea ));
   textPanel.setOpaque(false);
   c.add(textPanel,BorderLayout.NORTH);
   
   scoreArea = new JTextArea(4, 12);
   scoreArea.setOpaque(false);
   scoreArea.setText("                Score:\n\nGames    Pl1    Pl2    Pl3");
   scoreArea.setFont(new Font("Serif",Font.BOLD + Font.ITALIC, 14));
   scoreArea.setForeground(Color.GREEN);
   scoreArea.setEditable(false);
   textPanel.add(scoreArea);
      
   c.add(mainPanel);
   setJMenuBar(createMenuBar()); 
   setSize(880, 650);
   setLocationRelativeTo(null);
   setDefaultCloseOperation(javax.swing. WindowConstants.DISPOSE_ON_CLOSE);
   
   setVisible(true);
   startClient();
} 



public void handButtons(){
	   
	
	cardOnHand1 =  new JButton("", imgH);
   	cardOnHand1.setBounds(250, 70 , 50,70);
   	cardOnHand1.setBackground(Color.white);
   	
   	cardOnHand2 =  new JButton("", imgH);
   	cardOnHand2.setBounds(350, 140 , 50,70);
   	cardOnHand2.setBackground(Color.white);

   	cardOnHand3 =  new JButton("", imgH);
   	cardOnHand3.setBounds(250, 210 , 50,70);
   	cardOnHand3.setBackground(Color.white);
   	handPane.add(cardOnHand1, new Integer(0));
   	handPane.add(cardOnHand2, new Integer(1));
   	handPane.add(cardOnHand3, new Integer(2));
}

// start the client thread
public void startClient()
{
   try 
   {
      // make connection to server
      connection = new Socket( 
         InetAddress.getByName( gameHost ), 12345);

      // get streams for input and output
      
      output = new ObjectOutputStream( connection.getOutputStream() );      
      output.flush(); // flush output buffer to send header information
      input = new ObjectInputStream( connection.getInputStream() );
   } 
   catch ( IOException ioException )
   {
      ioException.printStackTrace();         
   } 

   // create and start worker thread for this client
   ExecutorService worker = Executors.newFixedThreadPool( 1 );
   worker.execute( this ); // execute client
} 

// control thread that allows continuous update of displayArea
public void run()
{
   String message = "";
   Card card = null;
   int hand = 0;
   while ( true )
   {	  
        try {
        	message = (String)input.readObject();
        	if(message.equals("Sending cards")){
        		card = (Card)input.readObject();
        		processMessage( message, card, 0 );
        		
        	} else if(message.equals("Update score")){
        		String score = (String) input.readObject();
        		scoreArea.setText(score);
        		scoreArea.setFont(new Font("Serif",Font.BOLD + Font.ITALIC, 14));
        		scoreArea.setForeground(Color.GREEN);
        		scoreArea.setEditable(false);
        		
        	} else if (message.equals("Sending Hand")){
        		hand = (int)input.readObject();

        		processMessage( message, null, hand );
        	} else if(message.equals("Sending spare cards")){
        		card = (Card)input.readObject();
        		spareCards.add(card);
        			
        		processMessage( message, card, 0 );
        	} else if(message.contains("You are player")){
        		processMessage( message, null,0 );
        		playerNumber = (int)input.readObject();
        	} else if(message.contains("Hand Finished")){
        		clearTable();
        	} else if(message.contains("Player played")){
        		card =(Card)input.readObject();
        		displayMessage(card +" played\n");
        		card.setIcon(card.getCardName());
        		dummy+=1;
        		if(cardOnHand2.getIcon()== imgH){ 	
        					cardOnHand2.setIcon(card.getIcon());
        					cardOnHand2.repaint();        			
        		}  
        		else 
        		{      			
        			cardOnHand1.setIcon(card.getIcon());
					cardOnHand1.repaint();	
        		}        			
        			handPane.repaint();        		
        	} else if(message.equals("Choose trump")){
        		chooseTrump();
        		if(!trump.equals("pass"))
        		{
        		changeListener();        		
        		displaySpareCards();
        		displayMessage("Exchange cards, then press the start button \n");
        		}
        	} else if(message.contains("pass")){
        		processMessage( message, null,0 );
        		playerCards.clear();
        		map.clear();
        		playerCards.clear();
        		spareCards.clear();
        		spareAll.clear();
        		spareCardToChange.clear();
        		cardToChange.clear();
        		spareButtons.clear();
        		for(JButton button:buttons)
        		{
        			for(ActionListener l:button.getActionListeners())
        			{
        				button.removeActionListener(l);
        			}
        		}
        		
        	} else if (message.contains("new game")){
        		
        		processMessage( message, null,0 );
        		playerCards.clear();
        		map.clear();
        		playerCards.clear();
        		spareCards.clear();
        		spareAll.clear();
        		spareCardToChange.clear();
        		cardToChange.clear();
        		spareButtons.clear();
        		buttons.clear();
        		initializeButtons();
        		
        	} else if(message.contains("Game over")){
        		displayMessage(message);
        		break;
        	} else if(message.contains("You win the hand")){
        		displayMessage(message+"\n");
        	} else if(message.contains("wins the hand")){
        		displayMessage(message+"\n");
        	}
        	else 
        	{
        		if(message.contains("Your Turn"))
        		{
        			myTurn = true;
        		}
        		processMessage( message, null,0 );
        	}     	
	} 
    catch (ClassNotFoundException | IOException e) 
    {	
		e.printStackTrace();
		break;
	} 
  }      
} 
 
public void initializeButtons(){
	imgH = new ImageIcon(getClass().getResource("cards/b2fv.png"));
	offset = 35;
	Point origin = new Point(150, 5);
	for (int i = 0; i < playerCard; i++) 
	{
		cardBtnN = createColoredButton("", imgH, origin);
	    buttons.add(createColoredButton("", imgH, origin));	          
	    playerPaneS.add(buttons.get(i), new Integer(i));	          
	    origin.x += offset;	        
	}
	playerPaneS.repaint();
}

public void displaySpareCards(){
	
	spareAll.clear();
	Point o = new Point(45, 0);
	spareHandPane.removeAll();
	   for (int i = 0; i < spareHand; i++) 
	   {
		   	offset = 80;
		   	spareCards.get(i).setIcon(spareCards.get(i).getCardName());
		   	cardSpare = new JButton("", spareCards.get(i).getIcon());
		   	cardSpare.setBounds(o.x, o.y , 50, 70);
		   	cardSpare.setBackground(Color.white);
		   	spareAll.put(cardSpare, spareCards.get(i));
		   	cardSpare.addActionListener(new ActionListener(){
		   	
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					if(spareCardToChange.size() == 0)
					{
						spareCardToChange.add(spareAll.get(e.getSource()));
						
						if( cardToChange.size() == 1 )
						{
							int position;
							position = spareCards.indexOf(spareCardToChange.get(0));
							spareCards.remove(spareCardToChange.get(0));
							spareCards.add(position,cardToChange.get(0));
							
							position = playerCards.indexOf(cardToChange.get(0));
							playerCards.remove(cardToChange.get(0));
							playerCards.add(position,spareCardToChange.get(0));
							
							spareCardToChange.clear();
							cardToChange.clear();
							refresh();
							displaySpareCards();
						}
					}
					
				}
		   		
		   	});
		   	spareHandPane.add(cardSpare, new Integer(i));
		   	
		   	o.y += offset;
		   }
	   startButton = createColoredButton("Start", null, new Point(0,0));
	   startButton.setBounds(45, 320, 50, 25);
	   startButton.addActionListener(new ActionListener(){

		@Override
		public void actionPerformed(ActionEvent e) {
			spareAll.clear();
			spareCards.clear();
			Point o = new Point(45, 0);
			Icon imgV = new ImageIcon(getClass().getResource("cards/b2fv.png"));
			spareHandPane.removeAll();
			   for (int i = 0; i < spareHand; i++) 
			   {
				   	offset = 80;
				   	cardSpare = new JButton("",imgV);
				   	cardSpare.setBounds(o.x, o.y , 50, 70);
				   	cardSpare.setBackground(Color.white);
				   	spareHandPane.add(cardSpare, new Integer(i));
				   	o.y += offset;				   	
			   }
			   spareHandPane.repaint();
			   spareCards.clear();
			   spareAll.clear();
			   spareCardToChange.clear();
			   cardToChange.clear();
			   spareButtons.clear();
			   
			    sortCards();
	       	 	for(Card c:playerCards)
	       	 	{
	       	 		c.setIcon(c.getCardName());
	       	 	}
	       	 	for(JButton button:buttons)
	       	 	{
	       	 		for(ActionListener l:button.getActionListeners())
	       	 		{
	       	 			button.removeActionListener(l);
	       	 		}
	       	 	}
	       	 	displayCards();
			}
		   
	   });
	   
	   spareHandPane.add(startButton);
	   spareHandPane.repaint();
}

public void refresh()
{
	map.clear();
	
	for (int i = 0; i < playerCard; i++) 
	{        
        //cardBtnS = createColoredButton("", playerCards.get(i).getIcon(),o);
		playerCards.get(i).setIcon(playerCards.get(i).getCardName());
        buttons.get(i).setIcon(playerCards.get(i).getIcon());
        map.put(buttons.get(i), playerCards.get(i));       
      
	}
	playerPaneS.repaint();
}

public void changeListener()
{
	for (int i = 0; i < playerCard; i++) {
        	buttons.get(i).removeActionListener(listener);
        	buttons.get(i).addActionListener(new ActionListener(){

        		@Override
				public void actionPerformed(ActionEvent event) 
				{					
					if(cardToChange.size() == 0)
					{
						cardToChange.add((map.get((JButton) event.getSource())));						
						if(spareCardToChange.size() == 1)
						{						
							int position;
							position = spareCards.indexOf(spareCardToChange.get(0));
							spareCards.remove(spareCardToChange.get(0));
							spareCards.add(position, cardToChange.get(0));
							
							position = playerCards.indexOf(cardToChange.get(0));
							playerCards.remove(cardToChange.get(0));
							playerCards.add(position,spareCardToChange.get(0));
							
							spareCardToChange.clear();
							cardToChange.clear();
							refresh();
							displaySpareCards();
						}
					}				
				}        		
        	}
        	);
	}
}

private void chooseTrump()
{
	@SuppressWarnings("unused")
	Dialog choose = new Dialog(this, Trumps);	
}

public void SetTrump(String trump)
{
	this.trump = trump;
	displayMessage("The trump is "+ trump+"\n");
	Trumps.remove(trump);
	if(trump.equals("pass"))
	{		
		map.clear();
		playerCards.clear();
		spareCards.clear();
		spareAll.clear();
		spareCardToChange.clear();
		cardToChange.clear();
		spareButtons.clear();
		for(JButton button:buttons)
		{
			for(ActionListener l:button.getActionListeners())
			{
				button.removeActionListener(l);
			}
		}		
	}
	try 
	{
		output.writeObject(trump);
		output.flush();
	} 
	catch (IOException e) 
	{		
		e.printStackTrace();
	}
	
}
private void processMessage( String message, Card card , int hand)
{   
	if(message.equals("Sending cards"))
	{
		
		playerCards.add(card);
		
        if(playerCards.size()==playerCard){
        	sortCards();
       	 	for(Card c:playerCards){
       	 		c.setIcon(c.getCardName());
       	 	}
       	 	displayCards();
       	 
        }
	} else if(message.equals("Sending Hand")){
		displayMessage( "hand: " +hand + "\n" );
	} else if(message.equals("Sending spare cards")){
		
	} else {
		displayMessage( message + "\n" );
	}
       
   
} 


private void displayMessage( final String messageToDisplay )
{
   SwingUtilities.invokeLater(
      new Runnable() 
      {
         public void run() 
         {
            displayArea.append( messageToDisplay ); // updates output
         } 
      } 
   ); 
} 

public void displayCards()
{
	for (int i = 0; i < playerCard; i++) 
	{
		//cardBtnS = createColoredButton("", playerCards.get(i).getIcon(),o);
		buttons.get(i).setIcon(playerCards.get(i).getIcon());
	    buttons.get(i).addActionListener(listener);
	    map.put(buttons.get(i), playerCards.get(i));       
	 }
	playerPaneS.repaint();	
}


private class h implements ActionListener {
	
	Icon icon;
	JButton button;
	Card card;
	@Override
	public void actionPerformed(ActionEvent event) {
		if(getMyTurn()==true){
			try {
				button = (JButton) event.getSource();
				card = getMap().get(button);
				output.writeObject(card);
				output.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			mapDel(button);
			playerCards.remove(card);
			icon = button.getIcon();
			cardOnHand3.setIcon(null);
			cardOnHand3.setIcon(icon);
			cardOnHand3.repaint();
			button = null;
			card = null;
			icon = null;
			handPane.repaint();
			reArrangeCards();
			myTurn = false;
			event = null;
			dummy+=1;
			
		}
	}	 
}

public HashMap<JButton,Card> getMap()
{
	return map;
}

public void mapDel(JButton b)
{
	map.remove(b);
}

public boolean getMyTurn()
{	
	return myTurn;
}

private void clearTable()
{
	try 
	{
		Thread.sleep(1000);
	} 
	catch (InterruptedException e) 
	{
		e.printStackTrace();
	}
	handPane.removeAll();
	handButtons();
	handPane.repaint();
} 

public void sortCards()
{
	ArrayList<Card> temp = new ArrayList<Card>();
	String[] cardSuits = {"pika","kypa","karo","spatia"};
	String[] cardNumbers ={"2","3","4","5","6","7","8","9","10","J","D","K","A"};
	
	HashMap <String, Integer> ratings = new HashMap<String, Integer>();
	for(int i = 0 ; i < cardNumbers.length ; i++)
	{
		ratings.put(cardNumbers[i], i);
		
	}
	
	for(int i = 0;i < cardSuits.length;i++)
	{
		for(int j = 0; j < playerCards.size();j++)
		{
			if(cardSuits[i].equalsIgnoreCase(playerCards.get(j).getSuit())){
				temp.add(playerCards.get(j));
			}
		}
	}

	for(int i = 0;i < cardSuits.length;i++)
	{
		for(int j = 0; j < temp.size(); j++)
		{		
			for (int k = j+1; k < temp.size(); k++)
			{
				if( ratings.get(temp.get(j).getCardNumber()) > ratings.get(temp.get(k).getCardNumber()) && 
					temp.get(j).getSuit().equalsIgnoreCase(temp.get(k).getSuit()))
				{
					Card smallerCard = temp.get(k);
					Card greaterCard = temp.get(j);
					int greaterCardPos = k;
					int smallerCardPos = j;
					temp.remove(smallerCard);
					temp.remove(greaterCard);
					temp.add(smallerCardPos, smallerCard);
					temp.add(greaterCardPos, greaterCard);
				}
			
			}
		}
	}
	
	playerCards.clear();
	for(Card card:temp)
	{
		playerCards.add(card);		
	}	
}

public void reArrangeCards(){
	int a = 0;
	playerPaneS.removeAll();
	Point o = new Point(150, 5);
	int off = 35;
	int i =0;
	for (Map.Entry<JButton, Card> entry : map.entrySet()) {
		a=a+1;
		entry.getKey().setBounds(o.x,o.y, 50, 67);
		playerPaneS.add(entry.getKey(),new Integer(i));
		o.x += off;
		i++;		
	}	
	playerPaneS.repaint();
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
		
		@Override
		public void actionPerformed(ActionEvent event) 
		{
			if (event.getSource() == connectToServerItem)
			{
				@SuppressWarnings("unused")
				ConnectToServerFrame startServer = new ConnectToServerFrame();				
			}
			
		}
	});
	
	exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
 exitItem.addActionListener(new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			
			int action = JOptionPane.showConfirmDialog(Client.this, "Do you really want to exit the game?",
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
		
}		
}









