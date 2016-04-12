package Tests;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.scar.android.Lock;
import com.scar.android.Server;
import com.scar.android.Session;

import net.sqlcipher.database.SQLiteOpenHelper;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
//import org.junit.Test;
import static org.mockito.Mockito.*;

/**
 * Created by Luke on 4/11/2016.
 */
public class ServerTests {

    //Ensures the wait time of the lock is five minutes
    @Mock
    Context context;

    @Mock
    SQLiteOpenHelper sql;

    @Mock
    SQLiteDatabase mockDB;

    @Mock
    Cursor curs;

    @Mock
    Activity mockActivity;

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
        int online=newServer.getStatus(mockActivity);
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
