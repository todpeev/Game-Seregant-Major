package client_server;

import java.awt.BorderLayout;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;



public class Server extends JFrame 
{
	   
	   /**
	 * 
	 */
	private static final long serialVersionUID = -8482195685495885612L;
	private JTextArea outputArea; // for outputting hands
	   private ArrayList<Player> Players; // list of Players
	   private ServerSocket server; // server socket to connect with clients
	   private ExecutorService runGame; // used to manage a pool of threads for the Players
	   private Lock gameLock; // to lock game when signaling all Players threads that the game can start;
	   private Condition allPlayersConnected; // a condition showing all Players are connected or not
	   private Condition nextPlayerTurn; // signals its next player's turn
	   private Game game;
	   private Condition allPlayed;
	   private int playerCard;
	   
	   public Server()
	   {
	      super( "Seregant Major Server" ); // set title of window
	      playerCard = 16;
	      // create ExecutorService with a thread for each player
	      runGame = Executors.newFixedThreadPool( 4 );
	      gameLock = new ReentrantLock(); // create lock for game

	      // condition variable for all Players being connected
	      allPlayersConnected = gameLock.newCondition();
	      allPlayed = gameLock.newCondition();
	      // condition variable for the next player's turn
	      nextPlayerTurn = gameLock.newCondition();      
	      Players = new ArrayList<Player>(); // create array of Players   
	 
	      try
	      {
	         server = new ServerSocket( 12345, 3 ); // set up ServerSocket
	      } 
	      catch ( IOException ioException ) 
	      {
	         ioException.printStackTrace();
	         System.exit( 1 );
	      } 

	      outputArea = new JTextArea(); // create JTextArea for output
	      add( outputArea, BorderLayout.CENTER );
	      outputArea.setText( "Server awaiting connections\n" );

	      setSize( 300, 300 ); 
	      setVisible( true ); 
	   } 

	   // wait for two connections so game can be played
	   public void execute()
	   {
	      // wait for each client to connect
	      for ( int i = 0; i < 3; i++ ) 
	      {
	         try // wait for connection, create Player, start runnable
	         {
	            Players.add(new Player( server.accept(),i));
	            runGame.execute( Players.get(i) ); // execute player runnable
	         } 
	         catch ( IOException ioException ) 
	         {
	            ioException.printStackTrace();
	            System.exit( 1 );
	         } 
	      } 

	      gameLock.lock(); // lock game to signal player X's thread

	      try
	      {
	         for(Player player:Players)
	         {
	        	 player.setSuspended( false );
	         }
	         allPlayersConnected.signalAll(); // resume the waiting players
	      } 
	      finally
	      {
	         gameLock.unlock(); // unlock game after signaling the first player
	      } 
	   } 
	   
	   // display message in outputArea
	   private void displayMessage( final String messageToDisplay )
	   {
	      // display message from event-dispatch thread of execution
	      SwingUtilities.invokeLater(
	         new Runnable() 
	         {
	            public void run() // updates outputArea
	            {
	               outputArea.append( messageToDisplay ); // add message
	            } 
	         } 
	      ); 
	   } 


	   // private inner class Player manages each Player as a runnable
	   private class Player implements Runnable
	   {
	    
		private Socket connection; // connection to client
	    private ObjectOutputStream output; // output stream to client
	    private ObjectInputStream input; // input stream from client
	  	private ArrayList<Card> playerCards;
		private int score;
		private int initialScore;
		private ArrayList<Integer> pastGamesScores;
		private boolean suspended = true; // shows whether a player thread is suspended
	    private int playerNumber;
	    private Lock lock;
	    private Condition wait;
	    private boolean is8Received = false;
	    private boolean gamePass = false;
	    private boolean initializingDone = false;
	    private Condition initialize;
	    
	    public Player( Socket socket, int number )
	    {
	        lock = new ReentrantLock();
	        wait = lock.newCondition();
	        initialize = lock.newCondition(); 
	        connection = socket;
	 		playerCards = new ArrayList<Card>();
			pastGamesScores =  new ArrayList<Integer>();
			playerNumber = number;

	        try
	        {
	        	output = new ObjectOutputStream( connection.getOutputStream() );
	            output.flush();
	            input = new ObjectInputStream( connection.getInputStream() );
	        } 
	        catch ( IOException ioException ) 
	        {
	            ioException.printStackTrace();
	            System.exit( 1 );
	        } 
	        
	     } 	    
	    
