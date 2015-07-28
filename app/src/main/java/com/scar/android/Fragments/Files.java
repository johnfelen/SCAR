package com.scar.android.Fragments;

import android.content.Intent;
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
import com.scar.android.Session;

import java.util.ArrayList;

/**
 * Created by John on 7/26/2015.
 */
public class Files extends Fragment {
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.files_tab_layout, container, false);
    }

    public void onStart()
    {
        super.onStart();

        ListView filesStored = (ListView) getActivity().findViewById(R.id.files_list);
        //TODO REMOVE NEXT 4 LINES FOR ACTUAL PRODUCTION CODE and uncomment below
        ArrayList<String> listFiles = new ArrayList<String>();
        listFiles.add("HELLO");
        listFiles.add("TWO");

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

    /*public void onResume() {
        super.onResume();
        refreshList();
    }

    public void refreshList() {
        if (Session.meta != null) {
            ScarFile[] metaDataFiles = Session.meta.listFiles();
            ArrayList<String> listFiles = getFileNames(metaDataFiles);    //remove extraneous meta data
            ListView filesStored = (ListView) getActivity().findViewById(R.id.files_list);

            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, listFiles);  //creates an array adapter to populate the listview
            filesStored.setAdapter(arrayAdapter);
        }
    }

    private ArrayList<String> getFileNames( ScarFile[] metaDataFiles )  //takes the ScarFile[] array and creates an ArrayList of only the name of the files
    {
        ArrayList<String> listFiles = new ArrayList<String>( metaDataFiles.length );

        for( ScarFile fileData:metaDataFiles )
        {
            listFiles.add( fileData.getFilename() );
        }

        return listFiles;
    }*/

}
