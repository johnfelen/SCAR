package com.scar.android;

/* Contains all information about a specific server in the db
 */
public class Server {
    public  int type, status; //See MetaData constants for types/statuses

    public String hostname, port, uname, pass, label;

    public Server(int type, int status,String l, String h, String p, String u, String pa) {
        this.type = type;
        this.status = status;
        label = l;
        hostname = h;
        port = p;
        uname = u;
        pass = pa;
    }

    public String toString() {
        return label;
    }
}
