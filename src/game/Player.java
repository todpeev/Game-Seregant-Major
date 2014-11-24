package game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Player {

	private String playerName;
	private ArrayList<Card> playerCards;
	private Scanner in = new Scanner(System.in);
	private int score;
	private ArrayList<String> pastTrumps;
	private String[] allTrumps = {"pika","kypa","karo","spatia","bez koz","pass"};
	private String trump;
	private boolean wonHand = false;
	private ArrayList<Integer> pastGamesScores;
	private Card temp;
	private Game game;
	
	public Player(){
		
		System.out.println("Please enter player name: ");
		playerName = in.nextLine();
		pastTrumps = new ArrayList<String>();
		wonHand = false;
		playerCards = new ArrayList<Card>();
		pastGamesScores =  new ArrayList<Integer>();
	}
	
	public void markCards(){
		for(Card card:playerCards){
			card.setPlayer(this);
		}
	}
	
	public void getCards(Card card){
		playerCards.add(card);
	}
	
	public void setInitialScore(int points){
		score = points;
	}
	
	public void viewCards(){
		System.out.println(this.getName() + ": "+ playerCards);
	}
	
	public int getScore(){
		return score;
	}
	
	public boolean chooseTrump(){
		int index = 0;
		System.out.println("Player " +getName()+" will select trump (enter number from 0-5): " + Arrays.toString(allTrumps));
		index = in.nextInt();
		
		
		if(!pastTrumps.contains(allTrumps[index])){
			pastTrumps.add(allTrumps[index]);
			System.out.println("Trump "+allTrumps[index] + " selected\n\n" );
			trump = allTrumps[index];
			return false;
		} else {
			System.out.println(this.getName()+ " has already called this trump");
			return true;
		}
	}
	
	public String getTrump(){
		return trump;
	}
	public String getName(){
		return playerName;
	}
	
	public void changeCards(){
		ArrayList<Card> freeCards = new ArrayList<Card>();
		freeCards = game.getFreeCards();
		System.out.println(getName() + " is changin cards.");
		System.out.print(getName() + " has the following cards: ");
		viewCards();
		System.out.println("Cards to take form: " + game.getFreeCards());
		System.out.println("To take a card first enter index of a card from the player cards (from 0 to 15): ");
		int index = in.nextInt();
		temp = playerCards.get(index);
		System.out.println("Now enter the index (from 0 to 3) of the cards to take from");
		int index_1 = in.nextInt();
		playerCards.set(index, freeCards.get(index_1));
		freeCards.set(index_1,temp);
		viewCards();
	}
	
	public Card getIndividualCard(int index){
		return playerCards.get(index);
	}
	
	public void setWonHand(Boolean check){
		wonHand= check;
	}
	
	public Boolean getWonHand(){
		return wonHand;
	}
	
	public Card Play(){
		Card cardPlayed;
		System.out.println(this.getName()+" turn to play");
		int index = in.nextInt();
		cardPlayed = playerCards.get(index);
		playerCards.remove(index);
		return cardPlayed;
	}
	
	public void increaseScore(){
		score = score + 1;
	}
	
	public void recordScore(){
		pastGamesScores.add(score);
	}
	
	public void clearPastScores(){
		pastGamesScores.clear();
	}
	
	public void setGame(Game g){
		game = g;
	}
	
	public void clearCards(){
		playerCards.clear();
	}
}
