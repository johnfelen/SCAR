package com.scar.android.Activities;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.scar.android.MetaData;
import com.android.scar.R;
import com.scar.android.Session;

// Activity for handling login
// Activity Flow:
//  Main -> Login

/**
 * Activity for logging into scar
 */
public class LoginActivity extends Activity
{
    int tries = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        final ActionBar actionBar = getActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#2B468B")));
        actionBar.setTitle("SCAR");

        // Init the MetaData db
        MetaData.init(this);
        //open data base here!
        Session.makeLock(getApplicationContext());
        tries = Session.getTries();

        Button
                login = (Button)findViewById(R.id.login),
                create = (Button)findViewById(R.id.new_password);

        login.setOnClickListener(new Button.OnClickListener() {
            @Override

            public void onClick(View v) {
                if (Session.isLocked()) {
                    tries = 0;
                    long remaining = Session.remaining();
                    if (remaining <= 0) {
                        Session.unlock();
                    } else {
                        long minutes = remaining / 60000;
                        int seconds = (int) ((remaining % 60000) / 1000);
                        if (minutes == 1) {
                            Toast.makeText(getApplicationContext(), "Your account is currently locked. You have " + minutes + " minute and " + seconds + " seconds until the account will be unlocked.", Toast.LENGTH_SHORT).show();
                        } else if (minutes == 0) {
                            Toast.makeText(getApplicationContext(), "Your account is currently locked. You have " + seconds + " seconds until the account will be unlocked.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Your account is currently locked. You have " + minutes + " minutes and " + seconds + " seconds until the account will be unlocked.", Toast.LENGTH_SHORT).show();
                        }

                    }
                }
                if (!Session.isLocked())
                {
                    MetaData meta = MetaData.load(LoginActivity.this, getPassword());
                    if (meta != null) {
                        //If successful open session
                        Session.init(meta, getPassword().getBytes(), LoginActivity.this);
                        //Return from this activity
                        LoginActivity.this.finish();
                    } else {
                        Session.setTries(++tries);
                        int remaining = 5 - tries;
                        if (remaining == 1) {
                            Toast.makeText(getApplicationContext(), "The password is invalid, you have " + remaining + " try remaining.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "The password is invalid, you have " + remaining + " tries remaining.", Toast.LENGTH_SHORT).show();
                        }
                        if (tries > 4) {
                            Session.setLocked();
                            tries = 0;
                        }
                    }
                }
                //Try to find metadata for password

            }
        });

        create.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {   //Start create password activity
                //Start CreatePassword activity
                Intent intent = new Intent(LoginActivity.this, CreatePassword.class);
                startActivity(intent);
            }
        });


    }


    /* Returns the current password the user has entered
     */
    private String getPassword() {
        return ((EditText)findViewById(R.id.Enter_password)).getText().toString();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        if (item.getItemId() ==  R.id.action_deletedbs)
        {
            MetaData.DeleteAllDB(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
