package com.scar.android.Activities;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.scar.R;
import com.scar.android.MetaData;

// Activity for handling password creation
// Activity Flow:
//  Main -> Login -> CreatePassword
//
public class CreatePassword extends Activity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_password_layout);

        final ActionBar actionBar = getActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#2B468B")));
        actionBar.setTitle("CREATE PASSWORD");

        Button create = (Button)findViewById(R.id.cp_create),
               generate =(Button) findViewById(R.id.cp_generate),
               cancel = (Button)findViewById(R.id.cp_cancel);

        cancel.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Leave the CreatePassword activity -> returns to Login activity
                CreatePassword.this.finish();
            }
        });

        generate.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Generate a random 32-char password
            }
        });

        create.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Checks given password against MetaData
                if (getPassword().length() >= 5) //TODO: change back to 32 after demo
                    if (MetaData.load( CreatePassword.this, getPassword()) == null) {
                        //New Password
                        MetaData.create( CreatePassword.this, getPassword());
                        //Leave CreatePassword activity
                        CreatePassword.this.finish();
                    } else {
                        //Password given already has a file for it
                        Toast.makeText(getApplicationContext(), "The password already exists", Toast.LENGTH_SHORT).show();
                    }
                else
                    Toast.makeText(getApplicationContext(), "The password must be at least 32 characters long", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private String getPassword() {
        return ((EditText)findViewById(R.id.cp_pass)).getText().toString();
    }
}
