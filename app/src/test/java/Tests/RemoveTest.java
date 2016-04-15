package Tests;


import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.test.mock.MockCursor;
import com.scar.android.Lock;
import com.scar.android.MetaData;
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
 * Created by Luke on 4/12/2016.
 */
public class RemoveTest {

    //@Mock
    Activity act=Mockito.mock(Activity.class);

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

    //Ensures MetaData is created and valid
    @Test
    public void testMeatCreate()
    {

        newMeta= new MetaData(act,dbname,key,newDB);
        assertTrue(newMeta.valid());
    }

}
