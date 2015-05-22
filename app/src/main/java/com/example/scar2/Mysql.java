package com.example.scar2;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;

import android.util.Log;


public class Mysql {
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
		} catch (Exception ex) {
			ex.printStackTrace();
			Log.v(Constant.LOGTAG, " " + Mysql.CLASSTAG + ex.getMessage());
		}
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
	
/*	public static void main(String[] args)
	{
		Mysql mysql = new Mysql("192.168.1.3:3060","root","poney373");  //MAYBE CHANGE HERE
		String sql1 = "select * from scar_db.files;";
		try {
			ResultSet rs = mysql.executeQuery(sql1);
			rs.next();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			Log.v(Constant.LOGTAG, " " + Mysql.CLASSTAG  +e.getMessage());
		}
	}*/

}
