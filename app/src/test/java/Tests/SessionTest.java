package Tests;

import android.app.Activity;
import android.content.Context;

import com.scar.android.MetaData;
import com.scar.android.Session;
import com.scar.android.setDB;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.io.File;

import static org.mockito.Mockito.when;

/**
 * Created by Luke on 4/15/2016.
 */
public class SessionTest {
    Activity act= Mockito.mock(Activity.class);

    //@Mock
    net.sqlcipher.database.SQLiteDatabase mockDB=Mockito.mock(net.sqlcipher.database.SQLiteDatabase.class);


    net.sqlcipher.database.SQLiteStatement mockStatement=Mockito.mock(net.sqlcipher.database.SQLiteStatement.class);

    @Mock
    int fid;

    @Mock
    String local;

    @Mock
    net.sqlcipher.Cursor mockCursor;

    File mockFile= Mockito.mock(File.class);

    //@Mock
    setDB newDB=Mockito.mock(setDB.class);

    Context mockContext=Mockito.mock(Context.class);


    @Mock
    int k;

    String key="123456";
    String dbname="DataBase";

    MetaData newMeta;
    @Before
    public void setup()
    {
        //newMeta= new MetaData(act,dbname,key);
        when(act.getDatabasePath(dbname)).thenReturn(mockFile);
        when(newDB.getDb()).thenReturn(mockDB);
        when(mockDB.compileStatement("delete from local_files where file_id = ? and localpath = ?")).thenReturn(mockStatement);
        when(mockDB.rawQuery("select * from local_files where file_id = " + fid + " and localpath = ?",
                new String[]{local})).thenReturn(mockCursor);
        when(mockDB.rawQuery("select * from servers where status = " + 0, null)).thenReturn(mockCursor);
        when(mockDB.rawQuery("SELECT id FROM files WHERE name = ?", new String[]{dbname})).thenReturn(mockCursor);
        when(mockDB.rawQuery("select * from local_files where file_id = " + k + " and localpath = ?",
                new String[]{dbname})).thenReturn(mockCursor);
    }

    //Ensures that when created the session is valid
    @Test
    public void sessionCreationTestPass()
    {
        newMeta= new MetaData(act,dbname,key,newDB);
        byte[] password={1,0,1,0,1,0,2,0,3};
        Session.init(newMeta, password, act);
        assertTrue(Session.valid());
    }

    //Ensures that the Session is set as false
    @Test
    public void sessionCreationTestFail()
    {
        newMeta= new MetaData(act,dbname,key,newDB);
        assertFalse(Session.valid());
    }

    //Ensures that you can create a Session and add to it to make it valid
    //Then when you clear it, it makes the session invalid
    @Test
    public void sessionCreationTestClear()
    {
        newMeta= new MetaData(act,dbname,key,newDB);
        byte[] password={1,0,1,0,1,0,2,0,3};
        Session.init(newMeta, password, act);
        assertTrue(Session.valid());
        Session.clear();
        assertFalse(Session.valid());
    }

    @Test
    public void sessionSetLockTest()
    {
        newMeta= new MetaData(act,dbname,key,newDB);
        byte[] password={1,0,1,0,1,0,2,0,3};
        Session.init(newMeta, password, act);
        Session.makeLock(mockContext);

    }

}
