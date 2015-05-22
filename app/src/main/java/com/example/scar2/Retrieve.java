package com.example.scar2;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.jlinalg.Matrix;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

//this activity is displayed when the retrieve button is clicked on the MainActivity page.
//this activity retrieves the document from the server
//this activity uses retrieve_layout.xml

public class Retrieve extends Activity  {
	
	public static final String CLASSTAG = Retrieve.class.getSimpleName();

	//Server init information
	Bitmap bitmap; // For building the image response
	ImageView image, dialog_image; // For the actual image response, uses imageResponse

	private Button getDoc;
	private ProgressDialog progressDialog;
	private byte[] byteArray;
	EditText doc_name, retrieve_pass;
    private final  Handler handler = new Handler() {
        @Override
        public void handleMessage(final Message msg) {
            AlertDialog.Builder newDialog = new AlertDialog.Builder(Retrieve.this);					
			if(msg.what<100)
			{
				progressDialog.setProgress(msg.what);
			}
			else
			{
				progressDialog.setProgress(100);
				progressDialog.dismiss();
				final Bitmap bitmap = BitmapFactory.decodeByteArray(
						byteArray, 0, byteArray.length);

				// print message
				newDialog.setTitle("Retrieved!");
				newDialog
						.setMessage("Would you like to save this document?");
				Log.v(Constant.LOGTAG, " " + Store.CLASSTAG
						+ " worker thread done, file stored");

				/*
				 * Guo's new edit!
				 */

				ViewGroup add_phone = (ViewGroup) getLayoutInflater()
						.inflate(R.layout.image_dialog_box, null);
				newDialog.setView(add_phone);
				int childCount = add_phone.getChildCount();
				// System.out.println("childCount" +childCount);
				add_phone.setEnabled(true);
				ImageView dialog_image = (ImageView) add_phone
						.getChildAt(0);
				// System.out.println(add_phone.getChildAt(0).getClass());

				dialog_image.setImageBitmap(bitmap);
				newDialog.setPositiveButton("Save Image",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {

								/*
								 * Writing image to file code
								 */
								try {

									// System.out.println("Saving to file...");
									String file_name = doc_name.getText()
											.toString() + "-test.jpg";
									File sdCard = Environment
											.getExternalStorageDirectory();
									File dir = new File(sdCard
											.getAbsolutePath() + "/server/");
									dir.mkdirs();
									File file = new File(dir, file_name);
									FileOutputStream f = new FileOutputStream(
											file);
									ByteArrayOutputStream bytes = new ByteArrayOutputStream();
									bitmap.compress(
											Bitmap.CompressFormat.JPEG, 40,
											bytes);

									f.write(bytes.toByteArray());
									f.close();
								} catch (Exception e) {
									Log.v(Constant.LOGTAG,
											" " + Retrieve.CLASSTAG
													+ e.getMessage());
								}
								doc_name.setText("");
								retrieve_pass.setText("");
								dialog.dismiss();
							}
						});
				newDialog.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								doc_name.setText("");
								retrieve_pass.setText("");
								dialog.dismiss();
							}
						});
				newDialog.show();
			}
        }
    };
	public Retrieve getStoreRef()
	{
		return this;
	}
	public void retrieveFile()
	{
		
			progressDialog = new ProgressDialog(this);
			progressDialog.setTitle("Retrieve File");
			progressDialog.setMessage("Working");
			progressDialog.setIndeterminate(false);
			progressDialog.setCancelable(false);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			progressDialog.setMax(100);
			progressDialog.setProgress(0);
			progressDialog.show();
			new Thread()
			{
				public void run()
				{

					try {
						ArrayList<Server> activeServers = LoadServer();	
						Server currentServer = activeServers.get(0); // For testing purposes, I'm getting the most recent that's ON.
						System.out.println("currentServer is: " +					
								currentServer.getHostname() +", " +currentServer.getPort());
						RetrieveData retrieve = new RetrieveData(10, 7);
						retrieve.setHandler(handler);
						Log.v(Constant.LOGTAG, " " + Retrieve.CLASSTAG + "Retrieving file: "+doc_name.getText().toString()+" pass: "+retrieve_pass.getText().toString());
						long start = System.currentTimeMillis();
						Matrix serverMatrix = retrieve.getMatrixFromServerHashed(doc_name.getText().toString(), retrieve_pass.getText().toString());
						handler.sendEmptyMessage(20);
						long end = System.currentTimeMillis();
						Log.v(Constant.LOGTAG, " " + Retrieve.CLASSTAG + "Compute matrix took :"+(end-start));
						start = System.currentTimeMillis();
						byteArray = retrieve.matrixToByteArr(serverMatrix);
						end = System.currentTimeMillis();
						Log.v(Constant.LOGTAG, " " + Retrieve.CLASSTAG + " Matrix to Byte took :"+(end-start));
						handler.sendEmptyMessage(100);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						Log.v(Constant.LOGTAG,Retrieve.CLASSTAG+" "+e.getMessage());
					}

				}
			}.start();

	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.retrieve_layout);
		//Internet permission
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
			.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		getDoc = (Button)findViewById(R.id.getDoc);
		doc_name = (EditText) findViewById(R.id.kind);
		retrieve_pass = (EditText) findViewById(R.id.retrieve_pass);
		//RETRIEVE BUTTON
		getDoc.setOnClickListener(new Button.OnClickListener()
		{
			@Override
			public void onClick(View arg0) 
			{	
				if(true)
				{
					retrieveFile();					
				}
			}			
		});
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
		startActivityForResult(myIntent, 0);
		return true;
	}


	//get the list of file names from shared preferences and put it into an ArrayList
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



	/*
		try {
			if (bitmap != null && !bitmap.isRecycled()) {
				bitmap.recycle();
				bitmap = null;
				System.gc();
			}
	this.bitmap = bitmap;			
	*/

	//Loads the server from the Global setting file
	public ArrayList<Server> LoadServer(){
		ArrayList<Server> OnServers=  new ArrayList<Server>(); // Our result
		DatabaseHandler db = new DatabaseHandler(getBaseContext()); // Connect to DB
		List<Server> serverList = db.getAllServers();// Gets the server list

        System.out.println(db.getAllServers());

			 //Dynamically add a new server to the list
			for (Server server : serverList) {
				
				if(server.getStatus()==1){ // If it is ON
					OnServers.add(server);
                }
				else{
					//If it is OFF
				}
				
			}
		return OnServers;
	}





	
	
}



