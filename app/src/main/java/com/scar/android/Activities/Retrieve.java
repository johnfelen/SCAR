package com.scar.android.Activities;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.scar.R;

//TODO: This needs to be redone

//this activity is displayed when the retrieve button is clicked on the MainActivity page.
//this activity retrieves the document from the server
//this activity uses retrieve_layout.xml

public class Retrieve extends Activity  {
	
	public static final String CLASSTAG = Retrieve.class.getSimpleName();

	private Button getDoc;
	private ProgressDialog progressDialog;
	EditText doc_name;
	private byte[] data = null;


    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(final Message msg) {
            AlertDialog.Builder newDialog = new AlertDialog.Builder(Retrieve.this);
            Log.v("SCAR", " " + Retrieve.CLASSTAG + " handleMessage : " + msg);
			if(msg.what == -1) {
                Log.v("SCAR", " " + Retrieve.CLASSTAG + " Display fail");
                progressDialog.setProgress(0);
                progressDialog.dismiss();
                newDialog.setTitle("Failed to retrieve file");
                newDialog.setMessage("The file has failed to be retrieved");
                newDialog.setNegativeButton("Close",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                            }
                        });
                newDialog.show();
            } else if(msg.what<100)
            {
                progressDialog.setProgress(msg.what);
            }
            else
			{
				progressDialog.setProgress(100);
				progressDialog.dismiss();

				// print message
				newDialog.setTitle("Retrieved!");
				newDialog
						.setMessage("Would you like to save this document?");
				Log.v("SCAR", " " + Store.CLASSTAG
						+ " worker thread done, file stored");

				//TODO: Save bytes[] to a file that the user can access elsewhere
			}
        }
    };

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
						//1. Gets the servers for the given filename
						// if no servers are found for the filename assume you use all servers instead (ie: file was not stored via this app; thus, not in our db)
						//2. Feed filename, password, servers and n = 100, k = 50 to a new scar.GetFile instance
						//3. Get the bytes[] back from get() via scar.GetFile instance
						//    set the data class variable to these bytes
						//4. Goto line 89 and fill out the saving bytes[] to a file
						handler.sendEmptyMessage(100); //Completed
					} catch (Exception e) {
						// TODO Auto-generated catch block
						Log.v("SCAR",Retrieve.CLASSTAG+" "+e.getMessage());
					}

				}
			}.start();

	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.retrieve_layout);
		//Internet permission TODO: is this needed?
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
			.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		getDoc = (Button)findViewById(R.id.getDoc);
		doc_name = (EditText) findViewById(R.id.kind);
		//RETRIEVE BUTTON
		getDoc.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(getFilename().equals("")) {
					AlertDialog.Builder newDialog = new AlertDialog.Builder(Retrieve.this);
					newDialog.setTitle("Alert!");
					newDialog.setMessage("You forgot to give the file name.");
					newDialog.setPositiveButton("OK", new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface dialog, int which){
							dialog.dismiss();
						}
					});
				} else {
					retrieveFile();
				}
			}
		});
	}


	@Override
	//TODO: what does this do?
	public boolean onOptionsItemSelected(MenuItem item){
		Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
		startActivityForResult(myIntent, 0);
		return true;
	}

	private String getFilename() {
		return doc_name.getText().toString();
	}
}



