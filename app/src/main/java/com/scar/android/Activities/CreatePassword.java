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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        actionBar.setTitle("SCAR");

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
                String checker = checkPassword(getPassword());
                if(checker.equals(""))
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
                    Toast.makeText(getApplicationContext(), checker, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private String getPassword() {
        return ((EditText)findViewById(R.id.cp_pass)).getText().toString();
    }

    private String checkPassword(String password)
    {
        String returnString = "";
        String pattern = "!|@|$|\\%|\\^|\\&|\\*|";
        String lowerCase = "[a-z]";
        String upperCase = "[A-Z]";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(password);
        if(password.length() < 12)
        {
            returnString.concat("Your password must be at least 12 characters. ");
        }
        if(!m.find())
        {
            returnString.concat("Your password must contain at least one of the following symbols: +" +
                    "!, @, $, %, ^, &, or *. ");
        }

        r = Pattern.compile((lowerCase));
        m = r.matcher(password);
        if(!m.find())
        {
            returnString.concat("Your password must contain a lower case letter. ");
        }

        r = Pattern.compile(upperCase);
        m = r.matcher(password);
        if(!m.find())
        {
            returnString.concat("Your password must contain an upper case letter. ");
        }

        return returnString;

    }
}
