package com.scar.android.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.android.scar.R;
import com.box.androidsdk.content.BoxConfig;
import com.box.androidsdk.content.auth.BoxAuthentication;
import com.box.androidsdk.content.models.BoxSession;
import com.scar.android.StoreFrag;

public class BoxStore extends Fragment implements StoreFrag {
    private int type;
    BoxSession mSession = null;



    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        BoxConfig.IS_LOG_ENABLED=true;
        BoxConfig.CLIENT_ID="v2hh10k66lknsvrx54t0gup49spx2svs";
        BoxConfig.CLIENT_SECRET="gI7CAQfUpbx4gB3EQobkDyNlG6kvnga0";
        BoxConfig.REDIRECT_URL="http://localhost";
        return inflater.inflate(R.layout.edit_box_server, container, false);
    }

    public void onStart() {
        super.onStart();
        Bundle bun = getArguments();
        type = bun.getInt("type");
        setLabel(bun.getString("lbl"));
        setHost(bun.getString("host"));
        setPort(bun.getString("port"));
        setUsername(bun.getByteArray("uname"));
        setPassword(bun.getByteArray("pass"));

        Button login= (Button)getActivity().findViewById(R.id.box_signin);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initialize();
            }

        });


    }

    private void initialize(){
        mSession = new BoxSession(getActivity());
        mSession.authenticate();
    }

    /*
    public void onResume(){
        super.onResume();

        //Handle Box OAUTH2 Return

        if(mSession.isEnabledBoxAppAuthentication()){
            setLabel(BoxAuthentication.getInstance().getAuthInfo(mSession.getUserId(),getActivity()).accessToken());
        }
    }

    */

    @Override
    public int getType() {
        return 0;
    }

    @Override
    public String getLabel() {
        return null;
    }

    @Override
    public String getHost() {
        return null;
    }

    @Override
    public String getPort() {
        return null;
    }

    @Override
    public byte[] getUsername() {
        return new byte[0];
    }

    @Override
    public byte[] getPassword() {
        return new byte[0];
    }

    @Override
    public void setLabel(String a) {

    }

    @Override
    public void setHost(String a) {

    }

    @Override
    public void setPort(String a) {

    }

    @Override
    public void setUsername(byte[] a) {

    }

    @Override
    public void setPassword(byte[] a) {

    }
}
