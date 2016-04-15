package Tests;

import com.scar.android.ScarFile;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Created by Luke on 4/15/2016.
 */
public class ScarFileTest {
    @Test
    public void ScarTest()
    {
        int id=1;
        String name="Scar";
        ScarFile newScar=new ScarFile(id,name);
        String tester=newScar.getFilename();
        assertTrue(tester.equals(name));
    }

    @Test
    public void ScarIDTest()
    {
        int id=1;
        String name="Scar";
        ScarFile newScar=new ScarFile(id,name);
        assertTrue(newScar.id==(id));
    }

    @Test
    public void getLocalPathsTest()
    {
        String tester1="s";
        String tester2="New";
        String tester3="Brand";
        int id=1;
        String name="Scar";
        ScarFile newScar=new ScarFile(id,name);
        newScar.addLocal(tester1);
        newScar.addLocal(tester2);
        newScar.addLocal(tester3);
        ArrayList<String> scarArray=newScar.getLocalpaths();
        for(int i=0;i<scarArray.size();++i)
        {
            if(i==0)
            {
                assertTrue(scarArray.get(i).equals(tester1));
            }
            else if(i==1)
            {
                assertTrue(scarArray.get(i).equals(tester2));
            }
            else if(i==2)
            {
                assertTrue(scarArray.get(i).equals(tester3));
            }
        }

    }
}
