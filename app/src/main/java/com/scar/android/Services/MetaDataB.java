package com.scar.android.Services;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import com.scar.android.ScarFile;
import com.scar.android.Server;

import java.io.File;
import java.util.ArrayList;

import scar.ChunkMeta;


/**
 * Created by Spencer on 3/29/2016.
 */
public class MetaDataB extends SQLiteOpenHelper{
    //need to change type of SQL used
    public static final int
            TYPE_MYSQL_STORE = 0,
            TYPE_CASS_STORE = 1,
            TYPE_SQLITE_STORE = 2,
    //TYPE_LOCALFILE_STORE = 3,
    TYPE_DROPBOX_STORE = 3,
            TYPE_GDRIVE_STORE = 4,

    STATUS_ACTIVE = 0,
            STATUS_DISABLE = 1;

    private final String dbname;

    public MetaDataB(Context con, String dbnm) {
        super(con, dbnm, null, 1);
        dbname = dbnm;
    }

    public void onUpgrade (SQLiteDatabase db, int oldVersion, int newVersion)
    {

    }
    public void onCreate(SQLiteDatabase db){

                db.execSQL("CREATE TABLE IF NOT EXISTS servers ("
                +"id INTEGER,"
                +"status INTEGER,"
                +"type INTEGER,"
                +"label TEXT,"
                +"hostname TEXT,"
                +"port TEXT,"
                +"PRIMARY KEY(id),"
                +"FOREIGN KEY(id) REFERENCES chunks_public(physical_id))");

        //new table for chunks: file id, name, virtual id (int), physical id (int) points to server ID, chunk ID
        //separate database for scheduler without some of the fields.
        db.execSQL("CREATE TABLE IF NOT EXISTS chunks_public ("
                +"virtual_id INTEGER,"
                +"physical_id INTEGER,"
                +"chunk_id INTEGER," //chunk id
                +"PRIMARY KEY(chunk_id),"
                +"FOREIGN KEY(physical_id) REFERENCES servers(id))");

    }


    // Puts the database back into a state of initial creation
    public void clean() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        SQLiteStatement stmt = db.compileStatement("delete from local_files");
        stmt.execute();
        stmt.close();
        stmt = db.compileStatement("delete from chunks_public");
        stmt.execute();
        stmt.close();
        stmt = db.compileStatement("delete from servers");
        stmt.execute();
        stmt.close();
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    /*
    public void close() {
        db.close();
    }
    */
    //Ensures the MetaData is still valid
    /*
    public boolean valid()
    {
        return db != null && dbname != null;
    }
    */

    public void deleteFile(ArrayList<Integer> chunkIds)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        for(int i = 0; i < chunkIds.size(); i++)
        {
            SQLiteStatement stmt = db.compileStatement("DELETE FROM chunks_public WHERE chunk_id = ?");
            stmt.bindLong(1, chunkIds.get(i));
            stmt.executeUpdateDelete();
            stmt.close();
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public ChunkMeta[] getChunks()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery("SELECT virtual_id, physical_id "
                +"FROM chunks_public", null);

        ChunkMeta[] chunks = new ChunkMeta[cur.getCount()];
        cur.moveToFirst();

        int i = 0;
        while(!cur.isAfterLast())
        {
            chunks[i++] = new ChunkMeta("empty",
                    (int)cur.getLong(cur.getColumnIndex("virtual_id")),
                    (int)cur.getLong(cur.getColumnIndex("physical_id")));
            cur.moveToNext();
        }

        cur.close();
        return chunks;
    }

    //not sure what to do about this method since we do not have username/password
    private Server[] collectServers(Cursor cur) {
        Server servers[] = new Server[cur.getCount()];
        int i = 0;
        cur.moveToFirst();

        while(!cur.isAfterLast()) {
            servers[i++] = new Server(cur.getInt(cur.getColumnIndex("id")),
                    cur.getInt(cur.getColumnIndex("type")),
                    cur.getInt(cur.getColumnIndex("status")),
                    cur.getString(cur.getColumnIndex("label")),
                    cur.getString(cur.getColumnIndex("hostname")),
                    cur.getString(cur.getColumnIndex("port")),
                    null, null);
            cur.moveToNext();
        }
        cur.close();

        return servers;
    }

    //get all chunks

    public Server[] getAllServerInfo()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery("select * from servers", null);
        return collectServers(cur);
    }

    //instead supply filename and return chunks rename as GETCHUNKS

    //not sure what to do with this method here. not even sure what its purpose is or what calls it
    public void setChunks(ChunkMeta[] srvs) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        //Update with new chunks
        for(ChunkMeta chunk : srvs) {
            SQLiteStatement stmt = db.compileStatement("INSERT INTO chunks_public(virtual_id, physical_id) VALUES (?, ?)");
            stmt.bindLong(1, chunk.virtual);
            stmt.bindLong(2, chunk.physical);
            stmt.executeInsert();
            stmt.close();
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public void newServer(int type, String host, String port) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        SQLiteStatement stmt = db.compileStatement("insert into servers (id, type, hostname, port) values ((select max(id)+1 from servers), ?, ? , ?)");
        stmt.bindLong(1, type);
        if(host != null)
            stmt.bindString(2, host);
        if(port != null)
            stmt.bindString(3, port);

        stmt.executeInsert();
        stmt.close();
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public void updateServer(Server srv) {
        updateServer(srv.id, srv.type, srv.status, srv.label, srv.hostname, srv.port);
    }

    public void updateServer(int id, int type, int status, String label, String host, String port) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        SQLiteStatement stmt = db.compileStatement("update servers set type = ?, status = ?, label = ?, hostname = ?, port = ? where id = ?");
        stmt.bindLong(1, type);
        stmt.bindLong(2, status);
        stmt.bindString(3, label);
        if(host != null)
            stmt.bindString(4, host);
        if(port != null)
            stmt.bindString(5, port);

        stmt.bindLong(6, id);
        stmt.execute();
        stmt.close();
        db.setTransactionSuccessful();
        db.endTransaction();
    }
}
