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
import android.os.Handler;
import android.os.IBinder;
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

    public IBinder onBind(Intent intent)
    {
        return null;
    }

    public Context context = this;
    public Handler handler = null;
    public static Runnable runnable = null;

    public void onCreate()
    {
        super.onCreate();
        meta = new MetaDataB(this, "PublicDatabase");

        handler = new Handler();
        runnable = new Runnable() {
            public void run() {
                //Toast.makeText(context, "Service is still running", Toast.LENGTH_LONG).show();
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
                    Notify();
                }

                handler.postDelayed(runnable, 300000);
            }
        };

        handler.postDelayed(runnable, 15000);



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
        return START_STICKY;
    }


    NotificationCompat.Builder mBuilder =
            new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.scar_img)
                    .setContentTitle("Scar Files")
                    .setContentText("You have Scar files that need relocating. Please log in and take care of this")
                    .setPriority(2);




}
