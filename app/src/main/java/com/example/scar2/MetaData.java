package com.example.scar2;

import android.database.sqlite.SQLiteQuery;
import android.database.sqlite.SQLiteQueryBuilder;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteStatement;

import java.io.File;

import scar.IServer;

//TODO: SQLitDatabase.loadLibs(this) in main Activity...
//TODO: Fill in server loading for each type
//SQLite naming format: #.db starting from 0

/* Overview:
 *   Opening/Creating:
 *     load(key) - Tries to open a database that has the password key
 *     create(key) - Makes a new database with the password key
 *   Using:
 *     listFiles() - Returns a list of all file names and local file paths
 *                    that has been stored/recieved by this app
 *     getAllServers() - Get all servers known to the app
 *     getServers(filename) - Returns the servers used for the filename for receiving
 *     setServers(filename) - Sets the current filename to use the current servers known
 *                             in the db; removes any older known ones beforehand.
 *     newFile(filename) - Creates a new file in the db and sets up the servers for it
 *     newServer(type, hostname, port, uname, pass) - Creates a new server for the app
 *
 */

public class MetaData {
    private final int
        TYPE_MYSQL_STORE = 0,
        TYPE_CASS_STORE = 1,
        TYPE_SQLITE_STORE = 2,
        TYPE_LOCALFILE_STORE = 3,
        TYPE_DROPBOX_STORE = 4;

    private final SQLiteDatabase db;

    public MetaData(SQLiteDatabase db) {
        this.db = db;
        //Setup tables if needed
        db.execSQL("CREATE TABLE IF NOT EXISTS servers (id INTEGER,type INTEGER,hostname TEXT,port TEXT,username TEXT,password TEXT,PRIMARY KEY(id))");
        db.execSQL("CREATE TABLE IF NOT EXISTS files (id INTEGER,name TEXT,local TEXT,PRIMARY KEY(id))");
        db.execSQL("CREATE TABLE IF NOT EXISTS servers_used (server_id INTEGER,file_id INTEGER,PRIMAY KEY(server_id, file_id),FOREIGN KEY(server_id) REFERENCES server(id),FOREIGN KEY(file_id) REFERENCES file(id))");
    }

    /*  Load a previously created database based off
     *  your password given at login
     *
     *  Return null if no database found for given key
     */
    public static MetaData load(String key) {
        int dbid = 0;
        while(true) {
            File fdb = new File(dbid+".db");
            if(fdb.exists()){
                //Try to open db with given key
                try {
                    SQLiteDatabase db = SQLiteDatabase.openDatabase(dbid+".db", key, null, SQLiteDatabase.OPEN_READWRITE);
                    //Correct database
                    return new MetaData(db);
                } catch(Exception e) {
                    //Failed to open with given key, or some other issue.
                    // Either way discard this db for this key
                }
            } else break; //stop checking
        }
        return null; //Faild to find a db for this key
    }

    /* Load a new database with the key being
     * the password you created at login
     *
     * Note call load(key) before this to ensure a db doesn't already
     *  exist with the given key
     */
    public static MetaData create(String key) {
        int dbid = 0;
        //get next dbid
        while(true){
            File fdb = new File(dbid+".db");
            if(fdb.exists()) continue;
            else break; //found next id
        }
        //Make the db
        return new MetaData(SQLiteDatabase.openOrCreateDatabase(dbid+".db", key, null));
    }

    public ScarFile[] listFiles() {
        //Get number of files known atm
        Cursor cursor = db.rawQuery("select * from files", null);
        ScarFile[] files = new ScarFile[cursor.getCount()];
        int i = 0;
        cursor.moveToFirst();

        while(!cursor.isAfterLast()) {
            files[i++] = new ScarFile(cursor.getString(cursor.getColumnIndex("name")),
                                      cursor.getString(cursor.getColumnIndex("local")));
        }

        cursor.close();
        return files;
    }

    private IServer[] collectServers(Cursor cur) {
        IServer servers[] = new IServer[cur.getCount()];
        int i = 0;
        cur.moveToFirst();

        while(!cur.isAfterLast()) {
            switch(cur.getInt(cur.getColumnIndex("type"))) {
                case TYPE_MYSQL_STORE:
                    break;
                case TYPE_CASS_STORE:
                    break;
                case TYPE_SQLITE_STORE:
                    break;
                case TYPE_LOCALFILE_STORE:
                    break;
                case TYPE_DROPBOX_STORE:
                    break;
            }
        }

        return servers;
    }

    public IServer[] getAllServers() {
        Cursor cursor = db.rawQuery("select * from servers", null);
        return collectServers(cursor);
    }

    public IServer[] getServers(String fn) {
        Cursor scur = db.rawQuery("select * " +
                "from servers join servers_used on servers.id = servers_used.server_id" +
                "where servers_used.file_id = (select ifnull(id,-1) from files where name = ?)",
                                    new String[] { fn });
        return collectServers(scur);
    }

    public void setServers(String fn) {
        Cursor fcur = db.rawQuery("select id from files where name = ?", new String[] { fn });
        fcur.moveToFirst();
        if(fcur.getCount() > 0) {
            int fid = fcur.getInt(fcur.getColumnIndex("id"));
            //Remove old servers
            db.rawQuery("delete from servers_used where file_id = " + fid, null);
            //Update with new servers
            Cursor srvs = db.rawQuery("select id from servers", null);
            srvs.moveToFirst();

            while (!srvs.isAfterLast()) {
                db.rawQuery("insert into servers_used (server_id, file_id) values (" +
                            srvs.getInt(srvs.getColumnIndex("id")) + "," +
                            fid + ")", null);
            }
        }
    }

    public void newFile(String fn) {
        db.rawQuery("insert into files values ((select max(id)+1 from files), ?, null)",
                new String[] { fn });
        setServers(fn);
    }

    public void newServer(int type, String host, String port, String uname, String pass) {
        db.rawQuery("insert into servers values ((select max(id)+1 from servers), " + type +
                    "?, ?, ?, ?)",
                new String[] { host, port, uname, pass });
    }
}
