package com.example.scar2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.jlinalg.Matrix;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

//this activity is displayed when the user chooses the Store button on the MainActivity.java page
//this activity lets the user select a file and stores it on the server
//this activity uses the store_layout.xml file 

public class Store extends Activity {

	public static final String CLASSTAG = Store.class.getSimpleName();
	private PrintWriter pw;
	private Button getImg, store_btn;
	private ProgressDialog progressDialog;
	TextView textTargetUri;
	EditText password, f_name;
	String path ="";
	private ArrayList<Mysql> serverList = new ArrayList<Mysql>();
    private final  Handler handler = new Handler() {
        @Override
        public void handleMessage(final Message msg) {
            if(msg.what<100)
            {
            	progressDialog.setProgress(msg.what);
            }
            else{
            progressDialog.setProgress(100);
            progressDialog.dismiss();
            Log.v(Constant.LOGTAG, " " + Store.CLASSTAG + " worker thread done, file stored");
/*            new AlertDialog.Builder(getStoreRef()).setTitle("SCAR").
            setMessage("File Stored").
            setPositiveButton("Continue",
            new android.content.DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int arg1) {
                    // Just close alert.
                }
            }).show();*/
            }

        }
    };
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);



		//Internet permission
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
			.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		setContentView(R.layout.store_layout);
		//ActionBar actionBar = getActionBar();
		//actionBar.setDisplayHomeAsUpEnabled(true);
		//Server init
		getImg = (Button)findViewById(R.id.getImage);
		store_btn = (Button)findViewById(R.id.store_button);
		//set the store button to false upon all text entered
		//store_btn.setEnabled(false);

		password = (EditText) findViewById(R.id.Enter_Pass);
		f_name = (EditText) findViewById(R.id.Enter_fname);
		//get array list of file names

		//GET IMAGE BUTTON
		getImg.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(
						Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(intent, 0);

			}
		});


		//STORE BUTTON
		store_btn.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				AlertDialog.Builder newDialog = new AlertDialog.Builder(Store.this);


				//Saving list of file names in shared preferences
				if(password.getText().toString().compareTo("") == 0  || f_name.getText().toString().compareTo("") == 0)
				{
					//nothing was entered
					//don't save anything in shared preferences

					//print message
					newDialog.setTitle("Text box was empty!");
					newDialog.setMessage("Nothing was entered.");
					newDialog.setPositiveButton("OK", new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface dialog, int which){
							password.setText("");
							f_name.setText("");
							dialog.dismiss();
						}
					});
				}
				else
				{
					//something was entered
					//add string to end of array list

					//if the array already contains the word entered

						//file name has not been used

						//No image selected
						if(path.compareTo("")==0){

							//print message
							newDialog.setTitle("Alert!");
							newDialog.setMessage("You did not select a picture. Try Again.");
							newDialog.setPositiveButton("OK", new DialogInterface.OnClickListener(){
								@Override
								public void onClick(DialogInterface dialog, int which){
									password.setText("");
									f_name.setText("");
									dialog.dismiss();
								}
							});
						}
						else
						{
							//Image and password selected
							try {

									storeFile();
							} catch (FileNotFoundException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}							
						}
						//print message
/*						newDialog.setTitle("File Stored!");
						newDialog.setPositiveButton("OK", new DialogInterface.OnClickListener(){
							@Override
							public void onClick(DialogInterface dialog, int which){
								password.setText("");
								f_name.setText("");
								dialog.dismiss();
							}
						});*/
					}
				
				

			}});

	}

	public void storeFile() throws FileNotFoundException
	{
		int nServers = MainActivity.serverList.size();
		for(int i = 0; i < nServers; i++)
		{
			Server s = MainActivity.serverList.get(i);
			String hostName = s.getHostname()+":"+s.getPort();
			Mysql mysql = new Mysql(hostName,s.getUsername(),s.getPassword());
			serverList.add(mysql);
		}
		String s = Environment.getExternalStorageState();
		Log.v(Constant.LOGTAG, " "+ Store.CLASSTAG+" is dir ready? "+ s);		


		progressDialog = new ProgressDialog(this);
		progressDialog.setTitle("Store File");
		progressDialog.setMessage("Working");
		progressDialog.setIndeterminate(false);
		progressDialog.setCancelable(false);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressDialog.setMax(100);
		progressDialog.setProgress(0);
		progressDialog.show();
        new Thread() {
            @Override
            public void run() {
        		try {
                    //for testing purpose
                    File sdCard = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                    File dir = new File(sdCard.getAbsolutePath() + "/files");
                    if (!dir.exists())
                        dir.mkdir();
                    File file = new File(dir, "info.txt");
                    FileOutputStream fo = new FileOutputStream(file, true);
                    pw = new PrintWriter(fo);
                    int k = 1;
                    //for(int k = 5; k < 10; k++)
                    //{
                    //for(int i = 0; i < 10; i++)
                    //{
                    Mysql headerdb = new Mysql("10.0.3.2:3306", "root", "poney373");    //10.0.3.2 is for genymotion and 10.0.2.2 is for the normal android emulator
                    Mysql pittdb = new Mysql("mysql.cs.pitt.edu:3306", "diablo0897", "ChangeMe");

                    if (!headerdb.isConnected() || !pittdb.isConnected())   //check if the Mysql objects are connected with the server
                    {
                        Log.v(Constant.LOGTAG, "Failed to connect");
                        return;
                    }

		        		String sql = "truncate headers;";
		        		String sql2 = "truncate files";
	        			headerdb.executeUpdate(sql);
	        			headerdb.executeUpdate(sql2);
	        			pittdb.executeUpdate(sql2);


	        			Log.v(Constant.LOGTAG, " "+ Store.CLASSTAG+" is file ready? "+ file.exists());

	
		        		//begin storefile method
	
		        		StringBuilder str = new StringBuilder();
		        		str.append(f_name.getText().toString());
		
		        		//save entered data into shared preferences
		
		        		//load the server when they press the store button
		        		 
		        		//STORING SCAR STUFF						
		        		String filename = f_name.getText().toString().trim();
		        		String pass = password.getText().toString().trim();
	
	/*        		for(int i = 0; i < serverList.size(); i++)
	        		{
	        			Mysql server = serverList.get(i);
	        			String sql = "truncate files;";
	        			try {
							PreparedStatement stmt = Mysql.conn.prepareStatement(sql);
							stmt.executeUpdate();
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
	        		}*/
		        		Log.v(Constant.LOGTAG, " " + Store.CLASSTAG + "Storing file: "+filename+" pass: "+pass );
		        		long startTime = System.currentTimeMillis();
		        		MakeMatrix splitter = new MakeMatrix(path, k, 10);
		        		//gives us back the full matrix to send
		        		Matrix final_Matrix = null;
		        		final_Matrix = splitter.fileToByteArray();
		        		long file_size = splitter.getFileSize();
	        			pw.println("File name:"+ filename+" file_size: " +file_size+" k value is: "+k+" run: "+(0+1)+"\r\n");//0 = i HERE
		        		System.out.println("file_size: " +file_size);
		        		handler.sendEmptyMessage(20);
		        		long endTime = System.currentTimeMillis();
		        		pw.print("Convert file to matrix time: "+(endTime-startTime)+"\r\n");
		        		startTime = System.currentTimeMillis();
		        		StoreData store;
	        			store = new StoreData(final_Matrix, 10, k, filename, file_size);
	        			store.setHandler(handler);
	        			store.storeHash(filename, pass);
	        			endTime = System.currentTimeMillis();
		        		pw.print("Store file time: "+(endTime-startTime)+"\r\n");
					//}
        			//}
	        		pw.flush();
	        		pw.close();
	        		Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
	        		intent.setData(Uri.fromFile(file));
	        		sendBroadcast(intent);
        			handler.sendEmptyMessage(100);
        		} catch (IOException e) {
        			// TODO Auto-generated catch block
        			Log.v(Constant.LOGTAG, " " + Store.CLASSTAG + e.getMessage());
        		} catch (SQLException e) {
        			// TODO Auto-generated catch block
        			Log.v(Constant.LOGTAG, " " + Store.CLASSTAG + e.getMessage());
        		}

            }
        }.start();

	}
	
	/*
	 * fileToByteArray() -converts a path into byte[]. Uses absolute path.
	 */
	public  Store getStoreRef()
	{
		return this;
	}
	public static byte[] fileToByteArray(String path) throws IOException {
		File imagefile = new File(path);
		byte[] data = new byte[(int) imagefile.length()];
		FileInputStream fis = new FileInputStream(imagefile);
		fis.read(data);
		fis.close();
		return data;
	}

	//get the list of file names and put it into an ArrayList
	public static ArrayList<String> getStringFromPref(Activity activity)
	{
		//Get the string and parse it using StringTokenizer:
		SharedPreferences prefs = activity.getSharedPreferences("file_name", Context.MODE_PRIVATE);
		String savedString = prefs.getString("file_names", "");
		StringTokenizer st = new StringTokenizer(savedString, ",");

		ArrayList<String> file_names_arr = new ArrayList<String>();
		while(st.hasMoreTokens())
		{
			file_names_arr.add(st.nextToken());
		}

		return file_names_arr;
	}


	//get the array list and save it back into preferences
	public static void saveStringToPref(ArrayList<String> file_names_arr, Activity activity)
	{
		SharedPreferences prefs = activity.getSharedPreferences("file_name", Context.MODE_PRIVATE);
		StringBuilder str = new StringBuilder();

		for (int i = 0; i < file_names_arr.size(); i++) {
			str.append(file_names_arr.get(i)).append(",");
		}


		SharedPreferences.Editor e = prefs.edit().putString("file_names", str.toString());
		e.commit();

	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item){

		Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
		startActivityForResult(myIntent, 0);
		return true;

	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == 888) {
			System.out.println("Back from settings");
		} else if (resultCode == RESULT_OK) { // RESULT == OK means that you're back from the selection intent. 

			//targetUri is the Uri from the intent
			Uri targetUri = data.getData();
			//I'm using the targetUri to find the realPath (e.g.) .../folder/image.jpg)
			this.path = getRealPathFromURI(targetUri);
			System.out.println("Path is: " +this.path);

			String [] str = this.path.split("\\/");
			f_name.setText(str[str.length-1]);

		}
	}

	/**
	 * @description the this.path variable will use the return value from this method
	 * @param contentUri
	 * @return String of the absolute path
	 */
	public String getRealPathFromURI(Uri contentUri) {

		// can post image
		String[] proj = { MediaColumns.DATA };
		Cursor cursor = managedQuery(contentUri, proj, // Which columns to
				// return
				null, // WHERE clause; which rows to return (all rows)
				null, // WHERE clause selection arguments (none)
				null); // Order-by clause (ascending by name)
		int column_index = cursor
				.getColumnIndexOrThrow(MediaColumns.DATA);
		cursor.moveToFirst();

		return cursor.getString(column_index);
	}


	//Loads the server from the Global setting file
/*	public ArrayList<Server> LoadServer(){
		ArrayList<Server> OnServers=  new ArrayList<Server>(); // Our result
		DatabaseHandler db = new DatabaseHandler(getBaseContext()); // Connect to DB
		//Dynamically add a new server to the list
		for (Server server : MainActivity.serverList) {

			if(server.getStatus()==1){ // If it is ON
				OnServers.add(server);
			}
			else{
				//If it is OFF
			}

		}
		return OnServers;
	}*/


}
