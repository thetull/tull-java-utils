package net.tullco.tullutils.test_classes;

import java.io.File;

public class TestResourceLoader {
	public static File getResource(String path){
		return new File(TestResourceLoader.class.getClassLoader().getResource(path).getFile());
	}
}
