package server;

import java.net.*;
import java.io.*;
import java.util.ArrayList;

import security.SecurityPK;
import security.SecuritySK;
import util.Print;

class Server extends Thread {
	static ArrayList<Server> allClient = new ArrayList<Server>();

	private ServerSocket socket = null;
	private SecurityPK rsa;
	private SecuritySK secret;
	public DataOutputStream out;
	public DataInputStream in;
	private int id = 0;

	public Server(ServerSocket ss) {
		this.socket = ss;
		this.rsa = new SecurityPK();
		this.secret = new SecuritySK();
	}

	public void run() {
		int position;
		int winner = 0;
		int nbTour = 0;
		try {
			// En attente de connexion d'un client
			Socket sockcli = this.socket.accept();
			this.in = new DataInputStream(sockcli.getInputStream());
			this.out = new DataOutputStream(sockcli.getOutputStream());

			// Envoie de la clé secrète chiffré avec la clé publique
			byte[] publicKey = new byte[1024];
			in.read(publicKey, 0, 1024);
			this.rsa.setPublicKey(publicKey);
			byte[] secretKeyGenerated = this.secret.generateSecretKey();
			byte[] secretKeyEncript = this.rsa.encrypt(secretKeyGenerated);
			out.write(secretKeyEncript);

			allClient.add(this);
			// Vérification si la liste est paire alors il s'agit d'un nouveau duo
			if (allClient.size() % 2 == 0) {
				Server player1 = allClient.get(allClient.size() - 2);
				Server player2 = allClient.get(allClient.size() - 1);
				
				// Lancement de la partie pour les deux joueurs
				player1.out.write(writeEncrypt("101".getBytes()));
				player2.out.write(writeEncrypt("101".getBytes()));

				int[][] morpion = { { 0, 0, 0 }, { 0, 0, 0 }, { 0, 0, 0 } };

				player1.out.write(writeEncrypt(sendResponse(201, morpion))); // Tour du joueur
				player2.out.write(writeEncrypt(sendResponse(202, morpion))); // Tour adverse

				// Tant qu'il n'y a pas de gagnant
				while (winner == 0) {

					// Tour du joueur n°1
					position = readResponse(player1);
					// Vérification de la validité des données récupérées
					while (position < 1 || position > 9 || getValueAtIndex(position, morpion) != 0) {
						if (position == -1)
							player1.out.write(writeEncrypt("405".getBytes()));
						else if (position < 1 || position > 9)
							player1.out.write(writeEncrypt("404".getBytes()));
						else
							player1.out.write(writeEncrypt("401".getBytes()));
						position = readResponse(player1);
					}
					morpion = placeToken(position, morpion, 1);
					// Vérification de l'état du morpion
					if ((winner = winConditions(morpion)) == 1) {
						player1.out.write(writeEncrypt(sendResponse(204, morpion)));
						player2.out.write(writeEncrypt(sendResponse(205, morpion)));
						break;
					}
					nbTour++;
					// Si le morpion est finis alors il y a égalité
					if (nbTour >= 9) {
						player1.out.write(writeEncrypt(sendResponse(203, morpion)));
						player2.out.write(writeEncrypt(sendResponse(203, morpion)));
						winner = -1;
						break;
					}

					player1.out.write(writeEncrypt(sendResponse(202, morpion)));
					player2.out.write(writeEncrypt(sendResponse(201, morpion)));

					// Tour du joueur n°2
					position = readResponse(player2);
					// Vérification de la validité des données récupérées
					while (position < 1 || position > 9 || getValueAtIndex(position, morpion) != 0) {
						if (position == -1)
							player2.out.write("405".getBytes());
						else if (position < 1 || position > 9)
							player2.out.write("404".getBytes());
						else
							player2.out.write("401".getBytes());
						position = readResponse(player2);
					}
					morpion = placeToken(position, morpion, 2);
					// Vérification de l'état du morpion
					if ((winner = winConditions(morpion)) == 2) {
						player1.out.write(writeEncrypt(sendResponse(205, morpion)));
						player2.out.write(writeEncrypt(sendResponse(204, morpion)));
						break;
					}
					nbTour++;
					// Si le morpion est finis alors il y a égalité
					player2.out.write(writeEncrypt(sendResponse(202, morpion)));
					player1.out.write(writeEncrypt(sendResponse(201, morpion)));
				}
			} else {
				this.out.write(writeEncrypt("100".getBytes()));
			}
		} catch (IOException ex) {
			int index = allClient.indexOf(this);
			int index2;
			if (index % 2 == 0)
				index2 = index - 1;
			else
				index2 = index + 1;
			try {
				allClient.get(index2 - 1).out.write("500".getBytes());
				allClient.remove(index - 1);
				if (index > index2)
					allClient.remove(index2 - 1);
				else
					allClient.remove(index2 - 2);
			} catch (IOException ex2) {
			}
		}
	}

