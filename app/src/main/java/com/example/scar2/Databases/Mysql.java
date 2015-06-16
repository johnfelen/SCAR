package com.example.scar2;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;

import android.util.Log;


public class Mysql implements IServer {
	public static final String CLASSTAG = Mysql.class.getSimpleName();
	public String dbserver;
	public String database = "scar_db" ;
	public String userName;
	public String pwd;
	public String dbDriver = "com.mysql.jdbc.Driver";
	public String dbPrefix = "jdbc:mysql://";
	public String dbConnect;
	public Connection conn = null;
	public Statement stmt = null;
    public boolean isConnected = false;

	public Mysql(String dbserver, String userName, String pwd)
	{
		this.dbserver=dbserver;
		this.userName=userName;
		this.pwd=pwd;
		dbConnect = dbPrefix + dbserver + "/" + database;
		try {
			Class.forName(dbDriver).newInstance();
			conn = DriverManager.getConnection(dbConnect, userName, pwd);
			Log.v(Constant.LOGTAG, " " + Mysql.CLASSTAG + " Database connected");
			stmt = conn.createStatement();
            this.isConnected = true;
		} catch (Exception ex) {
			ex.printStackTrace();
			Log.v(Constant.LOGTAG, " " + Mysql.CLASSTAG + ex.getMessage());
		}
	}

    public boolean isConnected()    //checks if the server successfully connected
    {
        return isConnected;
    }

	
	public ResultSet executeQuery(String sql) throws SQLException {
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(sql);
			
		} catch (SQLException ex) {
			Log.v(Constant.LOGTAG, " " + Mysql.CLASSTAG + "executeQuery wrong: "+sql);
			throw new SQLException("executeQuery wrong!!!");
		} 
		return rs;
	}

	public void executeUpdate(String sql) throws SQLException {
		try {
			stmt.executeUpdate(sql);
		} catch (SQLException ex) {
			Log.v(Constant.LOGTAG, " " + Mysql.CLASSTAG + "executeUpdate wrong: "+sql);
			throw new SQLException("executeUpdate wrong!!!");
		} 
	}

	public void closeStmt() {
		try {
			stmt.close();
		} catch (SQLException e) {
			Log.v(Constant.LOGTAG, " " + Mysql.CLASSTAG + "close stmt wrong " +e.getMessage());
		}
	}

	public void closeConn() {
		try {
			conn.close();
		} catch (SQLException e) {
			Log.v(Constant.LOGTAG, " " + Mysql.CLASSTAG + "close connection wrong " +e.getMessage());
		}
	}
	

  //Stores our data bytes with key fn
  public void storeData(String fn, byte[] data) {
    PreparedStatement stmt = conn.prepareStatement("insert into scar_db.files values (?, ?)");
    stmt.setString(1, fn);
    stmt.setBytes(2, data);
    stmt.executeUpdate();
  }

  //Gets our data bytes from key fn
  public byte[] getData(String fn) {
    if(isConnected()) {
      PreparedStatement stmt = conn.prepareStatement("select value from scar_db.files f where f.key = ?");
      stmt.setString(1, fn);
      ResultSet rs = stmt.executeQuery();
      if(rs.next()) {
        return rs.getBytes(1);
      }
    }
    return null;
  }
}
