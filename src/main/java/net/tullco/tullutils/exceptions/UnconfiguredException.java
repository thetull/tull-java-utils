package net.tullco.tullutils.exceptions;

public class UnconfiguredException extends RuntimeException {

	private static final long serialVersionUID = -646824827665418406L;

	public UnconfiguredException(){
		super();
	};
	public UnconfiguredException(String message){
		super(message);
	}
	public UnconfiguredException(Throwable cause){
		super(cause);
	}
	public UnconfiguredException(String message,Throwable cause){
		super(message,cause);
	}
}
