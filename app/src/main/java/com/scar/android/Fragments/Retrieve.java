package com.scar.android.Fragments;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.android.scar.R;
import com.scar.android.ScarFile;
import com.scar.android.Server;
import com.scar.android.Session;

import java.util.ArrayList;

import scar.GetFile;
import scar.IServer;

//TODO: This needs to be redone

//this activity is displayed when the retrieve button is clicked on the MainActivity page.
//this activity retrieves the document from the server
//this activity uses retrieve_layout.xml

public class Retrieve extends Fragment {
	private Button getDoc;
	private ProgressDialog progressDialog;
	EditText doc_name;
	private byte[] data = null;


    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(final Message msg) {
            AlertDialog.Builder newDialog = new AlertDialog.Builder(Retrieve.this.getActivity());
			if(msg.what == -1) {
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

				//Save bytes[] to a file that the user can access elsewhere
				String local = MediaStore.Images.Media.insertImage( getActivity().getContentResolver(), getBitmap( data ), getFilename() , "Recovered from SCAR");
				//Add to meta data
				ScarFile f = Session.meta.getFile(getFilename());
				Session.meta.addLocalFile(f.id, local);
			}
        }
    };


	public void retrieveFile()
	{
		
			progressDialog = new ProgressDialog(this.getActivity());
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
						//Gets the servers for the given filename
						Server[] servers = Session.meta.getServers(getFilename());

						// if no servers are found for the filename assume you use all servers instead (ie: file was not stored via this app; thus, not in our db)
						if( servers.length == 0 )
						{
							servers = Session.meta.getAllActiveServers();
						}

						servers = testServers(servers);
						IServer[] actualServers = toActualServers(servers);

						//Feed filename, password, servers and n = 100, k = 50 to a new scar.GetFile instance
						//TODO: allow user to enter hash string of encryption key in case it's not stored on this device.
						byte[] key = Session.meta.getFileKey(getFilename());
						GetFile get =  new scar.GetFile( getFilename(),
								                         key,
								                         50,
								                         100,
								                         actualServers );

						//Get the bytes[] back from get() via scar.GetFile instance
						data = get.get();

						if(Session.meta.getFile(getFilename()) == null) {
							//Add file to the meta if it wasn't already
							Session.meta.newFile(getFilename(), key);
							ScarFile f = Session.meta.getFile(getFilename());
							Session.meta.setServers(f.id, servers);
						}

						//4. Goto line 89 and fill out the saving bytes[] to a file
						handler.sendEmptyMessage(100); //Completed
					} catch (Exception e) {
						//Failed
						handler.sendEmptyMessage(-1);
					}

				}
			}.start();

	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.retrieve_layout, container, false);
	}

	public void onStart() {
		super.onStart();
		//Internet permission TODO: is this needed?
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
			.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		getDoc = (Button)getActivity().findViewById(R.id.getDoc);
		doc_name = (EditText)getActivity().findViewById(R.id.kind);
		//RETRIEVE BUTTON
		getDoc.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (getFilename().equals("")) {
					AlertDialog.Builder newDialog = new AlertDialog.Builder(Retrieve.this.getActivity());
					newDialog.setTitle("Alert!");
					newDialog.setMessage("You forgot to give the file name.");
					newDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
				} else {
					retrieveFile();
				}
			}
		});
	}

	private IServer[] toActualServers(Server srvs[]){
		IServer[] ret = new IServer[srvs.length];
		int i = 0;

		for(Server srv : srvs)
			ret[i++] = srv.getActual();

		return ret;
	}

	private Server[] testServers(Server srvs[]) {
		ArrayList<Server> ret = new ArrayList<Server>();

		for(Server srv : srvs) {
			if (srv.getActual().getStatus())
				ret.add(srv);
		}

		return ret.toArray(new Server[0]);
	}
	private String getFilename() {
		return doc_name.getText().toString();
	}

	private Bitmap getBitmap( byte[] bytes )	//takes bytes and converts it to a bitmap
	{
		return BitmapFactory.decodeByteArray( bytes, 0, bytes.length );
	}

}



