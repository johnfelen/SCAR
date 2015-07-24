package com.scar.android.Activities;

import android.app.ActionBar;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.Menu;
import android.widget.TabHost;
import android.view.*;
import android.widget.TextView;

import com.android.scar.R;
import com.scar.android.Fragments.Retrieve;
import com.scar.android.Fragments.ServerList;
import com.scar.android.Fragments.Store;
import com.scar.android.Session;

public class MainActivity extends FragmentActivity {
    private FragmentTabHost tabHost;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        final ActionBar actionBar = getActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#2B468B")));

        // create the TabHost that will contain the Tabs
        tabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        tabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);

        //TODO: change Store to Files when created
        tabHost.addTab(tabHost.newTabSpec("1st Tab").setIndicator("FILES"), Store.class, null);
        tabHost.addTab(tabHost.newTabSpec("2nd Tab").setIndicator("STORE"), Store.class, null);
        tabHost.addTab(tabHost.newTabSpec("3rd Tab").setIndicator("RETRIEVE"), Retrieve.class, null);
        tabHost.addTab(tabHost.newTabSpec("4th Tab").setIndicator("SERVERS"), ServerList.class, null);


        for(int i=0;i<tabHost.getTabWidget().getChildCount();i++)
        {
            if (i == 0) //since the first tab is always selected first
            {
                tabHost.getTabWidget().getChildAt(i).setBackgroundColor(Color.parseColor("#98EF8E"));
                getTabTV(i).setTextColor(Color.parseColor("#2B468B"));
            }

            else    //the rest of the tabs are in the unselected color
            {
                tabHost.getTabWidget().getChildAt(i).setBackgroundColor(Color.parseColor("#2B468B"));
                getTabTV(i).setTextColor(Color.parseColor("#98EF8E"));
            }
        }


        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener(){
            @Override
            public void onTabChanged(String tabId) {

                for(int i=0;i<tabHost.getTabWidget().getChildCount();i++)   //make all the tabs looked unselected first
                {
                    tabHost.getTabWidget().getChildAt(i).setBackgroundColor(Color.parseColor("#2B468B"));
                    getTabTV(i).setTextColor(Color.parseColor("#98EF8E"));
                }

                //then make the tab that actually was selected in the selected scheme
                tabHost.getTabWidget().getChildAt(tabHost.getCurrentTab()).setBackgroundColor(Color.parseColor("#98EF8E"));
                getTabTV( tabHost.getCurrentTab() ).setTextColor(Color.parseColor("#2B468B"));
            }
        });
    }

    protected void onResume() {
        super.onResume();
        //Check if Session is valid before continuing
        if (!Session.valid()) {
            //Force user to Login first, MainActivity will go on Stop in the meantime.
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }

    public TextView getTabTV(int childIndex)
    {
        ViewGroup tabVG = (ViewGroup) tabHost.getTabWidget().getChildAt( childIndex );
        return (TextView) tabVG.getChildAt(1);
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
		else
		{
			return super.onOptionsItemSelected(item);
		}
	}
}
