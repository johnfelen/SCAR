package com.scar.android.ServerImpl;


import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AppKeyPair;

import java.io.*;
import java.lang.*;

public class DropBox implements scar.IServer {
    public static final String APP_KEY = "INSERT_KEY";            //Given by DropBox when app is registered
    public static final String APP_SECRET = "INSERT_SECRET";      //Given by DropBox when app is registered

    //URL for authorization page
    private String authURL;
    //app code from URL
    private String code;
    //token for accessing app's Dropbox.
    private String accessToken;
    //Dropbox client for storage and retrieval
    private DropboxAPI<AndroidAuthSession> client;
    private boolean connected;

    public DropBox(final String token) {
        accessToken = token;
        connected = false;
    }

    public boolean connect() {
        try {
            AppKeyPair keyPair = new AppKeyPair(APP_KEY, APP_SECRET);
            AndroidAuthSession session = new AndroidAuthSession(keyPair);
            session.setOAuth2AccessToken(accessToken);

            client = new DropboxAPI<AndroidAuthSession>(session);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

  /*
  public void connect() throws IOException, DbxException{
    //begin OAuth process
    DbxAppInfo appInfo = new DbxAppInfo(APP_KEY, APP_SECRET);
    DbxRequestConfig config = new DbxRequestConfig("appName/Ver", Locale.getDefault().toString());
    DbxWebAuthNoRedirect webAuth = new DbxWebAuthNoRedirect(config, appInfo);

    authURL = webAuth.start();        //create authorization URL

    //Code to handle redirects to and from authorization page

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
  */

    public void close() {
    /* TODO: Disconnect a session */
    }

    public boolean getStatus() {
    /* TODO: Try to connect to see if it's reachable */
        if (connected)
            return true;
        connected = connect();
        return connected;
    }

    //using fn to store chucks in Dropbox
    public void storeData(String fn, byte[] chunks) {
        ByteArrayInputStream byteStream = new ByteArrayInputStream(chunks);

        try {
            client.putFileOverwriteRequest("/" + fn, byteStream, chunks.length, null);
        } catch (Exception e) {
            //return false;
        }
        //return true;
    }

    public byte[] getData(String fn) {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        byte[] chunks;

        //unsure if implementation/syntax is right on this
        try {
            client.getFile("/" + fn, null, byteStream, null);
            chunks = byteStream.toByteArray();
        } catch (Exception e) {
            //failed to fetch files
            //TODO: handle cases where access token is no longer valid/ user unlinked their account -> Reauth user and get new token
            return null;
        }

        return chunks;
    }
}
