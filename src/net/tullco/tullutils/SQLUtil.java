package net.tullco.tullutils;

import java.sql.Statement;
import java.io.Closeable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLUtil implements Closeable {

	private final Connection conn;
	private boolean isClosed=false;
	
	public SQLUtil(Connection conn){
		this.conn=conn;
	}
	
	public ResultSet executeSelect(String statement) throws SQLException{
		throwIfClosed();
		Statement s = this.conn.createStatement();
		s.closeOnCompletion();
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

	public void executeUpdate(String statement) throws SQLException{
		throwIfClosed();
		Statement s = this.conn.createStatement();
		s.executeUpdate(statement);
		s.close();
	}
	/**
	 * Throws an SQL Exception if the object is closed.
	 * @throws SQLException Thrown if the object is closed.
	 */
	private void throwIfClosed() throws SQLException {
		if(isClosed)
			throw new SQLException("This object has already been closed");
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
