package com.scar.android;

import android.app.Activity;
import android.os.Bundle;

import com.scar.android.ServerImpl.DropBox;
import com.scar.android.ServerImpl.SQLiteStore;

import java.net.InetAddress;

import scar.IServer;

/**
 *  Contains all information about a specific server in the db
 */
public class Server {
    public static final int ONLINE = 0, OFFLINE = 1, DISABLED = 2;
    public final int id;
    public int type, status; //See MetaData constants for types/statuses

    public String hostname, port, label;
    public byte[] uname, pass;

    public Server(int id, int type, int status,String l, String h, String p, byte[] u, byte[] pa) {
        this.id = id;
        this.type = type;
        this.status = status;
        label = l;
        hostname = h;
        port = p;
        uname = u;
        pass = pa;
    }

    public Server(Bundle args){
        id = args.getInt("id");
        type = args.getInt("type");
        status = args.getInt("status");
        label = args.getString("lbl");
        hostname = args.getString("host");
        port = args.getString("port");
        uname = args.getByteArray("uname");
        pass = args.getByteArray("pass");
    }

    public String toString() {
        return label;
    }

  /**
   * Get status of a server
   * @param act App's activity to allow for pinging a server
   */
    public int getStatus(Activity act) {
        if(status == MetaData.STATUS_DISABLE) return DISABLED;
        IServer srv = getActual(act);
        if(srv == null) return OFFLINE;
        else {
            if(!srv.getStatus()) {
                srv.close();
                return OFFLINE;
            }
            srv.close();
        }
        return ONLINE;
    }

  /**
   * Returns the actual server backing for this server for use in storing and retrival
   * @param act App's activity to allow for this
   */
    public IServer getActual(Activity act) {
        IServer srv = null;
        switch(type) {
            case MetaData.TYPE_MYSQL_STORE:
                break;
            case MetaData.TYPE_CASS_STORE:
                break;
            case MetaData.TYPE_SQLITE_STORE:
                srv = new SQLiteStore(act, hostname);
                break;
            case MetaData.TYPE_DROPBOX_STORE:
                srv = new DropBox(label);
                break;
        }
        return srv;
    }

  /**
   * Capture all this server's information into a bundle for later use
   */
    public Bundle bundle() {
        Bundle bun = new Bundle();
        bun.putString("host", hostname);
        bun.putString("port", port);
        bun.putByteArray("uname", uname);
        bun.putByteArray("pass", pass);
        bun.putString("lbl", label);
        bun.putInt("id", id);
        bun.putInt("type", type);
        bun.putInt("status", status);
        return bun;
    }
}
