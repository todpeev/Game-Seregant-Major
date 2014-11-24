package client_server;

import javax.swing.JFrame;

public class ServerTest {

	public static void main(String[] args) {
		Server s = new Server();
		s.execute();
		s.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

	}

}
