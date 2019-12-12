package net.tullco.tullutils;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.StringJoiner;

import com.opencsv.CSVWriter;

/**
 * A JDBC connection wrapper that abstracts away some of the complexities of querying for most use cases.
 * @author Tull Gearreald
 *
 */
public class SQLUtil implements Closeable {

	private final Connection conn;
	private boolean isClosed=false;
	
	/**
	 * Creates an SQL Util object that you can use to make queries against the given connection.
	 * @param conn The connection that the new object will make queries against.
	 */
	public SQLUtil(Connection conn){
		this.conn=conn;
	}
	/**
	 * Executes a select statement and returns the ResultSet.
	 * @param statement The statement to execute
	 * @return The ResultSet containing the results.
	 * @throws SQLException If the resource is closed or another error occurs.
	 */
	public ResultSet executeSelect(String statement) throws SQLException{
		throwIfClosed();
		Statement s = this.conn.createStatement();
		s.setFetchSize(5000);
		return s.executeQuery(statement);
	}

	/**
	 * Executes the insert statement against the connection used when the object was created.
	 * @param statement The insert statement
	 * @return The id of the inserted row.
	 * @throws SQLException If the statement is not an insert statement or there is an
	 * error, or the resource is closed.
	 */
	public int executeInsert(String statement) throws SQLException{
		throwIfClosed();
		if(!statement.toLowerCase().contains("insert"))
			throw new SQLException("This is not an insert statement: "+statement);
		Statement s = this.conn.createStatement();
		s.executeUpdate(statement);
		ResultSet rs=s.getGeneratedKeys();
		int last_insert_id = rs.getInt(1);
		s.close();
		return last_insert_id;
	}
	/**
	 * Executes an update statement against the connection used in the constructor.
	 * @param statement The update statement
	 * @throws SQLException If the statement is not an update statement or there is an
	 * error, or the resource is closed.
	 */
	public void executeUpdate(String statement) throws SQLException{
		throwIfClosed();
		if(!statement.toLowerCase().contains("update"))
			throw new SQLException("This is not an update statement: "+statement);
		Statement s = this.conn.createStatement();
		s.executeUpdate(statement);
		s.close();
	}
	/**
	 * Gets the results of the given query as a CSV and saves them to the given file.
	 * @param csv The file to save the results to.
	 * @param statement The SELECT statement to run against the database.
	 * @throws SQLException If there was a problem running the query.
	 * @throws IOException If there was a problem writing the file.
	 */
	public void getResultsAsCSV(File csv, String statement) throws SQLException, IOException {
		ResultSet rs = this.executeSelect(statement);
		ResultSetMetaData rsmd = rs.getMetaData();
		CSVWriter writer = FileUtils.getCSVWriter(csv);
		String[] headers = new String[rsmd.getColumnCount()];
		for(int i=1;i<=rsmd.getColumnCount();i++){
			headers[i-1] = rsmd.getColumnLabel(i);			
		}
		writer.writeNext(headers);
		try{
			writeResultSetToCSV(writer,rs);
		}finally{
			writer.close();
		}
	}
	/**
	 * This method appends the results of the query to the given CSV. No headers will be written.
	 * @param csv The CSV to append the results to.
	 * @param statement The SELECT statement to run against the database.
	 * @throws SQLException If there was a problem running the query.
	 * @throws IOException If there was a problem writing the file.
	 */
	public void appendResultsToCSV(File csv, String statement) throws SQLException, IOException {
		CSVWriter writer = FileUtils.getCSVWriter(csv, true);
		try{
			writeResultSetToCSV(writer,this.executeSelect(statement));
		}finally{
			writer.close();
		}
	}
	/**
	 * Will chunk up the query into smaller chunks and combines the results. Can either put chunk files in the given directory or output all results to the same file.
	 * @param statement The SQL string you want to chunk. Make sure it has minIdString and maxIdString in the appropriate locations.
	 * @param minIdString The string that is in the location you want to put the minimum id in the query. All instances will be replaced. 
	 * @param maxIdString The string that is in the location you want to put the maximum id in the query. All instances will be replaced.
	 * @param minId The smallest id that you want to check. (Inclusive)
	 * @param maxId The largest id that you want to check. (Exclusive)
	 * @param chunkSize The size of the chunks you want to run.
	 * @param f The File/Directory you want to output to.
	 * @param separateFiles True if you want the outputs to be in individual files for each chunk. False if you want the results all in one file.
	 * @throws IOException If there is an IOException writing to the file.
	 * @throws SQLException If there is an SQLException while running the query.
	 */
	public void chunkifyQueryByIdCSV(String statement, String minIdString, String maxIdString, int minId, int maxId, int chunkSize, File f, boolean separateFiles) throws SQLException, IOException{
		int currentStart = minId;
		int currentEnd = minId+chunkSize;
		int maxRunId;
		do{
			String chunkedStatement = statement.replace(minIdString, Integer.toString(currentStart)).replace(maxIdString, Integer.toString(currentEnd));
			System.out.println("Running "+currentStart+" to "+currentEnd+".");
			if(separateFiles){
				f.mkdir();
				File chunk = new File(f, currentStart+"-"+currentEnd+".csv");
				getResultsAsCSV(chunk,chunkedStatement);
			}else{
				if(minId==currentStart){
					getResultsAsCSV(f,chunkedStatement);
				}else{
					appendResultsToCSV(f,chunkedStatement);
				}
			}
			maxRunId=currentEnd;
			currentStart+=chunkSize;
			currentEnd+=chunkSize;
		}while(maxRunId<maxId);
	}
	
