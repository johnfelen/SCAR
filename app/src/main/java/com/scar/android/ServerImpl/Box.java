package com.scar.android.ServerImpl;

import com.box.androidsdk.content.BoxApi;
import com.box.androidsdk.content.BoxConfig;
import com.box.androidsdk.content.BoxException;
import com.box.androidsdk.content.auth.BoxAuthentication;
import com.box.androidsdk.content.models.BoxFolder;
import com.box.androidsdk.content.models.BoxSession;
import com.box.androidsdk.content.models.BoxUser;
import com.box.sdk.BoxAPIConnection;
import com.box.sdk.BoxFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

/**
 * Created by Chris on 4/29/2016.
 */
public class Box implements scar.IServer {
    public static final String CLIENT_ID = "v2hh10k66lknsvrx54t0gup49spx2svs";
    public static final String CLIENT_SECRET = "gI7CAQfUpbx4gB3EQobkDyNlG6kvnga0";
    private String accessToken;
    private String refreshToken;

    private boolean connected;

    private BoxAPIConnection api;

    public Box(final String token, final String refresh){
        accessToken=token;
        refreshToken=refresh;
        connected=false;
    }

    public void connect(){
        api = new BoxAPIConnection(CLIENT_ID,CLIENT_SECRET,accessToken,refreshToken);
    }



    @Override
    public boolean storeData(String s, byte[] bytes) {
        if(api!=null){
            connect();

            com.box.sdk.BoxFolder folder = com.box.sdk.BoxFolder.getRootFolder(api);
            ByteArrayInputStream byteStream = new ByteArrayInputStream(bytes);

            BoxFile newfile = new BoxFile(api,s);
            newfile.uploadVersion(byteStream);
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteFile(String s) {
        if(api!=null){
            connect();
            BoxFile delfile = new BoxFile(api,s);
            delfile.delete();
            return true;
        }
        return false;
    }

    @Override
    public byte[] getData(String s) {
        if (api != null) {
            ByteArrayOutputStream dl = new ByteArrayOutputStream();
            BoxFile file = new BoxFile(api,s);
            file.download(dl);
            return dl.toByteArray();
        }

        return new byte[0];
    }

    @Override
    public int id() {
        return 5;
    }

    @Override
    public boolean getStatus() {
        if(api!=null) {
            return true;
        }else{
            return false;
        }
    }

    @Override
    public void close() {
        if(api!=null){
            api.setExpires(1);
        }

    }
}
