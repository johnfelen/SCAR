package com.example.scar2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.app.LocalActivityManager;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import android.view.Menu;
import android.widget.Button;
import android.widget.TabHost;
import android.view.*;
import android.widget.TabHost.TabSpec;

@SuppressWarnings("deprecation")
public class MainActivity extends TabActivity {

	static final String DATABASE_NAME = "serversDB"; // DB name
	static final String DB_PATH ="/data/data/com.example.scar2/databases/" ;
	public static ArrayList<Server> serverList = new ArrayList<Server>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
        serverList.add((new Server(1,"MysqlPitt","mysql.cs.pitt.edu",3306,"diablo0897","ChangeMe",1)));
        serverList.add((new Server(2,"MysqlLocal","10.0.3.2",3306,"root","poney373",1)));
        // create the TabHost that will contain the Tabs
        TabHost tabHost = (TabHost)findViewById(android.R.id.tabhost);

        TabSpec tab1 = tabHost.newTabSpec("First Tab");
        TabSpec tab2 = tabHost.newTabSpec("Second Tab");
        TabSpec tab3 = tabHost.newTabSpec("Third tab");

       // Set the Tab name and Activity
       // that will be opened when particular Tab will be selected
        tab1.setIndicator("STORE"); //, getResources().getDrawable(R.drawable.send_tab) );
        tab1.setContent(new Intent(this,Store.class));

        tab2.setIndicator("RETRIEVE"); //, getResources().getDrawable(R.drawable.download_tab));
        tab2.setContent(new Intent(this,Retrieve.class));

        tab3.setIndicator("ADD"); //, getResources().getDrawable(R.drawable.home_tab));
        tab3.setContent(new Intent(this,New_Server.class));
        
        /** Add the tabs  to the TabHost to display. */
        tabHost.addTab(tab1);
        tabHost.addTab(tab2);
        tabHost.addTab(tab3);
        
        
        
        
        /*
         * Below is for database
         */
        
        //System.out.println("Does database exist?" +checkDataBase()); 
        
        /*
         * 
         */
        
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
	
	
	/*
	 * Checks if database exists, if not then create the two default servers.
	 * 
	 */
	private boolean checkDataBase() {
		 boolean doesExist = false;
	     File dbFile = new File(DB_PATH + DATABASE_NAME);
	      if(dbFile.exists()){
	    	  doesExist = true;
	      }
	   
	      else{
	    
	    	  doesExist =false;
	    	  System.out.println("Database does not exist, creating a database");
	    	  DatabaseHandler db = new DatabaseHandler(this);
	    	  Log.d("Insert: ", "Inserting .."); 
	          //db.addServer(new Server(1, "Redis", "ra.cs.pitt.edu", 8084, "username", "password", 1));        
	          //db.addServer(new Server(2, "Mongo DB", "192.168.1.5",27017, "username", "password", 1));        

	          //Debugging, reading preset servers
	          Log.d("Reading: ", "Reading all contacts.."); 
	          List<Server> contacts = db.getAllServers();       
	           
	          for (Server cn : contacts) {
	              String log = 	  "Id: "+cn.getID()
	            		  +", Type: " + cn.getServer()
	            		  +" ,Hostname: "  + cn.getHostname()
	            		  + " ,Port: " + cn.getPort()
	            		  + ", username: " +cn.getUsername()
	            		  +", password: " +cn.getPassword();
            		  
	            		  
	                  // Writing Contacts to log
	          Log.d("Name: ", log);
	          
	    	// database doesn't exist yet.
	    }
	      }
	return doesExist;		    
	}

}
