package com.scar.android.Fragments;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.android.scar.R;
import com.scar.android.MetaData;
import com.scar.android.Session;

import org.apache.commons.io.IOUtils;

import java.io.InputStream;

import scar.IServer;
import scar.StoreFile;

//TODO: This needs to be redone

//this activity is displayed when the user chooses the Store button on the MainActivity.java page
//this activity lets the user select a file and stores it on the server
//this activity uses the store_layout.xml file 

public class Store extends Fragment {
	private Button get_doc_btn, store_btn;
	private ProgressDialog progressDialog;
	EditText f_name, s_name;
	Uri imageSelected;

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
            }
        }
    };

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.store_layout, container, false);
	}

	public void onStart() {
		super.onStart();
		//Internet permission TODO: is this needed?
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
			.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}

		get_doc_btn = (Button)getActivity().findViewById(R.id.getImage);
		store_btn = (Button)getActivity().findViewById(R.id.store_button);
		f_name = (EditText) getActivity().findViewById(R.id.Enter_fname);
		s_name = (EditText)	getActivity().findViewById(R.id.Server_fname);

		//select document BUTTON
		get_doc_btn.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(
						Intent.ACTION_PICK,
						//TODO: External_content_uri is likely not the right thing we want
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(intent, 0);
				imageSelected = intent.getData();	//gets the image selected and saved as a Uri
			}
		});


		//STORE BUTTON
		store_btn.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View arg0) {
				if (getFilename().equals("")) {
					AlertDialog.Builder newDialog = new AlertDialog.Builder(Store.this.getActivity());
					newDialog.setTitle("Alert!");
					newDialog.setMessage("You forgot to give your file a name.");
					newDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
				} else {
					storeFile();
				}
			}
		});

	}

	public void storeFile() {
		progressDialog = new ProgressDialog(this.getActivity());
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
                    //Get file that user wants to store
					InputStream input = getActivity().getContentResolver().openInputStream( imageSelected );	//creates an input stream from the image that the user selected
					byte[] fileBytes = IOUtils.toByteArray( input );
					input.close();

					String serverFname = getServerFilename();	//Get filename file will go under

					IServer[] currentServers = Session.meta.getAllServers();	//Get all current servers known in SCAR meta db

					StoreFile store = new scar.StoreFile( fileBytes, serverFname, new String( Session.password ), 50, 100, currentServers );	//Feed filename, password, servers and n = 100, k = 50 to a new scar.StoreFile instance

					store.store();	//	run StoreFile with store function

        			handler.sendEmptyMessage(100); //completed successfully
        		} catch (Exception e) {
					handler.sendEmptyMessage(-1);
        		}

            }
        }.start();

	}

	private String getFilename() {
		return f_name.getText().toString();
	}

	private String getServerFilename()
	{
		return s_name.getText().toString();
	}

}
