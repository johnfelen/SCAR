import com.datastax.driver.core.*;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Host;
import com.datastax.driver.core.Metadata;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;

/* Table Name scar_files
 * Columns: 'key' - varchar(32), 'data' - bytes[]/BLOB
 *
 * 'key' = the filename hash used to get the file when requested (fn)
 * 'data' = our chunk data (data)
 */

public class Cassandra implements IServer {
    private Cluster cluster;            //Cassandra architecture.  Entry point to access data
    private Session session;            //object that is manipulated to store/access data

    public boolean isConnected = false;

    //get the session reference
    public Session getSession(){
        return this.session;
    }

    //connect to cluster.  node = IP Address
    public void connect(String node){
        try {
            cluster = Cluster.builer().addContactPoint(node).build();
            isConnected = true;
            //get connection metadata to see what we are connecting to
            Metadata metadata = cluster.getMetadata();
            System.out.printf("Connected to cluster: %s\n", metadata.getClusterName());
            
            //store session for manipulation
            session = cluster.connect();
        }
        catch (Exception ex){
            //failed connection
            System.out.println("Unable to connect to Cassandra cluster.  You fail at life!");
            ex.printStackTrace();
            isConnected = false;
        }
    }

    //close connections
    public void close(){
        session.close();
        cluster.close();
    }
    
    //Current implementation assumes byte[] stored/retrieved as a whole. 
    //Unsure if byte[] needs to be separated by "chunks" of data.
    //If so, are we storing across multiple rows?  multiple clusters?

    //Data Storage with input fn as key
    public void storeData(String fn, byte[] chunks){
        //insert data into table
        session.execute("INSERT INTO scar_files (key, data) VALUES (fn, chunks);");
    }

    //Retrieve Data with fn as key
    public byte[] getData(String fn){
        //query a table for data and store the rows
        ResultSet results = session.execute("SELECT * FROM scar_files WHERE key = fn;");

        for (Row row : results){
            return row.getBytes("data");
        }
    }
}
