package com.metrafonic.couchdroid;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;

import com.metrafonic.couchdroid.setup.Setup1;
import com.metrafonic.couchdroid.setup.Setup2;
import com.metrafonic.couchdroid.setup.Setup3;


public class SettingsActivity extends ActionBarActivity implements Setup3.OnFragmentInteractionListener, Setup2.OnFragmentInteractionListener, Setup1.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        if (savedInstanceState == null){
            android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
            final android.support.v4.app.FragmentTransaction transaction = fm.beginTransaction();
            Setup1 newFragment = new Setup1();
            transaction.add(R.id.container, newFragment, "search");
            //fm.popBackStack(null, fm.POP_BACK_STACK_INCLUSIVE);
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            transaction.commit();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_help) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.metrafonic.com/couchdroid-for-android/"));
            startActivity(browserIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
