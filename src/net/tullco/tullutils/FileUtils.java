package net.tullco.tullutils;

import java.io.BufferedWriter;
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

	public static FileWriter getFileWriter(String s) throws IOException{
		File f = new File(s);
		return getFileWriter(f);
	}
	
	public static FileWriter getFileWriter(File f) throws IOException{
		FileWriter fw=null;
		boolean succeeded=false;
		while(!succeeded){
			try{
				fw=new FileWriter(f);
				succeeded=true;
			}catch(FileNotFoundException e){
				System.out.println("Could not open for writing. Trying again...");
			}
			try{
				Thread.sleep(5000);
			}catch(InterruptedException e2){
				break;
			}
		}
		return fw;
	}
	public static CSVReader getCSVReader(File f) throws FileNotFoundException{
		return new CSVReader(new FileReader(f));
	}
	
	public static CSVReader getCSVReader(String s) throws FileNotFoundException{
		File f = new File(s);
		return getCSVReader(f);
	}
	
	public static CSVWriter getCSVWriter(File f) throws IOException {
		return new CSVWriter(getFileWriter(f));
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
	
	public static void writeStringToFile(String s, File f) throws IOException {
		BufferedWriter writer = new BufferedWriter(getFileWriter(f));
		writer.write(s);
		writer.close();
	}
	public static void writeStringToFile(String s, String filePath) throws IOException {
		File f = new File(filePath);
		writeStringToFile(s,f);
	}
}
