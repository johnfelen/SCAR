package Tests;

import android.app.Activity;

import com.scar.android.FileSaveUtil;

import org.junit.Test;
import org.mockito.Mock;
import static org.junit.Assert.*;
/**
 * Created by Luke on 4/15/2016.
 */
public class FileSaveUtilTest {

    String name;
    String id;
    //public static byte[] data;

    @Mock
    Activity act;


    //Ensures the File is stored with the correct name and format
    //Stored file in this example is SCAR\Picture.png
    @Test
    public void FileSaveUtilTester()
    {
        name="Picture";
        id="png";
        byte[] data={1,0,1,0,1,0,2,0,3};
        FileSaveUtil newSaver=new FileSaveUtil(name,id,data);
        assertTrue(newSaver.save(act).equals("SCAR\\Picture.png"));
    }
}
