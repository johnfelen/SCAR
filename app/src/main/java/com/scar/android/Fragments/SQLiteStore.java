package com.scar.android.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.android.scar.R;
import com.scar.android.MetaData;
import com.scar.android.StoreFrag;

import java.util.StringTokenizer;

public class SQLiteStore extends Fragment implements StoreFrag{

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.edit_sqlite_server, container, false);
    }

    public void onStart() {
        super.onStart();
        Bundle bun = getArguments();
        setLabel(bun.getString("lbl"));
        setHost(bun.getString("host"));
        setPort(bun.getString("port"));
        setUsername(bun.getByteArray("uname"));
        setPassword(bun.getByteArray("pass"));
    }

    public int getType() {
        return MetaData.TYPE_SQLITE_STORE;
    }

    public String getLabel() {
        return ((EditText)getActivity().findViewById(R.id.as_label)).getText().toString();
    }

    public String getHost() {
        return ((EditText)getActivity().findViewById(R.id.as_dbname)).getText().toString();
    }

    public String getPort() { return ""; }
    public String getUsername() { return ""; }
    public String getPassword() { return ""; }

    public void setLabel(String x) {
        if(x != null)
            ((EditText)getActivity().findViewById(R.id.as_label)).setText(x.toCharArray(), 0, x.length());
    }
    public void setHost(String x) {
        if(x != null)
            ((EditText)getActivity().findViewById(R.id.as_dbname)).setText(x.toCharArray(), 0, x.length());
    }
    public void setPort(String x) {}
    public void setUsername(byte[] x) {}
    public void setPassword(byte[] x) {}
}