package com.scar.android.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.scar.R;
import com.scar.android.Activities.AddServer;
import com.scar.android.Activities.ModifyServer;
import com.scar.android.Server;
import com.scar.android.Session;

import java.util.ArrayList;


//TODO: This needs to be redone completely, see Github wiki as to new idea for server layout[s]

//this activity is used when the new server button is clicked in the MainActivity.java file
//this page allows a user to add a new server
//this file goes with the new_server_layout.xml file

public class ServerList extends Fragment
{

	public static ServerList newInstance(int num)
	{
		ServerList fragment = new ServerList();

		// Supply num input as an argument.
		Bundle args = new Bundle();
		args.putInt("num", num);
		fragment.setArguments(args);

		return fragment;
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.serverlist_layout, container, false);
	}

	public void onStart() {
		super.onStart();

		Button new_server = (Button)getActivity().findViewById(R.id.ns_add_server);
		ListView lst = (ListView) getActivity().findViewById(R.id.ns_server_list);

		//ADD NEW SERVER BUTTON shows a dialog box
		new_server.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View arg0) {
				Intent intent = new Intent(getActivity(), AddServer.class);
				startActivityForResult(intent, 0);
			}
		});

		lst.setOnItemClickListener(new ListView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View arg0, int pos, long id) {
				Intent intent = new Intent(getActivity(), ModifyServer.class);
				intent.putExtras(((Server) parent.getItemAtPosition(pos)).bundle());
				startActivity(intent);
			}
		});

		lst.setAdapter(new ArrayAdapter<Server>(getActivity(), R.layout.server_item, new ArrayList<Server>()){
			public View getView(int position, View view, ViewGroup parent) {
				LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				View ret = inflater.inflate(R.layout.server_item, parent, false);

				TextView name = (TextView) ret.findViewById(R.id.si_name);
				ImageView stat = (ImageView) ret.findViewById(R.id.si_status);

				Server srv = getItem(position);
				name.setText(srv.label.toCharArray(), 0, srv.label.length());
				switch (srv.getStatus(getActivity())) {
					case Server.ONLINE:
						stat.setImageResource(android.R.drawable.button_onoff_indicator_on);
						break;
					case Server.OFFLINE:
						stat.setImageResource(android.R.drawable.button_onoff_indicator_off);
						break;
					case Server.DISABLED:
						stat.setImageResource(android.R.drawable.ic_delete);
						break;
				}

				return ret;
			}
		});
	}


	public void onResume() {
		super.onResume();
		//Update list in background
		new RefreshListTask().execute(this);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent args) {
		//Get our data back from AddServer Activity when it's done
		if(resultCode == AddServer.SUCCESS && args != null) {
			Session.meta.newServer(args.getIntExtra("type", 0),
								   args.getStringExtra("lbl"),
									args.getStringExtra("host"),
									args.getStringExtra("port"),
									args.getByteArrayExtra("uname"),
									args.getByteArrayExtra("pass"));
		}
	}

	private class RefreshListTask extends AsyncTask<Fragment, Object, Object> {
		//TODO: Show a "loading..." text while loading in the list
		//TODO: Be smart about the updates and only add/remove servers as needed
		protected Object doInBackground(Fragment... params) {
			if(Session.meta != null) {
				//Setup the server list widget
				ListView lst = (ListView) params[0].getActivity().findViewById(R.id.ns_server_list);
				final ArrayAdapter<Server> adp = (ArrayAdapter<Server>)lst.getAdapter();
				final Server[] srvs = Session.meta.getAllServerInfo();

				params[0].getActivity().runOnUiThread(new Runnable() {
					public void run() {
						adp.clear();
						adp.addAll(srvs);
					}
				});
			}
			return null;
		}

		protected void onProgressUpdate(Object... values) {}
		protected void onPostExecute(Object res) { }
	}
}

