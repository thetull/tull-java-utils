package net.tullco.tullutils.test_classes;

import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;

import org.junit.Test;

import net.tullco.tullutils.encryptionutils.AESDecryptor;
import net.tullco.tullutils.encryptionutils.AESEncryptor;
import net.tullco.tullutils.exceptions.CryptException;

public class AESEncryptionTest {

	@Test
	public void encryptDecryptBytes() throws UnsupportedEncodingException, CryptException  {
		String input = "I am a test string. Hello. I'm about to be encrypted and then decrypted. It's kind of like going through a wood chipper, and then being reassembled. Hopefully this works! :)";
		byte[] inputBytes = input.getBytes("UTF-8");
		AESEncryptor e = new AESEncryptor("password");
		byte[] encryptedBytes = e.encryptBytes(inputBytes);
		AESDecryptor d = new AESDecryptor("password", e.getSalt(), e.getIV());
		byte[] decryptedBytes = d.decryptBytes(encryptedBytes);
		String output = new String(decryptedBytes, "UTF-8");
		assertEquals(input, output);
	}

	@Test
	public void encryptDecryptString() throws CryptException, UnsupportedEncodingException {
		String input = "I am a test string. Hello. I'm about to be encrypted and then decrypted. It's kind of like going through a wood chipper, and then being reassembled. Hopefully this works! :)";
		AESEncryptor e = new AESEncryptor("password");
		byte[] encryptedBytes = e.encryptString(input, "UTF-8");
		AESDecryptor d = new AESDecryptor("password", e.getSalt(), e.getIV());
		String output = d.decryptBytesToString(encryptedBytes, "UTF-8");
		assertEquals(input, output);
	}
	
}
