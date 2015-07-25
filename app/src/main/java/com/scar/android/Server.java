package com.scar.android;

import android.os.Bundle;

/* Contains all information about a specific server in the db
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
    public int getStatus() {
        if(status == MetaData.STATUS_DISABLE)
            return DISABLED;
        //Todo: test status of this server
        return ONLINE;
    }

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
