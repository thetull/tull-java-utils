package net.tullco.tullutils.exceptions;

public class InvalidHTTPMethodException extends RuntimeException {

	private static final long serialVersionUID = -6468696276511318406L;

	public InvalidHTTPMethodException(){
		super();
	};
	public InvalidHTTPMethodException(String message){
		super(message);
	}
	public InvalidHTTPMethodException(Throwable cause){
		super(cause);
	}
	public InvalidHTTPMethodException(String message,Throwable cause){
		super(message,cause);
	}
}
