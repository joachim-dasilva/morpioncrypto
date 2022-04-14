package server;

import java.net.*;
import java.io.*;
import java.util.ArrayList;

import game.Game;
import morpion.Morpion;
import player.Player;
import security.SecurityPK;
import security.SecuritySK;
import util.Print;

class Server extends Thread {
	private static ArrayList<Player> players = new ArrayList<Player>();
	private ServerSocket socket = null;
	
	public DataOutputStream out;
	public DataInputStream in;
	private int id = 0;

	public Server(ServerSocket serverSocket) {
		this.socket = serverSocket;
	}

	public void run() {
		try {
			// En attente de connexion d'un client
			Player player = new Player(this.socket.accept());
			players.add(player);
			
			// Vérification si la liste est paire alors il s'agit d'un nouveau duo
			if (players.size() % 2 == 0) {
				Game game = new Game(players.get(players.size()-2), player);
				game.start();
			} else {
				player.sendMessage("100".getBytes());
			}
		} catch (IOException ex) {
		}
	}

	public static void main(String args[]) throws Exception {
		ServerSocket sockserv = null;
		// Ouverture du Socket serveur sur le port 1234
		sockserv = new ServerSocket(1234);
		try {
			Print.success("Serveur lancé sur le port 1234");
			// Accepte la connexion de 10 joueurs au maximum
			for (int i = 0; i < 10; i++) {
				Server th = new Server(sockserv);
				th.start();
			}
			while (true) {
			}
		} finally {
			try {
				sockserv.close();
			} catch (IOException ex) {
			}
		}
	}
}