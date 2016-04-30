package com.scar.android.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.scar.R;
import com.box.androidsdk.content.BoxConfig;
import com.box.androidsdk.content.auth.BoxAuthentication;
import com.box.androidsdk.content.models.BoxSession;
import com.scar.android.MetaData;
import com.scar.android.StoreFrag;

public class BoxStore extends Fragment implements StoreFrag, BoxAuthentication.AuthListener {
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
        if(mSession.isEnabledBoxAppAuthentication()){
            Toast.makeText(getActivity(),"Auth get!!", Toast.LENGTH_LONG).show();
            setLabel(BoxAuthentication.getInstance().getAuthInfo(mSession.getUserId(),getActivity()).accessToken());
            setHost(BoxAuthentication.getInstance().getAuthInfo(mSession.getUserId(),getActivity()).refreshToken());
        }
    }

    @Override
    public void onRefreshed(BoxAuthentication.BoxAuthenticationInfo info) {
        Toast.makeText(getActivity(),"Auth get!!", Toast.LENGTH_LONG).show();
        setLabel(BoxAuthentication.getInstance().getAuthInfo(mSession.getUserId(),getActivity()).accessToken());
        setHost(BoxAuthentication.getInstance().getAuthInfo(mSession.getUserId(),getActivity()).refreshToken());//
    }

    @Override
    public void onAuthCreated(BoxAuthentication.BoxAuthenticationInfo info){
        Toast.makeText(getActivity(),"Auth get!!", Toast.LENGTH_LONG).show();
        setLabel(BoxAuthentication.getInstance().getAuthInfo(mSession.getUserId(),getActivity()).accessToken());
        setHost(BoxAuthentication.getInstance().getAuthInfo(mSession.getUserId(),getActivity()).refreshToken());//
    }

    @Override
    public void onAuthFailure(BoxAuthentication.BoxAuthenticationInfo info, Exception ex) {
        Toast.makeText(getActivity(),"Auth failure!!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLoggedOut(BoxAuthentication.BoxAuthenticationInfo info, Exception ex) {

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
        return MetaData.TYPE_BOX_STORE;
    }

    @Override
    public String getLabel() {
        return ((EditText)getActivity().findViewById(R.id.box_tok)).getText().toString();
    }

    @Override
    public String getHost() {
        return ((EditText)getActivity().findViewById(R.id.refresh_tok)).getText().toString();
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
        if(a!=null){
            ((EditText)getActivity().findViewById(R.id.box_tok)).setText(a.toCharArray(),0,a.length());
        }

    }

    @Override
    public void setHost(String a) {
        if(a!=null)
        ((EditText)getActivity().findViewById(R.id.refresh_tok)).setText(a.toCharArray(),0,a.length());
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
