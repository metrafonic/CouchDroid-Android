package com.metrafonic.couchdroid;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by hema1506 on 26/11/13.
 */
public class FragmentHome extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        Button button = (Button) rootView.findViewById(R.id.button);

        final String PREFS_NAME = "ServerPrefsFile";
        SharedPreferences settings = this.getActivity().getSharedPreferences(PREFS_NAME, 0);
        if (savedInstanceState == null){

        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                FragmentStart myFragment = new FragmentStart();
                //myFragment.setArguments(response);
                ft.replace(R.id.fragmentLayout, myFragment);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
            }
        });

        return rootView;
    }
}
