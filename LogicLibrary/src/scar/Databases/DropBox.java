import com.dropbox.core.*;
import java.io.*;
import java.lang.*;
import java.util.Locale;

public class DropBox{
  
  final String APP_KEY = "INSERT_KEY";            //Given by DropBox when app is registered
  final String APP_SECRET = "INSERT_SECRET";      //Given by DropBox when app is registered
  private String authURL;                         //URL for authorization page
  private String code;                            //app code from URL
  private String accessToken;                     //token for accessing app's Dropbox.  NEED TO SAVE!!!
  private DbxClient client;                       //Dropbox client for storage and retrieval
  
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
    }catch (Exception ex){
      System.out.println("Unable to connet to client!");
    }
  }
  
  //using fn to store chucks in Dropbox
  public void dbStore(String fn, byte[] chunks){
    ByteArrayInputStream byteStream = new ByteArrayInputStream(chunks);
    
    try{
      DbxEntry.File upFile = client.uploadFile("/" + fn, DbxWriteMode.add(), chunks.length, byteStream);
      System.out.println("Uploaded: " + upFile.toString());
    } finally {
      byteStream.close();
    }
  }
  
  public byte[] dbGet(String fn){
    ByteArrayOUtputStream byteStream = new ByteArrayOutputStream();
    byte[] chunks;
    
    //unsure if implementation/syntax is right on this
    try{
      DbxEntry.File downFile = client.getFile("/" + fn, null, byteStream);
      chunks = byteStream.toByteArray();
      System.out.println("Metadata: " + downFile.toString());
    } finally{
      byteStream.close();
    }
    
    return chunks;
  }
}
