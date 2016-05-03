package com.scar.android.Services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;

import com.android.scar.R;
import com.scar.android.Activities.MainActivity;
import com.scar.android.Server;
import com.scar.android.Session;

import java.util.HashSet;

import scar.ChunkMetaPub;

/**
 * Created by Spencer on 3/30/2016.
 *  This is a background monitor for the application that runs hourly to monitor data
 * inside the MetaDataB database.
 */
public class Background extends Service
{
    public Background()
    {
        super();
    }
    public MetaDataB meta;
    public Intent intent;

    public IBinder onBind(Intent intent)
    {
        return null;
    }

    public Context context = this;
    public Handler handler = null;
    public static Runnable runnable = null;
    private Messenger messageHandler;
    HashSet<ChunkMetaPub> relocate;
    public boolean chunksReady = false;

    public void onCreate()
    {
        super.onCreate();
        meta = new MetaDataB(this, "PublicDatabase");

        relocate = new HashSet<ChunkMetaPub>();

        handler = new Handler();
        runnable = new Runnable() {
            public void run() {
                Session.metaBackground = meta;

                ChunkMetaPub[] allChunks = meta.getChunks();
                Server[] allServers = meta.getAllServerInfo();

                int numServers = allServers.length;
                //relocate when a quarter of the chunks can be relocated
                double threshold = 0.25 * allChunks.length;

                for(int i = 0; i < allChunks.length; i++)
                {
                    ChunkMetaPub current = allChunks[i];
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
                    Notify();
                    if (Session.valid())
                    {
                        sendMessage(relocate);
                        relocate = new HashSet<ChunkMetaPub>();
                        chunksReady = false;
                    }
                }

                handler.postDelayed(runnable, 3600000); //runs every hour
            }
        };
    }

    public void sendMessage(HashSet<ChunkMetaPub> chunks)
    {
        Message message = Message.obtain();
        message.obj = chunks;

        try {
            messageHandler.send(message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void Notify()
    {
        Intent resultIntent = new Intent(context, MainActivity.class);

        //when notification is clicked user is sent to main activity
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(1, mBuilder.build());
    }

    public void onDestroy()
    {
        meta.close();
    }

    public int onStartCommand(Intent intent, int flags, int startId)
    {
        this.intent = intent;
        if(Session.valid())
        {
            Bundle extras = intent.getExtras();
            messageHandler = (Messenger) extras.get("MESSENGER");
            if(chunksReady)
            {
                sendMessage(relocate);
                relocate = new HashSet<ChunkMetaPub>();
                chunksReady = false;
            }
        }

        return START_STICKY;
    }


    NotificationCompat.Builder mBuilder =
            new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.scar_img)
                    .setContentTitle("Scar Files")
                    .setContentText("You have Scar files that need relocating. Please log in and take care of this")
                    .setPriority(2);
}
