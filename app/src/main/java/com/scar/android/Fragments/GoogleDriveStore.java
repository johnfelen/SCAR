package com.scar.android.Fragments;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.android.scar.R;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.*;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.scar.android.StoreFrag;

public class GoogleDriveStore extends Fragment implements StoreFrag, GoogleApiClient.OnConnectionFailedListener {

    private int type;
    GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 9001;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //setup google sign-in
        GoogleSignInOptions gso= new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to GoogleSignIn.API and the options above.
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .enableAutoManage(getActivity(), this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        return inflater.inflate(R.layout.edit_gdrive_server, container, false);
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

        Button signin = (Button) getActivity().findViewById(R.id.goog_signin);
        signin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }

        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            GoogleSignInAccount acct =result.getSignInAccount();
            acct.getIdToken();
        }
    }


    @Override
    public int getType() {
        return 0;
    }

    @Override
    public String getLabel() {
        return null;
    }

    @Override
    public String getHostName() {
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

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
