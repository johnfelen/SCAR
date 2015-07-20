package com.scar.android.Activities;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.android.scar.R;

//TODO: This needs to be redone completely, see Github wiki as to new idea for server layout[s]

//this activity is used when the new server button is clicked in the MainActivity.java file
//this page allows a user to add a new server
//this file goes with the new_server_layout.xml file

public class New_Server  extends Activity 
{
	public int selected;
	private Button new_server, load_server;
	private EditText password, port_number ;
	private EditText username, hostname;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.newserver_layout);

		new_server = (Button)findViewById(R.id.submit_new_server);
		load_server = (Button)findViewById(R.id.submit_load_server); 
		

		//ADD NEW SERVER BUTTON shows a dialog box 
		new_server.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View arg0) 				
			{
				// TODO Auto-generated method stub

				AlertDialog.Builder newDialog = new AlertDialog.Builder(New_Server.this);
				newDialog.setTitle("NEW SERVER");
				ViewGroup add_server = (ViewGroup) getLayoutInflater().inflate(R.layout.add_server_dialog_box, null);
				newDialog.setView(add_server);

				//CODE FOR THE SPINNER
				final Spinner server_spinner = (Spinner) add_server.findViewById(R.id.servers_array);
				// Create an ArrayAdapter using the string array and a default spinner layout
				ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(New_Server.this, R.array.servers_array, android.R.layout.simple_spinner_item);
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
							Toast.makeText(New_Server.this,"No Host Name", Toast.LENGTH_LONG).show();
							dialog.dismiss();
						}
						else
						{
							Toast.makeText(New_Server.this,"Added new server! " + password.getText() + " " + username.getText() 
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

						//Dynamically add a new server to the list
						LinearLayout serversList = (LinearLayout) findViewById(R.id.layout_server);
						
						Switch dynamic_switch = new Switch(New_Server.this);
						
					    dynamic_switch.setText(server_spinner.getSelectedItem().toString() + ", "+ hostname.getText() 
									+ "\nUser: " + username.getText() + " Port: " + port_number.getText());
						
						//ADDING A NEW SWITCH FOR SERVER & DIVIDER LINE
						LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				        params.setMargins(0, 20, 0, 20);
						dynamic_switch.setLayoutParams(params);
						serversList.addView(dynamic_switch);
						View line = new View(New_Server.this);
						LinearLayout.LayoutParams line_params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, 2);
						line.setLayoutParams(line_params);
						line.setBackgroundColor(Color.LTGRAY);	
						serversList.addView(line);  
					}
				});

				newDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which){

						Toast.makeText(New_Server.this,"Cancelled.", Toast.LENGTH_LONG).show();
						dialog.dismiss();
					}
				});

				newDialog.show();
		}});
		
	}
		

	
    //back button clicked goes back home to MainActivity.java
    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivityForResult(myIntent, 0);
        return true;
    }
}

