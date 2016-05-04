package com.scar.android.Fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.scar.R;
import com.scar.android.Activities.MetaFile;
import com.scar.android.ScarFile;
import com.scar.android.Server;
import com.scar.android.Session;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * This fragment simply shows a list of known files stored by our app
 * Created by John on 7/26/2015.
 */
public class Files extends Fragment {

    public static Files newInstance(int num)
    {
        Files fragment = new Files();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("num", num);
        fragment.setArguments(args);

        return fragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.files_tab_layout, container, false);
    }

    public void onStart()
    {
        super.onStart();

        ListView filesStored = (ListView) getActivity().findViewById(R.id.files_list);
        ArrayList<String> listFiles = new ArrayList<String>();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, listFiles);  //creates an array adapter to populate the listview
        filesStored.setAdapter(arrayAdapter);

        filesStored.setOnItemClickListener(new AdapterView.OnItemClickListener() {  //implement the item click for ListView
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String nameOfFile = (String) parent.getItemAtPosition(position);

                //start the MetaFile Activity based on the file that the user selected
                Intent intent = new Intent(getActivity(), MetaFile.class).putExtra("nameOfFile", nameOfFile);
                startActivity(intent);
            }
        });
    }

    public void onResume() {
        super.onResume();
        new RefreshListTask().execute(this);
    }


    private ArrayList<String> getFileNames( ScarFile[] metaDataFiles )  //takes the ScarFile[] array and creates an ArrayList of only the name of the files
    {
        ArrayList<String> listFiles = new ArrayList<String>( metaDataFiles.length );

        for( ScarFile fileData:metaDataFiles )
        {
            listFiles.add( fileData.getFilename() );
        }

        return listFiles;
    }


    private class RefreshListTask extends AsyncTask<Fragment, Object, Object> {
        //TODO: Show a "loading..." text while loading in the list
        //TODO: Be smart about the updates and only add/remove servers as needed
        protected Object doInBackground(Fragment... params) {
            if(Session.meta != null) {
                //Setup the server list widget
                ScarFile[] metaDataFiles = Session.meta.listFiles();
                final ArrayList<String> listFiles = getFileNames(metaDataFiles);    //remove extraneous meta data
                ListView filesStored = (ListView) getActivity().findViewById(R.id.files_list);
                final ArrayAdapter<String> adp = (ArrayAdapter<String>)filesStored.getAdapter();

                params[0].getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        adp.clear();
                        adp.addAll(listFiles);
                    }
                });
            }
            return null;
        }

        protected void onProgressUpdate(Object... values) {}
        protected void onPostExecute(Object res) { }
    }
}