	    public void Play(Player p) throws ClassNotFoundException, IOException, InterruptedException
	    {	
	    	
	  		while(p.playerNumber != game.order.get(0))
	  		{
	  			gameLock.lock();
	  			try
	  			{
	  				nextPlayerTurn.await();
	  			}
	  			finally 
	  			{
	  				gameLock.unlock();
	  			}
	  				
	  		}	  		
	  		
	  		System.out.println("Your turn " + playerNumber);
	  		for(Player player:Players){
	  			if(this.playerNumber != player.playerNumber){
	  				player.output.writeObject("Player "+ playerNumber +" turn");
	  				player.output.flush();
	  			}
	  		}
	  		output.writeObject("Your Turn");
			output.flush();
	  		Card cardPlayed = (Card) input.readObject();
	  		game.cardsPlayed.add(cardPlayed);
	  		game.HandWin();
	  		System.out.println(game.order);
	  		game.order.remove(0);
	  		for(Player player:Players){
	  			if(p.playerNumber!=player.playerNumber){
	  				player.output.writeObject("Player played");
	  				
	  				player.output.writeObject(cardPlayed);
	  				player.output.flush();
	  			}
	  			
	  			if(game.order.isEmpty())
	  			{	  				
	  				player.output.writeObject("Hand Finished");
	  				player.output.flush();
	  			}	  			
	  		}
  			
  			gameLock.lock();
  			try
  			{
  				nextPlayerTurn.signalAll();  				
  			} 
  			finally 
  			{
  				gameLock.unlock();
  			}
	  		System.out.println(cardPlayed);
	  		
  			if(game.order.isEmpty()){
  				
  				game.order.add(-1);
  				game.order.add(-1);
  				game.order.add(-1);
  				gameLock.lock();
  				allPlayed.signalAll();
  				gameLock.unlock();
  				
  			} 
  			else 
  			{  				
  				gameLock.lock();
  				allPlayed.await();
  				gameLock.unlock();
  			}
	  		
			
	  	}	     
	    
	    
	    public void run()
	    {
	         try 
	         {
	            displayMessage( "Player " + Players.size() + " connected\n" );
	            output.writeObject( "Connection to 3-5-8 Game server sucessfull!" ); // send notification to the client he sucessfully connected
	            output.writeObject( "You are player " + playerNumber );
	            output.writeObject( playerNumber );
	            output.flush(); // flush output
	            if (Players.size() == 1)
	            {	   	         	
		        	 game = new Game();		          
	            } 
	            	            
	            if ( Players.size()!=3 ) 
	            {
	               output.writeObject( "Waiting for " + (3 - Players.size()) + " more player(s) to connect..." );
	               output.flush(); // flush output
	               gameLock.lock(); // lock game to  wait for the next player

	               try 
	               {
	                  while( suspended )
	                  {
	                     allPlayersConnected.await(); // wait for all players to connect
	                  } 
	               } 
	               catch ( InterruptedException exception ) 
	               {
	                  exception.printStackTrace();
	               } 
	               finally
	               {
	                  gameLock.unlock(); 
	               } 

	               
	               output.writeObject( "All players connected. The game is starting..." );
	               output.flush(); 
	            } 
	            else
	            {
	               output.writeObject( "The game is starting..." );
	               output.flush();             
	            } 
 		
        		
	            	// ot shuffledDeck se razdavat karti na igrachite,po 16 karti na chovek;
	            	for(int i = 1;i <= playerCard; i++)
	            	{
	            		if(playerNumber == 1)
	            		{
	            			game.unshuffledDeck.clear();
	            			game.shuffledDeck.clear();
	   		        	 	game.initializeDeck();
	   		        	 	game.Shuffle();
	   		        	 	for(Player player:Players)
	   		        	 	{
	   		        	 		player.initializingDone = true;
	   		        	 		player.lock.lock();
	   		        	 		try
	   		        	 		{
	   		        	 			player.initialize.signalAll();
	   		        	 		} 
	   		        	 		finally 
	   		        	 		{
	   		        	 			player.lock.unlock();
	   		        	 		}
	   		        	 	}
	            		} 
	            		else 
	            		{
	            				lock.lock();
	            				try
	            				{
	            					while(initializingDone == false)
	            					{
	            						initialize.await();
	            					}
	            				} finally 
	            				{
	            					lock.unlock();
	            				}
	            		}
	            		
	            		initializingDone = false;
	            		
	            		distributeCards();
	      				distributeHand();
	            		if(this.initialScore == -8)
	            		{
	            			for(Card card:game.getFreeCards())
	            			{
	            				output.writeObject("Sending spare cards");
	            				output.writeObject(card);
	            			}
	            		}     	

	            		if(initialScore == -8)
	            		{
	            			chooseTrump(this);
	            			if(game.gameTrump.equals("pass"))
	            			{
	            				game.initializeDeck();
	       		        	 	game.Shuffle();
	            				for(Player player:Players)
	            				{
	            					player.gamePass=true;
	            				}
	            			}
	            			for(Player player:Players){
	            				player.is8Received=true;
	            				player.lock.lock();
	            					try
	            					{
	            						player.wait.signalAll();
	            					} 
	            					finally 
	            					{
	            						player.lock.unlock();
	            					}
	            			}
	            		} 
	            		else 
	            		{
	            			lock.lock();	            		
	            			try{
	            				while(is8Received == false )
	            				{
	            					wait.await();
	            				}
	            			
	            			} finally 
	            			{
	            				lock.unlock();
	            			}

	            		}
	            		
	            		is8Received = false;
    					if(gamePass == true)
    					{
    						gamePass = false;
    						continue;
    					}
	            		
	            	// start throwing the cards	
	            	for(int j = 1;j <= playerCard; j++)
	            	{
	            		int b = 0;
	            		int a = 0;
	            		
	            		// if this is the first hand, check who was awarded 8 points,
	            		// otherwise check who won the last hand. Based on this compute game order
	            		if( j == 1 )
	            		{	            		
	            			for(Player player:Players)
	            			{
	            				if(player.initialScore == -8)
	            				{
	            					b = player.playerNumber;
	            					a = player.playerNumber;
	            				}
	            			}
	            			
	            			if( this.initialScore == -8 )
	            			{
	            				try
	            				{
	            					game.order.remove(0);
	            					game.order.add(0, this.playerNumber);
	            				}
	            				catch (IndexOutOfBoundsException e) 
	            				{
	            					game.order.add(this.playerNumber);
	            				}           				

	            				System.out.println("poziciq 0: " +this.playerNumber+" "+ game.order+" "+b);
	            				
	            			} 
	            			else if( (b+1)%3 == this.playerNumber )
	            			{
	            				try
	            				{
	            					game.order.remove(1);
	            					game.order.add(1, this.playerNumber);
	            					b = a;
	            				}
	            				catch (IndexOutOfBoundsException e)
	            				{
	            					game.order.add(this.playerNumber);
	            				}
	            				System.out.println("poziciq 1: " +this.playerNumber+" "+ game.order+" "+b);
	            			} else {
	            				try
	            				{
	            					game.order.remove(2);
	            					game.order.add(2, this.playerNumber);
	            					b = a;
	            				} 
	            				catch (IndexOutOfBoundsException e) 
	            				{
	            					game.order.add(this.playerNumber);
	            				}
	            				System.out.println("poziciq 2: " +this.playerNumber+" "+ game.order+" "+b);
	            			}	            			
	            		} 
	            		else
	            		{ 	            			
	            			if(game.handWinner == this.playerNumber)
	            			{
	            				try
	            				{
	            					game.order.remove(0);
	            					game.order.add(0, this.playerNumber);
	            				} 
	            				catch (IndexOutOfBoundsException e) 
	            				{	            	
	            					game.order.add(this.playerNumber);
	            				}
	            			} 
	            			else if((game.handWinner+1)%3 == this.playerNumber)
	            			{
	            				try
	            				{
	            					game.order.remove(1);
	            					game.order.add(1, this.playerNumber);
	            				} 
	            				catch (IndexOutOfBoundsException e) 
	            				{
	            					game.order.add(this.playerNumber);
	            				}	            				
	            			} 
	            			else 
	            			{
	            				try
	            				{
	            					game.order.remove(2);
	            					game.order.add(2, this.playerNumber);
	            				} 
	            				catch (IndexOutOfBoundsException e) 
	            				{
	            					game.order.add(this.playerNumber);
	            				}
	            			}
	            		}
	            		Play(this);
	            	}
	            	
	            	
	            	if(playerNumber == 1 && i != playerCard)
	            	{
	            		int maxScore = -100;
	            		int roundWinner = -1;
	            		for(Player player:Players)
	            		{
	            			if(player.score > maxScore){
	            				maxScore = player.score;
	            				roundWinner = player.playerNumber;
	            			}
	            		}
	            		game.gamesWon[roundWinner] = game.gamesWon[roundWinner] + 1;
            			game.scoreText = game.scoreText + "\n                 " + game.gamesWon[0] + "       " + game.gamesWon[1] + "        " + game.gamesWon[2];
	            		for(Player player:Players)
	            		{            			
	            			if(player.score == maxScore)
	            			{
	            				player.output.writeObject("You win this round");
	            				player.output.flush();
	            			} 
	            			else 
	            			{
	            				player.output.writeObject(roundWinner +" wins this round");
	            				player.output.flush();
	            					            				
	            			}
	            			
	            			player.output.writeObject("Update score");
	            			player.output.writeObject(game.scoreText);
	            			player.output.flush();
	            		}
	            	}
	            	
	            	if(i == playerCard)
	            	{
	            		output.writeObject("Game over");
		            	output.flush();		            	
	            	} 
	            	else 
	            	{
	            		score = 0;
	            		output.writeObject("new game");
	            		output.flush();
	            	}
	            }           	
	            output.writeObject("Game over");
	            output.flush();        
	         } 
	         catch (IOException e) 
	         {
				e.printStackTrace();
				
			} 
	        catch (ClassNotFoundException e) 
			{
				e.printStackTrace();
			} 
	        catch (InterruptedException e) 
			{
				e.printStackTrace();
			}
	        finally
	        {
	            try
	            {
	               connection.close(); // close connection to client
	            } 
	            catch ( IOException ioException ) 
	            {
	               ioException.printStackTrace();
	               System.exit( 1 );
	            } 
	         }
	      } // end method run

