package game;

import morpion.Morpion;
import player.Player;

public class Game {
	private Player player1;
	private Player player2;
	private Morpion morpion;

	private int winner = 0;
	private int tour = 0;

	/**
	 * Initialise le morpion ainsi et ajoute les joueurs au jeu
	 * 
	 * @param joueur 1 (premier client connecté)
	 * @param joueur 1 (deuxième client connecté)
	 */
	public Game(Player player1, Player player2) {
		this.player1 = player1;
		this.player2 = player2;
		this.morpion = new Morpion();
	}

	/**
	 * Permet de gérer le déroulement de la partie
	 */
	public void start() {
		player1.sendMessage(sendResponse(101, morpion));
		player2.sendMessage(sendResponse(101, morpion));
		player1.sendMessage(sendResponse(201, morpion)); // Tour du joueur
		player2.sendMessage(sendResponse(202, morpion)); // Tour adverse

		// Tant qu'il n'y a pas de gagnant
		while (winner == 0) {
			// Tour du joueur n°1
			int jetonPos = playerTour(player1);
			morpion.placeToken(jetonPos, 1);

			// Vérification de l'état du morpion
			if ((winner = morpion.winConditions()) == 1) {
				player1.sendMessage(sendResponse(204, morpion));
				player2.sendMessage(sendResponse(205, morpion));
				break;
			}
			tour++;
			// Si le morpion est finis alors il y a égalité
			if (tour >= 9) {
				player1.sendMessage(sendResponse(203, morpion));
				player2.sendMessage(sendResponse(203, morpion));
				winner = -1;
				break;
			}

			player1.sendMessage(sendResponse(202, morpion));
			player2.sendMessage(sendResponse(201, morpion));

			// Tour du joueur n°2
			jetonPos = playerTour(player2);
			morpion.placeToken(jetonPos, 2);

			// Vérification de l'état du morpion
			if ((winner = morpion.winConditions()) == 2) {
				player1.sendMessage(sendResponse(205, morpion));
				player2.sendMessage(sendResponse(204, morpion));
				break;
			}
			tour++;

			player2.sendMessage(sendResponse(202, morpion));
			player1.sendMessage(sendResponse(201, morpion));
		}
	}

	
	/**
	 * Ajoute le jeton du joueur ayant envoyé le message sur le morpion
	 * 
	 * @param client ayant envoyé le message
	 * @return la position du jeton
	 */
	private int playerTour(Player player) {
		int position = readResponse(player);
		// Vérification de la validité des données récupérées
		while (position < 1 || position > 9 || morpion.getValueAtIndex(position) != 0) {
			if (position == -1)
				player.sendMessage(sendResponse(405, morpion));
			else if (position < 1 || position > 9)
				player.sendMessage(sendResponse(404, morpion));
			else
				player.sendMessage(sendResponse(401, morpion));
			position = readResponse(player);
		}
		return position;
	}

	
	/**
	 * Transforme le code envoyé par le serveur (entier entre 100 et 500) ainsi que le morpion
	 * en tableau d'octets pour pouvoir l'envoyer au client
	 * 
	 * @param code envoyé par le serveur
	 * @param le morpion
	 * @return le code et le morpion sous forme de tableau d'octets
	 */
	private byte[] sendResponse(int code, Morpion morpion) {
		String response = Integer.toString(code) + " : " + morpion.toString();
		return response.getBytes();
	}

	
	/**
	 * Lis le message envoyé par le client
	 * 
	 * @param le joueur ayant envoyé le message
	 * @return le message envoyé par le client
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
