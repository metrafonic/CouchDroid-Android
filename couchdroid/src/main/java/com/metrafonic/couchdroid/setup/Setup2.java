package com.metrafonic.couchdroid.setup;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.metrafonic.couchdroid.R;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Setup2.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Setup2#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class Setup2 extends Fragment {
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
     * @return A new instance of fragment Setup2.
     */
    // TODO: Rename and change types and number of parameters
    public static Setup2 newInstance(String param1, String param2) {
        Setup2 fragment = new Setup2();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    public Setup2() {
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

        View rootView = inflater.inflate(R.layout.fragment_setup2, container, false);
        Button NextButton = (Button) rootView.findViewById(R.id.buttonSetupNext);
        final EditText editHostname = (EditText) rootView.findViewById(R.id.editTextSetupHostname);
        final EditText editPort = (EditText) rootView.findViewById(R.id.editTextSetupPort);
        final EditText editDirectory = (EditText) rootView.findViewById(R.id.editTextSetupDirectory);
        final EditText editUsername = (EditText) rootView.findViewById(R.id.editTextSetupUsername);
        final EditText editPassword = (EditText) rootView.findViewById(R.id.editTextSetupPassword);
        final CheckBox setSSL = (CheckBox) rootView.findViewById(R.id.checkBoxSSL);


        final String PREFS_NAME = "settings";
        final SharedPreferences settings = getActivity().getSharedPreferences(PREFS_NAME, 0);
        editHostname.setText(settings.getString("hostname", ""));
        editPort.setText(settings.getString("port", ""));
        editDirectory.setText(settings.getString("directory", ""));
        editUsername.setText(settings.getString("username", ""));
        editPassword.setText(settings.getString("password", ""));
        setSSL.setChecked(settings.getBoolean("ssl", false));

        NextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle data = new Bundle();
                data.putString("hostname", editHostname.getText().toString());
                data.putString("port", editPort.getText().toString());
                data.putString("directory", editDirectory.getText().toString());
                data.putString("username", editUsername.getText().toString());
                data.putString("password", editPassword.getText().toString());
                if (setSSL.isChecked()==true){
                    data.putString("head", "https://");
                }else{
                    data.putString("head", "http://");
                }
                data.putBoolean("ssl", setSSL.isChecked());
                android.support.v4.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
                Setup3 myFragment = new Setup3();
                myFragment.setArguments(data);
                ft.replace(R.id.container, myFragment);
                ft.addToBackStack(null);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
                ft.commit();
            }
        });

        return rootView;
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
