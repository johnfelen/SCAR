package Tests;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.scar.android.Fragments.DropBoxStore;
import com.scar.android.Fragments.GenericStore;
import com.scar.android.Fragments.SQLiteStore;
import com.scar.android.ScarFile;
import com.scar.android.StoreFrag;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;
/**
 * Created by Luke on 4/17/2016.
 */
public class SQLiteStoreTest extends SQLiteStore {
    String label="LabelTest";
    String host="HostTest";
    String port="PortTest";

    byte[] username={1,3,5,1,2,31,0,5,7,3};
    byte[] password={2,5,3,6,8,4,2,4,6,8,2,1,3,56};

    private Intent newIntent;


    //This test should not pass.
    //This just shows that the program is incomplete
    @Test
    public void StoreSQLiteTests()
    {
        this.setPassword(password);
        this.setPort(port);
        this.setUsername(username);

        assertNull(this.getPort());
        assertNull(this.getPassword());
        assertNull(this.getUsername());

    }


    //Shows that no matter what get status always returns true
    @Test
    public void StoreStatusTest()
    {
        assertTrue(this.getStatus());
    }
}
