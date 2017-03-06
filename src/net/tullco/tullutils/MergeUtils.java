package net.tullco.tullutils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

public class MergeUtils {
	
	/**
	 * Merges two CSV files together into a temporary file using the keys at the specified indices.
	 * The temporary file containing the merged data will be returned.
	 * 
	 * The base file will have the non-key columns of the supplement added to the end of it.
	 * 
	 * Note that the supplement file will be entirely loaded into memory, so it should be a smaller file. The base file will not be loaded into memory.
	 * @param supplement The file containing the data to be merged. This file will be entirely loaded into memory. 
	 * @param base The file containing the data to merge into. This file will not be loaded into memory.
	 * @param supplementKeyIndex The index of the merge key in the supplementary file
	 * @param baseKeyIndex The index of the merge key in base file.
	 * @return The file with the merged information. This will be a temp file, so you should copy the results out if you want it to persist.
	 * @throws IOException If there was a problem writing the merged file.
	 */
	public static File mergeFiles(File supplement, File base, int supplementKeyIndex, int baseKeyIndex) throws IOException{
		File output = File.createTempFile("data_merge", ".csv");
		mergeFiles(supplement, base, supplementKeyIndex, baseKeyIndex, output);
		return output;
	}
	/**
	 * Merges two CSV files together into a new file using the keys at the specified indices.
	 * 
	 * The base file will have the non-key columns of the supplement added to the end of it.
	 * 
	 * Note that the supplement file will be entirely loaded into memory, so it should be a smaller file. The base file will not be loaded into memory.
	 * @param supplementCsv The file containing the data to be merged. This file will be entirely loaded into memory. 
	 * @param baseCsv The file containing the data to merge into. This file will not be loaded into memory.
	 * @param supplementKeyIndex The index of the merge key in the supplementary file
	 * @param baseKeyIndex The index of the merge key in base file.
	 * @param destination The output file for the merged data
	 * @throws IOException If there was a problem writing the merged file.
	 */
	public static void mergeFiles(File supplementCsv, File baseCsv, int supplementKeyIndex, int baseKeyIndex, File destination) throws IOException{
		//First, load the small file into memory.
		CSVReader smallReader = FileUtils.getCSVReader(supplementCsv);
		String[] smallHeaders = removeItemFromStringArray(smallReader.readNext(),supplementKeyIndex);
		HashMap<String,String[]> mergeData = new HashMap<String,String[]>();
		String[] line;
		while ( (line = smallReader.readNext()) != null){
			String key = line[supplementKeyIndex];
			String[] rowData = removeItemFromStringArray(line,supplementKeyIndex);
			mergeData.put(key, rowData);
		}
		smallReader.close();
		
		//Now, lets get the large file ready for work...
		CSVReader largeReader = FileUtils.getCSVReader(baseCsv);
		CSVWriter writer = FileUtils.getCSVWriter(destination);
		String[] largeHeaders = largeReader.readNext();
		String[] outputHeaders = mergeStringArrays(largeHeaders,smallHeaders);
		writer.writeNext(outputHeaders);
		while ( (line = largeReader.readNext()) != null){
			String key= line[baseKeyIndex];
			String[] mergeLine = mergeData.get(key);
			if(mergeLine==null){
				mergeLine=createArrayOfEmptyStrings(smallHeaders.length);
			}
			writer.writeNext(mergeStringArrays(line,mergeLine));
		}
		largeReader.close();
		writer.close();
	}
	private static String[] mergeStringArrays(String[] startingArray, String[] mergeArray){
		if(startingArray==null)
			return mergeArray;
		if(mergeArray==null)
			return startingArray;
		String[] newArray = new String[startingArray.length+mergeArray.length];
		for(int i=0;i<newArray.length;i++){
			if(i<startingArray.length)
				newArray[i] = startingArray[i];
			else
				newArray[i] = mergeArray[i-startingArray.length];
		}
		return newArray;
	}
	private static String[] removeItemFromStringArray(String[] array, int popIndex){
		String[] newArray = new String[array.length-1];
		int oldArrayIndex=0;
		int newArrayIndex=0;
		
		while(oldArrayIndex<array.length){
			if(oldArrayIndex == popIndex){
				oldArrayIndex++;
				continue;
			}
			newArray[newArrayIndex]=array[oldArrayIndex];
			newArrayIndex++;
			oldArrayIndex++;
		}
		return newArray;
	}
	private static String[] createArrayOfEmptyStrings(int length){
		String[] empty = new String[length];
		for(int i=0;i<length;i++){
			empty[i]="";
		}
		return empty;
	}
}