	    public void distributeHand()
	     {
	    	  int initialScore;
	    	  initialScore = game.distributeHand();
	    	  try 
	    	  {
				this.initialScore = initialScore;
				score = this.initialScore;
	    		output.writeObject("Sending Hand");
				output.writeObject(initialScore);
				output.flush();
	    	  } 
	    	  catch (IOException e) 
	    	  {
				e.printStackTrace();
	    	  }		
				
			}
	      
	    public void setSuspended( boolean status ){
	         suspended = status; 
	    } 

	  	public void getCards(Card card){
	  		card.setPlayerNumber(this.playerNumber);
	  		playerCards.add(card);
	  		//System.out.println(card);
	  		try {
	  			output.writeObject("Sending cards");
	  			output.writeObject(card);
				output.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
	  	}	  	
	  	

	  	public void chooseTrump(Player p) throws IOException, ClassNotFoundException{
	  			
	  		output.writeObject("Choose trump");
	  		output.flush();
	  		game.gameTrump = (String)input.readObject();
	  		for(Player player:Players){
	  			if(player.playerNumber!=p.playerNumber){
	  				player.output.writeObject("The trump is "+ (String)game.gameTrump);
	  				player.output.flush();
	  				
	  			}
	  		}
	  			
  		}
	  	
	  	
	  	@SuppressWarnings("unused")
		public void clearPastScores(){
	  		pastGamesScores.clear();
	  	}
	  	
	  	@SuppressWarnings("unused")
		public void setGame(Game g){
	  		game = g;
	  	}
	  	

		public void distributeCards(){
		    int dummy = 1;					
			while(dummy<=playerCard)
			{
				this.getCards(game.getShuffled());
				dummy++;
					
			}
			dummy =1;
		}
		

	   } 

