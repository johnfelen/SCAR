import com.datastax.driver.core.*;
import com.datastax.driver.core.exceptions.InvalidQueryException;
import com.datastax.driver.core.utils.UUIDs;
import com.datastax.driver.osgi.api.MailboxException;
import com.datastax.driver.osgi.api.MailboxMessage;
import com.datastax.driver.osgi.api.MailboxService;

import static com.datastax.driver.core.querybuilder.QueryBuilder.bindMarker;
import static com.datastax.driver.core.querybuilder.QueryBuilder.delete;
import static com.datastax.driver.core.querybuilder.QueryBuilder.eq;
import static com.datastax.driver.core.querybuilder.QueryBuilder.insertInto;
import static com.datastax.driver.core.querybuilder.QueryBuilder.select;

public class Cassandra implements IServer {
    public static final String CLASSTAG = ??; //Mysql.class.getSimpleName();
    public String dbserver;
    public String database = "scar_db" ;
    public String userName;
    public String pwd;
    public String dbDriver = "com.mysql.jdbc.Driver";
    public String dbPrefix = "jdbc:mysql://";
    public String dbConnect;
    public Connection conn = null;
    public Statement stmt = null;
    public boolean isConnected = false;

    public Cassandra (String dbserver, String username, String pwd){
        this.dbserver=dbserver;
        this.userName=userName;
        this.pwd=pwd;
        dbConnect = dbPrefix + dbserver + "/" + database;
        tryConnect();
    }

    public void tryConnect(){
        try{

        }
        catch{

        }
    }

    public boolean isConnected(){
        return isConnected;
    }

    public retType executeQuery(String sql){

    }

    publlic retType executeUpdate(String sql){

    }

    public void closeStmt(){

    }

    public void closeConn(){

    }

    //Data Storage with input fn as key
    public void storeData(String fn, byte[] data){

    }

    //Retrieve Data with fn as key
    public byte[] getData(String fn){

    }
}