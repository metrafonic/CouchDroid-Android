package com.metrafonic.couchdroid;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by hema1506 on 26/11/13.
 */
public class ActivitySettings extends Activity {
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_start);
        Button button = (Button) findViewById(R.id.button);
        final EditText editApiKey = (EditText) findViewById(R.id.editTextApiKey);
        final EditText editHostname = (EditText) findViewById(R.id.editTextHostname);
        final EditText editPort = (EditText) findViewById(R.id.editTextPort);
        final String PREFS_NAME = "ServerPrefsFile";
        final SharedPreferences appsettings = this.getSharedPreferences(PREFS_NAME, 0);


        editHostname.setText(appsettings.getString("hostname", null));
        editPort.setText(appsettings.getString("port", null));
        editApiKey.setText(appsettings.getString("apikey", null));

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                appsettings.edit()
                        .putString("hostname", editHostname.getText().toString())
                        .putString("port", editPort.getText().toString())
                        .putString("apikey", editApiKey.getText().toString())
                        .commit();

                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(intent);
            }
        });

    }
}
