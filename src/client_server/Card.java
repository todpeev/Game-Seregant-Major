package client_server;

import java.io.Serializable;

import javax.swing.Icon;
import javax.swing.ImageIcon;

public class Card implements Serializable {


	private static final long serialVersionUID = 1L;
	private String cardSuit;
	private String cardNumber;
	private transient Icon cardIcon;
	private int playerNumber;
	
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

	public void setIcon(String i){
		String icon = "cards/" + i + ".gif";
		cardIcon = new ImageIcon(getClass().getResource(icon));
	}
	
	public Icon getIcon(){
		return cardIcon;
	}
	
	public String getCardName(){
		String name = cardNumber + " " + cardSuit;
		return name;
	}
	
	public void setPlayerNumber(int number){
		playerNumber = number;
	}
	
	public int getPlayerNumber(){
		return playerNumber;
	}
	
}