	   private class Game  {

			private ArrayList<Card> unshuffledDeck;
			private ArrayList<Card> shuffledDeck;
			private String[] cardNumbers ={"2","3","4","5","6","7","8","9","10","J","D","K","A"};
			private String[] cardSuits = {"pika","kypa","karo","spatia"};
			private Random generator;
			private String gameTrump;
			private HashMap<String,Integer> ratings; 
			private ArrayList<Card> cardsPlayed;
			ArrayList<Integer> hands; 
			private int handWinner=-1;
			private ArrayList<Integer> order;
			private int[] gamesWon;
			private String scoreText;
			//constructor, initcializira igrata;
			public Game()
			{				
				order = new ArrayList<Integer>();
				order.add(-1);
				order.add(-1);
				order.add(-1);
				generator = new Random();
				shuffledDeck = new ArrayList<Card>();//teste razburkani karti;
				unshuffledDeck = new ArrayList<Card>();//teste nerazburkani karti
				new ReentrantLock();
				cardsPlayed = new ArrayList<Card>();//spisuk sas karti izigrani v edna ruka (3 karti);	
				hands = new ArrayList<Integer>();				
				initializeRatings(); // za vsqka karta naznachava tochki, koito se izpolzvat za da se opredeli koq karta e po silna;
				gamesWon = new int[3];
				scoreText = "                Score:\n\nGame     Pl1    Pl2    Pl3";
			}
			
