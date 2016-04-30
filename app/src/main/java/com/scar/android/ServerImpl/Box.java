package com.scar.android.ServerImpl;

import com.box.androidsdk.content.BoxApi;
import com.box.androidsdk.content.BoxConfig;
import com.box.androidsdk.content.BoxException;
import com.box.androidsdk.content.auth.BoxAuthentication;
import com.box.androidsdk.content.models.BoxSession;
import com.box.androidsdk.content.models.BoxUser;
import com.box.sdk.BoxAPIConnection;

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
        return false;
    }

    @Override
    public boolean deleteFile(String s) {
        return false;
    }

    @Override
    public byte[] getData(String s) {
        return new byte[0];
    }

    @Override
    public int id() {
        return 0;
    }

    @Override
    public boolean getStatus() {
        return false;
    }

    @Override
    public void close() {
        api.setExpires(0);

    }
}
