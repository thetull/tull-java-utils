package net.tullco.tullutils.encryptionutils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import net.tullco.tullutils.exceptions.CryptException;

public class AESDecryptor {
	
	private String passphrase;
	private byte[] salt;
	private byte[] iv;
	private Cipher decryptionCipher;

	private final static int ITERATIONS = 65536;
	private final static int KEY_LENGTH = 128;
	
	/**
	 * This creates a decryptor using the given passphrase, salt, and iv.
	 * @param passphrase The password used to encrypt the file.
	 * @param salt The salt used to decrypt the file.
	 * @param iv The initialization vector for the encryption process.
	 * @throws CryptException If there were problems initializing the decryptor.
	 */
	public AESDecryptor(String passphrase, byte[] salt, byte[] iv) throws CryptException {
		this.passphrase = passphrase;
		this.salt = salt;
		this.iv = iv;
		initializeDecryption();
	}
	
	private void initializeDecryption() throws CryptException {
		try {
			SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			KeySpec spec = new PBEKeySpec(passphrase.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
			SecretKeySpec secret = new SecretKeySpec(secretKeyFactory.generateSecret(spec).getEncoded(), "AES");
			
			decryptionCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			decryptionCipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(iv));
		} catch (InvalidKeyException | NoSuchAlgorithmException | InvalidKeySpecException | NoSuchPaddingException
				| InvalidAlgorithmParameterException e) {
			throw new CryptException(e);
		}
	}
	
	/**
	 * Get the initialization vector used in the decryption process
	 * @return The iv as a byte array.
	 */
	public byte[] getIV(){
		return iv;
	}

	/**
	 * Get the salt used in the decryption process
	 * @return The salt as a byte array.
	 */
	public byte[] getSalt(){
		return salt;
	}
	
	/**
	 * Decrypt the bytes to their original form. This function works best with small byte arrays.
	 * @param b The byte array to decrypt
	 * @return A new array containing the decrypted bytes
	 * @throws CryptException If the decryption failed
	 */
	public byte[] decryptBytes(byte[] b) throws CryptException {
		try {
			return decryptionCipher.doFinal(b);
		} catch (IllegalBlockSizeException | BadPaddingException e) {
			throw new CryptException(e);
		}
	}
	/**
	 * Decrypt a byte array into a string. This function works best with smaller byte arrays.
	 * @param b The byte array to decrypt
	 * @return A string containing the decrypted information.
	 * @throws CryptException If the decryption failed
	 */
	public String decryptBytesToString(byte[] b, String encoding) throws CryptException, UnsupportedEncodingException {
		return new String(decryptBytes(b), encoding);
	}

	/**
	 * Currently unimplemented. May be implemented in the future. Will encrypt and decrypt large files.
	 * @param input The encrypted file
	 * @param output The output file where the decrypted data will be stored.
	 */
	public void decryptFile(File input, File output){
		throw new UnsupportedOperationException("File decryption is not yet available.");
	}
}
