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
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((left == null) ? 0 : left.hashCode());
		result = prime * result + ((right == null) ? 0 : right.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		@SuppressWarnings("rawtypes")
		Pair other = (Pair) obj;
		if (left == null) {
			if (other.left != null)
				return false;
		} else if (!left.equals(other.left))
			return false;
		if (right == null) {
			if (other.right != null)
				return false;
		} else if (!right.equals(other.right))
			return false;
		return true;
	}
}
