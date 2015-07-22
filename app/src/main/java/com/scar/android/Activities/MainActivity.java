package com.scar.android.Activities;

import android.app.ActionBar;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.app.TabActivity;
import android.content.Intent;
import android.view.Menu;
import android.widget.TabHost;
import android.view.*;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.android.scar.R;
import com.scar.android.Session;

@SuppressWarnings("deprecation")
public class MainActivity extends TabActivity {
    TabHost tabHost;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // create the TabHost that will contain the Tabs
        tabHost = (TabHost) findViewById(android.R.id.tabhost);
        //tabHost.getTabWidget().setShowDividers(TabWidget.SHOW_DIVIDER_MIDDLE);
        //tabHost.getTabWidget().setDividerDrawable(R.drawable.tab_divider);

        final ActionBar actionBar = getActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#2B468B")));

        TabSpec tab1 = tabHost.newTabSpec("First Tab");
        TabSpec tab2 = tabHost.newTabSpec("Second Tab");
        TabSpec tab3 = tabHost.newTabSpec("Third tab");

        // Set the Tab name and Activity
        // that will be opened when particular Tab will be selected
        tab1.setIndicator("STORE"); //, getResources().getDrawable(R.drawable.send_tab) );
        tab1.setContent(new Intent(this,Store.class));

        tab2.setIndicator("RETRIEVE"); //, getResources().getDrawable(R.drawable.download_tab));
        tab2.setContent(new Intent(this,Retrieve.class));

        tab3.setIndicator("SERVERS"); //, getResources().getDrawable(R.drawable.home_tab));
        tab3.setContent(new Intent(this,New_Server.class));

        /** Add the tabs  to the TabHost to display. **/
        tabHost.addTab(tab1);
        tabHost.addTab(tab2);
        tabHost.addTab(tab3);

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
        ViewGroup tabVG = (ViewGroup) getTabHost().getTabWidget().getChildAt( childIndex );
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
