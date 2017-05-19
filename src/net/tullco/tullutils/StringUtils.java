package net.tullco.tullutils;

public class StringUtils {
	public static String left(String s, int length){
		if(length==0)
			return "";
		else if(length>0)
			return s.substring(0, length);
		else{
			return s.substring(0,s.length()+length);
		}
	}
	public static String right(String s, int length){
		if(length==0)
			return "";
		else if(length>0)
			return s.substring(s.length()-length, s.length());
		else{
			return s.substring(-length,s.length());
		}
	}
	public static String assureStartsWith(String base, String assure){
		if(!base.startsWith(assure))
			return assure+base;
		return base;
	}

	public static String assureEndsWith(String base, String assure){
		if(!base.endsWith(assure))
			return base+assure;
		return base;
	}
	public static String leftPad(String base, char character, int padding){
		String paddingString = "";
		for(int i=0;i<=base.length()-padding;i++)
			paddingString+=character;
		return paddingString+base;
		
	}
	public static String rightPad(String base, char character, int padding){
		while(base.length()<padding)
			base+=character;
		return base;
		
	}
}
