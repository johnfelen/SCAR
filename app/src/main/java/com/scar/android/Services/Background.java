package com.scar.android.Services;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

/**
 * Created by Spencer on 3/30/2016.
 */
public class Background extends IntentService
{
    public static final String ACTION = "com.scar.android.Services.Background";
    public Background()
    {
        super("Background-service");
    }
    public MetaDataB meta;

    public void onCreate()
    {
        super.onCreate();
        meta = new MetaDataB(this, "Publicdatabase");
        //create your MetaDataB
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        //handle the checking here
        String val = intent.getStringExtra("foo");
        // Construct an Intent tying it to the ACTION (arbitrary event namespace)
        Intent in = new Intent(ACTION);
        // Put extras into the intent as usual
        in.putExtra("resultCode", Activity.RESULT_OK);
        in.putExtra("resultValue", "My Result Value. Passed in: " + val);
        // Fire the broadcast with intent packaged
        LocalBroadcastManager.getInstance(this).sendBroadcast(in);
        // or sendBroadcast(in) for a normal broadcast;
    }
}
