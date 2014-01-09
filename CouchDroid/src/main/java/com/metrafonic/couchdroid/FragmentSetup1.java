package com.metrafonic.couchdroid;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by Mathias on 1/8/14.
 */
public class FragmentSetup1 extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_setup_1, container, false);
        Button EnterButton = (Button) rootView.findViewById(R.id.buttonSetupEnter);

        EnterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.support.v4.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
                FragmentSetup2 myFragment = new FragmentSetup2();
                ft.replace(R.id.fragmentlayoutsetup, myFragment);
                ft.addToBackStack(null);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
                ft.commit();
            }
        });

        return rootView;
    }
}
