package net.tullco.tullutils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;

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
	 * @throws CsvValidationException 
	 */
	public static File mergeFiles(File supplement, File base, int supplementKeyIndex, int baseKeyIndex) throws IOException, CsvValidationException{
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
	 * @throws CsvValidationException 
	 */
	public static void mergeFiles(File supplementCsv, File baseCsv, int supplementKeyIndex, int baseKeyIndex, File destination) throws IOException, CsvValidationException{
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
	/**
	 * Merges two CSV files together into a new file using the keys at the specified indices.
	 * 
	 * The base file will have the non-key columns of the supplement added to the end of it.
	 * 
	 * Note that this method will load neither file into memory, but is very heavy on disk access, and is likely to be slow. If you can, use mergeFiles.
	 * @param supplementCsv The file containing the data to be merged.
	 * @param baseCsv The file containing the data to merge into.
	 * @param supplementKeyIndex The index of the merge key in the supplementary file
	 * @param baseKeyIndex The index of the merge key in base file.
	 * @param destination The output file for the merged data
	 * @throws IOException If there was a problem writing the merged file.
	 * @throws CsvValidationException 
	 */
	public static void mergeFilesSlow(File supplementCsv, File baseCsv, int supplementKeyIndex, int baseKeyIndex, File destination) throws IOException, CsvValidationException {
		CSVReader baseReader = FileUtils.getCSVReader(baseCsv);
		String[] baseHeaders = baseReader.readNext();
		CSVReader supplementReader = FileUtils.getCSVReader(supplementCsv);
		String[] supplementHeaders = removeItemFromStringArray(supplementReader.readNext(),supplementKeyIndex);
		supplementReader.close();
		String[] outputHeaders = mergeStringArrays(baseHeaders,supplementHeaders);
		CSVWriter writer = FileUtils.getCSVWriter(destination);
		writer.writeNext(outputHeaders);
		String[] baseLine;
		while ( (baseLine = baseReader.readNext()) != null){
			String key = baseLine[baseKeyIndex];
			supplementReader = FileUtils.getCSVReader(supplementCsv);
			supplementReader.readNext();
			String[] outputLine = null;
			String[] supplementLine;
			while ( (supplementLine = supplementReader.readNext()) != null){
				if(supplementLine[supplementKeyIndex].equals(key)){
					outputLine=mergeStringArrays(baseLine,removeItemFromStringArray(supplementLine,supplementKeyIndex));
					break;
				}
			}
			supplementReader.close();
			if(outputLine==null){
				outputLine=createArrayOfEmptyStrings(supplementHeaders.length);
			}
			writer.writeNext(outputLine);
			writer.flush();
		}
		baseReader.close();
		writer.close();
	}
	/**
	 * Creates a new array containing the contents of the starting array with the merge array concatenated to the end.
	 * Will have length startingArray.length+mergeArray.length.
	 * @param startingArray The starting array.
	 * @param mergeArray The array to concatenate.
	 * @return A new array containing the contents of both arrays.
	 */
	public static String[] mergeStringArrays(String[] startingArray, String[] mergeArray){
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
	/**
	 * Creates a new array containing the contents of the starting array with the merge array concatenated to the end.
	 * Will have length startingArray.length+mergeArray.length.
	 * @param startingArray The starting array.
	 * @param mergeArray The array to concatenate.
	 * @return A new array containing the contents of both arrays.
	 */
	public static byte[] mergeByteArrays(byte[] startingArray, byte[] mergeArray){
		if(startingArray==null)
			return mergeArray;
		if(mergeArray==null)
			return startingArray;
		byte[] newArray = new byte[startingArray.length+mergeArray.length];
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
