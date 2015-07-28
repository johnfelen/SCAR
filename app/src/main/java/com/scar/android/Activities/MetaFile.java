package com.scar.android.Activities;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import com.android.scar.R;
import com.scar.android.ScarFile;
import com.scar.android.Session;

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

        //get the name of the file that was sent to start this activity and then get the ScarFile that is associated with it.
        String nameOfFile = getIntent().getExtras().getParcelable( "nameOfFile" );
        ScarFile selected = Session.meta.getFile(nameOfFile);

    }
}
