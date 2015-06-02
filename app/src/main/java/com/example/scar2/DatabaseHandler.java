package com.example.scar2;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper {

	static final int DATABASE_VERISON = 1; // DB version
	static final String DATABASE_NAME = "serversDB"; // DB name
	static final String TABLE_SERVERS = "servers";
	// Columns
	static final String KEY_ID = "id";
	static final String KEY_HOSTNAME = "hostname";
	static final String KEY_USERNAME = "username";
	static final String KEY_PASSWORD = "password";
	static final String KEY_STATUS = "status";
	static final String KEY_PORT = "portnumber";
	static final String KEY_TYPE = "server";

	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERISON);
	}

	// Creates the tables
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		String CREATE_SERVERS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_SERVERS + "("
				+ KEY_ID + " INTEGER PRIMARY KEY,"
				+ KEY_TYPE +" TEXT,"
				+ KEY_HOSTNAME + " TEXT,"
				+ KEY_PORT + " INTEGER," 
				+ KEY_USERNAME + " TEXT,"
				+ KEY_PASSWORD + " TEXT," 
				+ KEY_STATUS + " INTEGER" 
				+ ")";

		db.execSQL(CREATE_SERVERS_TABLE);

	}

	// upgrades database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVerison, int newVerison) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_SERVERS);
		// Create tables again
		onCreate(db);
	}

	/**
	 * @description Adds a server to the table
	 * @param server
	 */
	public void addServer(Server server) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		// putting the server information
		values.put(KEY_HOSTNAME, server.getHostname());
		values.put(KEY_PORT, server.getPort());
		values.put(KEY_USERNAME, server.getUsername());
		values.put(KEY_PASSWORD, server.getPassword());
		values.put(KEY_STATUS, server.getStatus());
		values.put(KEY_TYPE, server.getServer());
		// inserting it into db
		db.insert(TABLE_SERVERS, null, values);
		db.close();

	}

	/**
	 * @description gets a single server
	 * @param id
	 * @return
	 */
	public Server getServer(int id) {
		SQLiteDatabase db = this.getReadableDatabase();

		// SEARCH query
		Cursor cursor = db.query(TABLE_SERVERS,
				new String[] { KEY_ID, KEY_HOSTNAME, KEY_PORT, KEY_USERNAME,
						KEY_PASSWORD, KEY_STATUS }, KEY_ID + "=?",
				new String[] { String.valueOf(id) }, null, null, null, null);

		if (cursor != null) {
            cursor.moveToFirst();

            // Finds the server result
            Server server = new Server(Integer.parseInt(cursor.getString(0)), cursor.getString(1),
                    cursor.getString(2), Integer.parseInt(cursor.getString(3)),
                    cursor.getString(4), cursor.getString(5),
                    Integer.parseInt(cursor.getString(6)));

            // Now return it
            return server;
        }

        return null;
	}

	/**
	 * @description returns all of the servers in a list
	 * @return
	 */
	public List<Server> getAllServers() {
		List<Server> serverList = new ArrayList<Server>();

		String selectQuery = "SELECT  * FROM " + TABLE_SERVERS;

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				Server server = new Server();
				server.setID(Integer.parseInt(cursor.getString(0)));
				server.setServer(cursor.getString(1));
				server.setHostname(cursor.getString(2));
				server.setPort(Integer.parseInt(cursor.getString(3)));
				server.setUsername(cursor.getString(4));
				server.setPassword(cursor.getString(5));
				server.setStatus(Integer.parseInt(cursor.getString(6)));

				// Adding contact to list
				serverList.add(server);
			} while (cursor.moveToNext());
		}
		return serverList;
	}

	/**
	 * @description returns the number of servers
	 * @return
	 */
	public int getServersCount() {
		String countQuery = "SELECT  * FROM " + TABLE_SERVERS;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		cursor.close();

		// return count
		return cursor.getCount();
	}

	/**
	 * @description update the server with server parameter 
	 * @param server
	 * @return
	 */
	public int updateServer(Server server) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_TYPE, server.getServer());
		values.put(KEY_HOSTNAME, server.getHostname());
		values.put(KEY_PORT, server.getPort());
		values.put(KEY_USERNAME, server.getUsername());
		values.put(KEY_PASSWORD, server.getPassword());
		values.put(KEY_STATUS, server.getStatus());

		return db.update(TABLE_SERVERS, values, KEY_ID + " = ?",
				new String[] { String.valueOf(server.getID()) });

	}

	/**
	 * @description deletes a server record
	 * @param server
	 */
	public void deleteServer(Server server) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_SERVERS, KEY_ID + " = ?",
				new String[] { String.valueOf(server.getID()) });
		db.close();
	}

}
