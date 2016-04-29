package com.scar.android.ServerImpl;

/**
 * Created by Chris on 4/20/2016.
 */

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;

public class GoogleDrive implements scar.IServer{
    public static final String APP_KEY="";
    public static final String APP_SECRET="";
    private GoogleApiClient mGoogleApiClient;

    public void connect(){

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

    }
}
