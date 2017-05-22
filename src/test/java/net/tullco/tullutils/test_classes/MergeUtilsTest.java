package net.tullco.tullutils.test_classes;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;

import com.opencsv.CSVReader;

import net.tullco.tullutils.FileUtils;
import net.tullco.tullutils.MergeUtils;

public class MergeUtilsTest {

	@Test
	public void testMergeFilesFileFileIntInt() throws IOException {
		File f1 = TestResourceLoader.getResource("csv/MergeTest1.csv");
		File f2 = TestResourceLoader.getResource("csv/MergeTest2.csv");
		File temp = MergeUtils.mergeFiles(f2, f1, 0, 0);
		FileUtils.getCSVReader(temp);
		
		String[] expectedHeaders = {"id","first_name","last_name"};
		String[] expectedData = {"1","Brady","Haran"};
		CSVReader reader = FileUtils.getCSVReader(temp);
		String[] actualHeaders = reader.readNext();
		String[] actualData = reader.readNext();
		reader.close();
		assertTrue(Arrays.equals(expectedHeaders,actualHeaders));
		assertTrue(Arrays.equals(expectedData, actualData));
	}

	@Test
	public void testMergeFilesFileFileIntIntFile() throws IOException {
		File f1 = TestResourceLoader.getResource("csv/MergeTest1.csv");
		File f2 = TestResourceLoader.getResource("csv/MergeTest2.csv");
		File temp = File.createTempFile("tullfile_test", "csv");
		MergeUtils.mergeFiles(f2, f1, 0, 0, temp);
		FileUtils.getCSVReader(temp);
		
		String[] expectedHeaders = {"id","first_name","last_name"};
		String[] expectedData = {"1","Brady","Haran"};
		CSVReader reader = FileUtils.getCSVReader(temp);
		String[] actualHeaders = reader.readNext();
		String[] actualData = reader.readNext();
		reader.close();
		assertTrue(Arrays.equals(expectedHeaders,actualHeaders));
		assertTrue(Arrays.equals(expectedData, actualData));
	}

	@Test
	public void testMergeFilesSlow() throws IOException {
		File f1 = TestResourceLoader.getResource("csv/MergeTest1.csv");
		File f2 = TestResourceLoader.getResource("csv/MergeTest2.csv");
		File temp = File.createTempFile("tullfile_test", "csv");
		MergeUtils.mergeFilesSlow(f2, f1, 0, 0, temp);
		FileUtils.getCSVReader(temp);
		
		String[] expectedHeaders = {"id","first_name","last_name"};
		String[] expectedData = {"1","Brady","Haran"};
		CSVReader reader = FileUtils.getCSVReader(temp);
		String[] actualHeaders = reader.readNext();
		String[] actualData = reader.readNext();
		reader.close();
		assertTrue(Arrays.equals(expectedHeaders,actualHeaders));
		assertTrue(Arrays.equals(expectedData, actualData));
	}

	@Test
	public void testMergeStringArrays() {
		String[] array1 = {"Test","Oops"};
		String[] array2 = {"*sigh*","nope"};
		String[] expected = {"Test","Oops","*sigh*","nope"};
		String[] combined = MergeUtils.mergeStringArrays(array1,array2);
		assertTrue(Arrays.equals(expected, combined));
	}

	@Test
	public void testMergeByteArrays() {
		byte[] b1 = {100,10};
		byte[] b2 = {90,5};
		byte[] expected = {100,10,90,5};
		byte[] combined = MergeUtils.mergeByteArrays(b1,b2);
		assertTrue(Arrays.equals(expected, combined));
	}

}
