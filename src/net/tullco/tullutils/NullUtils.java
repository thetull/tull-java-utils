package net.tullco.tullutils;

/**
 * A set of methods that I find useful for dealing with nulls.
 * @author Tull Gearreald
 */
public final class NullUtils {

	@SafeVarargs
	/**
	 * Returns the first non-null argument that is passed to it.
	 * @param <T> The type parameter. Don't mix types unless you want unpredictable results. :)
	 * @param objects The objects that you want the first non-null of.
	 * @return The first non-null object from the arguments.
	 */
	public final static <T> T coalesce(T... objects){
		for (int i=0;i<objects.length;i++){
			if(objects[i]!=null)
				return objects[i];
		}
		return null;
	}
	/**
	 * Returns null if the two arguments are equal according to base.equals(comparator).
	 * Otherwise returns the base object
	 * @param base The object to compare
	 * @param comparator The object to compare to.
	 * @return Either the base object, or null if the comparator is equal to the base.
	 */
	public final static <T> T nullif(T base, Object comparator){
		if(base.equals(comparator))
			return null;
		else
			return base;
	}
	/**
	 * If the string passed into this function is null, returns an empty string. Otherwise returns null.
	 * @param s The string to test
	 * @return Either an empty string or the string that was passed in.
	 */
	public final static String nullToEmpty(String s){
		if(s==null){
			return "";
		}
		else
			return s;
	}
	/**
	 * If the string passed into this function is empty, returns null, otherwise returns the String.
	 * @param s The string to test
	 * @return Either null or the string that was passed in.
	 */
	public final static String emptyToNull(String s){
		if(s.equals(""))
			return null;
		else
			return s;
	}
}
