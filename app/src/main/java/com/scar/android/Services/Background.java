package com.scar.android.Services;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.scar.android.Server;
import com.scar.android.Session;

import java.util.HashSet;

import scar.ChunkMeta;

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
            ChunkMeta[] allChunks = meta.getChunks();
            Server[] allServers = meta.getAllServerInfo();
            HashSet<ChunkMeta> relocate = new HashSet<ChunkMeta>();
            int numServers = allServers.length;
            int threshold = Integer.MAX_VALUE;

            for(int i = 0; i < allChunks.length; i++)
            {
                ChunkMeta current = allChunks[i];
                if(relocate.contains(current))
                {
                    continue;
                }
                else if(current.physical != current.virtual && current.virtual <= numServers)
                {
                    relocate.add(current);
                }
            }

            if(relocate.size() > threshold)
            {
                //send notification and handle it somewhere
            }

            //if it does, check and see if it is in chunk meta (maybe check this first tbh)
            //set threshold of maybe like 25% needing relocation before sending notification
            //then maybe look into dead server notification (should be kinda easy to add i think)
            try
            {
                Thread.sleep(3000000); //5 minutes
            }catch(InterruptedException e)
            {
                Thread.currentThread().interrupt();
            }
        }
    }
}
