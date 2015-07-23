package com.scar.android.Activities;

import android.app.AlertDialog;
import android.support.v4.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.scar.R;

//TODO: This needs to be redone completely, see Github wiki as to new idea for server layout[s]

//this activity is used when the new server button is clicked in the MainActivity.java file
//this page allows a user to add a new server
//this file goes with the new_server_layout.xml file

public class New_Server  extends Fragment
{
	private Button new_server;
	private EditText password, username, hostname, port_number;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.newserver_layout, container, false);
	}

	public void onStart() {
		super.onStart();

		new_server = (Button)getActivity().findViewById(R.id.submit_new_server);
		

		//ADD NEW SERVER BUTTON shows a dialog box 
		new_server.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View arg0) 				
			{
				// TODO Auto-generated method stub

				AlertDialog.Builder newDialog = new AlertDialog.Builder(New_Server.this.getActivity());
				newDialog.setTitle("NEW SERVER");
				ViewGroup add_server = (ViewGroup) getActivity().getLayoutInflater().inflate(R.layout.add_server_dialog_box, null);
				newDialog.setView(add_server);

				//CODE FOR THE SPINNER
				final Spinner server_spinner = (Spinner) add_server.findViewById(R.id.servers_array);
				// Create an ArrayAdapter using the string array and a default spinner layout
				ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(New_Server.this.getActivity(), R.array.servers_array, android.R.layout.simple_spinner_item);
				// Specify the layout to use when the list of choices appears
				adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				// Apply the adapter to the spinner
				server_spinner.setAdapter(adapter);
				
				password = (EditText) add_server.findViewById(R.id.server_pass);
				username = (EditText) add_server.findViewById(R.id.username);
				hostname = (EditText) add_server.findViewById(R.id.host_name);
				port_number = (EditText) add_server.findViewById(R.id.port_number);

				newDialog.setPositiveButton("ADD SERVER", new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which){

						if(hostname.getText().toString().compareTo("") == 0)
						{
							//no hostname entered 
							Toast.makeText(New_Server.this.getActivity(),"No Host Name", Toast.LENGTH_LONG).show();
							dialog.dismiss();
						}
						else
						{
							Toast.makeText(New_Server.this.getActivity(),"Added new server! " + password.getText() + " " + username.getText()
									+ " " + hostname.getText() + " "+ port_number.getText() + " " + server_spinner.getSelectedItem().toString(), Toast.LENGTH_LONG).show();
							dialog.dismiss();
					
							/*
							 * Adding server to database store
							 */
							
							String host = hostname.getText().toString().trim();
							String server_type = server_spinner.getSelectedItem().toString();
							int port = Integer.parseInt(port_number.getText().toString().trim());
							int status = 1;
							String s_username = username.getText().toString().trim();
							String s_password = password.getText().toString().trim();
						
						
						}

						//TODO: Dynamically add a new server to the list

					}
				});

				newDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which){
						dialog.dismiss();
					}
				});

				newDialog.show();
		}});
		
	}
		

	
    //back button clicked goes back home to MainActivity.java
    //public boolean onOptionsItemSelected(MenuItem item){
    //    Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
    //    startActivityForResult(myIntent, 0);
    //    return true;
    //}
}

