package game;

public class Main {

	public static void main(String[] args) {
		
		Game g = new Game();
		g.addPlayer();
		g.addPlayer();
		System.out.println("\n\n");
		g.startGame();
	}

}
