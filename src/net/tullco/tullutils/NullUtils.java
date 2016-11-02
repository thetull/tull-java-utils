package net.tullco.tullutils;

public class NullUtils {

	@SafeVarargs
	public static <T> T coalesce(T... objects){
		for (int i=0;i<objects.length;i++){
			if(objects[i]!=null)
				return objects[i];
		}
		return null;
	}
	
	public static Object nullif(Object base, Object comparator){
		if(base.equals(comparator))
			return null;
		else
			return base;
	}
	
	public static String nullToEmpty(String s){
		if(s==null){
			return "";
		}
		else
			return s;
	}
	
	public static String emptyToNull(String s){
		if(s.equals(""))
			return null;
		else
			return s;
	}
}
