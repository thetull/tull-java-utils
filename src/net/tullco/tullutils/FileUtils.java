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

public final class FileUtils {

	/**
	 * Gets a File Writer from a string containing the full path.
	 * If the file writer cannot be accessed for writing, it will continue to try,
	 * potentially blocking execution. This is to give you a chance to fix the
	 * problem instead of crashing and losing what you were about to write.
	 * @param s The full path string.
	 * @return A file writer for the path.
	 * @throws IOException If the path is invalid or a directory.
	 */
	public static FileWriter getFileWriter(String s) throws IOException{
		File f = new File(s);
		return getFileWriter(f);
	}
	/**
	 * Gets a File Writer for a File object
	 * If the file writer cannot be accessed for writing, it will continue to try,
	 * potentially blocking execution. This is to give you a chance to fix the
	 * problem instead of crashing and losing what you were about to write.
	 * @param f A file object that you want to write to.
	 * @return A file writer for the path.
	 * @throws IOException If the path is invalid or a directory.
	 */
	public static FileWriter getFileWriter(File f) throws IOException{
		FileWriter fw=null;
		while(true){
			try{
				fw=new FileWriter(f);
				break;
			}catch(FileNotFoundException e){
				System.out.println("Could not open for writing. Trying again in 5 seconds...");
			}
			try{
				Thread.sleep(5000);
			}catch(InterruptedException e2){
				break;
			}
		}
		return fw;
	}
	/**
	 * Gets a File Reader for the file at the given string.
	 * @param s The full path to the file.
	 * @return A file reader for the file.
	 * @throws IOException If it's not a file or can't be found.
	 */
	public static FileReader getFileReader(String s) throws IOException{
		File f = new File(s);
		return getFileReader(f);
	}
	/**
	 * Gets a file reader for the given file.
	 * @param f The file.
	 * @return A file reader for the given file.
	 * @throws FileNotFoundException If it's not a file or can't be found.
	 */
	public static FileReader getFileReader(File f) throws FileNotFoundException{
		return new FileReader(f);
	}
	/**
	 * Gets a CSVReader for the given file.
	 * @param f The file to create a reader for
	 * @return A CSVReader to the file.
	 * @throws FileNotFoundException If the file isn't found.
	 */
	public static CSVReader getCSVReader(File f) throws FileNotFoundException{
		return new CSVReader(getFileReader(f));
	}
	/**
	 * Gets a CSVReader for the file at the given path.
	 * @param s The full path to the file.
	 * @return A CSVReader to the file at the path.
	 * @throws FileNotFoundException If the path doesn't point to a file.
	 */
	public static CSVReader getCSVReader(String s) throws FileNotFoundException{
		File f = new File(s);
		return getCSVReader(f);
	}
	/**
	 * Gets a CSVWriter for the file. If it can't get a write lock on the file,
	 * it will wait until it can.
	 * @param f The file.
	 * @return A CSVWriter to the file.
	 * @throws IOException If something goes very wrong.
	 */
	public static CSVWriter getCSVWriter(File f) throws IOException {
		return new CSVWriter(getFileWriter(f));
	}
	/**
	 * Gets a CSVWriter for the file at the given path. If it can't get a write lock on the file,
	 * it will wait until it can.
	 * @param s The full path of the file.
	 * @return A CSVWriter to the file.
	 * @throws IOException If something goes very wrong.
	 */
	public static CSVWriter getCSVWriter(String s) throws IOException {
		File f = new File(s);
		return getCSVWriter(f);
	}
	
	/**
	 * Gets a file as a string.
	 * @param s The path to the file.
	 * @return A String containing the data in the file.
	 * @throws IOException If something goes very wrong.
	 */
	public static String getFileAsString(String s) throws IOException {
		String contents = new String(Files.readAllBytes(Paths.get(s)));
		return contents;
	}
	/**
	 * Gets a file as a string.
	 * @param f The file.
	 * @return A String containing the data in the file.
	 * @throws IOException If something goes very wrong.
	 */
	public static String getFileAsString(File f) throws IOException {
		String path = f.getAbsolutePath();
		return getFileAsString(path);
	}
	
	/**
	 * Writes a string to the given file
	 * @param s The string to write
	 * @param f The File to write to.
	 * @throws IOException If something goes very wrong.
	 */
	public static void writeStringToFile(String s, File f) throws IOException {
		BufferedWriter writer = new BufferedWriter(getFileWriter(f));
		writer.write(s);
		writer.flush();
		writer.close();
	}
	/**
	 * Writes a string to the file at the given path.
	 * @param s The string to write
	 * @param filePath The path to the file you want to write to.
	 * @throws IOException If something goes very wrong.
	 */
	public static void writeStringToFile(String s, String filePath) throws IOException {
		File f = new File(filePath);
		writeStringToFile(s,f);
	}
}
