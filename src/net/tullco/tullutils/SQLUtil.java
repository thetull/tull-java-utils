package net.tullco.tullutils;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLUtil {

	private final Connection conn;
	private boolean isClosed=false;
	
	public SQLUtil(Connection conn){
		this.conn=conn;
	}
	
	public ResultSet executeSelect(String statement) throws SQLException{
		if(isClosed)
			throw new SQLException("This object has already been closed");
		Statement s = this.conn.createStatement();
		s.closeOnCompletion();
		return s.executeQuery(statement);
	}

	public int executeInsert(String statement) throws SQLException{
		if(isClosed)
			throw new SQLException("This object has already been closed");
		Statement s = this.conn.createStatement();
		s.executeUpdate(statement);
		ResultSet rs=s.getGeneratedKeys();
		int last_insert_id = rs.getInt(1);
		s.close();
		return last_insert_id;
	}

	public void executeUpdate(String statement) throws SQLException{
		if(isClosed)
			throw new SQLException("This object has already been closed");
		Statement s = this.conn.createStatement();
		s.executeUpdate(statement);
		s.close();
	}
	public void close() throws SQLException{
		isClosed=true;
		this.conn.close();
	}
}
