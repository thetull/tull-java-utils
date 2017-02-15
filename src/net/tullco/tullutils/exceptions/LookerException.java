package net.tullco.tullutils.exceptions;

public class LookerException extends Exception {

	private static final long serialVersionUID = -6468696276511318406L;

	public LookerException(){};
	public LookerException(String message){
		super(message);
	}
	public LookerException(Throwable cause){
		super(cause);
	}
	public LookerException(String message,Throwable cause){
		super(message,cause);
	}
}
