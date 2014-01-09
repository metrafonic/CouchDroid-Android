package com.metrafonic.couchdroid;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
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
    ViewGroup rootView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = this.getArguments();
        final String response = bundle.getString("movielist");
        final String listtype = bundle.getString("listtype");
        final String error = bundle.getString("error");
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        TextView textViewResponse = (TextView) rootView.findViewById(R.id.textView);
        TextView textViewListType = (TextView) rootView.findViewById(R.id.textViewListType);
        TextView textViewError = (TextView) rootView.findViewById(R.id.textViewError);
        final ScrollView scrollListView = (ScrollView) rootView.findViewById(R.id.scrolllistview);
        final String PREFS_NAME = "ServerPrefsFile";
        final SharedPreferences settings = this.getActivity().getSharedPreferences(PREFS_NAME, 0);


        if (savedInstanceState == null) {
            //textViewResponse.setText(response);
        }

        try {
            if (error.contains("null") == false) {
                textViewError.setText(Html.fromHtml(error));
            } else {
                textViewError.setText(null);
            }
        } catch (Exception c) {
            textViewError.setText(null);
        }

        //Organize Json
        final ArrayList<String> wantedMovieslist = new ArrayList<String>();
        final ArrayList<Integer> idMovie = new ArrayList<Integer>();
        final ArrayList<Integer> releasesStatusMovie = new ArrayList<Integer>();
        final ArrayList<Integer> releasesQualityMovie = new ArrayList<Integer>();
        final ArrayList<String> posterMovie = new ArrayList<String>();
        final ArrayList<String> plotMovie = new ArrayList<String>();
        final ArrayList<Integer> profileId = new ArrayList<Integer>();

        final ArrayList<String> labelQuality = new ArrayList<String>();


        JSONObject jsonResponse = null;
        JSONObject jsonQualityResponse = null;
        try {
            jsonResponse = new JSONObject(response);
            jsonQualityResponse = new JSONObject(settings.getString("responsequality", ""));


            JSONArray jsonMainNode = jsonResponse.optJSONArray("movies");
            JSONArray jsonQualityMainNode = jsonQualityResponse.optJSONArray("list");

            //System.out.println(settings.getString("responsequality", ""));

            for (int j = 0; j < jsonQualityMainNode.length(); j++) {
                JSONObject jsonQualityChildNode = jsonQualityMainNode.getJSONObject(j);
                //JSONObject jsonLibrary = jsonChildNode.getJSONObject("library");

                if (jsonQualityChildNode.has("label")) {
                    labelQuality.add(jsonQualityChildNode.getString("label"));
                } else labelQuality.add("unknown");
            }


            for (int i = 0; i < jsonMainNode.length(); i++) {
                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                JSONObject jsonLibrary = jsonChildNode.getJSONObject("library");
                JSONArray jsonReleases = jsonChildNode.getJSONArray("releases");
                JSONObject jsonInfo = jsonLibrary.getJSONObject("info");
                JSONArray jsonTitles = jsonInfo.optJSONArray("titles");

                JSONObject jsonImages = jsonInfo.getJSONObject("images");
                JSONArray jsonPoster = jsonImages.getJSONArray("poster");

                if (jsonChildNode.getInt("releases_count") >= 1) {
                    JSONObject jsonRelease = jsonReleases.getJSONObject(0);
                    releasesStatusMovie.add(jsonRelease.getInt("status_id"));
                    releasesQualityMovie.add(jsonRelease.getInt("quality_id"));
                } else {
                    releasesStatusMovie.add(0);
                    releasesQualityMovie.add(0);
                }


                System.out.println("LENGTH: " + jsonMainNode.length() + "i: " + i);

                if (jsonPoster.length() > 0) {
                    posterMovie.add(jsonPoster.get(0).toString());
                } else posterMovie.add("http://agraphicworld.files.wordpress.com/2010/09/amnesty_002.jpg");

                if (jsonChildNode.has("library_id")) {
                    idMovie.add(jsonChildNode.getInt("library_id"));
                } else idMovie.add(99+i);

                if (jsonInfo.has("plot")) {
                    plotMovie.add(jsonInfo.getString("plot").toString());
                } else plotMovie.add("no plot found!");

                if (jsonTitles.length() > 0) {
                    wantedMovieslist.add(jsonTitles.getString(0));
                } else wantedMovieslist.add(null);


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        //Create Horisontalview elements
        wantedLayout = (LinearLayout) rootView.findViewById(R.id._celllinearLayout);
        System.out.print("SIZE:  " + wantedMovieslist.size());
        for (int i = 0; i < wantedMovieslist.size(); i++) {

            cell = inflater.inflate(R.layout.cellwanted, container, false);
            final AQuery aq = new AQuery(cell);

            final ImageView imageView = (ImageView) cell.findViewById(R.id._image);
            final RelativeLayout cellText = (RelativeLayout) cell.findViewById(R.id.celltext);
            cell.setOnClickListener(new View.OnClickListener() {
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
            scrollListView.scrollTo(5, (int) settings.getFloat("scrollloc", 1));

            imageView.setTag(idMovie.get(i).toString());
            movieTitle = (TextView) cell.findViewById(R.id._imageName);
            TextView movieStatusId = (TextView) cell.findViewById(R.id.textViewMovieStatusId);
            TextView moviePlot = (TextView) cell.findViewById(R.id.textViewMoviePlot);
            //imageView.setImageResource(images[0]);
            if (releasesStatusMovie.get(i).toString().contains("0") == false) {
                movieStatusId.setText("Snatched" + releasesStatusMovie.get(i).toString());
                if (releasesStatusMovie.get(i).toString().contains("7")) {
                    movieStatusId.setBackgroundColor(Color.parseColor("#a2a232"));
                    movieStatusId.setText("Snatched - " + labelQuality.get(releasesQualityMovie.get(i) - 1).toString());
                }
                if (releasesStatusMovie.get(i).toString().contains("1")) {
                    movieStatusId.setBackgroundColor(Color.parseColor("#578bc3"));
                    movieStatusId.setText("Snatched - " + labelQuality.get(releasesQualityMovie.get(i) - 1).toString());
                }
                if (releasesStatusMovie.get(i).toString().contains("6")) {
                    movieStatusId.setBackgroundColor(Color.parseColor("#369545"));
                    movieStatusId.setText("Downloaded - " + labelQuality.get(releasesQualityMovie.get(i) - 1).toString());
                }
                if (releasesStatusMovie.get(i).toString().contains("0")) {

                    movieStatusId.setText("Wanted" + labelQuality.get(releasesQualityMovie.get(i) - 1).toString());
                }

            }
            //movieProfile.setText(labelProfile.get(i).toString());
            movieTitle.setText(wantedMovieslist.get(i).toString());
            moviePlot.setText(plotMovie.get(i).toString());
            aq.id(R.id._image).image(posterMovie.get(i).toString());

            wantedLayout.addView(cell);
        }


        return rootView;
    }


}