	/**
	 * Splits the query into smaller chunks. All IDs in the given list will be run and the results will be output to the given file as a CSV
	 * @param statement The SQL string you want to chunk. Assure it has idString in the appropriate location.
	 * @param idString The string to replace with comma separated ids.
	 * @param ids An iterable containing the IDs that you want to run.
	 * @param chunkSize The size of the chunks you want to run.
	 * @param f The File/Directory you want to output to.
	 * @throws SQLException If there is an SQLException while running the query.
	 * @throws IOException If there is an IOException writing to the file.
	 */
	public void chunkifyQueryByIdCSV(String statement, String idString, Iterable<? extends String> ids, int chunkSize, File f) throws SQLException, IOException{
		StringJoiner joiner = new StringJoiner(",");
		int elements = 0;
		int chunks = 1;
		boolean firstRun = true;
		for(String id: ids){
			joiner.add(id);
			elements++;
			if(elements >= chunkSize){
				System.out.println("Running chunk #" + chunks);
				String chunkedQuery = statement.replace(idString,joiner.toString());
				if(firstRun){
					getResultsAsCSV(f,chunkedQuery);
					firstRun = false;
				}else{
					appendResultsToCSV(f, chunkedQuery);
				}
				joiner = new StringJoiner(",");
				elements = 0;
				chunks++;
			}
		}
		if(elements >=0){
			System.out.println("Running last chunk #" + chunks);
			String chunkedQuery = statement.replace(idString,joiner.toString());
			if(firstRun){
				getResultsAsCSV(f,chunkedQuery);
			}else{
				appendResultsToCSV(f, chunkedQuery);
			}
		}
	}
	private void writeResultSetToCSV(CSVWriter writer, ResultSet rs) throws SQLException, IOException{
		ResultSetMetaData rsmd = rs.getMetaData();
		while(rs.next()){
			String[] rowData = new String[rsmd.getColumnCount()];
			for(int i=1; i<=rsmd.getColumnCount(); i++){
				try{
					if((rsmd.getColumnType(i)==Types.INTEGER) ||
							(rsmd.getColumnType(i)==Types.SMALLINT) ||
							(rsmd.getColumnType(i)==Types.TINYINT)){
						rowData[i-1] = Integer.toString(rs.getInt(i));
					}
					else if(rsmd.getColumnType(i)==Types.BIGINT){
						rowData[i-1] = Long.toString(rs.getLong(i));
					}
					else if(rsmd.getColumnType(i)==Types.DOUBLE ||
							rsmd.getColumnType(i)==Types.FLOAT ||
							rsmd.getColumnType(i)==Types.DECIMAL ||
							rsmd.getColumnType(i)==Types.NUMERIC ||
							rsmd.getColumnType(i)==Types.REAL){
						rowData[i-1] = Double.toString(rs.getDouble(i));
					}
					else if(rsmd.getColumnType(i)==Types.VARCHAR ||
							rsmd.getColumnType(i)==Types.BLOB ||
							rsmd.getColumnType(i)==Types.LONGNVARCHAR ||
							rsmd.getColumnType(i)==Types.LONGVARCHAR ||
							rsmd.getColumnType(i)==Types.CHAR ||
							rsmd.getColumnType(i)==Types.SQLXML){
						rowData[i-1] = rs.getString(i);
					}
					else if(rsmd.getColumnType(i)==Types.BOOLEAN ||
							rsmd.getColumnType(i)==(Types.BIT)){
						rowData[i-1] = Boolean.toString(rs.getBoolean(i));
					}
					else if(rsmd.getColumnType(i)==Types.DATE){
						rowData[i-1] = rs.getDate(i).toLocalDate().toString();
					}
					else if(rsmd.getColumnType(i)==Types.TIME ||
						rsmd.getColumnType(i)==Types.TIME_WITH_TIMEZONE){
						rowData[i-1] = rs.getTime(i).toLocalTime().toString();
					}
					else if(rsmd.getColumnType(i)==Types.TIMESTAMP ||
							rsmd.getColumnType(i)==Types.TIMESTAMP_WITH_TIMEZONE){
						rowData[i-1] = rs.getTimestamp(i).toLocalDateTime().toString();
					}
					else{
						rowData[i-1] = rs.getObject(i).toString();
					}
					if(rs.wasNull())
						rowData[i-1] = "";
				}catch(NullPointerException e){
					rowData[i-1]="";
				}
			}
			writer.writeNext(rowData);
		}
		rs.close();
	}
	/**
	 * Throws an SQL Exception if the object is closed.
	 * @throws SQLException Thrown if the object is closed.
	 */
	private void throwIfClosed() throws SQLException {
		if(isClosed)
			throw new SQLException("This resource has already been closed");
	}
	public void close() {
		if(isClosed)
			return;
		isClosed=true;
		try {
			this.conn.close();
		} catch (SQLException e) {}
	}
}
