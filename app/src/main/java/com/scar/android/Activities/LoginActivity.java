package com.scar.android.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.scar.android.MetaData;
import com.android.scar.R;
import com.scar.android.Session;

/**
 * Created by John on 7/7/2015.
 */
public class LoginActivity extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        // Init the MetaData db
        MetaData.init(this);

        Button
                login = (Button)findViewById(R.id.login),
                create = (Button)findViewById(R.id.new_password);

        login.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Try to find metadata for password
                MetaData meta = MetaData.load(getPassword());
                if(meta != null) {
                    //If successful open session
                    Session.init(meta, getPassword());
                    //put user into main activity
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        });
        create.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Start create password activity
            }
        });

		/*Login part*/

    }


    /* Returns the current password the user has entered
     */
    private String getPassword() {
        return ((EditText)findViewById(R.id.Enter_password)).getText().toString();
    }
}
