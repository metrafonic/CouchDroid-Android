package com.metrafonic.couchdroid;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by hema1506 on 26/11/13.
 */
public class FragmentHome extends Fragment {

    private LinearLayout wantedLayout;

    private int[] images = {R.drawable.ic_launcher, R.drawable.logobanner};
    private View cell;
    private TextView movieTitle;
    float scrollY = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        Bundle bundle = this.getArguments();
        final String response = bundle.getString("movielist");
        final String listtype = bundle.getString("listtype");
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        TextView textViewResponse = (TextView) rootView.findViewById(R.id.textView);
        TextView textViewListType = (TextView) rootView.findViewById(R.id.textViewListType);
        final ScrollView scrollListView = (ScrollView) rootView.findViewById(R.id.scrolllistview);

        textViewListType.setText(listtype);


        final String PREFS_NAME = "ServerPrefsFile";
        final SharedPreferences settings = this.getActivity().getSharedPreferences(PREFS_NAME, 0);
        if (savedInstanceState == null){
            //textViewResponse.setText(response);
        }

        //Organize Json
        final ArrayList<String> wantedMovieslist = new ArrayList<String>();
        final ArrayList<Integer> idMovie = new ArrayList<Integer>();
        final ArrayList<String> posterMovie = new ArrayList<String>();
            final ArrayList<String> plotMovie = new ArrayList<String>();
            JSONObject jsonResponse = null;
        try {
            jsonResponse = new JSONObject(response);
            JSONArray jsonMainNode = jsonResponse.optJSONArray("movies");


            for (int i = 0; i < jsonMainNode.length(); i++) {
                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                JSONObject jsonLibrary = jsonChildNode.getJSONObject("library");
                JSONObject jsonInfo = jsonLibrary.getJSONObject("info");
                JSONArray jsonTitles = jsonInfo.optJSONArray("titles");

                JSONObject jsonImages = jsonInfo.getJSONObject("images");
                JSONArray jsonPoster = jsonImages.getJSONArray("poster");

                posterMovie.add(jsonPoster.get(0).toString());
                idMovie.add(jsonChildNode.getInt("library_id"));
                wantedMovieslist.add(jsonTitles.getString(0));
                plotMovie.add(jsonInfo.getString("plot").toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        //Create Horisontalview elements
        wantedLayout = (LinearLayout) rootView.findViewById(R.id._celllinearLayout);
        for (int i = 0; i < wantedMovieslist.size(); i++) {

            cell = inflater.inflate(R.layout.cellwanted, container, false);
            final AQuery aq = new AQuery(cell);

            final ImageView imageView = (ImageView) cell.findViewById(R.id._image);
            final RelativeLayout cellText = (RelativeLayout) cell.findViewById(R.id.celltext);
            cellText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle data = new Bundle();
                    data.putInt("movieid", Integer.parseInt(imageView.getTag().toString()));
                    data.putString("response", response);
                    android.support.v4.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
                    FragmentMovie myFragment = new FragmentMovie();
                    myFragment.setArguments(data);
                    ft.replace(R.id.fragmentLayout, myFragment);
                    ft.addToBackStack(null);
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
                    ft.commit();
                }
            });
            imageView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Bundle data = new Bundle();
                    data.putInt("movieid", Integer.parseInt(imageView.getTag().toString()));
                    data.putString("response", response);
                    android.support.v4.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
                    FragmentMovie myFragment = new FragmentMovie();
                    myFragment.setArguments(data);
                    ft.replace(R.id.fragmentLayout, myFragment);
                    ft.addToBackStack(null);
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
                    ft.commit();
                    settings.edit().putFloat("scollloc", scrollListView.getY()).commit();

                }
            });
            scrollListView.scrollTo(5, (int) settings.getFloat("scrollloc", 1));

            imageView.setTag(idMovie.get(i).toString());

            movieTitle = (TextView) cell.findViewById(R.id._imageName);
            TextView moviePlot = (TextView) cell.findViewById(R.id.textViewMoviePlot);
            //imageView.setImageResource(images[0]);
            movieTitle.setText(wantedMovieslist.get(i).toString());
            moviePlot.setText(plotMovie.get(i).toString());
            aq.id(R.id._image).image(posterMovie.get(i).toString());

            wantedLayout.addView(cell);
        }


        return rootView;
    }

}
