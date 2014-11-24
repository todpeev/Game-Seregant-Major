package game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

public class Game {

	private ArrayList<Card> unshuffledDeck;
	private ArrayList<Card> shuffledDeck;
	private ArrayList<Player> Players;
	private String[] cardNumbers ={"2","3","4","5","6","7","8","9","10","J","D","K","A"};
	private String[] cardSuits = {"pika","kypa","karo","spatia"};
	private Random generator;
	private String gameTrump;
	private Scanner in = new Scanner(System.in);
	private HashMap<String,Integer> ratings; 
	private ArrayList<Card> cardsPlayed;
	private Boolean IsfirstGame;
	private ArrayList<Player> playOrder;
	
	//constructor, initcializira igrata;
	public Game(){
		generator = new Random();
		shuffledDeck = new ArrayList<Card>();//teste razburkani karti;
		unshuffledDeck = new ArrayList<Card>();//teste nerazburkani karti
		Players = new ArrayList<Player>();//spisuk s igrachite;
		cardsPlayed = new ArrayList<Card>();//spisuk sas karti izigrani v edna ruka (3 karti);
		playOrder = new ArrayList<Player>();//izpolzva se za da zapishe igrachite v reda v koito shte davat karti v edna ruka;
		addPlayer();//dobavq igrach v Players
		
		initializeRatings();//za vsqka karta naznachava tochki, koito se izpolzvat za da se opredeli koq karta e po silna;
		
	}
	
	public void startGame(){
		
		//za vseki igrach se zapisva che ychastva v segashnata igra;
		for(Player player:Players){
			player.setGame(this);
		}
		
		// Igraqt se obshto 15 igri(t.e. 15 razdavaniq):
		for(int i = 1; i<=15;i++){
			initializeDeck(); // suzdava ArrayList sas nerazburkani 52 karti koeto se kazva  unshuffledDeck;
			Shuffle(); // suzdava ArrayList shuffledDeck sas 52 karti koito sa razburkani,izpolzvaiki unshuffledDeck;
			distributeCards(); // ot shuffledDeck se razdavat karti na igrachite,po 16 karti na chovek;
			distributeHand(); // na slychaen princip razpredelq tochki na igrachite:-3,-5,-8; 
			chooseTrump(); // igracha s -8 tochki izbira koz;
			
			//Ako igracha s -8 tochki e izbral "pass" se zapochva nanovo:
			if(gameTrump.equals("pass")){
				unshuffledDeck.clear();
				shuffledDeck.clear();
				for(Player player:Players){
					player.clearCards();
				}
				continue;
			}
			changeCards(); //igracha s -8 tochki izbira karti ot 4-te ostanali nerazdadeni karti;
			
			//za vsqka karta se okazva na koi igrach e razdadena:
			for(Player player:Players){
				player.markCards();
			}
			
			//zapochva se igrata, igraqt se 16 ruce:
			for(int j = 1;j<=16; j++){
				//otbelqzva se dalie e purva igra:
				if(j ==1){
					IsfirstGame = true;
				} else {
					IsfirstGame = false;
				}
				
				playOrder = getPlayOrder();//reshava se koiigrach da igrae purvi karta;
				HandWinner();//reshava se koi igrach pecheli rukata;
				System.out.println("\n\n");
				
			}//prikluchva edinichnata igra, vsichki igrachi sa dali kartite si;
			
			System.out.println("\n\n\n Game Over \n\n\n");
			recordScore();//za vseki igrach se zapisva krainiq rezultat ot igrata predi da se pristupi kam sledvashtoto razdavane;
			unshuffledDeck.clear();//izchistva ot karti unshuffledDeck
			shuffledDeck.clear();//izchistva ot karti shuffledDeck
		
		}
		clearPastScores();//pochistva zapisite na tochkite za igrachite v predishnite igri
	}
	
	public void clearPastScores(){
		for(Player player:Players){
			player.clearPastScores();
		}
	}
	
