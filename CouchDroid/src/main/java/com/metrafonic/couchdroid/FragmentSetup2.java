package com.metrafonic.couchdroid;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by Mathias on 1/8/14.
 */
public class FragmentSetup2 extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_setup_2, container, false);
        Button NextButton = (Button) rootView.findViewById(R.id.buttonSetupNext);
        final EditText editHostname = (EditText) rootView.findViewById(R.id.editTextSetupHostname);
        final EditText editPort = (EditText) rootView.findViewById(R.id.editTextSetupPort);
        final EditText editDirectory = (EditText) rootView.findViewById(R.id.editTextSetupDirectory);
        final EditText editUsername = (EditText) rootView.findViewById(R.id.editTextSetupUsername);
        final EditText editPassword = (EditText) rootView.findViewById(R.id.editTextSetupPassword);



        NextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle data = new Bundle();
                data.putString("hostname", editHostname.getText().toString());
                data.putString("port", editPort.getText().toString());
                data.putString("directory", editDirectory.getText().toString());
                data.putString("username", editUsername.getText().toString());
                data.putString("password", editPassword.getText().toString());
                android.support.v4.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
                FragmentSetup3 myFragment = new FragmentSetup3();
                myFragment.setArguments(data);
                ft.replace(R.id.fragmentlayoutsetup, myFragment);
                ft.addToBackStack(null);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
                ft.commit();
            }
        });

        return rootView;
    }
}