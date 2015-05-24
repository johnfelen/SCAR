package com.example.scar2;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TextView;

//This Activity will talk about the project
public class About extends Activity {

	@Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.about_layout);
        setTitle("About");  //set the title of the activity bar

    }
}
