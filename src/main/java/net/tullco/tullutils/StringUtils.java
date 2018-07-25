package net.tullco.tullutils;

import java.io.PrintWriter;
import java.io.StringWriter;

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
		if(s==null)
			return null;
		if(length==0)
			return "";
		if(s.length()<length)
			return s;
		if(s.length()<Math.abs(length))
			return "";
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
		if(s==null)
			return null;
		if(length==0)
			return "";
		if(s.length()<length)
			return s;
		if(s.length()<Math.abs(length))
			return "";
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
	 * This method assures that a string does not start with a given string. If the string doesn't start with it, then there is no change.
	 * Otherwise, it is removed and returned.
	 * @param base The base string.
	 * @param assure The string you want to assure the base does not start with.
	 * @return The base string not starting with the assure string.
	 */
	public static String assureNotStartsWith(String base, String assure){
		String output = base;
		while(output.startsWith(assure))
			output = output.substring(assure.length());
		return output;
	}
	/**
	 * This method assures that a string does not end with a given string. If the string doesn't end with it, then there is no change.
	 * Otherwise, it is removed and returned.
	 * @param base The base string.
	 * @param assure The string you want to assure the base does not end with.
	 * @return The base string not ending with the assure string.
	 */
	public static String assureNotEndsWith(String base, String assure){
		String output = base;
		while(output.endsWith(assure))
			output = output.substring(0, output.length()-assure.length());
		return output;
	}

	/**
	 * This method removed the specified string from the beginning and the end of the base string, if it's present.
	 * Otherwise, there is no change.
	 * @param base The string to trim.
	 * @param trim The string to trim from the beginning/end of the string.
	 * @return The string with the characters trimmed from the beginning/end.
	 */
	public static String trim(String base, String trim){
		return assureNotEndsWith(assureNotStartsWith(base, trim), trim);
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
		for(int i=0;i<padding-base.length();i++)
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
	/**
	 * Takes the exception stack trace and returns it as a string.
	 * @param e The exception to convert.
	 * @return The stack trace of the given exception as a string.
	 */
	public static String exceptionStackTraceToString(Exception e){
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);
		e.printStackTrace(printWriter);
		String stackTrace = stringWriter.toString();
		return stackTrace;
	}
}
