package com.scar.android.Activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.scar.R;
import com.scar.android.ScarFile;
import com.scar.android.Server;
import com.scar.android.Session;

import java.io.File;

/**
 * Created by John on 7/27/2015.
 */
public class MetaFile extends Activity {
    ScarFile selected = null;

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
        selected = Session.meta.getFile(nameOfFile);
        //TODO: auto-remove local paths that no longer exist

        //set the name of the file
        TextView fileName = (TextView) findViewById(R.id.filename);
        fileName.setText(nameOfFile);

        //set the file paths listView
        ListView lst = (ListView) findViewById(R.id.file_path);
        refreshFileList();
        lst.setClickable(true);
        lst.setLongClickable(true);
        //TODO: Fix Delete and Open
        /*
        lst.setOnItemClickListener(new ListView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> _parent, View view, int _position, long id) {
                final AdapterView<?> parent = _parent;
                final int position = _position;
                AlertDialog.Builder newDialog = new AlertDialog.Builder(MetaFile.this);
                newDialog.setTitle("What action would you like to perform?");
                newDialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String file = (String) parent.getItemAtPosition(position);
                        File f = new File(Uri.parse(file).toString());
                        f.delete();
                        Session.meta.removeLocalFile(selected.id, file);
                        selected = Session.meta.getFile(selected.getFilename());
                        MetaFile.this.refreshFileList();
                    }
                });
                newDialog.setNegativeButton("Close",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                            }
                        });

                newDialog.setNeutralButton("Open", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent open = new Intent(Intent.ACTION_VIEW);
                        open.setData(Uri.parse((String) parent.getItemAtPosition(position)));
                        startActivity(open);
                    }
                });

                newDialog.show();
            }

            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    */
        //sets the serverlist listview in metafile
        lst = (ListView) findViewById( R.id.server_list );

        lst.setAdapter(new ArrayAdapter<Server>(this, R.layout.server_item, Session.meta.getServers(nameOfFile)) {
            public View getView(int position, View view, ViewGroup parent) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View ret = inflater.inflate(R.layout.server_item, parent, false);

                TextView name = (TextView) ret.findViewById(R.id.si_name);
                ImageView stat = (ImageView) ret.findViewById(R.id.si_status);

                Server srv = getItem(position);
                name.setText(srv.label.toCharArray(), 0, srv.label.length());
                switch (srv.getStatus(MetaFile.this)) {
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
        lst.setClickable(false);

    }

    public void refreshFileList() {
        ListView lst = (ListView) findViewById(R.id.file_path);
        lst.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, selected.getLocalpaths()));
    }
}
