package com.scar.android.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.scar.R;
import com.scar.android.ScarFile;
import com.scar.android.Session;

import java.util.ArrayList;

/**
 * Created by John on 7/26/2015.
 */
public class Files extends Fragment {

    ListView filesStored;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.files_tab_layout, container, false);
    }

    public void onStart()
    {
        super.onStart();

        ScarFile[] metaDataFiles = Session.meta.listFiles();
        ArrayList<String> listFiles = getFileNames( metaDataFiles );    //remove extraneous meta data
        filesStored = (ListView) getActivity().findViewById(R.id.files_list);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>( getActivity(), android.R.layout.simple_list_item_1, listFiles );  //creates an array adapter to populate the listview
        filesStored.setAdapter( arrayAdapter );

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

}
