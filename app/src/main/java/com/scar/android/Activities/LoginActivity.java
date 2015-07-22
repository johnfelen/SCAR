package com.scar.android.Activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.scar.android.MetaData;
import com.android.scar.R;
import com.scar.android.Session;

/**
 * Created by John on 7/7/2015.
 */
public class LoginActivity extends Activity
{
    String newPassword; //a newly created password will be stored here

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        /*Hide the ActionBar*/
        ActionBar actionBar = getActionBar();
        actionBar.hide();
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
            public void onClick(View v) {   //Start create password activity
                    /*Creates the alertdialog with title and message*/
                    AlertDialog.Builder createNewPass = new AlertDialog.Builder(LoginActivity.this);
                    createNewPass.setTitle("Create Password");
                    createNewPass.setMessage("A password must be at least 32 characters long.");

                    /*This input EditText will be where they actually enter a password*/
                    final EditText input = new EditText(LoginActivity.this);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT);
                    input.setLayoutParams(lp);
                    createNewPass.setView(input);

                    /*This onclicklistenere will check if the password is atleast 32 characters and then store it into newPassword*/
                    createNewPass.setPositiveButton("CREATE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int whichButton) {
                            newPassword = input.getText().toString();
                        }
                    });
                    createNewPass.setNegativeButton("CANCEL", null);
                    createNewPass.create().show();
            }
        });

    }


    /* Returns the current password the user has entered
     */
    private String getPassword() {
        return ((EditText)findViewById(R.id.Enter_password)).getText().toString();
    }
}