	/*
	 * Vérification de l'état du morpion
	 */
	public int winConditions(int[][] morpion) {
		int token;
		boolean wrong;
		// Lignes
		for (int row = 0; row < morpion.length; row++) {
			token = morpion[row][0];
			wrong = false;
			for (int col = 0; !wrong && col < morpion[0].length; col++) {
				if (morpion[row][col] == 0 || morpion[row][col] != token)
					wrong = true;
			}
			if (!wrong)
				return token;
		}
		// Colonnes
		for (int col = 0; col < morpion[0].length; col++) {
			token = morpion[0][col];
			wrong = false;
			for (int row = 0; !wrong && row < morpion.length; row++) {
				if (morpion[row][col] == 0 || morpion[row][col] != token)
					wrong = true;
			}
			if (!wrong)
				return token;
		}
		// Diagonale 1
		token = morpion[0][0];
		wrong = false;
		for (int i = 0; !wrong && i < morpion.length; i++) {
			if (morpion[i][i] == 0 || morpion[i][i] != token)
				wrong = true;
		}
		if (!wrong)
			return token;

		// Diagonale 2
		token = morpion[morpion.length - 1][0];
		wrong = false;
		for (int i = 0; !wrong && i < morpion.length; i++) {
			if (morpion[morpion.length - 1 - i][i] == 0 || morpion[morpion.length - 1 - i][i] != token)
				wrong = true;
		}
		if (!wrong)
			return token;
		return 0;
	}

	/*
	 * Placer le jeton d'un joueur sur un morpion
	 */
	public int[][] placeToken(int a, int[][] morpion, int player) {
		int row = 0;
		while (a - 3 > 0) {
			row++;
			a = a - 3;
		}
		morpion[row][a - 1] = player;
		return morpion;
	}

	/*
	 * Récupérer la valeur de la case d'un morpion
	 */
	public int getValueAtIndex(int index, int[][] morpion) {
		int row = 0;
		while (index - 3 > 0) {
			row++;
			index = index - 3;
		}
		return morpion[row][index - 1];
	}

	/*
	 * Lecture du message d'un joueur
	 */
	public int readResponse(Server player) {
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

	/*
	 * Réponse à envoyer à un joueur
	 */
	public byte[] sendResponse(int code, int[][] morpion) {
		if (morpion.length > 0) {
			String result = Integer.toString(code) + " : ";
			for (int i = 0; i < morpion.length; i++) {
				for (int j = 0; j < morpion[0].length; j++) {
					char value = ' ';
					if (morpion[i][j] == 1)
						value = 'O';
					else if (morpion[i][j] == 2)
						value = 'X';
					result += " " + value + " " + "|";
				}
				result = result.substring(0, result.length() - 2);
				result += "\n--- --- ---\n";
			}
			result = result.substring(0, result.length() - 12);
			return result.getBytes();
		}
		return Integer.toString(code).getBytes();
	}
	
	public byte[] writeEncrypt(byte[] message) {
		return this.secret.encrypt(new String(message));
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
			while (true) {}
		} finally {
			try {
				sockserv.close();
			} catch (IOException ex) {
			}
		}
	}
}