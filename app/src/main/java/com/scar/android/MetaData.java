package com.scar.android;

import android.app.Activity;
import android.util.Log;
import scar.*;

import com.scar.android.ServerImpl.SQLiteStore;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteStatement;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;


import scar.DerivedKeyGen;
import scar.Encryption;
import scar.IServer;



//SQLite naming format: #.db starting from 0

/**
 * MetaData has the purpose of storing all vital information to the application in an encrypted 
 * sqlite database via sqlcipher using our login password as the key<br>
 * Tables:<br>
 * Files<br>
 *   - id            : int    , File ID [PK]<br>
 *   - Filename      : text   , name of file<br>
 *   - Key           : byte[] , encrypted key for this file { SALT, IV, GCM encrypted data }<br> 
 * <br>
 * Servers<br>
 *   - id            : int   , Server ID [PK]<br>
 *   - type          : int   , Server Type<br>
 *   - status        : int   , Server Status<br>
 *   - label         : text  , Server Name <br>
 *   - hostname      : text  , Server hostname<br>
 *   - port          : text  , Server port<br>
 *   - username      : byte[], Server username { SALT, IV, GCM encrypted data }<br>
 *   - password      : byte[], Server password { SALT, IV, GCM encrypted data }<br>
 *<br>
 * Servers_Used<br>
 *   - file_id       : int, File ID<br>
 *   - server_id     : int, Server ID associated with the file<br>
 *<br>
 * Local_Files<br>
 *   - file_id       : int , File ID<br>
 *   - localpath     : text, Path to the a local file for the given file<br>
 *<br>
 * Overview:<br>
 *   Opening/Creating:<br>
 *     load(key) - Tries to open a database that has the password key<br>
 *     create(key) - Makes a new database with the password key<br>
 *   Using:<br>
 *     getFileKey() - Returns the key for this file if it has any<br>
 *     setFileKey(id, key) - Sets the key for this file<br>
 *     getFile(fn) - Returns the file with the given fn if any<br>
 *     listFiles() - Returns a list of all file names and local file paths<br>
 *                    that has been stored/recieved by this app<br>
 *     getAllActiveServers() - Get all servers known to the app in a functional state<br>
 *     getAllServersInfo() - Get all servers known to the app in a descriptive state<br>
 *     getServers(filename) - Returns the servers used for the filename for receiving RENAMING TO GET CHUNKS!<br>
 *     setServers(filename, srvs) - Sets the current filename to use the given servers<br>
 *
 *     addLocalFile(fn, local) - Adds a local path for the given file<br>
 *     removeLocalFile(fid, local) - Removes this local path<br>
 *
 *     newFile(filename) - Creates a new file in the db and sets up the servers for it<br>
 *     newServer(type, hostname, port, uname, pass) - Creates a new server for the app<br>
 *
 *     updateFile(id, filename) - Updates a file in SCAR<br>
 *     updateServer(id, type, hostname, port, uname, pass) - Updates a server in SCAR<br>
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

  /**
   * Creates/Opens a new Meta database with the given name and key
   * @param act App's activity
   * @param dbnm database name
   * @param key key for this database
   */
    public MetaData(Activity act, String dbnm, String key) {
        dbname = dbnm;
        File dbf = act.getDatabasePath(dbname);
        setDB newDB=new setDB(act,dbf,key);
        db=newDB.getDb();
        //db = SQLiteDatabase.openDatabase(dbf.getPath(), key, null, SQLiteDatabase.OPEN_READWRITE);
        //Setup tables if needed
        db.execSQL("CREATE TABLE IF NOT EXISTS servers ("
                +"id INTEGER,"
                +"status INTEGER,"
                +"type INTEGER,"
                +"label TEXT,"
                +"hostname TEXT,"
                +"port TEXT,"
                +"username BLOB,"
                +"password BLOB,"
                +"PRIMARY KEY(id),"
                +"FOREIGN KEY(id) REFERENCES chunks_private(physical_id))");
        db.execSQL("CREATE TABLE IF NOT EXISTS files ("
                    +"id INTEGER,"
                    +"name TEXT,"
                    +"key BLOB,"
                    +"PRIMARY KEY(id),"
                    +"FOREIGN KEY(id) REFERENCES chunks_private(file_id))");
        //new table for chunks: file id, name, virtual id (int), physical id (int) points to server ID, chunk ID
        //separate database for scheduler without some of the fields.
        db.execSQL("CREATE TABLE IF NOT EXISTS chunks_private ("
                +"file_id INTEGER,"
                +"name TEXT,"
                +"virtual_id INTEGER,"
                +"physical_id INTEGER,"
                +"chunk_id INTEGER," //chunk id
                +"PRIMARY KEY(chunk_id),"
                +"FOREIGN KEY(physical_id) REFERENCES servers(id),"
                +"FOREIGN KEY(file_id) REFERENCES files(id))");

        db.execSQL("CREATE TABLE IF NOT EXISTS local_files ("
                + "file_id INTEGER,"
                + "localpath TEXT,"
                + "PRIMARY KEY(file_id, localpath),"
                + "FOREIGN KEY(file_id) REFERENCES file(id))");
    }

    //constructor just used for unit testing
    public MetaData(Activity act, String dbnm, String key,setDB newDB) {
        dbname = dbnm;
        File dbf = act.getDatabasePath(dbname);
        //newDB=new setDB(act,dbf,key);
        db=newDB.getDb();
        //db = SQLiteDatabase.openDatabase(dbf.getPath(), key, null, SQLiteDatabase.OPEN_READWRITE);
        //Setup tables if needed
        db.execSQL("CREATE TABLE IF NOT EXISTS servers ("
                +"id INTEGER,"
                +"status INTEGER,"
                +"type INTEGER,"
                +"label TEXT,"
                +"hostname TEXT,"
                +"port TEXT,"
                +"username BLOB,"
                +"password BLOB,"
                +"PRIMARY KEY(id),"
                +"FOREIGN KEY(id) REFERENCES chunks_private(physical_id))");
        db.execSQL("CREATE TABLE IF NOT EXISTS files ("
                +"id INTEGER,"
                +"name TEXT,"
                +"key BLOB,"
                +"PRIMARY KEY(id),"
                +"FOREIGN KEY(id) REFERENCES chunks_private(file_id))");
        //new table for chunks: file id, name, virtual id (int), physical id (int) points to server ID, chunk ID
        //separate database for scheduler without some of the fields.
        db.execSQL("CREATE TABLE IF NOT EXISTS chunks_private ("
                +"file_id INTEGER,"
                +"name TEXT,"
                +"virtual_id INTEGER,"
                +"physical_id INTEGER,"
                +"chunk_id INTEGER," //chunk id
                +"PRIMARY KEY(chunk_id),"
                +"FOREIGN KEY(physical_id) REFERENCES servers(id),"
                +"FOREIGN KEY(file_id) REFERENCES files(id))");

        db.execSQL("CREATE TABLE IF NOT EXISTS local_files ("
                + "file_id INTEGER,"
                + "localpath TEXT,"
                + "PRIMARY KEY(file_id, localpath),"
                + "FOREIGN KEY(file_id) REFERENCES file(id))");
    }
    //public SQLiteDatabase setDB(Activity act,File DBName,String key)
    //{
    //    return SQLiteDatabase.openDatabase(DBName.getPath(), key, null, SQLiteDatabase.OPEN_READWRITE);
    //}
    /* sets up sqlcipher to work properly
     */
    public static void init(Activity act) {
        SQLiteDatabase.loadLibs(act);
    }

    /* Deletes all the meta databases
     *
     */
    public static void DeleteAllDB(Activity act) {
        int dbid = 0;
        //get next dbid
        while(true){
            File fdb = new File(dbid+".db");
            if(fdb.exists()) fdb.delete();
            else break; //found next id
        }
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
                    db.close();
                    return new MetaData(act, dbid+".db",key);
                } catch(Exception e) {
                    //Failed to open with given key, or some other issue.
                    // Either way discard this db for this key
                }
            } else break; //stop checking
            dbid++;
        }
        return null; //Failed to find a db for this key
    }

  /** Load a new database with the key being
   * the password you created at login.
   *<br>
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

  /**
   * Puts the database back into a state of initial creation
   */
  public void clean() {
        db.beginTransaction();
        SQLiteStatement stmt = db.compileStatement("delete from local_files");
        stmt.execute();
        stmt.close();
        stmt = db.compileStatement("delete from chunks_private");
        stmt.execute();
        stmt.close();
        stmt = db.compileStatement("delete from files");
        stmt.execute();
        stmt.close();
        stmt = db.compileStatement("delete from servers");
        stmt.execute();
        stmt.close();
        db.setTransactionSuccessful();
        db.endTransaction();
    }

  /**
   * Closes the database
   */
    public void close() {
        db.close();
    }

  /** 
   * Ensures the MetaData is still valid
   */
    public boolean valid()
    {
        return db != null && dbname != null;
    }

  /**
   * @return a list of all known files stored on this app
   */
    public ScarFile[] listFiles() {
        //Get number of files known atm
        Cursor cursor = db.rawQuery("select * from files", null);
        ScarFile[] files = new ScarFile[cursor.getCount()];
        int i = 0;
        cursor.moveToFirst();

        while(!cursor.isAfterLast()) {
            files[i++] = new ScarFile(cursor.getInt(cursor.getColumnIndex("id")),
                                      cursor.getString(cursor.getColumnIndex("name")));
            cursor.moveToNext();
        }

        cursor.close();

        for(ScarFile sf : files) {
            cursor = db.rawQuery("select * from local_files where file_id = " + sf.id, null);
            cursor.moveToFirst();

            while(!cursor.isAfterLast()) {
                sf.addLocal(cursor.getString(cursor.getColumnIndex("localpath")));
                cursor.moveToNext();
            }

            cursor.close();
        }

        return files;
    }

    public byte[] getFileKey(String fn){
        byte[] key = null;
        Cursor cur = db.rawQuery("select * from files where name = ?", new String[] { fn });
        cur.moveToFirst();

        if(!cur.isAfterLast())
            key = decryptText(cur.getBlob(cur.getColumnIndex("key")));

        cur.close();
        return key;
    }

    public void setFileKey(int fid, byte[] key) {
        db.beginTransaction();
        SQLiteStatement stmt = db.compileStatement("update files set key = ? where id = ?");
        stmt.bindBlob(1, key);
        stmt.bindLong(2, fid);
        stmt.execute();
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public ScarFile getFile(String fn) {
        ScarFile sf = null;
        db.beginTransaction();
        Cursor cur = db.rawQuery("select * from files where name = ?", new String[] { fn });
        cur.moveToFirst();
        if(!cur.isAfterLast()) {
            sf = new ScarFile(cur.getInt(cur.getColumnIndex("id")),
                              cur.getString(cur.getColumnIndex("name")));
            //Add in local paths
            cur.close();
            cur = db.rawQuery("select * from local_files where file_id = " + sf.id, null);
            cur.moveToFirst();

            while(!cur.isAfterLast()) {
                sf.addLocal(cur.getString(cur.getColumnIndex("localpath")));
                cur.moveToNext();
            }
            cur.close();
        } else
            cur.close();

        db.setTransactionSuccessful();
        db.endTransaction();
        return sf;
    }

  /**
   * @param cur database cursor
   * @return a list of all servers known to this app
   */
    private Server[] collectServers(Cursor cur) {
        Server servers[] = new Server[cur.getCount()];
        int i = 0;
        cur.moveToFirst();

        while(!cur.isAfterLast()) {
            //TODO: Decryption of keys should be done on an as needed basis, not by default.
            byte[] uname = cur.getBlob(cur.getColumnIndex("username"));
            if(uname != null)
                uname = decryptText(uname);
            byte[] pass= cur.getBlob(cur.getColumnIndex("password"));
            if(pass != null)
                pass = decryptText(pass);
            servers[i++] = new Server(cur.getInt(cur.getColumnIndex("id")),
                                    cur.getInt(cur.getColumnIndex("type")),
                                    cur.getInt(cur.getColumnIndex("status")),
                                    cur.getString(cur.getColumnIndex("label")),
                                    cur.getString(cur.getColumnIndex("hostname")),
                                    cur.getString(cur.getColumnIndex("port")),
                                    uname,
                                    pass);
            cur.moveToNext();
        }
        cur.close();

        return servers;
    }

  /**
   * @return a list of all servers known to this application
   */
    public Server[] getAllServerInfo() {
        Cursor cur = db.rawQuery("select * from servers", null);
        return collectServers(cur);
    }

  /**
   * @return a list of all servers that are alive 
   */
    public Server[] getAllActiveServers() {
        Cursor cursor = db.rawQuery("select * from servers where status = " + STATUS_ACTIVE, null);
        return collectServers(cursor);
    }

  /**
   * @param fn filename
   * @return Array of all chunkmetas associated with this filename
   */
    public ChunkMeta[] getChunks(String fn) {

        Cursor cur = db.rawQuery("select id from files where name = ?", new String[]{ fn });
        cur.moveToFirst();
        if(!cur.isAfterLast())
        {
            long id = cur.getInt(cur.getColumnIndex("id"));
            cur.close();

            cur = db.rawQuery("SELECT name, virtual_id, physical_id "
                    +"FROM chunks_private "
                    +"WHERE file_id = " + id, null);

            ChunkMeta[] chunks = new ChunkMeta[cur.getCount()];
            cur.moveToFirst();

            int i = 0;
            while(!cur.isAfterLast()) {
                chunks[i++] = new ChunkMeta(cur.getString(cur.getColumnIndex("name")),
                        (int)cur.getLong(cur.getColumnIndex("virtual_id")),
                        (int)cur.getLong(cur.getColumnIndex("physical_id")));
                cur.moveToNext();
            }

            cur.close();
            return chunks;
        }
        else
            return null;
    }

    //not sure what to do with this method here. not even sure what its purpose is or what calls it
  /**
   * Sets the chunk metas for a given file
   * @param fid file id (row id in files table)
   * @param srvs all chunk metas for this file id
   */
    public void setChunks(int fid, ChunkMeta[] srvs) {
        db.beginTransaction();
        //Remove old servers
        SQLiteStatement stmt = db.compileStatement("DELETE FROM chunks_private WHERE file_id = ?");
        stmt.bindLong(1, fid);
        stmt.executeUpdateDelete();
        stmt.close();
        db.setTransactionSuccessful();
        db.endTransaction();
        db.beginTransaction();
        //Update with new servers
        for(ChunkMeta chunk : srvs) {
            stmt = db.compileStatement("INSERT INTO chunks_private(file_id, virtual_id, physical_id, name) VALUES (?, ?, ?, ?)");
            //maybe need to set physical id here too? or check if virtual id server is up? not sure
            stmt.bindLong(1, fid);
            stmt.bindLong(2, chunk.virtual);
            stmt.bindLong(3, chunk.physical);
            stmt.bindString(4, chunk.name);
            stmt.executeInsert();
            stmt.close();
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

  /**
   * Updates an existing chunk meta with a new one
   * @param chunk an updated chunk meta
   * @return The updated chunk meta
   */
    public ChunkMeta relocate(ChunkMetaPub chunk)
    {
        db.beginTransaction();
        Cursor cur = db.rawQuery("SELECT * FROM chunks_private WHERE chunk_id = " + chunk.chunkID, null);
        cur.moveToFirst();

        int fid = cur.getColumnIndex("file_id");
        int cID = cur.getColumnIndex("chunk_id");
        ChunkMeta relocated = new ChunkMeta(cur.getString(cur.getColumnIndex("name")),
                                        cur.getColumnIndex("virtual"),
                cur.getColumnIndex("virtual"));
        cur.close();

        SQLiteStatement stmt = db.compileStatement("DELETE FROM chunks_private WHERE name = ?");
        stmt.bindString(1, relocated.name);
        stmt.executeUpdateDelete();
        stmt.close();

        stmt = db.compileStatement("INSERT INTO chunks_private(file_id, virtual_id, physical_id, name, chunk_id) VALUES (?, ?, ?, ?, ?)");
        stmt.bindLong(1, fid);
        stmt.bindLong(2, relocated.virtual);
        stmt.bindLong(3, relocated.physical);
        stmt.bindString(4, relocated.name);
        stmt.bindLong(5, cID);
        stmt.executeUpdateDelete();
        stmt.close();
        db.setTransactionSuccessful();
        db.endTransaction();

        return relocated;
    }

  /**
   * Adds a new file to our database
   * @param fn filename
   * @param key file's key
   */
    public void newFile(String fn, byte[] key) {
        db.beginTransaction();
        SQLiteStatement stmt = db.compileStatement("insert into files values ((select max(id)+1 from files), ?, ?)");
        stmt.bindString(1, fn);
        stmt.bindBlob(2, encryptText(key));
        stmt.executeInsert();
        stmt.close();
        db.setTransactionSuccessful();
        db.endTransaction();
    }

  /**
   * Adds a new servers into the database with the given information.
   * Note: not all of it needs to be defined depending on the type implementation
   * @param type server type
   * @param label server label
   * @param host hostname
   * @param port port number
   * @param uname username
   * @param pass password
   */
    public void newServer(int type, String label, String host, String port, byte[] uname, byte[] pass) {
        db.beginTransaction();
        SQLiteStatement stmt = db.compileStatement("insert into servers (id, type, status, label, hostname, port, username, password) values ((select max(id)+1 from servers), ?, ?, ?, ? , ?, ? , ?)");
        stmt.bindLong(1, type);
        stmt.bindLong(2, STATUS_ACTIVE);
        stmt.bindString(3, label);
        if(host != null)
            stmt.bindString(4, host);
        if(port != null)
            stmt.bindString(5, port);
        if(uname != null) {
            uname = encryptText(uname);
            stmt.bindBlob(6, uname);
        }
        if(pass != null) {
            pass = encryptText(pass);
            stmt.bindBlob(7, pass);
        }
        stmt.executeInsert();
        stmt.close();
        db.setTransactionSuccessful();
        db.endTransaction();
    }

  /**
   * adds a local pathname to a given file
   * @param fid fileid
   * @param local local filename
   */
    public void addLocalFile(int fid, String local) {
        db.beginTransaction();
        Cursor cur = db.rawQuery("select * from local_files where file_id = " + fid + " and localpath = ?",
                new String[]{local});
        cur.moveToFirst();
        if(cur.isAfterLast()) {
            SQLiteStatement stmt = db.compileStatement("insert into local_files  values (?, ?)");
            stmt.bindLong(1, fid);
            stmt.bindString(2, local);
            stmt.executeInsert();
            stmt.close();
        }
        cur.close();
        db.setTransactionSuccessful();
        db.endTransaction();
    }

  /**
   * Deletes a file and a file's chunk metas from our database
   * @param filename filename
   * @return Array of chunk ids deleted
   */
    public ArrayList<Integer> deleteFile(String filename)
    {
        db.beginTransaction();
        Cursor cur = db.rawQuery("SELECT id FROM files WHERE name = ?", new String[]{filename});
        cur.moveToFirst();
        int fid = cur.getInt(cur.getColumnIndex("id"));
        cur.close();

        SQLiteStatement stmt = db.compileStatement("DELETE FROM files WHERE id = ?");
        stmt.bindLong(1, fid);
        stmt.executeUpdateDelete();
        stmt.close();

        cur = db.rawQuery("SELECT chunk_id FROM chunks_private WHERE file_id = " + fid, null);
        cur.moveToFirst();
        ArrayList<Integer> chunkHolder = new ArrayList<Integer>();
        while(!cur.isAfterLast())
        {
            chunkHolder.add(cur.getInt(cur.getColumnIndex("chunk_id")));
            cur.moveToNext();
        }
        cur.close();

        stmt = db.compileStatement("DELETE FROM chunks_private WHERE file_id = ?");
        stmt.bindLong(1, fid);
        stmt.executeUpdateDelete();
        stmt.close();
        db.setTransactionSuccessful();
        db.endTransaction();

       return chunkHolder;
    }

  /**
   * Removes a local pathname for a file
   * @param fid file id
   * @param local local pathname
   */ 
    public void removeLocalFile(int fid, String local) {
        db.beginTransaction();
        SQLiteStatement stmt = db.compileStatement("delete from local_files where file_id = ? and localpath = ?");
        stmt.bindLong(1, fid);
        stmt.bindString(2, local);
        stmt.executeUpdateDelete();
        stmt.close();
        db.setTransactionSuccessful();
        db.endTransaction();
    }

  /**
   * Updates a server with new information.
   * @param srv Server information
   */
    public void updateServer(Server srv) {
        updateServer(srv.id, srv.type, srv.status, srv.label, srv.hostname, srv.port, srv.uname, srv.pass);
    }

  /**
   * Updates a server with new information.
   * @param id server's id
   * @param type server type
   * @param status server's status
   * @param label server label
   * @param host hostname
   * @param port port number
   * @param uname username
   * @param pass password
   */
    public void updateServer(int id, int type, int status, String label, String host, String port, byte[] uname, byte[] pass) {
        db.beginTransaction();
        SQLiteStatement stmt = db.compileStatement("update servers set type = ?, status = ?, label = ?, hostname = ?, port = ?, username = ?, password = ? where id = ?");
        stmt.bindLong(1, type);
        stmt.bindLong(2, status);
        stmt.bindString(3, label);
        if(host != null)
            stmt.bindString(4, host);
        if(port != null)
            stmt.bindString(5, port);
        if(uname != null) {
            uname = encryptText(uname);
            stmt.bindBlob(6, uname);
        }
        if(pass != null) {
            pass = encryptText(pass);
            stmt.bindBlob(7, pass);
        }
        stmt.bindLong(8, id);
        stmt.execute();
        stmt.close();
        db.setTransactionSuccessful();
        db.endTransaction();
    }


  /** Encrypts text if needed for storage in the database
   * using the login password to derive the key.
   * Output Format:<br>
   *   _________________<br>
   *  | n-byte SALT     | <br>
   *  |-----------------|<br>
   *  | m-byte IV       |<br>
   *  |-----------------|<br>
   *  | cipher text     |<br>
   *  |-----------------|<br>
   *  | 16-byte MAC     |<br>
   *  |_________________|<br>
   * @param data binary data of the text
   * @return encrypted data of the plain text   
   */
    public byte[] encryptText(byte[] data) {
        Encryption encrypt = new Encryption();
        DerivedKeyGen keyGen = new DerivedKeyGen();

        byte[] pack = keyGen.generateKeyPackage(Session.password, 256);
        byte[] dkey = new byte[pack.length-DerivedKeyGen.SALT_SIZE];
        System.arraycopy(pack, DerivedKeyGen.SALT_SIZE, dkey, 0, dkey.length);

        dkey = encrypt.encrypt(data, dkey);

        byte[] ret = new byte[dkey.length + DerivedKeyGen.SALT_SIZE];
        System.arraycopy(pack, 0, ret, 0, DerivedKeyGen.SALT_SIZE);
        System.arraycopy(dkey, 0, ret, DerivedKeyGen.SALT_SIZE, dkey.length);
        return ret;
    }

  /**
   * Decrypts encrypted text in this database
   * using the login password to derive the key.
   * See encryptText(byte[]) for format of input data
   * @param data input encrypted text
   * @return binary form of the plain text
   */
    public byte[] decryptText(byte[] data) {
        Encryption decrypt = new Encryption();
        DerivedKeyGen keyGen = new DerivedKeyGen();
        byte[] salt = new byte[DerivedKeyGen.SALT_SIZE];
        byte[] ciph = new byte[data.length - salt.length];

        System.arraycopy(data, 0, salt, 0, salt.length);
        System.arraycopy(data, salt.length, ciph, 0, ciph.length);

        byte[] ret = decrypt.decrypt(ciph, keyGen.generateKey(Session.password, salt, 256));

        if(ret == null);
            //Todo: throw some error, this shouldn't be possible since it would mean the database is corrupted, but sql-cipher should check for that.
            //      unless the data was modified in memory post-decryption and not by us

        return ret;
    }
}
