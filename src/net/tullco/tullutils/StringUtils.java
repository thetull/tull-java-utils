package net.tullco.tullutils;

/**
 * A static container class for String utility methods.
 * @author Tull Gearreald
 *
 */
public class StringUtils {
	/**
	 * If length is greater than 0, then this method will give you length characters from the left of the string.
	 * If length is less than 0, then this method will give you the string with length characters removed from the right.
	 * @param s The string to crop.
	 * @param length The number of characters to crop.
	 * @return A cropped string.
	 */
	public static String left(String s, int length){
		if(length==0)
			return "";
		if(s.length()<length)
			return s;
		else if(length>0)
			return s.substring(0, length);
		else{
			return s.substring(0,s.length()+length);
		}
	}
	/**
	 * If length is greater than 0, then this method will give you length characters from the right of the string.
	 * If length is less than 0, then this method will give you the string with length characters removed from the left.
	 * @param s The string to crop.
	 * @param length The number of characters to crop.
	 * @return A cropped string.
	 */
	public static String right(String s, int length){
		if(length==0)
			return "";
		if(s.length()<length)
			return s;
		else if(length>0)
			return s.substring(s.length()-length, s.length());
		else{
			return s.substring(-length,s.length());
		}
	}
	/**
	 * This method assures that a string starts with a given string. If the string already starts with it, then there is no change.
	 * Otherwise, it is added and returned.
	 * @param base The base string.
	 * @param assure The string you want to assure the base starts with.
	 * @return The base string starting with the assure string.
	 */
	public static String assureStartsWith(String base, String assure){
		if(!base.startsWith(assure))
			return assure+base;
		return base;
	}
	/**
	 * This method assures that a string ends with a given string. If the string already ends with it, then there is no change.
	 * Otherwise, it is added and returned.
	 * @param base The base string.
	 * @param assure The string you want to assure the base ends with.
	 * @return The base string ending with the assure string.
	 */
	public static String assureEndsWith(String base, String assure){
		if(!base.endsWith(assure))
			return base+assure;
		return base;
	}
	/**
	 * Adds the given character to the left of the given string until it reaches the requested length.
	 * If the string is already as long as or longer than the padding, then it will be returned unchanged. 
	 * @param base The string to pad.
	 * @param character The character to pad with.
	 * @param padding The desired length to pad to.
	 * @return The padded string.
	 */
	public static String leftPad(String base, char character, int padding){
		String paddingString = "";
		for(int i=0;i<=base.length()-padding;i++)
			paddingString+=character;
		return paddingString+base;
	}
	/**
	 * Adds the given character to the right of the given string until it reaches the requested length.
	 * If the string is already as long as or longer than the padding, then it will be returned unchanged. 
	 * @param base The string to pad.
	 * @param character The character to pad with.
	 * @param padding The desired length to pad to.
	 * @return The padded string.
	 */
	public static String rightPad(String base, char character, int padding){
		while(base.length()<padding)
			base+=character;
		return base;
	}
}
