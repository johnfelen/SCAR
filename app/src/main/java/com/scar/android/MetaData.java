package com.scar.android;

import android.app.Activity;
import android.util.Log;

import com.scar.android.ServerImpl.SQLiteStore;

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
 *     updateFile(id, filename, local) - Updates a file in SCAR
 *     updateServer(id, type, hostname, port, uname, pass) - Updates a server in SCAR
 */

public class MetaData {
    public static final int
        TYPE_MYSQL_STORE = 0,
        TYPE_CASS_STORE = 1,
        TYPE_SQLITE_STORE = 2,
        //TYPE_LOCALFILE_STORE = 3,
        TYPE_DROPBOX_STORE = 3,
        TYPE_GDRIVE_STORE = 4,

        STATUS_ACTIVE = 0,
        STATUS_DISABLE = 1;

    private final SQLiteDatabase db;
    private final String dbname;

    public MetaData(Activity act, String dbnm, String key) {
        dbname = dbnm;
        File dbf = act.getDatabasePath(dbname);
        db = SQLiteDatabase.openDatabase(dbf.getPath(), key, null, SQLiteDatabase.OPEN_READWRITE);
        //Setup tables if needed
        db.execSQL("CREATE TABLE IF NOT EXISTS servers (id INTEGER,status INTEGER, type INTEGER,label TEXT, hostname TEXT,port TEXT,username TEXT,password TEXT,PRIMARY KEY(id))");
        db.execSQL("CREATE TABLE IF NOT EXISTS files (id INTEGER,name TEXT,local TEXT,PRIMARY KEY(id))");
        db.execSQL("CREATE TABLE IF NOT EXISTS servers_used (server_id INTEGER,file_id INTEGER,PRIMARY KEY(server_id, file_id),FOREIGN KEY(server_id) REFERENCES server(id),FOREIGN KEY(file_id) REFERENCES file(id))");
    }

    /* sets up sqlcipher to work properly
     */
    public static void init(Activity act) {
        SQLiteDatabase.loadLibs(act);
    }

    /*  Load a previously created database based off
     *  your password given at login
     *
     *  Return null if no database found for given key
     */
    public static MetaData load(Activity act, String key) {
        int dbid = 0;
        while(true) {
            File fdb = act.getDatabasePath(dbid+".db");
            if(fdb.exists()){
                //Try to open db with given key
                try {
                    SQLiteDatabase db = SQLiteDatabase.openDatabase(fdb.getPath(), key, null, SQLiteDatabase.OPEN_READWRITE);
                    //Correct database
                    return new MetaData(act, dbid+".db",key);
                } catch(Exception e) {
                    //Failed to open with given key, or some other issue.
                    // Either way discard this db for this key
                }
            } else break; //stop checking
            dbid++;
        }
        return null; //Faild to find a db for this key
    }

    /* Load a new database with the key being
     * the password you created at login
     *
     * Note call load(key) before this to ensure a db doesn't already
     *  exist with the given key
     */
    public static void create(Activity act, String key) {
        int dbid = 0;
        //get next dbid
        while(true){
            File fdb = new File(dbid+".db");
            if(fdb.exists()) continue;
            else break; //found next id
        }
        //Make the db for later use
        String dbname = dbid + ".db";
        File dbf = act.getDatabasePath(dbname);
        dbf.getParentFile().mkdirs();
        dbf.delete();
        SQLiteDatabase.openOrCreateDatabase(dbf.getPath(), key, null).close();
    }

    //Ensures the MetaData is still valid
    public boolean valid() {
        return db != null && dbname != null;
    }

    //Close the database
    public void close() {
        db.close();
    }

    //Reopen the database
    public void open() {

    }

