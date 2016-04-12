package com.scar.android.Fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Parcel;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.scar.R;
import com.scar.android.FileSaveUtil;
import com.scar.android.ScarFile;
import com.scar.android.Server;
import com.scar.android.Session;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import scar.ChunkMeta;
import scar.ChunkMetaPub;
import scar.GetFile;
import scar.IServer;

/**
 * Created by Spencer on 4/12/2016.
 */
public class Relocate extends Fragment
{

    private Button getDoc;
    private ProgressDialog progressDialog;
    EditText doc_name;
    private byte[] data = null;

    public void updateProgress(int what) {
        AlertDialog.Builder newDialog = new AlertDialog.Builder(Relocate.this.getActivity());
        if(what == -1) {
            progressDialog.setProgress(0);
            progressDialog.dismiss();
            newDialog.setTitle("Failed to relocate all chunk");
            newDialog.setMessage("The chunks have failed to be moved");
            newDialog.setNegativeButton("Close",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                newDialog.show();
                getActivity().onBackPressed();
            } else if(what<100)
            {
                progressDialog.setProgress(what);
            }
            else
            {
                progressDialog.setProgress(100);
                progressDialog.dismiss();
                getActivity().onBackPressed();
            }
        }


        public void relocateChunks(final HashSet<ChunkMetaPub> chunks)
        {
            progressDialog = new ProgressDialog(this.getActivity());
            progressDialog.setTitle("Relocating Chunks");
            progressDialog.setMessage("Working");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setMax(100);
            progressDialog.setProgress(0);
            progressDialog.show();
            new Thread()
            {
                public void update(final int per) {
                    Relocate.this.getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            updateProgress(per);
                        }
                    });
                }

                public void run() {
                    Iterator iterator = chunks.iterator();
                    Server[] servers = Session.meta.getAllActiveServers();

                    if (servers == null || servers.length == 0)
                    {
                        update(-1);
                    }

                    IServer[] actualServers = toActualServers(servers);
                    update(10);
                    while (iterator.hasNext()) {
                        ChunkMetaPub current = (ChunkMetaPub) iterator.next();
                        if (actualServers.length > current.virtual)
                        {

                            IServer used = actualServers[current.physical];

                            ChunkMeta changed = Session.meta.relocate(current);
                            Session.metaBackground.relocate(current);
                            byte[] data = used.getData(changed.name);

                            used.deleteFile(changed.name);

                            IServer destination = actualServers[current.virtual];
                            destination.storeData(changed.name, data);
                        }

                    }
                }
            }.start();

        }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Bundle args = getArguments();
        Parcel myChunks = args.getParcelable("chunks");
        return inflater.inflate(R.layout.retrieve_layout, container, false);
    }

    private IServer[] toActualServers(Server srvs[])
    {
        IServer[] ret = new IServer[srvs.length];
        int i = 0;

        for(Server srv : srvs)
        {
            ret[i++] = srv.getActual(Relocate.this.getActivity());
        }


        return ret;
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
    }




}
