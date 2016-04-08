package com.scar.android.ServerImpl;


import android.util.Log;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AppKeyPair;

import java.io.*;
import java.lang.*;

public class DropBox implements scar.IServer {
    public static final String APP_KEY = "waqkrdbshgbrwt0";            //Given by DropBox when app is registered
    public static final String APP_SECRET = "fux54cxmkmi9695";      //Given by DropBox when app is registered

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

    public int id()
    {
        return 0;
    }

    public void connect() {
        try {
            AppKeyPair keyPair = new AppKeyPair(APP_KEY, APP_SECRET);
            AndroidAuthSession session = new AndroidAuthSession(keyPair);
            session.setOAuth2AccessToken(accessToken);

            client = new DropboxAPI<AndroidAuthSession>(session);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() {
        if(client != null && client.getSession() != null && client.getSession().isLinked())
            client.getSession().unlink();
    }

    public boolean getStatus() {
        if(client != null && client.getSession() != null && client.getSession().isLinked())
            return true;
        connect();
        return client != null && client.getSession() != null && client.getSession().isLinked();
    }

    //using fn to store chucks in Dropbox
    public boolean storeData(String fn, byte[] chunks) {
        if(client == null)
            connect();
        ByteArrayInputStream byteStream = new ByteArrayInputStream(chunks);

        try {
            synchronized (client) {
                client.putFileOverwriteRequest("/" + fn, byteStream, chunks.length, null).upload();
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public boolean deleteFile(String file)
    {
        try{
            client.delete(file);
        }catch (Exception e)
        {
            return false;
        }

        return true;
    }


    public byte[] getData(String fn) {
        if(client == null)
            connect();
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        byte[] chunks;

        //unsure if implementation/syntax is right on this
        try {
            synchronized (client) {
                client.getFile("/" + fn, null, byteStream, null);
            }
            chunks = byteStream.toByteArray();
        } catch (Exception e) {
            //failed to fetch files
            //TODO: handle cases where access token is no longer valid/ user unlinked their account -> Reauth user and get new token
            return null;
        }

        return chunks;
    }
}
