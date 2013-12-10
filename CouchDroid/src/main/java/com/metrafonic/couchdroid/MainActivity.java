package com.metrafonic.couchdroid;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
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
        final Button buttonManage = (Button) findViewById(R.id.buttonManage);
        final Button buttonWanted = (Button) findViewById(R.id.buttonWanted);
        final String PREFS_NAME = "ServerPrefsFile";
        final SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

        final String webaddress = ("http://" + settings.getString("hostname", null) + ":" + settings.getString("port", null) + "/api/" + settings.getString("apikey", null));
        System.out.println(webaddress);

        if (settings.getString("apikey", "none").length()<5) {
            Intent intent = new Intent(getBaseContext(), ActivitySettings.class);
            startActivity(intent);
        }
        System.out.println("something");

            System.out.println(webaddress);
            final AsyncHttpClient client = new AsyncHttpClient();
        if (savedInstanceState == null) {
            buttonWanted.setTypeface(null, Typeface.BOLD);
            buttonManage.setTypeface(null, Typeface.NORMAL);
            client.get(webaddress + "/movie.list?status=active", new AsyncHttpResponseHandler() {
            @Override
                public void onSuccess(String response) {
                    //System.out.println("something2");
                    //System.out.println(response);
                    Bundle data = new Bundle();
                data.putString("listtype", "Wanted Movies:");
                data.putString("movielist",response);
                    FragmentHome newFragment = new FragmentHome();
                    newFragment.setArguments(data);
                android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
                final android.support.v4.app.FragmentTransaction ft = fm.beginTransaction();
                ft.add(R.id.fragmentLayout, newFragment, "wanted");
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
        buttonManage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
                final android.support.v4.app.FragmentTransaction transaction = fm.beginTransaction();
                transaction.remove(fm.findFragmentById(R.id.fragmentLayout)).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
                buttonWanted.setTypeface(null, Typeface.NORMAL);
                buttonManage.setTypeface(null, Typeface.BOLD);
                buttonWanted.setClickable(false);
                buttonManage.setClickable(false);
                client.get(webaddress + "/movie.list", new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(String response) {
                        //System.out.println("something2");
                        //System.out.println(response);
                        Bundle data = new Bundle();
                        data.putString("movielist", response);
                        data.putString("listtype", "Manage Movies:");
                        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
                        final android.support.v4.app.FragmentTransaction transaction = fm.beginTransaction();
                        FragmentHome newFragment = new FragmentHome();
                        newFragment.setArguments(data);
                        transaction.replace(R.id.fragmentLayout, newFragment, "manage");
                        fm.popBackStack(null, fm.POP_BACK_STACK_INCLUSIVE);
                        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                        transaction.commit();
                        buttonWanted.setClickable(true);
                        buttonManage.setClickable(true);

                    }
                });
            }
        });
        buttonWanted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
                final android.support.v4.app.FragmentTransaction transaction = fm.beginTransaction();
                transaction.remove(fm.findFragmentById(R.id.fragmentLayout)).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
                buttonWanted.setClickable(false);
                buttonWanted.setTypeface(null, Typeface.BOLD);
                buttonManage.setTypeface(null, Typeface.NORMAL);
                buttonManage.setClickable(false);
                client.get(webaddress + "/movie.list?status=active", new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(String response) {
                        //System.out.println("something2");
                        //System.out.println(response);
                        Bundle data = new Bundle();
                        data.putString("movielist", response);
                        data.putString("listtype", "Wanted Movies:");
                        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
                        final android.support.v4.app.FragmentTransaction transaction = fm.beginTransaction();
                        FragmentHome newFragment = new FragmentHome();
                        newFragment.setArguments(data);
                        transaction.replace(R.id.fragmentLayout, newFragment, "wanted");
                        fm.popBackStack(null, fm.POP_BACK_STACK_INCLUSIVE);
                        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                        transaction.commit();
                        buttonWanted.setClickable(true);
                        buttonManage.setClickable(true);

                    }
                });
            }
        });


    }


}
