package com.scar.android.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.android.scar.R;
import com.scar.android.StoreFrag;

public class GenericStore extends Fragment implements StoreFrag {
    private int type;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.edit_generic_server, container, false);
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
    }


    public int getType() {
        return type;
    }

    public String getLabel() {
        return ((EditText)getActivity().findViewById(R.id.as_label)).getText().toString();
    }

    public String getHost() {
        return ((EditText)getActivity().findViewById(R.id.as_host)).getText().toString();
    }

    public String getPort() {
        return ((EditText)getActivity().findViewById(R.id.as_port)).getText().toString();
    }

    public byte[] getUsername() {
        return ((EditText)getActivity().findViewById(R.id.as_uname)).getText().toString().getBytes();
    }

    public byte[] getPassword() {
        return ((EditText)getActivity().findViewById(R.id.as_pass)).getText().toString().getBytes();
    }

    public void setLabel(String x) {
        if(x != null)
            ((EditText)getActivity().findViewById(R.id.as_label)).setText(x.toCharArray(), 0, x.length());
    }
    public void setHost(String x) {
        if(x != null)
            ((EditText)getActivity().findViewById(R.id.as_host)).setText(x.toCharArray(), 0, x.length());
    }
    public void setPort(String x) {
        if(x != null)
            ((EditText)getActivity().findViewById(R.id.as_port)).setText(x.toCharArray(), 0, x.length());
    }
    public void setUsername(byte[] x) {
        if (x != null) {
            ((EditText) getActivity().findViewById(R.id.as_uname)).setText(new String(x));
        }
    }
    public void setPassword(byte[] x) {
        if(x != null)
            ((EditText)getActivity().findViewById(R.id.as_pass)).setText(new String(x));
    }
}