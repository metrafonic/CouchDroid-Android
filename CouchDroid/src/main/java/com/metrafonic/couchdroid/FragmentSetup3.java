package com.metrafonic.couchdroid;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.youtube.player.internal.r;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Mathias on 1/8/14.
 */
public class FragmentSetup3 extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle extras = this.getArguments();
        String hostname = "";
        String port = "";
        String directory = "";
        String username = "";
        String password = "";
        if (getArguments() != null) {
            hostname = extras.getString("hostname");
            port = extras.getString("port");
            directory = extras.getString("directory");
            username = extras.getString("username");
            password = extras.getString("password");
        }
        View rootView = inflater.inflate(R.layout.fragment_setup_3, container, false);

        final Button ButtonConnect = (Button) rootView.findViewById(R.id.buttonSetupConnect);
        final RadioButton RadioAuto = (RadioButton) rootView.findViewById(R.id.radioButtonSetupAuto);
        final RadioButton RadioManual = (RadioButton) rootView.findViewById(R.id.radioButtonSetupManual);
        final EditText EditUsername = (EditText) rootView.findViewById(R.id.editTextSetupApiUsername);
        final EditText EditPassword = (EditText) rootView.findViewById(R.id.editTextSetupApiPassword);
        final EditText EditKey = (EditText) rootView.findViewById(R.id.editTextSetupApiKey);


        final String PREFS_NAME = "ServerPrefsFile";
        final SharedPreferences settings = getActivity().getSharedPreferences(PREFS_NAME, 0);

        EditUsername.setText(username);
        EditPassword.setText(password);
        EditKey.setText(settings.getString("apikey", "").toString());

        settings.edit()
                .putString("username", username)
                .putString("password", password)
                .putString("hostname", hostname)
                .putString("port", port)
                .putString("directory", directory)

                .commit();

        final AsyncHttpClient client = new AsyncHttpClient();
        client.setBasicAuth(username, password);

        final String finalUsername = username;
        final String finalPassword = password;
        final String finalHostname = hostname;
        final String finalPort = port;
        final String finalDirectory = directory;
        ButtonConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String apiUsername = EditUsername.getText().toString();
                String apiPassword = EditPassword.getText().toString();
                final String apiKey = EditKey.getText().toString();
                if (RadioAuto.isChecked()==true){
                    String url = ("http://" + finalHostname + ":" + finalPort + finalDirectory +"/getkey/?p=" + md5(apiPassword) + "&u=" + md5(apiUsername) + "");
                    client.get(url, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(String response) {

                            JSONObject jsonResponse = null;
                            try{
                                jsonResponse = new JSONObject(response);
                                if (jsonResponse.getBoolean("success")){
                                    Toast.makeText(getActivity(), "Recieved key: " + jsonResponse.getString("api_key").toString(), Toast.LENGTH_SHORT).show();
                                    settings.edit().putString("webaddress", ("http://" + finalHostname + ":" + finalPort + finalDirectory + "/api/" + jsonResponse.getString("api_key").toString())).commit();
                                    settings.edit().putBoolean("complete", true).commit();
                                    settings.edit().putString("apikey", jsonResponse.getString("api_key").toString()).commit();

                                    Intent myIntent = new Intent(getActivity(), MainActivity.class);
                                    //myIntent.putExtra("key", value); //Optional parameters
                                    myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    getActivity().startActivity(myIntent);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        @Override
                        public void onFailure(java.lang.Throwable error) {
                            Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
                            settings.edit().putBoolean("complete", false).commit();
                        }
                    });
                }
                if (RadioManual.isChecked()==true){
                    String url = ("http://" + finalHostname + ":" + finalPort + finalDirectory + "/api/" + apiKey + "/app.available");

                    client.get(url, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(String response) {
                            Toast.makeText(getActivity(), "API Key works!", Toast.LENGTH_SHORT).show();
                            settings.edit().putString("webaddress", ("http://" + finalHostname + ":" + finalPort + finalDirectory + "/api/" + apiKey)).commit();
                            settings.edit().putBoolean("complete", true).commit();
                            settings.edit().putString("apikey", apiKey).commit();
                            Intent myIntent = new Intent(getActivity(), MainActivity.class);
                            //myIntent.putExtra("key", value); //Optional parameters
                            myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            getActivity().startActivity(myIntent);
                        }
                        @Override
                        public void onFailure(java.lang.Throwable error) {
                            Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
                            settings.edit().putBoolean("complete", false).commit();
                        }
                    });
                }
            }
        });


        return rootView;
    }
    private String md5(String in) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
            digest.reset();
            digest.update(in.getBytes());
            byte[] a = digest.digest();
            int len = a.length;
            StringBuilder sb = new StringBuilder(len << 1);
            for (int i = 0; i < len; i++) {
                sb.append(Character.forDigit((a[i] & 0xf0) >> 4, 16));
                sb.append(Character.forDigit(a[i] & 0x0f, 16));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) { e.printStackTrace(); }
        return null;
    }
}