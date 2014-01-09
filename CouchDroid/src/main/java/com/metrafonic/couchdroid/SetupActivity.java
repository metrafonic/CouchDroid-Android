package com.metrafonic.couchdroid;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by Mathias on 1/8/14.
 */
public class SetupActivity extends FragmentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setupview);
        //getActionBar().setTitle("Settings");
        if (savedInstanceState == null){
            android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
            final android.support.v4.app.FragmentTransaction transaction = fm.beginTransaction();
            FragmentSetup2 newFragment = new FragmentSetup2();
            transaction.add(R.id.fragmentlayoutsetup, newFragment, "search");
            //fm.popBackStack(null, fm.POP_BACK_STACK_INCLUSIVE);
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            transaction.commit();
        }

    }
}