			public void HandWin()throws ClassNotFoundException, IOException, InterruptedException
			{
					Card highestCard = null;
					if(cardsPlayed.size() == 3)
					{
						highestCard = cardsPlayed.get(0);
						for(int i=1;i<=2;i++)
						{
							if(highestCard.getSuit().equalsIgnoreCase(cardsPlayed.get(i).getSuit()))
							{					
								if(ratings.get(highestCard.getCardNumber()) < ratings.get(cardsPlayed.get(i).getCardNumber()))
								{
									highestCard = cardsPlayed.get(i);
								} 
					
							} 
							else if(cardsPlayed.get(i).getSuit().equalsIgnoreCase(gameTrump)&&!highestCard.getSuit().equalsIgnoreCase(gameTrump))
							{
								highestCard = cardsPlayed.get(i);
							}
				
						}
						System.out.println("Highest card " + highestCard);
						handWinner = highestCard.getPlayerNumber();
					
						for(Player player:Players)
						{
							if(player.playerNumber==handWinner)
							{
								player.score= player.score+1;
								player.output.writeObject("You win the hand. Your score: "+ player.score );
								player.output.flush();
							} 
							else 
							{
								player.output.writeObject("Player "+ handWinner +" wins the hand");
								player.output.flush();
							}
						}
					
						cardsPlayed.clear();
						System.out.println("pLAYer "+ handWinner + " wins");
					}
				}

			
			public Card getShuffled()
			{
				Card shuffled = null;
				shuffled = shuffledDeck.get(0);
				shuffledDeck.remove(0);
				return shuffled;				
			}
			

			public void initializeRatings()
			{				
				ratings = new HashMap<String, Integer>();
				for(int i = 0 ; i < cardNumbers.length ; i++)
				{
					ratings.put(cardNumbers[i], i);
				}
			}
			

			public int distributeHand()
			{
				int index = 0;
				int initialScore;
				if(hands.isEmpty()){
				hands.add(-8);
				hands.add(-5);
				hands.add(-3);
				} 

				index = generator.nextInt(hands.size());
				initialScore = hands.get(index);
				hands.remove(index);
				return initialScore;

			}	

			public void Shuffle()
			{				
				int size = unshuffledDeck.size();
				int index = 0;				
				while(size>4){
					
					index = generator.nextInt(size);
					shuffledDeck.add(unshuffledDeck.get(index));
					unshuffledDeck.remove(index);
					size = unshuffledDeck.size();
				}
			}			
			
			public void initializeDeck()
			{				
				for(int i = 0;i<cardSuits.length;i++)
				{
					for(int j = 0;j<cardNumbers.length;j++)
					{
						unshuffledDeck.add(new Card(cardSuits[i], cardNumbers[j]));
					}
				}
	
			}

			public ArrayList<Card> getFreeCards()
			{
				return unshuffledDeck;
			}
			
		}
	}
	   
	   
	   

