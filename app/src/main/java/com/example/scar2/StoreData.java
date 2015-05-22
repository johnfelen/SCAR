package com.example.scar2;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.MessageDigest;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.zip.CRC32;

import org.jlinalg.IRingElement;
import org.jlinalg.IRingElementFactory;
import org.jlinalg.Matrix;
import org.jlinalg.Vector;
import org.jlinalg.field_p.FieldPFactoryMap;

import android.os.Handler;
import android.util.Log;



public class StoreData {

	public static final String CLASSTAG = StoreData.class.getSimpleName();
	Matrix matrix;
	int f, k;
	String file_name;
	long file_size;
	private ByteArrayOutputStream bos;
	private ObjectOutputStream oos;
	private Handler handler;
	private int nServers;
	private Mysql header_db = new Mysql("10.0.3.2:3306","root","poney373");
	private ArrayList<Mysql> serverList = new ArrayList<Mysql>();
	
	StoreData(Matrix M, int f, int k, String filename, long file_size) throws IOException
	{
		this.matrix = M;
		this.f = f;
		this.k = k;
		this.file_name = filename;
		this.file_size = file_size;
		bos = new ByteArrayOutputStream();
		oos = new ObjectOutputStream(bos);
		nServers = MainActivity.serverList.size();
		for(int i = 0; i < nServers; i++)
		{
			Server s = MainActivity.serverList.get(i);
			String hostName = s.getHostname()+":"+s.getPort();
			Mysql mysql = new Mysql(hostName,s.getUsername(),s.getPassword());
			serverList.add(mysql);
		}
	}
	public void setHandler(Handler handler)
	{
		this.handler=handler;
	}
	public Handler getHandler(Handler handler)
	{
		return this.handler;
	}
	void storeHeader(String filename,String pass,long f_size) throws SQLException
	{
		
		String sql = "insert into scar_db.headers (file_name,pass_word,file_size) values (\""+filename+"\",\""+pass+"\","+f_size+");";
		header_db.executeUpdate(sql);
	}
	/**
	 * @description algo is: input(filename, password) 1st key: hash(filename + password). 2nd key: hash(f1 + filename+password),..
	 * @param filename
	 * @param password
	 * @throws SQLException 
	 */
	public void storeHash(String filename, String password) throws SQLException{
		
		Matrix final_Matrix = this.matrix;
		storeHeader(filename,password,this.file_size);
		byte[] buffer;
		Hash hash = new Hash(); // Creating a Hashing object
		hash.setArr(); // Call this to initialize the array keys		
		hash.recursiveKey(f, filename, password); // Call this to create the keys
		ArrayList<String> tempKeys = (hash.getArr()); // Call this to retrieve it. Index 0 - 1st key

		for(int i = 0; i < this.f; i++){
			handler.sendEmptyMessage(i*80/f);
			ArrayList<Integer> _DATA = new ArrayList<Integer>();
			long start = System.currentTimeMillis();
			Vector v = final_Matrix.getRow(i+1);
			for(int j = 0; j < v.length(); j++)
			{
				IRingElement fl = v.getEntry(j+1);
				int value = Integer.parseInt(fl.toString().split("m")[0]);
				_DATA.add(value);
			}
			long end = System.currentTimeMillis();
			Log.v(Constant.LOGTAG, " " + StoreData.CLASSTAG + "  Read value from matrix took :"+(end-start));
			System.out.println("serialize took "+(end-start));
			int serverID = Integer.decode("0x"+tempKeys.get(i).substring(0,4));
			int s = serverID%nServers;
			storeRow(tempKeys.get(i), _DATA  , serverList.get(s) );			
		}

	}

	/**
	 * @description stores the key and value into the server. This is String, String based. Can be byte[] , byte[]. 
	 * @param key
	 * @param value
	 * @param server
	 */
	private void storeRow(String key, ArrayList<Integer> value, Mysql server){
		//System.out.println("==== in storeRow (MongoDB) ====");
		try {
			ByteArrayOutputStream bos;
			ObjectOutputStream oos;
			bos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(bos);
			String sql = "insert into scar_db.files values(\""+key+"\",?);";
			PreparedStatement stmt = server.conn.prepareStatement(sql);
			oos.writeObject(value);
			byte [] buffer = bos.toByteArray();
			stmt.setBytes(1, buffer);
			stmt.executeUpdate();
		} catch (Exception e) {
			Log.v(Constant.LOGTAG, " " + StoreData.CLASSTAG + e.getMessage());
			e.printStackTrace();
		}
	}

	public long calculateChecksum(byte [] b)
	{
		CRC32 checksum = new CRC32();
		checksum.update(b);	
		return checksum.getValue();
	}

	public long getunitSignature()
	{
		return 0;
	}



}
