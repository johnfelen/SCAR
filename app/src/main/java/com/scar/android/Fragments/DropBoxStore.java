package com.scar.android.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.android.scar.R;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AppKeyPair;
import com.scar.android.MetaData;
import com.scar.android.ServerImpl.DropBox;
import com.scar.android.StoreFrag;


public class DropBoxStore extends Fragment implements StoreFrag{
    private DropboxAPI<AndroidAuthSession> DBapi;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.edit_dropbox_server, container, false);
    }

    public void onStart() {
        super.onStart();
        Bundle bun = getArguments();
        setLabel(bun.getString("lbl"));
        setHost(bun.getString("host"));
        setPort(bun.getString("port"));
        setUsername(bun.getByteArray("uname"));
        setPassword(bun.getByteArray("pass"));

        // Setup dropbox session
        AppKeyPair appKeys = new AppKeyPair(DropBox.APP_KEY, DropBox.APP_SECRET);
        AndroidAuthSession session = new AndroidAuthSession(appKeys);
        DBapi = new DropboxAPI<AndroidAuthSession>(session);

        Button auth = (Button)getActivity().findViewById(R.id.as_auth);
        auth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* TODO: Perform the DropBox OAUTH2 process as detailed on
                  https://www.dropbox.com/developers/core/start/android
                  if we already have an access token this will overwrite the old one
                  which is fine
                   */
            }
        });
    }

    public void onResume() {
        //TODO: Handle DropBox OAUTH2 return, just ensure it was successful
        // you don't need to do anything special to store the access token
    }

    public int getType() {
        return MetaData.TYPE_DROPBOX_STORE;
    }

    public String getLabel() {
        return ((EditText)getActivity().findViewById(R.id.as_token)).getText().toString();
    }

    public String getHost() { return null;}
    public String getPort() { return null; }
    public byte[] getUsername() { return null; }
    public byte[] getPassword() { return null; }

    public void setLabel(String x) {
        if(x != null)
            ((EditText)getActivity().findViewById(R.id.as_token)).setText(x.toCharArray(), 0, x.length());
    }
    public void setHost(String x) {}
    public void setPort(String x) {}
    public void setUsername(byte[] x) {}
    public void setPassword(byte[] x) {}

    public boolean getStatus() { return true; }
}