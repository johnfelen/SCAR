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

//this activity is displayed when the user chooses the Store button on the MainActivity.java page
//this activity lets the user select a file and stores it on the server
//this activity uses the store_layout.xml file 

public class Store extends Activity {

	public static final String CLASSTAG = Store.class.getSimpleName();
	private Button get_doc_btn, store_btn;
	private ProgressDialog progressDialog;
	EditText f_name;

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
            Log.v("SCAR", " " + Store.CLASSTAG + " worker thread done, file stored");
            }
        }
    };
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//Internet permission TODO: is this needed?
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
			.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}

		setContentView(R.layout.store_layout);
		get_doc_btn = (Button)findViewById(R.id.getImage);
		store_btn = (Button)findViewById(R.id.store_button);
		f_name = (EditText) findViewById(R.id.Enter_fname);

		//select document BUTTON
		get_doc_btn.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(
						Intent.ACTION_PICK,
						//TODO: External_content_uri is likely not the right thing we want
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(intent, 0);

			}
		});


		//STORE BUTTON
		store_btn.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View arg0) {
				if(getFilename().equals("")) {
					AlertDialog.Builder newDialog = new AlertDialog.Builder(Store.this);
					newDialog.setTitle("Alert!");
					newDialog.setMessage("You forgot to give your file a name.");
					newDialog.setPositiveButton("OK", new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface dialog, int which){
							dialog.dismiss();
						}
					});
				} else {
					storeFile();
				}
			}});

	}

	public void storeFile()
	{
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
                    //1. Get file that user wants to store
					//2. Get filename file will go under
					//3. Get all current servers known in SCAR meta db
					//4. Feed filename, password, servers and n = 100, k = 50 to a new scar.StoreFile instance
					//5. run StoreFile with store function
        			handler.sendEmptyMessage(100); //completed successfully
        		} catch (Exception e) {
        			Log.v("SCAR", " " + Store.CLASSTAG + e.getMessage());
					handler.sendEmptyMessage(-1);
        		}

            }
        }.start();

	}

	@Override
	//TODO: what does this do?
	public boolean onOptionsItemSelected(MenuItem item){

		Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
		startActivityForResult(myIntent, 0);
		return true;

	}

	private String getFilename() {
		return f_name.getText().toString();
	}
}