    public ScarFile[] listFiles() {
        //Get number of files known atm
        Cursor cursor = db.rawQuery("select * from files", null);
        ScarFile[] files = new ScarFile[cursor.getCount()];
        int i = 0;
        cursor.moveToFirst();

        while(!cursor.isAfterLast()) {
            files[i++] = new ScarFile(cursor.getInt(cursor.getColumnIndex("id")),
                                      cursor.getString(cursor.getColumnIndex("name")),
                                      cursor.getString(cursor.getColumnIndex("local")));
            cursor.moveToNext();
        }

        cursor.close();
        return files;
    }

    private IServer[] collectServers(Cursor cur) {
        IServer servers[] = new IServer[cur.getCount()];
        int i = 0;
        cur.moveToFirst();

        while(!cur.isAfterLast()) {
            if(cur.getInt(cur.getColumnIndex("status")) == STATUS_ACTIVE) {
                switch (cur.getInt(cur.getColumnIndex("type"))) {
                    case TYPE_MYSQL_STORE:
                        break;
                    case TYPE_CASS_STORE:
                        break;
                    case TYPE_SQLITE_STORE:
                        servers[i] = new SQLiteStore(cur.getString(cur.getColumnIndex("hostname")));
                        break;
                    case TYPE_GDRIVE_STORE:
                        break;
                    case TYPE_DROPBOX_STORE:
                        break;
                }
            }
            ++i;
            cur.moveToNext();
        }
        cur.close();

        return servers;
    }

    public Server[] getAllServerInfo() {
        Cursor cur = db.rawQuery("select * from servers", null);
        Server srvs[] = new Server[cur.getCount()];
        int i = 0;
        cur.moveToFirst();

        while(!cur.isAfterLast()) {
            srvs[i++] = new Server(cur.getInt(cur.getColumnIndex("type")),
                                cur.getInt(cur.getColumnIndex("status")),
                                cur.getString(cur.getColumnIndex("label")),
                                cur.getString(cur.getColumnIndex("hostname")),
                                cur.getString(cur.getColumnIndex("port")),
                                cur.getString(cur.getColumnIndex("username")),
                                cur.getString(cur.getColumnIndex("password")));
            cur.moveToNext();
        }
        cur.close();

        return srvs;
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
        db.beginTransaction();
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
                srvs.moveToNext();
            }
            fcur.moveToNext();
        }
        fcur.close();
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public void newFile(String fn, String local) {
        db.beginTransaction();
        SQLiteStatement stmt = db.compileStatement("insert into files values ((select max(id)+1 from files), ? , ?)");
        stmt.bindString(1, fn);
        stmt.bindString(2, local);
        stmt.executeInsert();
        db.setTransactionSuccessful();
        db.endTransaction();
        setServers(fn);
    }

    public void newServer(int type, String label, String host, String port, String uname, String pass) {
        db.beginTransaction();
        SQLiteStatement stmt = db.compileStatement("insert into servers (id, type, status, label, hostname, port, username, password) values ((select max(id)+1 from servers), ?, ?, ?, ? , ?, ? , ?)");
        stmt.bindLong(1, type);
        stmt.bindLong(2, STATUS_ACTIVE);
        stmt.bindString(3, label);
        stmt.bindString(4, host);
        stmt.bindString(5, port);
        stmt.bindString(6, uname);
        stmt.bindString(7, pass);
        stmt.executeInsert();
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public void updateFile(int id, String fn, String local) {
        db.beginTransaction();
        SQLiteStatement stmt = db.compileStatement("update files set name = ?, local = ? where id = " + id);
        stmt.bindString(1, fn);
        stmt.bindString(2, local);
        stmt.executeUpdateDelete();
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public void updateServer(int id, int type, String host, String port, String uname, String pass) {
        db.beginTransaction();
        SQLiteStatement stmt = db.compileStatement("update servers set type = " + type + ", hostname = ?, port = ?, uname = ?, pass = ? where id = " + id);
        stmt.bindString(1, host);
        stmt.bindString(2, port);
        stmt.bindString(3, uname);
        stmt.bindString(4, pass);
        stmt.executeUpdateDelete();
        db.setTransactionSuccessful();
        db.endTransaction();
    }
}
