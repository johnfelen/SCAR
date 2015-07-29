package com.scar.android.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.drm.DrmStore;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.scar.R;
import com.scar.android.Activities.AddServer;
import com.scar.android.ScarFile;
import com.scar.android.Server;
import com.scar.android.Session;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;

import scar.IServer;
import scar.RndKeyGen;
import scar.StoreFile;

//TODO: This needs to be redone

//this activity is displayed when the user chooses the Store button on the MainActivity.java page
//this activity lets the user select a file and stores it on the server
//this activity uses the store_layout.xml file 

public class Store extends Fragment {
	private static final int
		DOC_SELECTED = 0;

	private Button get_doc_btn, store_btn;
	private ProgressDialog progressDialog;
	EditText s_name;
	TextView f_name;

	public void reset() {
		s_name.setText("");
		f_name.setText("");
	}

	public void updateProgress(int what) {
		if(what == -1) {
			//TODO: proper error msg to user
			progressDialog.dismiss();
			Toast.makeText(getActivity().getApplicationContext(), "An error has occurred during file upload", Toast.LENGTH_LONG).show();
			reset();
		} else if(what<100) {
			progressDialog.setProgress(what);
		} else {
			progressDialog.setProgress(100);
			progressDialog.dismiss();
			Toast.makeText(getActivity().getApplicationContext(), "The file has been uploaded successfully", Toast.LENGTH_LONG).show();
			reset();
		}
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.store_layout, container, false);
	}

	public void onStart() {
		super.onStart();
		//Internet permission
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
			.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}

		get_doc_btn = (Button)getActivity().findViewById(R.id.getImage);
		store_btn = (Button)getActivity().findViewById(R.id.store_button);
		f_name = (TextView) getActivity().findViewById(R.id.Enter_fname);
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
			}
		});


		//STORE BUTTON
		store_btn.setOnClickListener(new Button.OnClickListener() {
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

	public void onActivityResult(int requestCode, int resultCode, Intent args) {
		//Get our selected doc
		if(resultCode == Activity.RESULT_OK) {
			Uri imageSelected = args.getData();    //gets the image selected and saved as a Uri
			f_name.setText(imageSelected.toString());
		}
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
			public void update(final int per) {
				Store.this.getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						updateProgress(per);
					}
				});
			}

            @Override
            public void run() {
				IServer[] actualServers = null;
				try {
                    //Get file that user wants to store
					//creates an input stream from the image that the user selected
					InputStream input = getActivity().getContentResolver().openInputStream( Uri.parse(getFilename()) );
					update(5);
					byte[] fileBytes = IOUtils.toByteArray(input);
					input.close();

					//Get filename file will go under
					update(15);
					String serverFname = getServerFilename();

					//Get all current servers known in SCAR meta db
					update(20);
					Server[] currentServers = Session.meta.getAllActiveServers();
					currentServers = testServers(currentServers);
					actualServers = toActualServers(currentServers);

					//Feed filename, password, servers and n = 100, k = 50 to a new scar.StoreFile instance
					update(30);
					RndKeyGen keygen = new RndKeyGen();
					byte[] key = keygen.genBytes(32);
					update(35);
					StoreFile store = new scar.StoreFile( fileBytes,
							                              serverFname,
							                              key,
							                              50,
							                              100,
							                              actualServers );
					//	run StoreFile with store function
					store.store();


					update(90);
					// Add into meta data
					if(Session.meta.getFile(serverFname) == null)
						Session.meta.newFile(serverFname, key);
					ScarFile f = Session.meta.getFile(serverFname);
					Session.meta.setServers(f.id, currentServers);
					Session.meta.addLocalFile(f.id, getFilename());


					update(100); //completed successfully
        		} catch (Exception e) {
					e.printStackTrace();
					update(-1);
        		}

				if(actualServers != null)
					for(IServer srv : actualServers)
						srv.close();

            }
        }.start();

	}

	static String bytesToString(byte[] bs) {
		String s = "";
		for(byte b : bs)
			s += (b & 0xFF) + " " ;
		return s;
	}

	private IServer[] toActualServers(Server srvs[]){
		IServer[] ret = new IServer[srvs.length];
		int i = 0;

		for(Server srv : srvs)
			ret[i++] = srv.getActual(getActivity());

		return ret;
	}

	private Server[] testServers(Server srvs[]) {
		ArrayList<Server> ret = new ArrayList<Server>();

		for(Server srv : srvs) {
			if (srv.getActual(getActivity()).getStatus())
				ret.add(srv);
		}

		return ret.toArray(new Server[0]);
	}

	private String getFilename() {
		return f_name.getText().toString();
	}

	private String getServerFilename()
	{
		return s_name.getText().toString();
	}

}
