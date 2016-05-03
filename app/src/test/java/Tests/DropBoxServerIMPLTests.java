package Tests;
import android.app.Activity;

import com.scar.android.FileSaveUtil;
import com.scar.android.ServerImpl.DropBox;

import org.junit.Test;
import org.mockito.Mock;

import java.io.File;

import static org.junit.Assert.*;
/**
 * Created by Luke on 4/17/2016.
 */
public class DropBoxServerIMPLTests {

    @Mock
    File file;
    //UNSURE IF THIS IS SUPPOSED TO HAPPEN
    //assumed that it would return false since
    //there was no connection command entered
    byte[] data={1,2,6,0,1,3,7,2};

    @Test
    public void DropBoxInitializeConnectionsFalse()
    {
        DropBox newDropBox= new DropBox("Test");
        assertTrue(newDropBox.getStatus());
    }

    //Makes sure that the delete file command returns false
    //rather than crash when there is no file to delete
    @Test
    public void deleteFileFail()
    {
        DropBox newDropBox= new DropBox("Test");
        assertFalse(newDropBox.deleteFile("Tester"));
    }


    //returns null when the asked to return data
    @Test
    public void getDataTestReturnNull()
    {
        DropBox newDropBox= new DropBox("Test");
        assertNull(newDropBox.getData("Hello"));
    }

    @Test
    public void testDeleteFile()
    {
        DropBox newDropBox= new DropBox("Test");
        String filename="Testing";
        //newDropBox.connect();
        assertFalse(newDropBox.storeData(filename, data));

    }

}
