package com.scar.android.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.scar.R;

/**
 * Created by John on 7/26/2015.
 */
public class Files extends Fragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.files_tab_layout, container, false);
    }

}
