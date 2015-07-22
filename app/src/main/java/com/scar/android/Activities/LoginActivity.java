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
import android.widget.Toast;

import com.scar.android.MetaData;
import com.android.scar.R;
import com.scar.android.Session;

/**
 * Created by John on 7/7/2015.
 */
public class LoginActivity extends Activity
{
    String checkPass = "";  //used to set the edit text if they did not type a password long enough in

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
                login( getPassword() ); //get the password from the editText and login
            }
        });

        create.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {   //Start create password activity
                    /*Creates the alertdialog with text and buttons*/
                    AlertDialog.Builder createNewPass = new AlertDialog.Builder(LoginActivity.this);
                    createNewPass.setTitle("Create Password");
                    createNewPass.setMessage("A password must be at least 32 characters long.");
                    createNewPass.setNegativeButton("CANCEL", null);
                    createNewPass.setCancelable(false);

                    /*This input EditText will be where they actually enter a password*/
                    final EditText input = new EditText(LoginActivity.this);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT);
                    input.setLayoutParams(lp);
                    input.setText(checkPass);
                    createNewPass.setView(input);

                    /*This onclicklistenere will check if the password is atleast 32 characters and then store it into newPassword*/
                    createNewPass.setPositiveButton("CREATE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int whichButton) {
                            checkPass = input.getText().toString();
                            if (checkPass.length() >= 32)
                            {
                                //TODO save the checkPass to the DB
                                //TODO check if password has already been created, if created make another toast
                                String tempLogin = checkPass;   //incase they log out so the password is not stored in create a new password
                                checkPass = null;
                                login( tempLogin ); //login with the newly created password
                            }

                            else
                            {
                                Toast.makeText(getApplicationContext(), "The password is not long enough", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    createNewPass.create().show();
            }
        });

    }


    /* Returns the current password the user has entered
     */
    private String getPassword() {
        return ((EditText)findViewById(R.id.Enter_password)).getText().toString();
    }

    private void login(String password)  //used to login to SCAR
    {
        //Try to find metadata for password
        MetaData meta = MetaData.load( password );
        if(meta != null) {
            //If successful open session
            Session.init(meta, password);
            //put user into main activity
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }
}
