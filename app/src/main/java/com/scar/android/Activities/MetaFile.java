package com.scar.android.Activities;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.scar.R;
import com.scar.android.ScarFile;
import com.scar.android.Server;
import com.scar.android.Session;

import java.util.ArrayList;

/**
 * Created by John on 7/27/2015.
 */
public class MetaFile extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.metafile_layout);

        //change the color of the actionbar
        final ActionBar actionBar = getActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#2B468B")));
        actionBar.setTitle("SCAR");

        //get the name of the file that was sent to start this activity and then get the ScarFile that is associated with it.
        String nameOfFile = getIntent().getStringExtra("nameOfFile");
        ScarFile selected = Session.meta.getFile( nameOfFile );

        //set the name of the file
        TextView fileName = (TextView) findViewById(R.id.filename);
        fileName.setText(nameOfFile);

        System.out.println(nameOfFile);

        setListView(R.id.file_path, selected.getLocalpaths(), false);  //set the file paths listView

        //sets the serverlist listview in metafile
        ListView listView = (ListView) findViewById( R.id.server_list );

        listView.setAdapter(new ArrayAdapter<Server>(this, R.layout.server_item, new ArrayList<Server>()) {
        public View getView(int position, View view, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View ret = inflater.inflate(R.layout.serverlist_layout, parent, false);

            TextView name = (TextView) ret.findViewById(R.id.si_name);
            ImageView stat = (ImageView) ret.findViewById(R.id.si_status);

            Server srv = getItem(position);
            name.setText(srv.label.toCharArray(), 0, srv.label.length());
            switch (srv.getStatus( this )) {
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

    private void setListView( int id, ArrayList<String> itemArray, boolean sub ) //set the listviews in MetaFile
    {
        ListView listView = (ListView) findViewById(id);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, itemArray);  //creates an array adapter to populate the listview with itemArray

        listView.setAdapter(arrayAdapter);
    }

}
