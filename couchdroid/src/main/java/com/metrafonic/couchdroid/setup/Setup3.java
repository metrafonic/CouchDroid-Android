package com.metrafonic.couchdroid.setup;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.metrafonic.couchdroid.MainActivity;
import com.metrafonic.couchdroid.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Setup3.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Setup3#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class Setup3 extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Setup3.
     */
    // TODO: Rename and change types and number of parameters
    public static Setup3 newInstance(String param1, String param2) {
        Setup3 fragment = new Setup3();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    public Setup3() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

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
        View rootView = inflater.inflate(R.layout.fragment_setup3, container, false);

        final Button ButtonConnect = (Button) rootView.findViewById(R.id.buttonSetupConnect);
        final RadioButton RadioAuto = (RadioButton) rootView.findViewById(R.id.radioButtonSetupAuto);
        final RadioButton RadioManual = (RadioButton) rootView.findViewById(R.id.radioButtonSetupManual);
        final EditText EditUsername = (EditText) rootView.findViewById(R.id.editTextSetupApiUsername);
        final EditText EditPassword = (EditText) rootView.findViewById(R.id.editTextSetupApiPassword);
        final EditText EditKey = (EditText) rootView.findViewById(R.id.editTextSetupApiKey);


        final String PREFS_NAME = "settings";
        final SharedPreferences settings = getActivity().getSharedPreferences(PREFS_NAME, 0);

        EditUsername.setText(username);
        EditPassword.setText(password);
        EditKey.setText(settings.getString("apikey", "").toString());
        if (settings.getString("apikey", "").toString().length() > 6) {
            RadioAuto.setChecked(false);
            RadioManual.setChecked(true);
        }

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
                ButtonConnect.setText("Connecting...");
                ButtonConnect.setClickable(false);
                if (RadioAuto.isChecked()==true){
                    String url = ("http://" + finalUsername + ":" + finalPassword + "@" + finalHostname + ":" + finalPort + finalDirectory +"/getkey/?p=" + md5(apiPassword) + "&u=" + md5(apiUsername) + "");
                    client.get(url, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(String response) {

                            JSONObject jsonResponse = null;
                            try{
                                jsonResponse = new JSONObject(response);
                                if (jsonResponse.getBoolean("success")){
                                    Toast.makeText(getActivity(), "Recieved key: " + jsonResponse.getString("api_key").toString(), Toast.LENGTH_SHORT).show();
                                    settings.edit().putString("webaddress", ("http://" + finalUsername + ":" + finalPassword + "@" + finalHostname + ":" + finalPort + finalDirectory + "/api/" + jsonResponse.getString("api_key").toString())).commit();
                                    settings.edit().putBoolean("complete", true).commit();
                                    settings.edit().putString("apikey", jsonResponse.getString("api_key").toString()).commit();

                                    Intent myIntent = new Intent(getActivity(), MainActivity.class);
                                    //myIntent.putExtra("key", value); //Optional parameters
                                    myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    getActivity().startActivity(myIntent);
                                } else {
                                    Toast.makeText(getActivity(), response, Toast.LENGTH_SHORT).show();
                                    settings.edit().putBoolean("complete", false).commit();
                                    ButtonConnect.setText("Connect");
                                    ButtonConnect.setClickable(true);
                                }
                            } catch (JSONException e) {
                                Toast.makeText(getActivity(), "Failed. Check settings in previous screen", Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onFailure(java.lang.Throwable error, String response) {
                            try {
                                if (response.toString().length() > 5) {
                                    Toast.makeText(getActivity(), Html.fromHtml(response.toString()), Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_LONG).show();
                                }
                            } catch (Exception c) {
                                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_LONG).show();
                            }
                            settings.edit().putBoolean("complete", false).commit();
                            ButtonConnect.setText("Connect");
                            ButtonConnect.setClickable(true);
                        }
                    });
                }
                if (RadioManual.isChecked()==true){
                    final String url = ("http://" + finalUsername + ":" + finalPassword + "@" + finalHostname + ":" + finalPort + finalDirectory + "/api/" + apiKey + "/app.available");

                    ButtonConnect.setText("Connecting...");
                    client.get(url, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(String response) {
                            JSONObject jsonResponse;

                            try {
                                jsonResponse = new JSONObject(response);
                                if (jsonResponse.getBoolean("success")) {
                                    Toast.makeText(getActivity(), "API Key correct!", Toast.LENGTH_SHORT).show();
                                    settings.edit().putString("webaddress", ("http://" + finalUsername + ":" + finalPassword + "@" + finalHostname + ":" + finalPort + finalDirectory + "/api/" + apiKey)).commit();
                                    settings.edit().putBoolean("complete", true).commit();
                                    settings.edit().putString("apikey", apiKey).commit();
                                    Intent myIntent = new Intent(getActivity(), MainActivity.class);
                                    //myIntent.putExtra("key", value); //Optional parameters
                                    myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    getActivity().startActivity(myIntent);
                                } else {
                                    Toast.makeText(getActivity(), response, Toast.LENGTH_SHORT).show();
                                    settings.edit().putBoolean("complete", false).commit();
                                    ButtonConnect.setText("Connect");
                                    ButtonConnect.setClickable(true);
                                }
                            } catch (JSONException e) {
                                Toast.makeText(getActivity(), "Failed! wrong api key or wrong directory!", Toast.LENGTH_LONG).show();
                                settings.edit().putBoolean("complete", false).commit();
                                ButtonConnect.setText("Connect");
                                ButtonConnect.setClickable(true);
                            }

                        }
                        @Override
                        public void onFailure(java.lang.Throwable error, String response) {
                            try {
                                if (response.toString().length() > 5) {
                                    Toast.makeText(getActivity(), Html.fromHtml(response.toString()), Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_LONG).show();
                                }
                            } catch (Exception c) {
                                Toast.makeText(getActivity(), "Oh no! " + error.toString(), Toast.LENGTH_LONG).show();
                            }

                            settings.edit().putBoolean("complete", false).commit();
                            ButtonConnect.setText("Connect");
                            ButtonConnect.setClickable(true);
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

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
