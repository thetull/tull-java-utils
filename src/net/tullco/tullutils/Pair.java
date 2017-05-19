package net.tullco.tullutils;

public class Pair<T,U> {
	
	T left;
	U right;
	
	private Pair(T left, U right){
		this.left=left;
		this.right=right;
	}
	public T left(){
		return this.left;
	}
	public U right(){
		return this.right;
	}
	public T getKey(){
		return this.left;
	}
	public U getValue(){
		return this.right;
	}
	public static <T,U> Pair<T,U> of(T left, U right){
		return new Pair<T,U>(left,right);
	}
}
