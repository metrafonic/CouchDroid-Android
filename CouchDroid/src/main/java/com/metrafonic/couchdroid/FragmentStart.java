package com.metrafonic.couchdroid;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by hema1506 on 26/11/13.
 */
public class FragmentStart extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.fragment_start, container, false);
        Button button = (Button) rootView.findViewById(R.id.button);
        final EditText editApiKey = (EditText) rootView.findViewById(R.id.editTextApiKey);
        final EditText editHostname = (EditText) rootView.findViewById(R.id.editTextHostname);
        final EditText editPort = (EditText) rootView.findViewById(R.id.editTextPort);
        final String PREFS_NAME = "ServerPrefsFile";
        final SharedPreferences settings = this.getActivity().getSharedPreferences(PREFS_NAME, 0);

        editHostname.setText(settings.getString("hostname", null));
        editPort.setText(settings.getString("port", null));
        editApiKey.setText(settings.getString("apikey", null));

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settings.edit()
                        .putString("hostname", editHostname.getText().toString())
                        .putString("port", editPort.getText().toString())
                        .putString("apikey", editApiKey.getText().toString())
                        .commit();

                FragmentTransaction ft = getFragmentManager().beginTransaction();
                FragmentHome myFragment = new FragmentHome();
                //myFragment.setArguments(data);
                ft.replace(R.id.fragmentLayout, myFragment);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
            }
        });

        return rootView;
    }
}
