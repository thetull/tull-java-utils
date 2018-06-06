package net.tullco.tullutils.test_classes;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import org.junit.Test;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import net.tullco.tullutils.FileUtils;
import net.tullco.tullutils.test_utils.TestResourceLoader;

public class FileUtilsTest {

	@Test
	public void sha1Test() throws NoSuchAlgorithmException, IOException {
		File f = TestResourceLoader.getResource("csv/FileTest1.csv");
		String sha1Hash = FileUtils.sha1Hash(f);
		assertEquals("301F3085D0D1DB3531518578669D40DA771F0CF1",sha1Hash);
	}
	
	@Test
	public void getFileAsStringTest() throws IOException {
		File f = TestResourceLoader.getResource("txt/SimpleText.txt");
		String expectedString = "I love my wife dearly. <3";
		String s = FileUtils.getFileAsString(f);
		assertEquals(expectedString, s);
		String path = f.getAbsolutePath();
		s = FileUtils.getFileAsString(path);
		assertEquals(expectedString, s);
	}
	@Test
	public void writeStringToFileTest() throws IOException {
		File f = File.createTempFile("tullfile_test", ".txt");
		String expectedString = "I love my wife dearly. <3";
		FileUtils.writeStringToFile(expectedString, f);
		assertEquals(expectedString, FileUtils.getFileAsString(f));
		FileUtils.writeStringToFile(expectedString, f.getAbsolutePath());
		assertEquals(expectedString, FileUtils.getFileAsString(f));
	}
	@Test
	public void writeBytesToFileTest() throws IOException {
		File f = File.createTempFile("tullfile_test", ".txt");
		byte[] b = {122,123,34,22};
		FileUtils.writeBytesToFile(b, f);
		byte[] actual = FileUtils.getFileAsBytes(f);
		assertTrue(Arrays.equals(b,actual));
	}
	@Test
	public void fileReaderTest() throws IOException {
		File f = TestResourceLoader.getResource("txt/SimpleText.txt");
		BufferedReader reader = new BufferedReader(FileUtils.getFileReader(f));
		String expectedString = "I love my wife dearly. <3";
		String output = reader.readLine();
		assertEquals(expectedString, output);
		reader.close();
		reader = new BufferedReader(FileUtils.getFileReader(f.getAbsolutePath()));
		output = reader.readLine();
		assertEquals(expectedString, output);
	}
	@Test
	public void fileWriterTest() throws IOException {
		File f = File.createTempFile("tullfile_test", ".txt");
		f.deleteOnExit();
		FileWriter writer = FileUtils.getFileWriter(f);
		String expected = "Habi is awesome!";
		writer.write(expected);
		writer.close();
		BufferedReader reader = new BufferedReader(FileUtils.getFileReader(f));
		String output = reader.readLine();
		assertEquals(expected, output);
		reader.close();
		writer = FileUtils.getFileWriter(f.getAbsolutePath());
		writer.write(expected);
		writer.close();
		reader = new BufferedReader(FileUtils.getFileReader(f));
		output = reader.readLine();
		assertEquals(expected, output);
		reader.close();
	}
	@Test
	public void csvReaderTest() throws IOException {
		File f = TestResourceLoader.getResource("csv/FileTest1.csv");
		CSVReader reader = FileUtils.getCSVReader(f);
		String[] line = reader.readNext();
		reader.close();
		
		String[] expected = {"id","terrible_phrase"};

		assertTrue(Arrays.equals(expected, line));
		reader = FileUtils.getCSVReader(f.getAbsolutePath());
		line = reader.readNext();
		reader.close();
		assertTrue(Arrays.equals(expected, line));
	}
	@Test
	public void csvWriterTest() throws IOException {
		File f = File.createTempFile("tullfile_test", ".txt");
		String[] output = {"lol","olo"};
		CSVWriter writer = FileUtils.getCSVWriter(f);
		writer.writeNext(output);
		writer.close();
		CSVReader reader = FileUtils.getCSVReader(f);
		String[] line = reader.readNext();
		reader.close();
		assertTrue(Arrays.equals(output, line));

		writer = FileUtils.getCSVWriter(f.getAbsolutePath());
		writer.writeNext(output);
		writer.close();
		reader = FileUtils.getCSVReader(f);
		line = reader.readNext();
		reader.close();
		assertTrue(Arrays.equals(output, line));
	}
	@Test
	public void testFileEquals() throws IOException, NoSuchAlgorithmException {
		File f1 = TestResourceLoader.getResource("txt/SimpleText.txt");
		File f2 = TestResourceLoader.getResource("txt/SimpleText.txt");
		assertTrue(FileUtils.filesEqual(f1, f2));
		assertTrue(FileUtils.filesSHA1Equal(f1, f2));
		
		f2 = TestResourceLoader.getResource("csv/FileTest1.csv");
		assertFalse(FileUtils.filesEqual(f1, f2));
		assertFalse(FileUtils.filesSHA1Equal(f1, f2));
	}
	@Test
	public void testFileCopy() throws IOException, NoSuchAlgorithmException {
		File f1 = TestResourceLoader.getResource("txt/SimpleText.txt");
		File f2 = File.createTempFile("tullfile_test", ".txt");
		FileUtils.copyFile(f1, f2);
		assertTrue(FileUtils.filesEqual(f1, f2));
		assertTrue(FileUtils.filesSHA1Equal(f1, f2));
	}
}
