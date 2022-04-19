package client;

import java.net.*;

import security.SecurityPK;
import security.SecuritySK;
import util.Print;

import java.io.*;

class Client extends Thread {

	private Socket socket = null;
	private SecuritySK secret;
	private SecurityPK rsa;

	/**
	 * Initialisation de la socket
	 * génère une clé secrète ainsi qu'une clé publique
	 * 
	 * @param socket
	 */
	public Client(Socket s) {
		this.socket = s;
		this.secret = new SecuritySK();
		this.rsa = new SecurityPK();
	}

	/**
	 * Méthode définie par la classe Thread.
	 * Elle permet d'attendre la réponse du serveur afin de la traité.
	 * La réponse du serveur doit être un entier compris entre 100 et 500.
	 * En fonction de la réponse reçue, le client peut envoyer un message au serveur
	 */
	public void run() {
		DataInputStream in;
		DataOutputStream out;
		BufferedReader reader;
		byte mess[];
		try {
			in = new DataInputStream(this.socket.getInputStream());
			out = new DataOutputStream(this.socket.getOutputStream());

			// Génération de la clé publique (et clé privée)
			this.rsa.generateKeys();
			out.write(this.rsa.getPublicKey().getEncoded());
			byte[] secretKey = new byte[128];
			in.read(secretKey, 0, 128);
			byte[] secretKeyDecoded = this.rsa.decrypt(secretKey);
			this.secret.setSecretKey(secretKeyDecoded);

			while (true) {
				mess = new byte[64];
				in.readFully(mess); // Lecture du message entrant (venant du serveur). Il s'agit d'un code qui,
				mess = this.secret.decrypt(mess);

				switch (new String(mess).substring(0, 3)) { // une fois interprété, éxécute l'un des cas ci-dessous.

				// OK, en attente d’un deuxième joueur - attribution joueur n°1
				case "100":
					System.out.println("[Morpion] En attente d'un deuxième joueur...");
					break;
				// Lancement de la partie - attribution joueur n°2
				case "101":
					System.out.println("[Morpion] Lancement de la partie !");
					break;
				// Tour du joueur courant
				case "201":
					System.out.println(new String(mess).substring(6));
					System.out.println("[Morpion - Partie en cours] C'est à votre tour !");
					System.out.println("Choisir une position :");
					reader = new BufferedReader(new InputStreamReader(System.in));
					out.write(reader.readLine().getBytes()); // Lecture du message sortant sous forme de tableau d'octets
					break;
				// Tour adverse
				case "202":
					System.out.println(new String(mess).substring(6));
					System.out.println("[Morpion  - Partie en cours] C'est au tour de l'adversaire !");
					break;
				// Egalité (Fin de la partie)
				case "203":
					System.out.println(new String(mess).substring(6));
					System.out.println("[Morpion - Egalité] Aucun gagnant !");
					break;
				// Victoire (Fin de la partie)
				case "204":
					System.out.println(new String(mess).substring(6));
					System.out.println("[Morpion - Victoire] Félicitations, vous avez gagné !");
					try {
						this.socket.close();
					} catch (IOException ex) {
					}
					break;
				// Défaite (Fin de la partie)
				case "205":
					System.out.println(new String(mess).substring(6));
					System.out.println("[Morpion - Défait] Dommage, vous avez perdu !");
					try {
						this.socket.close();
					} catch (IOException ex) {
					}
					break;
				// Jeton déjà sur la case
				case "401":
					System.out.println("[Morpion - Erreur] Jeton déjà sur la case");
					System.out.println("Choisir une nouvelle position :");
					reader = new BufferedReader(new InputStreamReader(System.in));
					out.write(reader.readLine().getBytes());
					break;
				// Les coordonnées n’ont pas été trouvées
				case "404":
					System.out.println("[Morpion - Erreur] Coordonnées inexistantes");
					System.out.println("Choisir une nouvelle position :");
					reader = new BufferedReader(new InputStreamReader(System.in));
					out.write(reader.readLine().getBytes());
					break;
				// Format de données non autorisé
				case "405":
					System.out.println("[Morpion - Erreur] Format non autorisé");
					System.out.println("Choisir une nouvelle position :");
					reader = new BufferedReader(new InputStreamReader(System.in));
					out.write(reader.readLine().getBytes());
					break;
				// L’adversaire s’est déconnecté
				case "500":
					System.out.println("L\'adversaire s'est déconnecté");
				}
			}
		} catch (IOException ex) {
		}
	}

	public static void main(String args[]) throws Exception {
		Socket sockclient = null;
		DataInputStream in;
		DataOutputStream out;
		BufferedReader buffer;
		byte mess[];
		try {
			// Création du socket client et connexion à l'IP du serveur avec le port 1234
			sockclient = new Socket("localhost", 1234);
			Boolean isConnected = true;
			new Client(sockclient).start();
			while (true) {
			} // Sert à rester dans le try pour ne pas aller dans le finally (fermeture de la socket)
		} finally {
			try {
				sockclient.close();
			} catch (IOException ex) {
			}
		}
	}
}
