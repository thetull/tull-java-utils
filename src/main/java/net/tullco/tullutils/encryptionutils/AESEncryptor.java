package net.tullco.tullutils.encryptionutils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
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

public class AESEncryptor {
	
	private String passphrase;
	private byte[] salt;
	private byte[] iv;
	private Cipher encryptionCipher;
	
	private final static int SALT_LENGTH = 8;
	private final static int ITERATIONS = 65536;
	private final static int KEY_LENGTH = 128;
	
	/**
	 * This creates a encryptor with the given passphrase.
	 * @param passphrase The passphrase to use to encrypt the file.
	 * @throws CryptException If there are any problems initializing the encryptor.
	 */
	public AESEncryptor(String passphrase) throws CryptException {
		this.passphrase = passphrase;
		initializeEncryption();
	}
	
	private void initializeEncryption() throws CryptException {
		try {
			this.salt = new byte[SALT_LENGTH];
			new SecureRandom().nextBytes(this.salt);
			SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			KeySpec spec = new PBEKeySpec (passphrase.toCharArray (), salt, ITERATIONS, KEY_LENGTH);
			SecretKeySpec secret = new SecretKeySpec(secretKeyFactory.generateSecret(spec).getEncoded(), "AES");
			
			encryptionCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			encryptionCipher.init(Cipher.ENCRYPT_MODE, secret);
			this.iv = encryptionCipher.getParameters().getParameterSpec(IvParameterSpec.class).getIV();
		} catch (InvalidKeyException | NoSuchAlgorithmException | InvalidKeySpecException | NoSuchPaddingException
				| InvalidParameterSpecException e) {
			throw new CryptException(e);
		}
	}
	
	/**
	 * Gets the randomly generated IV.
	 * @return The IV.
	 */
	public byte[] getIV(){
		return iv;
	}

	/**
	 * Gets the randomly generated salt. It is generated using SecureRandom.
	 * @return The salt.
	 */
	public byte[] getSalt(){
		return salt;
	}
	
	/**
	 * Encrypts the byte array.
	 * @param b The byte array to encrypt
	 * @return The byte array encrypted
	 * @throws CryptException If there were any problems encrypting the bytes.
	 */
	public byte[] encryptBytes(byte[] b) throws CryptException {
		try {
			return encryptionCipher.doFinal(b);
		} catch (IllegalBlockSizeException | BadPaddingException e) {
			throw new CryptException(e);
		}
	}
	
	/**
	 * Encrypts the String.
	 * @param s The string to encrypt
	 * @param encoding The string encoding to use in the encryption
	 * @return The string encrypted as a byte array
	 * @throws CryptException If there were any problems encrypting the string
	 * @throws UnsupportedEncodingException If the specified encoding is not supported
	 */
	public byte[] encryptString(String s, String encoding) throws CryptException, UnsupportedEncodingException {
		return encryptBytes(s.getBytes(encoding));
	}
	/**
	 * Currently unimplemented. May be implemented in the future. Will encrypt and decrypt large files.
	 * @param input The input file
	 * @param output The output file where the encrypted data will be stored.
	 */
	public void encryptFile(File input, File output){
		throw new UnsupportedOperationException("File encryption is not yet available.");
	}
}
