package Tests;


import com.scar.android.Fragments.DropBoxStore;
import com.scar.android.ScarFile;
import com.scar.android.StoreFrag;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;
/**
 * Created by Luke on 4/17/2016.
 */
public class DropBoxStoreTests extends DropBoxStore{

    String label="LabelTest";
    String host="HostTest";
    String port="PortTest";

    byte[] username={1,3,5,1,2,31,0,5,7,3};
    byte[] password={2,5,3,6,8,4,2,4,6,8,2,1,3,56};

    @Test
    public void StoreFragItemTests()
    {
        //super.onStart();
        this.setHost(host);
        //this.setLabel(label);
        this.setPassword(password);
        this.setPort(port);
        this.setUsername(username);
        //String labelTest=this.getLabel();
        //String hostTest=this.getHost();
        //String portTest=this.getPort();
        //byte[] passwordTest=this.getPassword();
        //byte[] usernameTest=this.getUsername();
        //assertTrue(labelTest.equals(label));
        assertNull(this.getHost());
        assertNull(this.getPort());
        assertNull(this.getPassword());
        assertNull(this.getUsername());

    }

    @Test
    public void StoreStatusTest()
    {
        assertTrue(this.getStatus());
    }
}
