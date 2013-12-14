package com.metrafonic.couchdroid;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

/**
 * Created by hema1506 on 26/11/13.
 */
public class ActivitySettings extends Fragment {
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_start, container, false);
        Button button = (Button) rootView.findViewById(R.id.button);
        final EditText editApiKey = (EditText) rootView.findViewById(R.id.editTextApiKey);
        final EditText editHostname = (EditText) rootView.findViewById(R.id.editTextHostname);
        final EditText editPort = (EditText) rootView.findViewById(R.id.editTextPort);
        final String PREFS_NAME = "ServerPrefsFile";
        final SharedPreferences settings = getActivity().getSharedPreferences(PREFS_NAME, 0);
        final AsyncHttpClient client = new AsyncHttpClient();


        editHostname.setText(settings.getString("hostname", null));
        editPort.setText(settings.getString("port", null));
        editApiKey.setText(settings.getString("apikey", null));

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settings.edit()
                        .putString("hostname", editHostname.getText().toString())
                        .putString("port", editPort.getText().toString())
                        .putString("apikey", "1d2092c35fbd466ba0bb2530628a11ac"/*editApiKey.getText().toString()*/)
                        .commit();


                //Intent intent = new Intent(getBaseContext(), MainActivity.class);
                //startActivity(intent);
                settings.edit().putString("webaddress", ("http://" + settings.getString("hostname", "127.0.0.1") + ":" + settings.getString("port", "80") + "/api/" + settings.getString("apikey", "key"))).commit();
                final String webaddress = settings.getString("webaddress", null);
                client.get(webaddress + "/movie.list?status=active", new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(String response) {
                        System.out.println("it worked!");
                        settings.edit().putString("responsewanted", response).commit();
                        System.out.println("starting web request2");
                        client.get(webaddress + "/movie.list?status=done", new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(String response) {
                                settings.edit().putString("responsemanage", response).commit();
                                settings.edit().putString("currentfragment", "wanted").commit();
                                ((MainActivity) getActivity()).swag();

                            }
                        });

                    }
                });
            }
        });

        return rootView;
    }
}
