package net.tullco.tullutils;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

public class ResourceUtils {

	public static String getAbsoluteResourcePath(Class<? extends Object> resourceClass, String resourcePath){
		return ResourceUtils.getResourceURL(resourceClass,resourcePath).toExternalForm();
	}
	public static File getResourceFile(Class<? extends Object> resourceClass,String resourcePath){
		return new File(ResourceUtils.getAbsoluteResourcePath(resourceClass,resourcePath));
	}
	public static URL getResourceURL(Class<? extends Object> resourceClass,String resourcePath){
		return getLoader(resourceClass).getResource(resourcePath);
	}
	public static InputStream getResourceStream(Class<? extends Object> resourceClass,String resourcePath){
		return getLoader(resourceClass).getResourceAsStream(resourcePath);
	}
	private static ClassLoader getLoader(Class<? extends Object> c){
		return c.getClassLoader();
	}
	
}
