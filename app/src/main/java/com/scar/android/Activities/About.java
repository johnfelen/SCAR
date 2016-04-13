package com.scar.android.Activities;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import com.android.scar.R;

//This Activity will talk about the project
public class About extends Activity {

	@Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_layout);

        //change the color of the actionbar
        final ActionBar actionBar = getActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#2B468B")));
        setTitle("SCAR");  //set the title of the activity bar
        TextView scrollParagraph = (TextView)findViewById(R.id.AboutParagraph);
        scrollParagraph.setMovementMethod(new ScrollingMovementMethod());
    }
}
