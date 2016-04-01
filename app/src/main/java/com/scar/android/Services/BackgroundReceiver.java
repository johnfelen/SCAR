package com.scar.android.Services;

import android.app.Activity;
import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Spencer on 3/29/2016.
 */
public class BackgroundReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context con, Intent workIntent){
        Log.v("tag", "onReceive: RUNNING!");
        Toast.makeText(con, "I'm running", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(con, Background.class);
        con.startService(i);
    }

}
