package net.tullco.tullutils.exceptions;

public class GraphException extends Exception {

	private static final long serialVersionUID = -6468696276511318406L;

	public GraphException(){};
	public GraphException(String message){
		super(message);
	}
	public GraphException(Throwable cause){
		super(cause);
	}
	public GraphException(String message,Throwable cause){
		super(message,cause);
	}
}
