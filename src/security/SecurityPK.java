package security;

import java.security.*;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

public class SecurityPK {
	private PublicKey keyPublic;
	private PrivateKey keyPrivate;
	private Cipher cipher;

	
	/**
	 * génère une clé publique
	 */
	public SecurityPK() {
		try {
			cipher = Cipher.getInstance("RSA");
		} catch (Exception e) {
		}
	}

	
	/**
	 * Défini l'attribut keyPublic avec la clé publique donnée en paramètre
	 * 
	 * @param la clé publique encodée
	 */
	public void setPublicKey(byte[] keyPublicEncoded) {
		try {
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(keyPublicEncoded);
			this.keyPublic = keyFactory.generatePublic(publicKeySpec);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	/**
	 * Génère un couple clé publique et clé privée
	 */
	public KeyPair generateKeys() {
		try {
			KeyPairGenerator keygen = KeyPairGenerator.getInstance("RSA");
			keygen.initialize(1024);
			KeyPair keyPair = keygen.generateKeyPair();
			keyPublic = keyPair.getPublic();
			keyPrivate = keyPair.getPrivate();
			return keyPair;
		} catch (Exception e) {
		}
		return null;
	}

	/**
	 * récupère la clé publique
	 * 
	 * @return la clé publique
	 */
	public PublicKey getPublicKey() {
		return keyPublic;
	}

	
	/**
	 * Crypt un message (sous forme de chaine de charactère) à l'aide de la clé publique
	 * 
	 * @param message à crypter
	 */
	private byte[] encrypt(String message) {
		try {
			cipher.init(Cipher.ENCRYPT_MODE, this.keyPublic);
			return cipher.doFinal(message.getBytes());
		} catch (Exception e) {
		}
		return new byte[0];
	}

	
	/**
	 * Crypt un message (sous forme de taleau d'octet) à l'aide de la clé publique
	 * 
	 * @param message à crypter
	 */
	public byte[] encrypt(byte[] messageEncoded) {
		try {
			cipher.init(Cipher.ENCRYPT_MODE, this.keyPublic);
			return cipher.doFinal(messageEncoded);
		} catch (Exception e) {
		}
		return null;
	}

	
	/**
	 * Décrypt un message à l'aide de la clé privée
	 * 
	 * @param message
	 */
	public byte[] decrypt(byte[] message) {
		try {
			cipher.init(Cipher.DECRYPT_MODE, this.keyPrivate);
			return cipher.doFinal(message);
		} catch (Exception e) {
		}
		return null;
	}
}
