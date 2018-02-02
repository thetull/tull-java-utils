package net.tullco.tullutils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class CompressionUtils {

	private static int BYTE_ARRAY_BUFFER_SIZE = 1024*1024;
	
	/**
	 * Zips a given file into the given output destination.
	 * @param originFile The file to zip
	 * @param destinationFile The location of the resulting zip file
	 * @throws IOException If the origin file could not be read or the destination file could not be written.
	 */
	public static void zipFile(File originFile, File destinationFile) throws IOException{
		try(ZipOutputStream zipStream = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(destinationFile)));
				BufferedInputStream fileStream = new BufferedInputStream(new FileInputStream(originFile))){
			zipStream.setMethod(ZipOutputStream.DEFLATED);
			writeFileToZipStream(originFile, originFile.getName(), zipStream);
		}
	}
	/**
	 * Zips the given directory to the given file.
	 * @param originDirectory The directory whose contents you want to zip. If this is a file, it will be zipped with zipFile()
	 * @param destinationFile The destination directory of the zip file.
	 * @throws IOException If the origin directory could not be read or the destination file could not be written.
	 */
	public static void zipDirectory(File originDirectory, File destinationFile) throws IOException{
		if(originDirectory.isFile())
			zipFile(originDirectory, destinationFile);
		else{
			try(ZipOutputStream zipStream = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(destinationFile)))){
				zipDirectoryRecursive(originDirectory, "", zipStream);
			}
		}
	}
	/**
	 * Unzips a file to the given destination directory.
	 * @param zipFile The file to unzip
	 * @param destinationDirectory The directory to unzip to.
	 * @throws IOException If the origin file could not be read or the destination file could not be written.
	 */
	public static void unzipFile(File zipFile, File destinationDirectory) throws IOException{
		try(ZipInputStream zipStream = new ZipInputStream(new BufferedInputStream(new FileInputStream(zipFile)))){
			destinationDirectory.mkdir();
			ZipEntry entry = zipStream.getNextEntry();
			byte[] byteBuffer = new byte[BYTE_ARRAY_BUFFER_SIZE];
			while(entry != null){
				File outputFile = new File(destinationDirectory.getAbsolutePath() + File.separator + entry.getName());
				outputFile.getParentFile().mkdir();
				try(BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(outputFile))){
					while(true){
						int readBytes = zipStream.read(byteBuffer);
						if(readBytes == -1)
							break;
						outputStream.write(byteBuffer, 0, readBytes);
					}
				}
				entry = zipStream.getNextEntry();
			}
		}
	}
	private static void zipDirectoryRecursive(File originDirectory, String path, ZipOutputStream zipStream) throws IOException{
		File[] fileList = originDirectory.listFiles();
		for(File f: fileList){
			if(f.isFile())
				writeFileToZipStream(f, path + f.getName(), zipStream);
			else
				zipDirectoryRecursive(f, path + f.getName()+File.separator, zipStream);
		}
	}
	private static void writeFileToZipStream(File originFile, String path, ZipOutputStream zipStream) throws IOException{
		try(FileInputStream fileStream = new FileInputStream(originFile)){
			ZipEntry entry = new ZipEntry(path);
			zipStream.putNextEntry(entry);
			byte[] byteBuffer = new byte[BYTE_ARRAY_BUFFER_SIZE];
			while(true){
				int readBytes = fileStream.read(byteBuffer);
				if (readBytes == -1)
					break;
				zipStream.write(byteBuffer, 0, readBytes);
			}
			
		}
	}
}
