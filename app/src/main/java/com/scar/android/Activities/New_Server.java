package com.scar.android.Activities;

import android.app.AlertDialog;
import android.support.v4.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.scar.R;
import com.scar.android.MetaData;
import com.scar.android.Server;
import com.scar.android.Session;

//TODO: This needs to be redone completely, see Github wiki as to new idea for server layout[s]

//this activity is used when the new server button is clicked in the MainActivity.java file
//this page allows a user to add a new server
//this file goes with the new_server_layout.xml file

public class New_Server  extends Fragment
{
	private Button new_server;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.newserver_layout, container, false);
	}

	public void onStart() {
		super.onStart();

		new_server = (Button)getActivity().findViewById(R.id.ns_add_server);
		

		//ADD NEW SERVER BUTTON shows a dialog box
		new_server.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View arg0)
			{
				final AlertDialog.Builder newDialog = new AlertDialog.Builder(New_Server.this.getActivity());
				newDialog.setTitle("NEW SERVER");
				final ViewGroup add_server = (ViewGroup) getActivity().getLayoutInflater().inflate(R.layout.add_server_dialog_box, null);
				newDialog.setView(add_server);

				//CODE FOR THE SPINNER
				final Spinner server_spinner = (Spinner) add_server.findViewById(R.id.servers_array);
				// Create an ArrayAdapter using the string array and a default spinner layout
				ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(New_Server.this.getActivity(), R.array.servers_array, android.R.layout.simple_spinner_item);
				// Specify the layout to use when the list of choices appears
				adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				// Apply the adapter to the spinner
				server_spinner.setAdapter(adapter);

				server_spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
					public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
						switch ((int) id) {
							case MetaData.TYPE_MYSQL_STORE:
							case MetaData.TYPE_CASS_STORE:
								add_server.findViewById(R.id.as_host).setVisibility(View.VISIBLE);
								add_server.findViewById(R.id.as_port).setVisibility(View.VISIBLE);
								add_server.findViewById(R.id.as_uname).setVisibility(View.VISIBLE);
								add_server.findViewById(R.id.as_pass).setVisibility(View.VISIBLE);
								((TextView) add_server.findViewById(R.id.server_hostname)).setText(R.string.server_hostname);
								break;
							case MetaData.TYPE_SQLITE_STORE:
								add_server.findViewById(R.id.as_host).setVisibility(View.VISIBLE);
								add_server.findViewById(R.id.as_port).setVisibility(View.INVISIBLE);
								add_server.findViewById(R.id.as_uname).setVisibility(View.INVISIBLE);
								add_server.findViewById(R.id.as_pass).setVisibility(View.INVISIBLE);
								((TextView) add_server.findViewById(R.id.server_hostname)).setText(R.string.as_dbname);
								break;
							case MetaData.TYPE_DROPBOX_STORE:
							case MetaData.TYPE_GDRIVE_STORE:
								//TODO: Implement these later
								add_server.findViewById(R.id.as_host).setVisibility(View.INVISIBLE);
								add_server.findViewById(R.id.as_port).setVisibility(View.INVISIBLE);
								add_server.findViewById(R.id.as_uname).setVisibility(View.VISIBLE);
								add_server.findViewById(R.id.as_pass).setVisibility(View.VISIBLE);
								break;
						}
					}

					public void onNothingSelected(AdapterView<?> parent) {
					}
				});
				


				newDialog.setPositiveButton("ADD SERVER", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						final String label = ((EditText) add_server.findViewById(R.id.as_label)).getText().toString();
						final String password = ((EditText) add_server.findViewById(R.id.server_pass)).getText().toString();
						final String username = ((EditText) add_server.findViewById(R.id.username)).getText().toString();
						final String hostname = ((EditText) add_server.findViewById(R.id.host_name)).getText().toString();
						final String port_number = ((EditText) add_server.findViewById(R.id.port_number)).getText().toString();

						switch ((int) server_spinner.getSelectedItemId()) {
							case MetaData.TYPE_MYSQL_STORE:
							case MetaData.TYPE_CASS_STORE:
								if (hostname.length() > 0 && port_number.length() > 0 && username.length() > 0) {
									Session.meta.newServer((int) server_spinner.getSelectedItemId(),
											label,
											hostname,
											port_number,
											username,
											password);
								} else {
									Toast.makeText(New_Server.this.getActivity(), "You need a valid hostname, port, and username", Toast.LENGTH_LONG).show();
								}
								break;
							case MetaData.TYPE_SQLITE_STORE:
								if (hostname.length() > 0) {
									Session.meta.newServer(MetaData.TYPE_SQLITE_STORE,
											label,
											hostname,
											"", "", "");
								} else {
									Toast.makeText(New_Server.this.getActivity(), "You need a valid database name", Toast.LENGTH_LONG).show();
								}
								break;
							case MetaData.TYPE_DROPBOX_STORE:
							case MetaData.TYPE_GDRIVE_STORE:
								//TODO: Implement these later
								//This should prompt an OAUTH session to get the access token
								// and store that
								break;
						}

						//Refresh the server list
						New_Server.this.refreshList();

					}
				});

				newDialog.setNegativeButton("CANCEL",
						new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

				newDialog.show();
		}});

		refreshList();
	}


	public void refreshList() {
		//Setup the server list widget
		ListView lst = (ListView) getActivity().findViewById(R.id.ns_server_list);
		ArrayAdapter adp = new ArrayAdapter<Server>(getActivity(), android.R.layout.simple_list_item_1, Session.meta.getAllServerInfo());
		lst.setAdapter(adp);
	}
}

