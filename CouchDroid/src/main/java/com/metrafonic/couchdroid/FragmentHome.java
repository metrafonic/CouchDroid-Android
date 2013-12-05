package com.metrafonic.couchdroid;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

import java.util.ArrayList;

/**
 * Created by hema1506 on 26/11/13.
 */
public class FragmentHome extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        Bundle bundle = this.getArguments();
        String response = bundle.getString("movielist", "no bundle");
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);


        final String PREFS_NAME = "ServerPrefsFile";
        SharedPreferences settings = this.getActivity().getSharedPreferences(PREFS_NAME, 0);
        if (savedInstanceState == null){

        }


        return rootView;
    }
}
