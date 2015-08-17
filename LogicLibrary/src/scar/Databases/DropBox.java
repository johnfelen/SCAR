import com.dropbox.core.*;
import java.io.*;
import java.util.Locale;

public class DropBox{
  
  final String APP_KEY = "INSERT_KEY";            //Given by DropBox when app is registered
  final String APP_SECRET = "INSERT_SECRET";      //Given by DropBox when app is registered
  private String authURL;                         //URL for authorization page
  private String code;                            //app code from URL
  private String accessToken;                     //token for accessing app's Dropbox.  NEED TO SAVE!!!
  private DbxClient client;                              //Dropbox client for storage and retrieval
  
  public void connect() throws IOException, DbxException{
    //begin OAuth process
    DbxAppInfo appInfo = new DbxAppInfo(APP_KEY, APP_SECRET);
    DbxRequestConfig config = new DbxRequestConfig("appName/Ver", Locale.getDefault().toString());
    DbxWebAuthNoRedirect webAuth = new DbxWebAuthNoRedirect(config, appInfo);
    
    authURL = webAuth.start();        //create authorization URL
    /*
    Code to handle redirects to and from authorization page
    */
    code = new BufferedReader(new InputStreamReader(System.in)).readLine().trim();    //read in code given by Auth URL
    
    DbxAuthFinish authFinish = webAuth.finish(code);        //use code to complete OAuth workflow
    accessToken = authFinish.accessToken;                   //store created token for app
    
    try{
      client = new DbxClient(config, accessToken);
      System.out.println("Linked Account: " + client.getAccountInfo().displayName);
    }
    catch (Exception ex){
      System.out.println("Unable to connet to client!");
    }
  }
  
  public void dbStore(){
  
  }
  
  public void dbGet(){
  
  }
}
