package Tests;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.scar.android.Lock;
import com.scar.android.Server;
import com.scar.android.Session;
import com.scar.android.setDB;

import net.sqlcipher.database.SQLiteOpenHelper;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
//import org.junit.Test;
import java.io.File;

import static org.mockito.Mockito.*;

/**
 * Created by Luke on 4/11/2016.
 */
public class ServerTests {

    //Ensures the wait time of the lock is five minutes
    @Mock
    Context context;

    //android.database.sqlite.SQLiteDatabase
    @Mock
    SQLiteOpenHelper sql;

    @Mock
    net.sqlcipher.database.SQLiteDatabase mockDB=Mockito.mock(net.sqlcipher.database.SQLiteDatabase.class);

    @Mock
    net.sqlcipher.Cursor mockCursor;

    @Mock
    Cursor curs;

    @Mock
    Activity act;

    File mockFile= Mockito.mock(File.class);

    @Mock
    int k;
    //@Mock
    setDB newDB=Mockito.mock(setDB.class);

    @Mock
    int fid;

    net.sqlcipher.database.SQLiteStatement mockStatement=Mockito.mock(net.sqlcipher.database.SQLiteStatement.class);

    @Mock
    String local;

    String key="123456";
    String dbname="DataBase";

    @Before
    public void setup()
    {
        //newMeta= new MetaData(act,dbname,key);
        //when(act.getDatabasePath(dbname)).thenReturn(mockFile);
        when(newDB.getDb()).thenReturn(mockDB);
        when(mockDB.compileStatement("delete from local_files where file_id = ? and localpath = ?")).thenReturn(mockStatement);
        when(mockDB.rawQuery("select * from local_files where file_id = " + fid + " and localpath = ?",
                new String[]{local})).thenReturn(mockCursor);
        when(mockDB.rawQuery("select * from servers where status = " + 0, null)).thenReturn(mockCursor);
        when(mockDB.rawQuery("SELECT id FROM files WHERE name = ?", new String[]{dbname})).thenReturn(mockCursor);
        when(mockDB.rawQuery("select * from local_files where file_id = " + k + " and localpath = ?",
                new String[]{dbname})).thenReturn(mockCursor);

    }



    @Test
    public void testServerInitializingManual()
    {
        int one=1;
        int two=2;
        int three=3;
        String first="Test1";
        String second="Test2";
        String third="Test3";
        byte[] firstByte=new byte[20];
        byte[] secondByte=new byte[20];

        Server newServer=new Server(one,two,three,first,second,third,firstByte,secondByte);
        assertEquals(newServer.id,one);
        assertEquals(newServer.type, two);
        assertEquals(newServer.status, three);
        assertTrue(newServer.label.equals(first));
        assertTrue(newServer.hostname.equals(second));
        assertTrue(newServer.port.equals(third));
        assertEquals(newServer.uname, firstByte);
        assertEquals(newServer.pass,secondByte);
    }

    @Test
    public void serverBundleTest()
    {
        int one=1;
        int two=2;
        int three=3;
        String first="Test1";
        String second="Test2";
        String third="Test3";
        byte[] firstByte=new byte[20];
        byte[] secondByte=new byte[20];

        Server newServer=new Server(one,two,three,first,second,third,firstByte,secondByte);
        Bundle newBundle=new Bundle(newServer.bundle());
        Server bundleServer=new Server(newBundle);
        assertEquals(newServer.id,one);
        assertEquals(newServer.type, two);
        assertEquals(newServer.status, three);
        assertTrue(newServer.label.equals(first));
        assertTrue(newServer.hostname.equals(second));
        assertTrue(newServer.port.equals(third));
        assertEquals(newServer.uname, firstByte);
        assertEquals(newServer.pass,secondByte);
    }

    /*@Test
    public void testServerStatus()
    {
        int one=1;
        int two=2;
        int three=3;
        String first="Test1";
        String second="Test2";
        String third="Test3";
        byte[] firstByte=new byte[20];
        byte[] secondByte=new byte[20];

        Server newServer=new Server(one,two,three,first,second,third,firstByte,secondByte);
        int online=newServer.getStatus(act);
        assertEquals(online,1);
    }*/

    @Test
    public void testServerId()
    {
        int one=1;
        int two=2;
        int three=3;
        String first="Test1";
        String second="Test2";
        String third="Test3";
        byte[] firstByte=new byte[20];
        byte[] secondByte=new byte[20];

        Server newServer=new Server(one,two,three,first,second,third,firstByte,secondByte);
        String get=newServer.toString();
        assertTrue(newServer.label.equals(get));
    }
}
