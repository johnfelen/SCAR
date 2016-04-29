package com.scar.android.Activities;

import android.app.ActionBar;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;

import com.android.scar.R;
import com.scar.android.Fragments.BoxStore;
import com.scar.android.Fragments.DropBoxStore;
import com.scar.android.Fragments.GenericStore;
import com.scar.android.Fragments.SQLiteStore;
import com.scar.android.MetaData;
import com.scar.android.Server;
import com.scar.android.Session;
import com.scar.android.StoreFrag;

public class ModifyServer extends FragmentActivity {
    private StoreFrag frag;
    private Server srv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modify_server);

        final ActionBar actionBar = getActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#2B468B")));
        actionBar.setTitle("SCAR");

        srv = new Server(getIntent().getExtras());

        Spinner type = (Spinner)findViewById(R.id.ms_types);
        type.setSelection(srv.type);
        type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                ModifyServer.this.setAddContent(pos);
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        Button update = (Button)findViewById(R.id.ms_update);
        update.setOnClickListener(new Button.OnClickListener() {
            public void onClick (View arg) {
                srv.label = frag.getLabel();
                srv.hostname = frag.getHost();
                srv.port = frag.getPort();
                srv.uname = frag.getUsername();
                srv.pass = frag.getPassword();
                Session.meta.updateServer(srv);
                Session.metaBackground.updateServer(srv);
                finish(); //kill activity
            }
        });

        Button act = (Button)findViewById(R.id.ms_act);
        switch(srv.getStatus(this)) {
            case Server.ONLINE:
            case Server.OFFLINE:
                act.setText(R.string.ms_delete);
                break;
            case Server.DISABLED:
                act.setText(R.string.ms_readd);
                break;
        }
        act.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View arg) {
                if (srv.status == MetaData.STATUS_ACTIVE)
                    srv.status = MetaData.STATUS_DISABLE;
                else
                    srv.status = MetaData.STATUS_ACTIVE;
                ModifyServer.this.updateStatus();
            }
        });

        setAddContent(srv.type);
        updateStatus();
    }

    private void updateStatus() {
        ImageView iv = (ImageView)findViewById(R.id.ms_status);
        switch(srv.getStatus(this)) {
            case Server.ONLINE:
                iv.setImageResource(android.R.drawable.button_onoff_indicator_on);
                break;
            case Server.OFFLINE:
                iv.setImageResource(android.R.drawable.button_onoff_indicator_off);
                break;
            case Server.DISABLED:
                iv.setImageResource(android.R.drawable.ic_delete);
                break;
        }
    }

    private void setAddContent(int type) {
        srv.type = type;
        switch(type) {
            case MetaData.TYPE_CASS_STORE:
            case MetaData.TYPE_MYSQL_STORE:
                frag = new GenericStore();
                break;
            case MetaData.TYPE_SQLITE_STORE:
                frag = new SQLiteStore();
                break;
            case MetaData.TYPE_GDRIVE_STORE:
                //TODO: change this later
                frag = new BoxStore();
                break;
            case MetaData.TYPE_DROPBOX_STORE:
                frag = new DropBoxStore();
                break;
        }
        ((Fragment) frag).setArguments(srv.bundle());
        getSupportFragmentManager().beginTransaction().replace(R.id.ms_content, (Fragment)frag).commit();
    }
}
