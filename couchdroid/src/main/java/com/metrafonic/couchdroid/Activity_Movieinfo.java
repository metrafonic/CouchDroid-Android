package com.metrafonic.couchdroid;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;

import org.json.JSONException;
import org.json.JSONObject;

public class Activity_Movieinfo extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movieinfo);

        PlaceholderFragment fragment = new PlaceholderFragment();
        fragment.setArguments(getIntent().getExtras());
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, fragment).commit();
        }
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#5c697b")));
        actionBar.setTitle("");
        actionBar.setLogo(R.drawable.couchdroid);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity__movieinfi, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_movieinfo, container, false);
            TextView textViewMovieTitle = (TextView) rootView.findViewById(R.id.textViewMovieTitle);
            TextView textViewMovieYear = (TextView) rootView.findViewById(R.id.textViewMovieYear);
            TextView textViewMoviePlot = (TextView) rootView.findViewById(R.id.textViewMoviePlot);
            final AQuery aq = new AQuery(rootView);

            String response = getArguments().getString("response");
            String poster = "";
            String backdrop = "";
            String plot = "";
            int year = 0;
            int key = getArguments().getInt("key");
            String title = "";

            JSONObject jsonResponse = null;
            try {
                jsonResponse = new JSONObject(getArguments().getString("response"));
                title = jsonResponse.getJSONArray("movies").getJSONObject(key).getJSONObject("library").getJSONObject("info").getJSONArray("titles").getString(0);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                jsonResponse = new JSONObject(getArguments().getString("response"));
                backdrop = jsonResponse.getJSONArray("movies").getJSONObject(key).getJSONObject("library").getJSONObject("info").getJSONObject("images").getJSONArray("backdrop").getString(0);
            } catch (JSONException e) {
            }
            try {
                jsonResponse = new JSONObject(getArguments().getString("response"));
                poster = jsonResponse.getJSONArray("movies").getJSONObject(key).getJSONObject("library").getJSONObject("info").getJSONObject("images").getJSONArray("poster_original").getString(0);
            } catch (JSONException e) {
            }
            try {
                jsonResponse = new JSONObject(getArguments().getString("response"));
                year = jsonResponse.getJSONArray("movies").getJSONObject(key).getJSONObject("library").getJSONObject("info").getInt("year");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                jsonResponse = new JSONObject(getArguments().getString("response"));
                plot = jsonResponse.getJSONArray("movies").getJSONObject(key).getJSONObject("library").getJSONObject("info").getString("plot");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //Toast.makeText(getActivity(), poster, Toast.LENGTH_SHORT).show();
            textViewMovieTitle.setText(title);
            textViewMovieYear.setText("(" + year + ")");
            textViewMoviePlot.setText(plot);
            aq.id(R.id.imageViewBehindTitle).image(backdrop);
            aq.id(R.id.imageViewMoviePoster).image(poster);

            return rootView;
        }
    }

}
