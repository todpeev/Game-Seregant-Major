package game;

public class Card {

	private String cardSuit;
	private String cardNumber;
	private Player player;
	
	public Card(String suit, String number){
		
		cardSuit = suit;
		cardNumber = number;
	}
	
	public String getSuit(){
		return cardSuit;
	}
	
	public String getCardNumber(){
		return cardNumber;
	}
	
	public String toString(){
		String card = cardNumber + " " + cardSuit;
		return card;
		
	}
	
	public void setPlayer(Player p){
		player = p;
	}
	
	public Player getPlayer(){
		return player;
	}
	
	
}
