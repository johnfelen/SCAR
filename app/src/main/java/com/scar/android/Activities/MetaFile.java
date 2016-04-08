package com.scar.android.Activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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
import java.io.IOException;

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
        lst.setOnItemClickListener(new ListView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> _parent, View view, int _position, long id) {
                final AdapterView<?> parent = _parent;
                final int position = _position;
                AlertDialog.Builder newDialog = new AlertDialog.Builder(MetaFile.this);
                newDialog.setTitle("What action would you like to perform?");

                //delete local filepath
                newDialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //change the way to get the file to be similar to open's way of getting it
                        String file = (String) parent.getItemAtPosition(position);
                        Log.d("ass", "onClick: " + getIntent().getStringExtra("nameOfFile"));

                        //delete file here



                        /*
                        //File f = new File(Uri.parse(file).toString());
                        File f = new File( (String) parent.getItemAtPosition(position) );    //gets the file at the filepath
                        f.delete();
                        Session.meta.removeLocalFile(selected.id, f.toString());
                        selected = Session.meta.getFile(selected.getFilename());
                        MetaFile.this.refreshFileList();

                        //1st option is only this one line of code, is very smilar to the open line of code
                        getContentResolver().delete(Uri.fromFile(f), null, null);    //scans the cache

                        //2nd option will go to a method below
                        //deleteFileFromMediaStore(getContentResolver(), f); //should delete from media store, I have the Uri set to internal, but it may be external depending on where its saved

                        /*3rd option, the first argument this, is not working because it is not context, may work but last resort because it must be fixed
                        MediaScannerConnection.scanFile(this, new String[] { Environment.getExternalStorageDirectory().toString() }, null, new MediaScannerConnection.OnScanCompletedListener() {
                            public void onScanCompleted(String path, Uri uri)
                            {
                                Log.i("ExternalStorage", "Scanned " + path + ":");
                                Log.i("ExternalStorage", "-> uri=" + uri);
                            }
                        });*/

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

                //opens the file
                newDialog.setNeutralButton("Open", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        File file = new File( (String) parent.getItemAtPosition(position) );    //gets the file at the filepath
                        intent.setDataAndType( Uri.fromFile( file ), "image/*" );
                        startActivity( intent );    //starts gallary activity with local path Uri
                    }
                });

                newDialog.show();
            }

            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //sets the serverlist listview in metafile
        lst = (ListView) findViewById( R.id.server_list );

        /*
        lst.setAdapter(new ArrayAdapter<Server>(this, R.layout.server_item, null){ //Session.meta.getChunks(nameOfFile)) {
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
    */
    }

    public void refreshFileList() {
        ListView lst = (ListView) findViewById(R.id.file_path);
        lst.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, selected.getLocalpaths()));
    }

    public static void deleteFileFromMediaStore(final ContentResolver contentResolver, final File file) {
        String canonicalPath;
        try {
            canonicalPath = file.getCanonicalPath();
        } catch (IOException e) {
            canonicalPath = file.getAbsolutePath();
        }
        final Uri uri = MediaStore.Files.getContentUri("internal");
        //final Uri uri = MediaStore.Files.getContentUri("external");
        final int result = contentResolver.delete(uri,
                MediaStore.Files.FileColumns.DATA + "=?", new String[] {canonicalPath});
        if (result == 0) {
            final String absolutePath = file.getAbsolutePath();
            if (!absolutePath.equals(canonicalPath)) {
                contentResolver.delete(uri,
                        MediaStore.Files.FileColumns.DATA + "=?", new String[]{absolutePath});
            }
        }
    }
}
