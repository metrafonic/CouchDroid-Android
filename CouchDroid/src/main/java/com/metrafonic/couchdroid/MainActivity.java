package com.metrafonic.couchdroid;

import android.app.Activity;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends FragmentActivity {
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final String PREFS_NAME = "ServerPrefsFile";
        final SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        final android.support.v4.app.FragmentTransaction ft = fm.beginTransaction();
        String webaddress = ("http://" + settings.getString("hostname", null) + ":" + settings.getString("port", null) + "/api/" + settings.getString("apikey", null));
        System.out.println(webaddress);

        if (settings.getString("apikey", "none").length()<5) {
            Bundle data = new Bundle();
            FragmentStart newFragment = new FragmentStart();
            newFragment.setArguments(data);
            ft.add(R.id.fragmentLayout, newFragment);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.commit();
        }
        System.out.println("something");
        if (savedInstanceState == null){
            System.out.println(webaddress);
            final AsyncHttpClient client = new AsyncHttpClient();
            client.get(webaddress+ "/movie.list", new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(String response) {
                    System.out.println("something2");
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

    }

}
