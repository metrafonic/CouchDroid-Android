package com.metrafonic.couchdroid;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;


public class MovieActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);
        PlaceholderFragment fragment = new PlaceholderFragment();
        fragment.setArguments(getIntent().getExtras());
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, fragment).commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.movie, menu);
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
        private OnFragmentInteractionListener mListener;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_movieitem, container, false);
            final SharedPreferences data = getActivity().getSharedPreferences("data", 0);
            final SharedPreferences settings = getActivity().getSharedPreferences("settings", 0);
            int key = getArguments().getInt("key");
            String response = data.getString("data", "none");

            TextView textViewMovieTitle = (TextView) rootView.findViewById(R.id.textViewMovieTitle);
            TextView textViewMovieYear = (TextView) rootView.findViewById(R.id.textViewMovieYear);
            TextView textViewMoviePlot = (TextView) rootView.findViewById(R.id.textViewMoviePlot);
            TextView textViewMovieMPAA = (TextView) rootView.findViewById(R.id.textViewMPAA);
            TextView textViewMovieRuntime = (TextView) rootView.findViewById(R.id.textViewRuntime);
            TextView textViewMovieIMDB = (TextView) rootView.findViewById(R.id.textViewMovieIMDB);
            TextView textViewMovieIMDBTitle = (TextView) rootView.findViewById(R.id.textViewMovieIMDBTitle);
            final Button buttonMovieTrailer = (Button) rootView.findViewById(R.id.buttonMovieTrailer);
            final Button buttonMovieDelete = (Button) rootView.findViewById(R.id.buttonMovieDelete);
            final AQuery aq = new AQuery(rootView);
            String poster = "";
            String backdrop = "";
            String plot = "";
            String mpaa = "";
            int id = 0;
            double imdb = 0.0;
            int runtime = 0;
            int year = 0;
            String title = "";
            JSONObject jsonResponse = null;
            JSONObject library = null;
            try {
                jsonResponse = new JSONObject(response);
                library = jsonResponse.getJSONArray("movies").getJSONObject(key);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                title = library.getJSONObject("info").getJSONArray("titles").getString(0);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                backdrop = library.getJSONObject("info").getJSONObject("images").getJSONArray("backdrop").getString(0);
            } catch (JSONException e) {
            }
            try {
                poster = library.getJSONObject("info").getJSONObject("images").getJSONArray("poster_original").getString(0);
            } catch (JSONException e) {
            }
            try {
                id = jsonResponse.getJSONArray("movies").getJSONObject(key).getInt("library_id");
            } catch (JSONException e) {
            }
            try {

                year = library.getJSONObject("info").getInt("year");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                runtime = library.getJSONObject("info").getInt("runtime");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                mpaa = library.getJSONObject("info").getString("mpaa");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                imdb = library.getJSONObject("info").getJSONObject("rating").optJSONArray("imdb").getDouble(0);
            } catch (JSONException e) {
                e.printStackTrace();
                imdb = 000;
            }

            try {
                plot = library.getJSONObject("info").getString("plot");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            textViewMovieTitle.setText(title);
            textViewMovieYear.setText("(" + year + ")");
            textViewMoviePlot.setText(plot);
            textViewMovieMPAA.setText(mpaa);
            textViewMovieRuntime.setText(runtime + " minutes");
            textViewMovieIMDB.setText(imdb + " ");
            textViewMovieIMDBTitle.setText("IMDB: " + imdb);
            aq.id(R.id.imageViewBehindTitle).image(backdrop, true, true, 500, 0, null, AQuery.FADE_IN);
            aq.id(R.id.imageViewMoviePoster).image(poster, true, true, 500, 0, null, AQuery.FADE_IN);

            final String finalTitle = title;
            buttonMovieTrailer.setOnClickListener(new View.OnClickListener() {
                final AsyncHttpClient client = new AsyncHttpClient();
                @Override
                public void onClick(View view) {
                    buttonMovieTrailer.setClickable(false);

                    client.get("http://gdata.youtube.com/feeds/api/videos?max-results=1&alt=json&q=" + finalTitle + " trailer", new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(String response) {

                            try {
                                JSONObject jsonResponseTrailer = new JSONObject(response);
                                String url = jsonResponseTrailer.getJSONObject("feed").getJSONArray("entry").getJSONObject(0).getJSONArray("link").getJSONObject(0).get("href").toString();
                                //Toast.makeText(getActivity(),url,Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getActivity(), TrailerActivity.class);
                                intent.putExtra("url", url);
                                getActivity().startActivity(intent);
                                buttonMovieTrailer.setClickable(true);
                            } catch (JSONException e) {
                                Toast.makeText(getActivity(), "No trailer found!", Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                                buttonMovieTrailer.setClickable(true);
                            }

                        }

                        @Override
                        public void onFailure(Throwable error) {
                        }
                    });
                }
            });
            final int finalId = id;
            buttonMovieDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final ProgressDialog ringProgressDialog = ProgressDialog.show(getActivity(), "Deleting movie ...", "Please wait ...", true);
                    ringProgressDialog.setCancelable(true);
                    final AsyncHttpClient client = new AsyncHttpClient();
                    client.get(settings.getString("webaddress", null) + "/movie.delete?id=" + finalId, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(String response) {
                            ringProgressDialog.dismiss();
                            Intent intent = new Intent(getActivity(), MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            getActivity().startActivity(intent);
                            //mListener.onRefreshClicked();
                        }

                        @Override
                        public void onFailure(Throwable error) {
                        }
                    });
                }
            });

            return rootView;
        }

        public interface OnFragmentInteractionListener {
            // TODO: Update argument type and name
            public void onFragmentInteraction(Uri uri);
            public void onRefreshClicked();
        }
    }

}
