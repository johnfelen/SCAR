package com.example.scar2;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.codec.binary.Base64;
import org.jlinalg.IRingElement;
import org.jlinalg.IRingElementFactory;
import org.jlinalg.Matrix;
import org.jlinalg.Vector;
import org.jlinalg.field_p.FieldPFactoryMap;

import android.os.Handler;
import android.util.Log;



public class RetrieveData 
{
	public static final String CLASSTAG=RetrieveData.class.getSimpleName();
	int f, k;
	long f_size;
	private Mysql header_db;
	private Handler handler;
	private int nServers;
	private ArrayList<Mysql> serverList = new ArrayList<Mysql>();

	RetrieveData(int f, int k)
	{
		this.f = f;
		this.k = k;
		header_db = new Mysql("10.0.3.2:3306","root","poney373");
        if (!header_db.isConnected())   //check if the Mysql objects are connected with the server
        {
            Log.v(Constant.LOGTAG, "Failed to connect");
            return;
        }
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
	
	long getFileSize(String filename,String pass) throws SQLException
	{
		String sql = "select file_size from scar_db.headers where file_name='"+filename+"' and pass_word='"+pass+"';";
		ResultSet rs = header_db.executeQuery(sql);
		rs.next();
		long f_size = rs.getLong(1);
		return f_size;
	}

	/**
	 * @description does the hashing algo from Hash.java. key is returned in a list (ArrayList)
	 * @param filename
	 * @param password
	 * @return
	 * @throws SQLException 
	 */
	public Matrix getMatrixFromServerHashed(String filename, String password) throws SQLException{

		byte[] _DATABACK;
		Vector[] _VECTORS = new Vector[k];
		System.out.println("\n=============== Retrieval ===============");
		this.f_size=getFileSize( filename, password);
		Hash hash = new Hash(); // Creating a Hashing object
		hash.setArr(); // Call this to initialize the array keys		
		hash.recursiveKey(f, filename, password); // Call this to create the keys
		ArrayList<String> tempKeys = (hash.getArr()); // Call this to retrieve it. Index 0 - 1st key


		int [] arr = new int [k];
		for(int i = 0; i < k; i++)
		{
			_DATABACK = null;
            BigInteger serverID = new BigInteger( tempKeys.get(i), 16 );   //create a new big integer from the hexadecimal key
            int serverIndex = serverID.mod( BigInteger.valueOf(nServers) ).intValue();   //this long line of code takes the serverID, mods it by the nServers(number of servers) and transforms it into an int to get the serverIndex
            _DATABACK = getRow(tempKeys.get(i),serverList.get( serverIndex ));
				Log.v(Constant.LOGTAG, " " + RetrieveData.CLASSTAG + " Retrieving key:"+tempKeys.get(i)+" ");
				//arr[i] = get header row index
			try{

                _VECTORS[i] = toVector(_DATABACK);

            }catch(Exception e)
			{
				e.printStackTrace();
				Log.v(Constant.LOGTAG, " " + RetrieveData.CLASSTAG + e.getMessage());
				//System.out.println("Skipped");
			}
		}
		long start = System.currentTimeMillis();
		Matrix serverMatrix = new Matrix(_VECTORS);
		int counter = 0;
		Vector v[] = new Vector[serverMatrix.getRows()];
		//reversing matrix we get back
		for(int i =serverMatrix.getRows() - 1; i >= 0; i--)
		{
			v[counter] = serverMatrix.getRow(i+1);

			counter++;

		}
		long end = System.currentTimeMillis();
		Log.v(Constant.LOGTAG, " " + RetrieveData.CLASSTAG + " inverse Matrix took:"+(end-start));

		serverMatrix = new Matrix(v);

		start = System.currentTimeMillis();
		Matrix decoding_Matrix = this.makeDecodingMatrix(this.k, this.f);
		end = System.currentTimeMillis();
		Log.v(Constant.LOGTAG, " " + RetrieveData.CLASSTAG + " make decode Matrix took:"+(end-start));
		//System.out.println(decoding_Matrix);
		start = System.currentTimeMillis();
		decoding_Matrix = decode(decoding_Matrix, arr);
		end = System.currentTimeMillis();
		Log.v(Constant.LOGTAG, " " + RetrieveData.CLASSTAG + " decode Matrix took:"+(end-start));
		//System.out.println(decoding_Matrix);
		start = System.currentTimeMillis();
		Matrix finalMatrix = (decoding_Matrix.inverse()).multiply(serverMatrix);
		end = System.currentTimeMillis();
		Log.v(Constant.LOGTAG, " " + RetrieveData.CLASSTAG + " Matrix multiplication took:"+(end-start));

		return finalMatrix;

	}

	/**
	 * @description returns the data as a string with key and server params
	 * @param key
	 * @param server (Redis)
	 * @return
	 */


	/**
	 * @description returns the data as a string with key and server params
	 * @param key
	 * @param server (MongoDB)
	 * @return
	 */
	private byte[] getRow(String key, Mysql server){
		//System.out.println("==== in getRow (MongoDB) ====");

		byte[] _DATABACK= null;

		try{
			String sql = "select value from scar_db.files f where f.key=\""+key+"\";";
			ResultSet rs = server.executeQuery(sql);
			if(rs.next())
			{
				_DATABACK = rs.getBytes(1);
			}
			else
			{
				throw new Exception("Invalid key or nothing found");
			}
			
		}catch(Exception e){

			Log.v(Constant.LOGTAG, " " + RetrieveData.CLASSTAG + e.getMessage());
		}
		return _DATABACK;
	}

	private Vector toVector(byte[] _CONTENT) throws IOException, ClassNotFoundException{
		//System.out.println("==== in toVector ====")

		/*
		 * Doing string splits because of the data is returned as a string
		 */
		if(_CONTENT == null){
			//No result is found from the server!
			return null;

		}

		else{
			ByteArrayInputStream bis = new ByteArrayInputStream(_CONTENT);
			ObjectInputStream ois = new ObjectInputStream(bis);
			@SuppressWarnings("unchecked")
			ArrayList<Integer> vectorData = (ArrayList<Integer>) ois.readObject();
			ois.close();
			/*
			 * Now doing the Vector conversion
			 */
			IRingElement[] theEntries = new IRingElement[vectorData.size()];
			IRingElementFactory elements = FieldPFactoryMap.getFactory(new Long(257));

			for(int i = 0; i < vectorData.size(); i++)
			{
				theEntries[i] = elements.get(vectorData.get(i));
			} 

			Vector <?> vector = new Vector(theEntries);

			return vector;
		}
	}
	@SuppressWarnings("unchecked")
	public Matrix makeDecodingMatrix(int k, int f)
	{

		//this function makes the encoding matrix

		Vector[] a = new Vector[(f-k)];
		IRingElementFactory elements = FieldPFactoryMap.getFactory(new Long(257));

		for(int i = 0; i < f-k; i++)
		{
			IRingElement[] theEntries = new IRingElement[k];

			for(int j = 0; j < k; j++)
			{
				theEntries[j] = this.power(this.power(elements.get(2), elements.get(f).subtract(elements.get(k)).add(elements.get(i))), elements.get(j));
			}

			a[i] = new Vector(theEntries);

			//System.out.println(a.toString());
		} 

		Vector [] b = new Vector[k];

		for ( int i = 0; i < k; i++)
		{
			IRingElement[] theEntries = new IRingElement[k];

			for ( int j = 0; j < k; j++)
			{
				if( (i == 0) && (j == 0) )
				{
					theEntries[j] = elements.get(1);
				}
				else if ( i == 0 )
				{
					theEntries[j] = elements.get(0);
				}
				else
				{
					theEntries[j] = this.power(this.power(elements.get(2), elements.get(i).subtract(elements.get(1))), elements.get(j));
				}
			}

			b[i] = new Vector(theEntries);
		}

		Matrix A_Matrix = new Matrix(a);
		Matrix B_Matrix = new Matrix(b);
		Matrix C_Matrix = A_Matrix.multiply(B_Matrix.inverse());

		Vector[] d = new Vector[f];

		for(int i = 0; i < k; i++)
		{
			IRingElement[] theEntries = new IRingElement[k];

			for( int j = 0; j < k; j++)
			{
				if(i == j)
				{
					theEntries[j] = elements.get(1);
				}
				else
				{
					theEntries[j] = elements.get(0);
				}
			}

			d[i] = new Vector(theEntries);
		}

		for(int i = k; i < f; i++)
		{
			IRingElement[] theEntries = new IRingElement[k];

			for(int j = 0; j < k; j++)
			{
				theEntries[j] = C_Matrix.get((i-k)+1, (j + 1));
			}

			d[i] = new Vector(theEntries);
		}

		Matrix D_Matrix = new Matrix(d);

		//D matrix is the encoded matrix
		return D_Matrix;
	}


	@SuppressWarnings("unchecked")
	private IRingElement power(IRingElement x, IRingElement n)
	{
		IRingElementFactory elements = FieldPFactoryMap.getFactory(new Long(257));

		IRingElement return_elem = x;

		if(n.compareTo(elements.get(0)) == 0)
		{
			return elements.get(1);
		}
		for(int i = 1; n.compareTo(elements.get(i)) > 0; i++)
		{
			return_elem = return_elem.multiply(x);
		}

		return return_elem;
	}
	public Matrix decode(Matrix decoding_Matrix, int[] arr)
	{
		//System.out.println("======= IN DECODE ======");

		Matrix decoded_Matrix = null;
		int counter = 0;
		Vector [] v = new Vector[arr.length]; 

		//System.out.println("matrixLen = " + decoding_Matrix.getRows());

		for(int i = arr.length-1; i >= 0; i--)
		{
			//System.out.println("i: " + i);
			//System.out.println("counter: " + counter);
			v[counter] = decoding_Matrix.getRow(i+1);

			//System.out.println(v[counter]);

			counter++;

		}

		decoded_Matrix = new Matrix(v);

		return decoded_Matrix;

	}



	public long getSize()
	{
		return this.f_size;
	}


	public byte[] matrixToByteArr(Matrix M)
	{
		Log.v(Constant.LOGTAG, " " + RetrieveData.CLASSTAG + "In martrixToByteArr");
		ArrayList<Byte> testingarray = new ArrayList<Byte>();
		int n = M.getRows();
		for(int i = 0; i < n; i++){
			long start = System.currentTimeMillis();
			handler.sendEmptyMessage((i)*80/n+20);
			Vector v = M.getRow(i+1);
			for(int j = 0; j < v.length();j++)
			{
				IRingElement fl = v.getEntry(j+1);
				int value = Integer.parseInt(fl.toString().split("m")[0]);
				testingarray.add((byte)value);				
			}
			long end1 = System.currentTimeMillis();
			
			Log.v(Constant.LOGTAG, " " + RetrieveData.CLASSTAG + "get row took:" +(end1-start));
//			String content = v.toString();
//			VectorToArray(testingarray, content);
			long end = System.currentTimeMillis();
			Log.v(Constant.LOGTAG, " " + RetrieveData.CLASSTAG + "One VectorToArray took :"+(end-end1));
		}

		//System.out.println("testingarray" + testingarray.size());

		//Convert to image
		long file_size1 = this.getSize();
		
		byte [] temp_test = new byte[testingarray.size()];
		for(int i = 0; i < testingarray.size(); i++)
		{
			temp_test[i] = testingarray.get(i);
			//System.out.print(testingarray.get(i));
		}
	
		byte [] arr = Arrays.copyOf(temp_test, (int)file_size1);
		Log.v(Constant.LOGTAG, " " + RetrieveData.CLASSTAG + "Arr.length = "+ arr.length);
		long start = System.currentTimeMillis();
		byte [] base64_decode_arr = Base64.decodeBase64(arr);
		long end = System.currentTimeMillis();
		Log.v(Constant.LOGTAG, " " + RetrieveData.CLASSTAG + "decode took :"+(end-start));
		Log.v(Constant.LOGTAG, " " + RetrieveData.CLASSTAG + "DECODED");
		
		return base64_decode_arr;

	}

	private static void VectorToArray(ArrayList<Byte> testingarray, String _CONTENT){
		System.out.println("==== in toVector ====");
		/*
		 * Doing string splits because of the data is returned as a string
		 */
		if(_CONTENT == null){
			//No result is found from the server!
		}

		else{
			long start = System.currentTimeMillis();
			_CONTENT = _CONTENT.replaceAll("\\(" , "").replaceAll("\\(", ""); // Getting rid of parenthesis
			long end = System.currentTimeMillis();
			System.out.println("replace took:"+(end-start));
			start = System.currentTimeMillis();
			String[] _CONTENT_SPLITS  = _CONTENT.split(", "); // Splitting the data with commas
			end = System.currentTimeMillis();
			System.out.println("split took:"+(end-start));
			//System.out.println(_CONTENT_SPLITS.length); 
			int[] vectorData = new int[_CONTENT_SPLITS.length]; // This contains the data for the vector
			start = System.currentTimeMillis();
			for(int i = 0; i < _CONTENT_SPLITS.length; i++){
				String[] _M_SPLITS = _CONTENT_SPLITS[i].split("m"); // Splitting between xxmxx

				int _VALUE = (byte)Integer.parseInt(_M_SPLITS[0]);
				testingarray.add((byte) _VALUE);
			}
			end = System.currentTimeMillis();
			System.out.println("replace m took:"+(end-start));
			
		}
	}

}
