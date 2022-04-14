package security;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import util.Print;

public class SecuritySK {
	private SecretKey secretKey;
	private Cipher cipher;

	
	/**
	 * génère une clé secrète
	 */
	public SecuritySK() {
		try {
			cipher = Cipher.getInstance("DES");
		} catch (Exception e) {}
	}

	
	/**
	 * Génère une clé secrète de taille 56 bits.
	 * 
	 * @return la clé secrète encodée
	 */
	public byte[] generateSecretKey() {
		try {
			KeyGenerator keygen = KeyGenerator.getInstance("DES");
			keygen.init(56);
			secretKey = keygen.generateKey();
		} catch (Exception e) {
			System.out.println(e);
		}
		return this.secretKey.getEncoded();
	}

	
	/**
	 * défini l'attribut secretKey avec la clé secrète donnée en paramètre
	 * 
	 * @param la clé encodée
	 */
	public void setSecretKey(byte[] keyEncoded) {
		secretKey = new SecretKeySpec(keyEncoded, 0, keyEncoded.length, "DES");
	}
	
	
	/**
	 * récupère la clé secrète
	 * 
	 * @return la clé secrète
	 */
	public SecretKey getSecretKey() {
		return this.secretKey;
	}

	
	/**
	 * Crypt un message à l'aide de la clé secrète
	 * 
	 * @param message à crypter
	 * @return le message crypté sous la forme d'un tableau d'octets
	 */
	public byte[] encrypt(String message) {
		try {
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			return cipher.doFinal(message.getBytes());
		} catch (Exception e) {}
		return new byte[0];
	}

	
	/**
	 * Décrypt un message à l'aide de la clé secrète
	 * @param le message à décrypter
	 * @return 
	 */
	public byte[] decrypt(byte[] message) {
		try {
			cipher.init(Cipher.DECRYPT_MODE, secretKey);
			return cipher.doFinal(message);
		} catch (Exception e) {}
		return new byte[0];
	}
}
