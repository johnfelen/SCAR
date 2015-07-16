import com.datastax.driver.core.*;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Host;
import com.datastax.driver.core.Metadata;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;

public class Cassandra implements IServer {
    private Cluster cluster;            //Cassandra architecture.  Entry point to access data
    private Session session;            //object that is manipulated to store/access data

    public boolean isConnected = false;

    //get the session reference
    public Session getSession(){
        return this.session;
    }

    //connect to cluster
    public void connect(String node){
        try {
            cluster = Cluster.builer().addContactPoint(node).build();
            isConnected = true;

            session = cluster.connect();
        }
        catch (Exception ex){
            ex.printStackTrace();
            isConnected = false;
        }
    }

    //close connections
    public void close(){
        session.close();
        cluster.close();
    }

    //Data Storage with input fn as key
    public void storeData(String fn, byte[] data){
        //insert data into table
        session.execute("INSERT INTO /tableName/ (columns) " +
                "VALUES (" +
                "data'" +
                ";");
        //repeat for multiple tables
    }

    //Retrieve Data with fn as key
    public byte[] getData(String fn){
        //query a table for data and store the rows
        ResultSet results = session.execute("SELECT * FROM /tableName/ " +
                "WHERE /parameters/");

        for (Row row : results){
            //iterate over the rows in the query results.  Use row.getString(/colName/) to get data
        }
    }
}
