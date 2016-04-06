package com.scar.android.Services;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.scar.android.Session;

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
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        while(true)
        {
            Session.metaBackground = meta;
        }
    }
}
