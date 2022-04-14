package game;

import morpion.Morpion;
import player.Player;

public class Game {
	private Player player1;
	private Player player2;
	private Morpion morpion;

	public Game(Player player1, Player player2) {
		this.player1 = player1;
		this.player2 = player2;
		this.morpion = new Morpion();
	}

	public void start() {
		int position;
		int winner = 0;
		int nbTour = 0;

		player1.sendMessage("101".getBytes());
		player2.sendMessage("101".getBytes());
		player1.sendMessage(sendResponse(201, morpion)); // Tour du joueur
		player2.sendMessage(sendResponse(202, morpion)); // Tour adverse

		// Tant qu'il n'y a pas de gagnant
		while (winner == 0) {

			// Tour du joueur n°1
			position = readResponse(player1);
			// Vérification de la validité des données récupérées
			while (position < 1 || position > 9 || morpion.getValueAtIndex(position) != 0) {
				if (position == -1)
					player1.sendMessage("405".getBytes());
				else if (position < 1 || position > 9)
					player1.sendMessage("404".getBytes());
				else
					player1.sendMessage("401".getBytes());
				position = readResponse(player1);
			}
			morpion.placeToken(position, 1);
			// Vérification de l'état du morpion
			if ((winner = morpion.winConditions()) == 1) {
				player1.sendMessage(sendResponse(204, morpion));
				player2.sendMessage(sendResponse(205, morpion));
				break;
			}
			nbTour++;
			// Si le morpion est finis alors il y a égalité
			if (nbTour >= 9) {
				player1.sendMessage(sendResponse(203, morpion));
				player2.sendMessage(sendResponse(203, morpion));
				winner = -1;
				break;
			}

			player1.sendMessage(sendResponse(202, morpion));
			player2.sendMessage(sendResponse(201, morpion));

			// Tour du joueur n°2
			position = readResponse(player2);
			// Vérification de la validité des données récupérées
			while (position < 1 || position > 9 || morpion.getValueAtIndex(position) != 0) {
				if (position == -1)
					player2.sendMessage("405".getBytes());
				else if (position < 1 || position > 9)
					player2.sendMessage("404".getBytes());
				else
					player2.sendMessage("401".getBytes());
				position = readResponse(player2);
			}
			morpion.placeToken(position, 2);
			// Vérification de l'état du morpion
			if ((winner = morpion.winConditions()) == 2) {
				player1.sendMessage(sendResponse(205, morpion));
				player2.sendMessage(sendResponse(204, morpion));
				break;
			}
			nbTour++;
			// Si le morpion est finis alors il y a égalité
			player2.sendMessage(sendResponse(202, morpion));
			player1.sendMessage(sendResponse(201, morpion));
		}
	}

	/*
	 * Réponse à envoyer à un joueur
	 */
	private byte[] sendResponse(int code, Morpion morpion) {
		String response = Integer.toString(code) + " : " + morpion.toString();
		return response.getBytes();
	}

	/*
	 * Lecture du message d'un joueur
	 */
	private int readResponse(Player player) {
		try {
			byte[] mess = new byte[80];
			player.in.read(mess, 0, 80);
			String response = new String(mess).trim();
			int value = Integer.parseInt(response);
			return value;
		} catch (Exception ex) {
		}
		return -1;
	}
}
