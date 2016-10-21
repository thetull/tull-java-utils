package net.tullco.tullutils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

public class FileUtils {

	
	public static CSVReader getCSVReader(File f) throws FileNotFoundException{
		return new CSVReader(new FileReader(f));
	}
	
	public static CSVReader getCSVReader(String s) throws FileNotFoundException{
		File f = new File(s);
		return getCSVReader(f);
	}
	
	public static CSVWriter getCSVWriter(File f) throws IOException {
		return new CSVWriter(new FileWriter(f));
	}
	public static CSVWriter getCSVWriter(String s) throws IOException {
		File f = new File(s);
		return getCSVWriter(f);
	}
	
	public static String getFileAsString(String s) throws IOException {
		String contents = new String(Files.readAllBytes(Paths.get(s)));
		return contents;
	}
	public static String getFileAsString(File f) throws IOException {
		String path = f.getAbsolutePath();
		return getFileAsString(path);
	}
}
