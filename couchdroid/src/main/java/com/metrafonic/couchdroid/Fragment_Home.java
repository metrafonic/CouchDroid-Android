package com.metrafonic.couchdroid;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by mathias on 2/1/14.
 */
public class Fragment_Home extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     * @param sectionNumber
     */
    public static Fragment_Home newInstance(String response, int sectionNumber) {
        Fragment_Home fragment = new Fragment_Home();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putString("response", response);
        fragment.setArguments(args);
        return fragment;
    }

    public Fragment_Home() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        JSONObject jsonResponse = null;
        LinearLayout layoutsnatchedavailable = (LinearLayout) rootView.findViewById(R.id.layoutSnatchedAvailable);

        try {
            jsonResponse = new JSONObject(getArguments().getString("response"));
            for (int i = 0; i < jsonResponse.getJSONArray("movies").length(); i++) {
                if (jsonResponse.getJSONArray("movies").getJSONObject(i).getJSONArray("releases").length() > 0) {
                    if (jsonResponse.getJSONArray("movies").getJSONObject(i).getJSONArray("releases").getJSONObject(0).getInt("status_id") == 7 || jsonResponse.getJSONArray("movies").getJSONObject(i).getJSONArray("releases").getJSONObject(0).getInt("status_id") == 1) {
                        View cell = inflater.inflate(R.layout.cell_snatchedavailable, container, false);
                        final AQuery aq = new AQuery(cell);
                        ImageView poster = (ImageView) cell.findViewById(R.id.imageViewPoster);
                        aq.id(R.id.imageViewPoster).image(jsonResponse.getJSONArray("movies").getJSONObject(i).getJSONObject("library").getJSONObject("info").getJSONObject("images").getJSONArray("poster").getString(0));
                        final int finalI = i;
                        cell.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent myIntent = new Intent(getActivity(), Activity_Movieinfo.class);
                                myIntent.putExtra("key", finalI); //Optional parameters
                                myIntent.putExtra("response", getArguments().getString("response"));
                                getActivity().startActivity(myIntent);
                            }
                        });
                        layoutsnatchedavailable.addView(cell);
                    }
                }

            }
            String title = jsonResponse.getJSONArray("movies").getJSONObject(0).getJSONObject("library").getJSONObject("info").getJSONArray("titles").getString(0);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
        }

        return rootView;
    }

    @Override
    public void onResume() {
        Toast.makeText(getActivity(), "resumed", Toast.LENGTH_SHORT).show();
        super.onResume();
    }

}
