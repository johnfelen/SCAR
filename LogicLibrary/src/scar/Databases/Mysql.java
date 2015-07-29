package scar;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;

import java.net.InetAddress;


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
    tryConnect();
	}

  public void tryConnect() {
		try {
			Class.forName(dbDriver).newInstance();
			conn = DriverManager.getConnection(dbConnect, userName, pwd);
			stmt = conn.createStatement();
      this.isConnected = true;
		} catch (Exception ex) {
			ex.printStackTrace();
			isConnected = false;
		}
  }

  public boolean getStatus() {
    try {
      if(!InetAddress.getByName(dbserver).isReachable(30))
        return false;
    } catch(Exception e) { return false; }
    return true;
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
			throw new SQLException("executeQuery wrong!!!");
		} 
		return rs;
	}

	public void executeUpdate(String sql) throws SQLException {
		try {
			stmt.executeUpdate(sql);
		} catch (SQLException ex) {
			throw new SQLException("executeUpdate wrong!!!");
		} 
	}

	public void closeStmt() {
		try {
			stmt.close();
		} catch (SQLException e) {
			
		}
	}

	public void close() {
		try {
			conn.close();
		} catch (SQLException e) {
			
		}
	}
	

  //Stores our data bytes with key fn
  public void storeData(String fn, byte[] data) {
    try { 
    PreparedStatement stmt = conn.prepareStatement("insert into scar_db.files values (?, ?)");
    stmt.setString(1, fn);
    stmt.setBytes(2, data);
    stmt.executeUpdate();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  //Gets our data bytes from key fn
  public byte[] getData(String fn) {
    try {
      if(isConnected()) {
        PreparedStatement stmt = conn.prepareStatement("select value from scar_db.files f where f.key = ?");
        stmt.setString(1, fn);
        ResultSet rs = stmt.executeQuery();
        if(rs.next()) {
          return rs.getBytes(1);
        }
      }
      return null;
    } catch(Exception e) {
      e.printStackTrace();
      return null;
    }
  } 
}
