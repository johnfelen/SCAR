package Tests;

import com.scar.android.ScarFile;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Created by Luke on 4/15/2016.
 */
public class ScarFileTest {

    ArrayList<String> tester=new ArrayList<String>();

    //Makes sure that the scarTest is created
    @Test
    public void ScarTest()
    {
        int id=1;
        String name="Scar";
        ScarFile newScar=new ScarFile(id,name);
        String tester=newScar.getFilename();
        assertTrue(tester.equals(name));
    }


    //Ensures that the ID passed in is the ID that is returned
    @Test
    public void ScarIDTest()
    {
        int id=1;
        String name="Scar";
        ScarFile newScar=new ScarFile(id,name);
        assertTrue(newScar.id==(id));
    }

    //Test to ensure that all the paths passed in are there
    @Test
    public void getLocalPathsTest()
    {
        for(int i=0;i<500;++i)
        {
            tester.add(Integer.toString(i));
        }
        int id=1;
        String name="Scar";
        ScarFile newScar=new ScarFile(id,name);
        ArrayList<String> scarArray=newScar.getLocalpaths();
        for(int i=0;i<scarArray.size();++i)
        {
            assertTrue(scarArray.get(i).equals(tester.get(i)));
        }

    }
}
