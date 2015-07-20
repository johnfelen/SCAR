package com.scar.android.Activities;

import android.app.Activity;
import android.os.Bundle;

import com.scar.android.MetaData;
import com.android.scar.R;

/**
 * Created by John on 7/7/2015.
 */
public class LoginActivity extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        MetaData.init(this);

		/*Login part*/
        setContentView(R.layout.login);

        //TODO check password and then start main activity
    }

}
