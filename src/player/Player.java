package player;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import security.SecurityPK;
import security.SecuritySK;
import util.Print;

public class Player {
	private SecurityPK rsa;
	private SecuritySK secret;

	public DataOutputStream out;
	public DataInputStream in;


	/**
	 * Génère une clé secrète et une clé publique
	 * Récupère la clé publique du serveur et envoi la clé secrète au serveur
	 */
	public Player(Socket socket) {
		this.rsa = new SecurityPK();
		this.secret = new SecuritySK();

		try {
			this.in = new DataInputStream(socket.getInputStream());
			this.out = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
		}

		waitForPublicKey();
		sendSecretKey();
	}


	/**
	 * Récupère la clé publique du serveur et attribut la clé à la clé RSA
	 */
	private void waitForPublicKey() {
		byte[] publicKey = new byte[1024];
		try {
			this.in.read(publicKey, 0, 1024);
		} catch (IOException e) {
		}
		this.rsa.setPublicKey(publicKey);
	}


	/**
	 * Envoi la clé secrète encryptée vers le serveur
	 */
	private void sendSecretKey() {
		byte[] secretKeyGenerated = this.secret.generateSecretKey();
		byte[] secretKeyEncript = this.rsa.encrypt(secretKeyGenerated);
		try {
			this.out.write(secretKeyEncript);
		} catch (IOException e) {
		}
	}


	/**
	 * Envoi un message encrypté vers le serveur
	 */
	public void sendMessage(byte[] message) {
		byte[] messageEncoded = this.secret.encrypt(new String(message));
		try {
			this.out.write(messageEncoded);
		} catch (IOException e) {
		}
	}
}
