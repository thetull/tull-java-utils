package net.tullco.tullutils.exceptions;

public class CryptException extends Exception {
	
	private static final long serialVersionUID = 7122505823890672811L;
	
	public CryptException(){};
	public CryptException(String message){
		super(message);
	}
	public CryptException(Throwable cause){
		super(cause);
	}
	public CryptException(String message,Throwable cause){
		super(message,cause);
	}
}