	public void recordScore(){
		for(Player player:Players){
			player.recordScore();
		}
	}
	public void HandWinner(){
		Card highestCard;
		
		for(Player player:playOrder){
			cardsPlayed.add(player.Play());
		}
		
		highestCard = cardsPlayed.get(0);
		System.out.println(highestCard);
		for(int i=1;i<=2;i++){
			if(highestCard.getSuit().equalsIgnoreCase(cardsPlayed.get(i).getSuit())){
			
				if(ratings.get(highestCard.getCardNumber())<ratings.get(cardsPlayed.get(i).getCardNumber())){
					highestCard = cardsPlayed.get(i);
				} 
			
			} else if(cardsPlayed.get(i).getSuit().equalsIgnoreCase(gameTrump)&&!highestCard.getSuit().equalsIgnoreCase(gameTrump)){
				highestCard = cardsPlayed.get(i);
			}
		
		}
		
		highestCard.getPlayer().increaseScore();
		highestCard.getPlayer().setWonHand(true);
		System.out.println("hand is won by: " +highestCard.getPlayer().getName()+" ,his score:"+ highestCard.getPlayer().getScore());
		cardsPlayed.clear();
	}
	
	public ArrayList<Player> getPlayOrder(){
		ArrayList<Player> Order = new ArrayList<Player>();
		int index =0;
		
		for(Player player:Players){
			int a = player.getScore();
			
			if((player.getWonHand()==true) ||(IsfirstGame && (a == -8))){
				
				Order.add(player);
				player.setWonHand(false);
				index=Players.indexOf(player);
			}
		}
		
		for(int i =1;i<=2;i++){
			if(index+1>2){
				index = 0;
			} else {
				index=index+1;
			}
			Order.add(Players.get(index));
		}
		
		return Order;
	}
	
	
	public void initializeRatings(){
		ratings = new HashMap<String, Integer>();
		for(int i = 0 ; i < cardNumbers.length ; i++){
			ratings.put(cardNumbers[i], 13-i);
		}
	}
	
	public void changeCards(){

		String done = "";
		
		for(Player player:Players){
			if(player.getScore()==-8){
				while(true){
					player.changeCards();
					System.out.println("Are you done changing cards (press y/n)");
					done = in.next();
					if(done.equalsIgnoreCase("y")){
						break;
					}
				}
			}
		}
	}
	
	public void chooseTrump(){
		
		Boolean trumpIsNotChosen = true;
		
		for(Player player:Players){
			if(player.getScore() == -8){
				System.out.println("\n\n"+player.getName()+ " "+ "chooses trump");
				
				while(trumpIsNotChosen){
					trumpIsNotChosen = player.chooseTrump();
					if(trumpIsNotChosen == false){
						gameTrump = player.getTrump();
					}
				}
			}
		}
	}
	
	public void distributeHand(){
		int index = 0;
		ArrayList<Integer> hands = new ArrayList<Integer>();
		hands.add(-8);
		hands.add(-5);
		hands.add(-3);
		
		for(Player player:Players){
			index = generator.nextInt(hands.size());
			player.setInitialScore(hands.get(index));
			hands.remove(index);
			
		}
	}
	
	
	public void distributeCards(){
		int dummy = 1;
		for(Player player:Players){
			while(dummy%17!=0){
				player.getCards(shuffledDeck.get(0));
				shuffledDeck.remove(0);
				dummy++;
			}
			dummy =1;
			player.viewCards();
			
		}
	}
	
	public void Shuffle(){
		
		int size = unshuffledDeck.size();
		int index = 0;
		
		while(size>4){
			
			index = generator.nextInt(size);
			shuffledDeck.add(unshuffledDeck.get(index));
			unshuffledDeck.remove(index);
			size = unshuffledDeck.size();
		}
		
		
	}
	
	
	public void initializeDeck(){
		
		for(int i = 0;i<cardSuits.length;i++){
			for(int j = 0;j<cardNumbers.length;j++){
				unshuffledDeck.add(new Card(cardSuits[i], cardNumbers[j]));
			}
		}
		System.out.println("Unshuffled deck: "+ unshuffledDeck);
	}
	
	public void addPlayer(){
		Players.add(new Player());
	}
	
	public ArrayList<Card> getFreeCards(){
		return unshuffledDeck;
	}
	
}
