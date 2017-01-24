package net.tullco.tullutils.exceptions;

public class UnapproximatableException extends Exception {
	private static final long serialVersionUID = 4566763051088940968L;
	
	public UnapproximatableException(){
		super();
	};
	public UnapproximatableException(String message){
		super(message);
	}
	public UnapproximatableException(Throwable cause){
		super(cause);
	}
	public UnapproximatableException(String message,Throwable cause){
		super(message,cause);
	}
}
