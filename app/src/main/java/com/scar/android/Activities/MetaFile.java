package com.scar.android.Activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import android.widget.Toast;

import com.android.scar.R;
import com.scar.android.FileSaveUtil;
import com.scar.android.MetaData;
import com.scar.android.ScarFile;
import com.scar.android.Server;
import com.scar.android.Session;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import scar.ChunkMeta;
import scar.DeleteFile;
import scar.IServer;

/**
 * Created by John on 7/27/2015.
 * Activity for displaying a files contents 
 * can also delete the file via this activity when in the app
 */
public class MetaFile extends Activity {
    ScarFile selected = null;
    private ProgressDialog progressDialog;

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
                newDialog.setTitle("Would you like to delete this file?");

                //delete local filepath
                newDialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        delete();
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

                newDialog.show();
            }

            public void delete()
            {
                progressDialog = new ProgressDialog(MetaFile.this);
                progressDialog.setTitle("Retrieve File");
                progressDialog.setMessage("Working");
                progressDialog.setIndeterminate(false);
                progressDialog.setCancelable(false);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.setMax(100);
                progressDialog.setProgress(0);
                progressDialog.show();

                new Thread() {
                    public void update(final int per) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                updateProgress(per);
                            }
                        });
                    }

                    public void run() {
                        String file = getIntent().getStringExtra("nameOfFile");
                        byte[] key = Session.meta.getFileKey(file);
                        Server[] servers = Session.meta.getAllActiveServers();
                        IServer actual[] = toActualServers(servers);
                        ChunkMeta[] chunks = Session.meta.getChunks(file);
                        update(20);

                        DeleteFile deleter = new DeleteFile(file, key, actual, 100);
                        try {
                            deleter.delete(chunks);
                        } catch (Exception e) {
                            e.printStackTrace();
                            update(-1);
                        }

                        update(80);
                        ArrayList<Integer> chunkIds = Session.meta.deleteFile(file);
                        Session.metaBackground.deleteFile(chunkIds);
                        update(100);
                    }
                }.start();
            }

            public void updateProgress(int what) {
                AlertDialog.Builder newDialog = new AlertDialog.Builder(MetaFile.this);
                if(what == -1) {
                    progressDialog.setProgress(0);
                    progressDialog.dismiss();
                    newDialog.setTitle("Failed to delete file");
                    newDialog.setMessage("The file has failed to be delete");
                    newDialog.setNegativeButton("Close",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.dismiss();
                                }
                            });
                    newDialog.show();
                } else if(what<100)
                {
                    progressDialog.setProgress(what);
                }
                else
                {
                    progressDialog.setProgress(100);
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "File successfully deleted!", Toast.LENGTH_SHORT).show();
                    MetaFile.this.finish();
                }
            }
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //sets the serverlist listview in metafile
        lst = (ListView) findViewById( R.id.server_list );
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

    private IServer[] toActualServers(Server srvs[]){
        IServer[] ret = new IServer[srvs.length];
        int i = 0;

        for(Server srv : srvs)
            ret[i++] = srv.getActual(this);

        return ret;
    }
}
