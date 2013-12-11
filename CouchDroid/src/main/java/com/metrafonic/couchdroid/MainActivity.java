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

        final String webaddress = ("http://" + settings.getString("hostname", "127.0.0.1") + ":" + settings.getString("port", "80") + "/api/" + settings.getString("apikey", "key"));
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
            buttonWanted.setClickable(false);
            buttonManage.setClickable(false);
            System.out.println("starting web request");
            //webaddress + "/movie.list?status=active"
            client.get(webaddress + "/movie.list?status=active", new AsyncHttpResponseHandler() {
            @Override
                public void onSuccess(String response) {
                System.out.println("it worked!");
                settings.edit().putString("responsewanted", response).commit();

            }
            });
            System.out.println("starting web request2");
            client.get(webaddress + "/movie.list?status=done", new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(String response) {
                    settings.edit().putString("responsemanage", response).commit();
                    Bundle data = new Bundle();
                    data.putString("movielist", settings.getString("responsewanted", "null").toString());
                    data.putString("listtype", "Wanted Movies:");
                    android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
                    final android.support.v4.app.FragmentTransaction transaction = fm.beginTransaction();
                    FragmentHome newFragment = new FragmentHome();
                    newFragment.setArguments(data);
                    transaction.add(R.id.fragmentLayout, newFragment, "wanted");
                    //fm.popBackStack(null, fm.POP_BACK_STACK_INCLUSIVE);
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    transaction.commit();
                    settings.edit().putString("currentfragment", "wanted").commit();

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
                android.support.v4.app.FragmentManager fm2 = getSupportFragmentManager();
                final android.support.v4.app.FragmentTransaction transaction2 = fm2.beginTransaction();
                transaction2.remove(fm2.findFragmentById(R.id.fragmentLayout)).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
                buttonWanted.setTypeface(null, Typeface.NORMAL);
                buttonManage.setTypeface(null, Typeface.BOLD);
                buttonWanted.setClickable(false);
                buttonManage.setClickable(false);
                settings.edit().putString("openfragment", "manage");
                Bundle data = new Bundle();
                data.putString("movielist", settings.getString("responsemanage", "null").toString());
                data.putString("listtype", "Manage Movies");
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
                settings.edit().putString("currentfragment", "manage").commit();

            }
        });
        buttonWanted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.support.v4.app.FragmentManager fm2 = getSupportFragmentManager();
                final android.support.v4.app.FragmentTransaction transaction2 = fm2.beginTransaction();
                transaction2.remove(fm2.findFragmentById(R.id.fragmentLayout)).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
                buttonWanted.setClickable(false);
                buttonWanted.setTypeface(null, Typeface.BOLD);
                buttonManage.setTypeface(null, Typeface.NORMAL);
                buttonManage.setClickable(false);
                        Bundle data = new Bundle();
                data.putString("movielist", settings.getString("responsewanted", "null").toString());
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
                settings.edit().putString("currentfragment", "wanted").commit();

            }
        });
/*
        if (settings.getString("currentfragment", null).toString().contains("wanted")){
            buttonWanted.setTypeface(null, Typeface.BOLD);
        }else if (settings.getString("currentfragment", null).toString().contains("manage")){
            buttonManage.setTypeface(null, Typeface.BOLD);
        }
*/
    }


}
