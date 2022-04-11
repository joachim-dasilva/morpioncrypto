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

	public SecurityPK() {
		try {
			cipher = Cipher.getInstance("RSA");
		} catch (Exception e) {
		}
	}

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

	public PublicKey getPublicKey() {
		return keyPublic;
	}

	private byte[] encrypt(String message) {
		try {
			cipher.init(Cipher.ENCRYPT_MODE, this.keyPublic);
			return cipher.doFinal(message.getBytes());
		} catch (Exception e) {
		}
		return new byte[0];
	}

	public byte[] encrypt(byte[] messageEncoded) {
		try {
			cipher.init(Cipher.ENCRYPT_MODE, this.keyPublic);
			return cipher.doFinal(messageEncoded);
		} catch (Exception e) {
		}
		return null;
	}

	public byte[] decrypt(byte[] message) {
		try {
			cipher.init(Cipher.DECRYPT_MODE, this.keyPrivate);
			return cipher.doFinal(message);
		} catch (Exception e) {
		}
		return null;
	}
}
