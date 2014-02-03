package com.metrafonic.couchdroid;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by mathias on 2/1/14.
 */
public class Fragment_Manage extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     *
     * @param sectionNumber
     */
    public static Fragment_Manage newInstance(String response, int sectionNumber) {
        Fragment_Manage fragment = new Fragment_Manage();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putString("response", response);
        fragment.setArguments(args);
        return fragment;
    }

    public Fragment_Manage() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_wanted, container, false);
        return rootView;
    }

    public interface OnDataRefresh {

    }

}
