package security;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import util.Print;

public class SecuritySK {
	private SecretKey secretKey;
	private Cipher cipher;

	public SecuritySK() {
		try {
			cipher = Cipher.getInstance("DES");
		} catch (Exception e) {
		}
	}

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

	public void setSecretKey(byte[] keyEncoded) {
		secretKey = new SecretKeySpec(keyEncoded, 0, keyEncoded.length, "DES");
	}
	
	public SecretKey getSecretKey() {
		return this.secretKey;
	}

	public byte[] encrypt(String message) {
		try {
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			return cipher.doFinal(message.getBytes());
		} catch (Exception e) {
		}
		return new byte[0];
	}

	public byte[] decrypt(byte[] message) {
		try {
			cipher.init(Cipher.DECRYPT_MODE, secretKey);
			return cipher.doFinal(message);
		} catch (Exception e) {
		}
		return null;
	}
}
