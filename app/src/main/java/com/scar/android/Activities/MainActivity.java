package com.scar.android.Activities;

import android.app.ActionBar;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.widget.Button;
import android.widget.TabHost;
import android.view.*;
import android.widget.TextView;
import android.widget.Toast;

import com.android.scar.R;
import com.scar.android.Fragments.Files;
import com.scar.android.Fragments.Retrieve;
import com.scar.android.Fragments.ServerList;
import com.scar.android.Fragments.Store;
import com.scar.android.Services.Background;
import com.scar.android.Services.BackgroundReceiver;
import com.scar.android.Session;

import java.util.Date;

public class MainActivity extends FragmentActivity {
    //private FragmentTabHost tabHost;
    /*These two are for the tabs and screen slider*/
    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;
    private long backgroundStartTime;   //will hold the timestamp of when the user puts the app in the background
    private boolean backgroundHasNotBeenSet = true;
    private PendingIntent pendingIntent;
    private AlarmManager manager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        final ActionBar actionBar = getActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#2B468B")));

        /*This will set up the pager and create the screen slider in the main tabs*/
        viewPager = (ViewPager) findViewById(R.id.pager);
        pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);

        Button store = (Button) findViewById(R.id.store_tab);
        store.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                viewPager.setCurrentItem(0);

            }
        });

        Button retrieve = (Button) findViewById(R.id.retrieve_tab);
        retrieve.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                viewPager.setCurrentItem(1);

            }
        });

        Button files = (Button) findViewById(R.id.files_tab);
        files.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                viewPager.setCurrentItem(2);

            }
        });

        Button servers = (Button) findViewById(R.id.servers_tab);
        servers.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                viewPager.setCurrentItem(3);

            }
        });
    }

    private void onStartService()
    {
        Intent backgroundChecker = new Intent(this, BackgroundReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, backgroundChecker, 0);

        manager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        int interval = 1000; //still need to figure out timer
        //need to use sendOrderedBroadcast
        //need to figureout how to send messages back to a scheduler thing
        manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
    }

    // Define the callback for what to do when data is received
    private BroadcastReceiver testReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            int resultCode = intent.getIntExtra("resultCode", RESULT_CANCELED);
            if (resultCode == RESULT_OK)
            {
                //scheduler here
            }
        }
    };

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter //allows sliding between main tabs
    {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:


                    return Store.newInstance(position);
                case 1:


                    return Retrieve.newInstance(position);

                case 2:

                    return Files.newInstance(position);

                case 3:

                    return ServerList.newInstance(position);

            }
            return null;
        }

        @Override
        public int getCount() {
            return 4;
        }
    }

    protected void onResume() {
        super.onResume();
        long currentTimeStamp = new Date().getTime();   //300000
        System.out.println("background start time: " + backgroundStartTime );
        System.out.println( "Current: " + currentTimeStamp );
        //Check if Session is valid before continuing, 300000 is 5 minutes

        if ( currentTimeStamp - backgroundStartTime > 300000 || !Session.valid() ) {
            //Force user to Login first, MainActivity will go on Stop in the meantime.
            Session.clear();
            backgroundHasNotBeenSet = true;
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
        else    //incase the user stays on the app for 5 minutes to make sure that the timeout starts when the user actually goes into the backgorund
        {
            backgroundHasNotBeenSet = true;
        }

    }

    protected void onPause()
    {
        super.onPause();
        if( backgroundHasNotBeenSet ) //will only set the background time once
        {
            backgroundStartTime = new Date().getTime(); //get the time when the app goes into 'background', may make false positive
            backgroundHasNotBeenSet = false;
        }

    }

    protected void onStop()
    {
        super.onStop();
        //Session.clear(); //I believe this is a fix to the leaving main activity and requiring user to re log in. need
        // to double check that this does not compromise the security of the app
    }


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		if (item.getItemId() ==  R.id.action_about)
		{
			Intent intent = new Intent(this, About.class);
			startActivity(intent);
			
			return true;
		}

        else if(item.getItemId() == R.id.action_clean) {
            //TODO: remove when no longer needed for testing
            Session.meta.clean();
            return true;
        }

        else if( item.getItemId() == R.id.action_logout )
        {
            Intent login = new Intent( this, LoginActivity.class );
            Session.clear();
            startActivity(login);
            return true;
        }
        return super.onOptionsItemSelected(item);
	}
}