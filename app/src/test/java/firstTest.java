import android.content.Context;

import com.scar.android.Lock;
import com.scar.android.Session;

import static org.junit.Assert.*;
import org.junit.Test;
//import org.junit.Test;
import static org.mockito.Mockito.*;

/**
 * Created by Luke on 4/11/2016.
 */
public class firstTest {

    //Ensures the wait time of the lock is five minutes
    Context context;
    @Test
    public void testSession()
    {
        //context = getInstrumentation().getContext();
        // create and configure mock
        Session newSession=new Session();
        Session.makeLock(context);
        long waitTime=newSession.remaining();
        assertEquals(waitTime,300000);
    }
}
