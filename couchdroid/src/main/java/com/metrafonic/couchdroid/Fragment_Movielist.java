package com.metrafonic.couchdroid;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by mathias on 2/1/14.
 */
public class Fragment_Movielist extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     *
     * @param sectionNumber
     */
    public static Fragment_Movielist newInstance(String response, int sectionNumber) {
        Fragment_Movielist fragment = new Fragment_Movielist();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putString("response", response);
        fragment.setArguments(args);
        return fragment;
    }

    public Fragment_Movielist() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movielist, container, false);
        //textView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));
        JSONObject jsonResponse = null;
        String title = "none";
        String plot = "none";
        String poster = "none";
        try {
            jsonResponse = new JSONObject(getArguments().getString("response"));
            title = jsonResponse.getJSONArray("movies").getJSONObject(0).getJSONObject("library").getJSONObject("info").getJSONArray("titles").getString(0);
            LinearLayout movieLayout = (LinearLayout) rootView.findViewById(R.id.homecell);
            for (int i = 0; i < jsonResponse.getJSONArray("movies").length(); i++) {
                innerloop:
                if (getArguments().getInt(ARG_SECTION_NUMBER)>0){
                    superloop:
                    if (getArguments().getInt(ARG_SECTION_NUMBER)==1){
                        if (jsonResponse.getJSONArray("movies").getJSONObject(i).getJSONArray("releases").length()>0){
                            switch (jsonResponse.getJSONArray("movies").getJSONObject(i).getJSONArray("releases").getJSONObject(0).getInt("status_id")){
                                case 1:break superloop;
                                case 7:break superloop;
                                case 0:break superloop;
                            }
                            break innerloop;
                        }
                        //break innerloop;
                    }else{
                        if (jsonResponse.getJSONArray("movies").getJSONObject(i).getJSONArray("releases").length()==0){
                            break innerloop;
                        }
                    }
                    View cell = inflater.inflate(R.layout.cellmovielist, container, false);
                    final AQuery aq = new AQuery(cell);
                    TextView movieTitle = (TextView) cell.findViewById(R.id.textViewMovieTitle);
                    TextView moviePlot = (TextView) cell.findViewById(R.id.textViewMoviePlot);
                    TextView movieStatusId = (TextView) cell.findViewById(R.id.textViewMovieStatusId);

                    title = jsonResponse.getJSONArray("movies").getJSONObject(i).getJSONObject("library").getJSONObject("info").getJSONArray("titles").getString(0);
                    plot = jsonResponse.getJSONArray("movies").getJSONObject(i).getJSONObject("library").getJSONObject("info").getString("plot");
                    try {
                        poster = jsonResponse.getJSONArray("movies").getJSONObject(i).getJSONObject("library").getJSONObject("info").getJSONObject("images").getJSONArray("poster").getString(0);
                        aq.id(R.id._image).image(poster);
                    } catch (Exception e) {
                        e.printStackTrace();

                    }

                    if (jsonResponse.getJSONArray("movies").getJSONObject(i).getJSONArray("releases").length()>0){
                        movieStatusId.setText("Done / Snatched");
                        movieStatusId.setBackgroundColor(Color.parseColor("#578bc3"));
                    }
                    movieTitle.setText(title);
                    moviePlot.setText(plot);
                    final String finalTitle = title;
                    cell.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(getActivity(), finalTitle, Toast.LENGTH_SHORT).show();
                        }
                    });
                    movieLayout.addView(cell);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return rootView;
    }

    public interface OnDataRefresh {

    }

}
