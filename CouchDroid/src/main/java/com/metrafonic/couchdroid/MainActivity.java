package com.metrafonic.couchdroid;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.util.ArrayList;


public class MainActivity extends FragmentActivity {
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button buttonSettings = (Button) findViewById(R.id.buttonSettings);
        final String PREFS_NAME = "ServerPrefsFile";
        final SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        final android.support.v4.app.FragmentTransaction ft = fm.beginTransaction();
        String webaddress = ("http://" + settings.getString("hostname", null) + ":" + settings.getString("port", null) + "/api/" + settings.getString("apikey", null));
        System.out.println(webaddress);

        if (settings.getString("apikey", "none").length()<5) {
            Intent intent = new Intent(getBaseContext(), ActivitySettings.class);
            startActivity(intent);
        }
        System.out.println("something");

            System.out.println(webaddress);
            final AsyncHttpClient client = new AsyncHttpClient();
        if (savedInstanceState == null) {
            client.get(webaddress + "/movie.list?status=active", new AsyncHttpResponseHandler() {
            @Override
                public void onSuccess(String response) {
                    //System.out.println("something2");
                    //System.out.println(response);
                    Bundle data = new Bundle();
                    data.putString("movielist",response);
                    FragmentHome newFragment = new FragmentHome();
                    newFragment.setArguments(data);
                    ft.add(R.id.fragmentLayout, newFragment);
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    ft.commit();

                }
            });
        }
        buttonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), ActivitySettings.class);
                startActivity(intent);
            }
        });

    }

}
