package com.scar.android.Services;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcel;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.android.scar.R;
import com.scar.android.Activities.MainActivity;
import com.scar.android.Server;
import com.scar.android.Session;

import java.util.HashSet;

import scar.ChunkMeta;
import scar.ChunkMetaPub;

/**
 * Created by Spencer on 3/30/2016.
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

    public void onCreate()
    {
        super.onCreate();
        intent = new Intent(this, Background.class);
        if(intent == null)
        Log.d("TAG", "MESSENGER inent is null! ");
        meta = new MetaDataB(this, "PublicDatabase");
        Bundle extras = intent.getExtras();
        if(extras == null)
            Log.d("TAG", "MESSENGER extras is null! ");
        messageHandler = (Messenger) extras.get("MESSENGER");

        handler = new Handler();
        runnable = new Runnable() {
            public void run() {
                //Toast.makeText(context, "Service is still running", Toast.LENGTH_LONG).show();
                Session.metaBackground = meta;

                ChunkMetaPub[] allChunks = meta.getChunks();
                Server[] allServers = meta.getAllServerInfo();
                HashSet<ChunkMetaPub> relocate = new HashSet<ChunkMetaPub>();
                int numServers = allServers.length;
                int threshold = Integer.MAX_VALUE;

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
                    sendMessage(relocate);
                }

                handler.postDelayed(runnable, 30000000); //basically disabling background runner
            }
        };

        handler.postDelayed(runnable, 15000);



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
        //sendAlert();

        //when notification is clicked user is sent to main activitys
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
        mNotificationManager.notify(69, mBuilder.build());
    }

    public void onDestroy()
    {
        meta.close();
    }

    public void sendAlert()
    {
        new AlertDialog.Builder(context)
                .setTitle("Delete entry")
                .setMessage("Are you sure you want to delete this entry?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public int onStartCommand(Intent intent, int flags, int startId)
    {
        this.intent = intent;
        return START_STICKY;
    }


    NotificationCompat.Builder mBuilder =
            new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.scar_img)
                    .setContentTitle("Scar Files")
                    .setContentText("You have Scar files that need relocating. Please log in and take care of this")
                    .setPriority(2);




}
