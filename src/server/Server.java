package server;

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import game.Game;
import player.Player;
import util.Print;

class Server extends Thread {
	private static ArrayList<Player> players = new ArrayList<Player>();
	private static ArrayList<Server> clients = new ArrayList<Server>();
	private ServerSocket socket = null;

	public Server(ServerSocket socket) {
		this.socket = socket;
	}

	public void run() {
		try {
			Player player = new Player(this.socket.accept());
			players.add(player);
			clients.add(this);

			if (players.size() % 2 == 0) {
				Game game = new Game(players.get(players.size() - 2), player);
				game.start();
			}
		} catch (IOException ex) {
			clientLeave();
		}
	}

	private void clientLeave() {
		int index = clients.indexOf(this);
		int index2;
		if (index % 2 == 0)
			index2 = index - 1;
		else
			index2 = index + 1;
		players.get(index2 - 1).sendMessage("500".getBytes());
		players.remove(index - 1);
		clients.remove(index - 1);
		if (index > index2) {
			clients.remove(index2 - 1);
			players.remove(index2 - 1);
		} else {
			clients.remove(index2 - 2);
			players.remove(index2 - 2);
		}
	}

	public static void main(String args[]) throws Exception {
		ServerSocket sockserv = null;
		sockserv = new ServerSocket(1234);
		try {
			Print.success("Serveur lancé sur le port 1234");
			for (int i = 0; i < 10; i++) {
				Server th = new Server(sockserv);
				th.start();
			}
			while (true) {
			}
		} finally {
			try {
				Print.alert("Serveur arrêté");
				sockserv.close();
			} catch (IOException ex) {
			}
		}
	}
}