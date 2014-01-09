package com.metrafonic.couchdroid;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.Inflater;


/**
 * Created by Mathias on 12/9/13.
 */
public class FragmentMovie extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle extras = this.getArguments();
        int movieId = 0;
        String response = null;
        if (getArguments() != null) {
            movieId = extras.getInt("movieid");
            response = extras.getString("response");
        }
        String titleforyoutube="";
        View rootView = inflater.inflate(R.layout.fragment_movieinfo, container, false);
        TextView movieTitle = (TextView) rootView.findViewById(R.id.textViewMovieName);
        TextView moviePlot = (TextView) rootView.findViewById(R.id.textViewMoviePlot);
        TextView movieMPAA = (TextView) rootView.findViewById(R.id.textViewMovieRating);
        TextView movieRuntime = (TextView) rootView.findViewById(R.id.textViewMovieRuntime);
        Button movieDelete = (Button) rootView.findViewById(R.id.buttonMovieDelete);
        final Button movieTrailer = (Button) rootView.findViewById(R.id.buttonMovieTrailer);

        movieTitle.setText(String.valueOf(movieId));

        final String PREFS_NAME = "ServerPrefsFile";
        final SharedPreferences settings = getActivity().getSharedPreferences(PREFS_NAME, 0);
        final String webaddress = settings.getString("webaddress", null);

        final AsyncHttpClient client = new AsyncHttpClient();
        client.setBasicAuth(settings.getString("username", ""), settings.getString("password", ""));

        JSONObject jsonResponse = null;
        final AQuery aq = new AQuery(rootView);

        try {
            //Try to organize the qualities
            JSONObject jsonQualities = null;
            jsonQualities = new JSONObject(settings.getString("responsequality", null).toString());

            //Try to "decompile"(idk??) the MOVIE RESPONSE and put it in tables
            jsonResponse = new JSONObject(response);
            JSONArray jsonMainNode = jsonResponse.optJSONArray("movies");
            for (int i = 0; i < jsonMainNode.length(); i++) {
                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                JSONObject jsonLibrary = jsonChildNode.getJSONObject("library");
                JSONObject jsonInfo = jsonLibrary.getJSONObject("info");
                JSONArray jsonTitles = jsonInfo.optJSONArray("titles");

                if (jsonChildNode.getInt("library_id") == movieId) {
                    JSONObject jsonImages = jsonInfo.getJSONObject("images");
                    JSONArray jsonPoster = jsonImages.getJSONArray("poster");
                    JSONArray jsonBackdrop = jsonImages.getJSONArray("backdrop");

                    if (jsonPoster.length() >= 1){
                        aq.id(R.id.imageViewPoster).image(jsonPoster.get(0).toString());
                    }else{aq.id(R.id.imageViewPoster).image("http://agraphicworld.files.wordpress.com/2010/09/amnesty_002.jpg");}
                    if (jsonBackdrop.length() >= 1){
                        aq.id(R.id.imageViewBackDrop).image(jsonBackdrop.get(0).toString());
                    }else{aq.id(R.id.imageViewBackDrop).image("http://agraphicworld.files.wordpress.com/2010/09/amnesty_002.jpg");}

                    movieTitle.setText(jsonTitles.get(0).toString() + " (" + jsonInfo.getInt("year") + ")");
                    titleforyoutube=jsonTitles.get(0).toString();
                    moviePlot.setText(jsonInfo.getString("plot").toString());
                    movieMPAA.setText("Rated: " + jsonInfo.getString("mpaa").toString());
                    if (jsonInfo.getString("mpaa").toString().contains("null"))
                        movieMPAA.setText("Rated: n/a");
                    movieRuntime.setText("Runtime: " + jsonInfo.getInt("runtime") + "min");
                    if (jsonInfo.getInt("runtime") == 0) movieRuntime.setText("Runtime: n/a");
                    //dialog.cancel();

                    break;
                }
            }

            //idMovie.add(jsonChildNode.getInt("library_id"));
            //list.add(jsonTitles.getString(0));
            final int finalMovieId1 = movieId;

            movieDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    client.get(webaddress+ "/movie.delete" + "?id=" + finalMovieId1, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(String response) {
                            Toast.makeText(getActivity(),
                                    "Successfully deleted movie", Toast.LENGTH_SHORT)
                                    .show();
                            client.get(webaddress + "/movie.list?status=active", new AsyncHttpResponseHandler() {
                                @Override
                                public void onSuccess(String response) {
                                    System.out.println("it worked!");
                                    settings.edit().putString("responsewanted", response).commit();
                                    System.out.println("starting web request2");
                                    client.get(webaddress + "/movie.list?status=done", new AsyncHttpResponseHandler() {
                                        @Override
                                        public void onSuccess(String response) {
                                            settings.edit().putString("responsemanage", response).commit();
                                            settings.edit().putString("currentfragment", "wanted").commit();
                                            getActivity().getSupportFragmentManager().popBackStack();
                                            ((MainActivity) getActivity()).swag();
                                        }

                                        @Override
                                        public void onFailure(java.lang.Throwable error) {
                                            System.out.println("timed out!!!!" + error);
                                            settings.edit().putString("responsewanted", "null").commit();
                                            settings.edit().putString("responsemanage", "null").commit();
                                            settings.edit().putString("currentfragment", "wanted").commit();
                                            settings.edit().putString("errormessage", error.toString()).commit();
                                            ((MainActivity) getActivity()).swag();
                                        }
                                    });
                                }

                                @Override
                                public void onFailure(java.lang.Throwable error) {
                                    System.out.println("timed out!!!!" + error);
                                    settings.edit().putString("responsewanted", "null").commit();
                                    settings.edit().putString("responsemanage", "null").commit();
                                    settings.edit().putString("currentfragment", "wanted").commit();
                                    settings.edit().putString("errormessage", error.toString()).commit();
                                    ((MainActivity) getActivity()).swag();
                                }
                            });
                        }

                        @Override
                        public void onFailure(java.lang.Throwable error) {
                            System.out.println("timed out!!!!" + error);
                            settings.edit().putString("responsewanted", "null").commit();
                            settings.edit().putString("responsemanage", "null").commit();
                            settings.edit().putString("currentfragment", "wanted").commit();
                            settings.edit().putString("errormessage", error.toString()).commit();
                            ((MainActivity) getActivity()).swag();
                        }
                    });
                }
            });
            final String finalTitleforyoutube = titleforyoutube;
            movieTrailer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    movieTrailer.setClickable(false);

                    client.get( "http://gdata.youtube.com/feeds/api/videos?max-results=1&alt=json&q=" + finalTitleforyoutube + " trailer", new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(String response) {

                            try {
                                JSONObject jsonResponseTrailer = new JSONObject(response);
                                String url = jsonResponseTrailer.getJSONObject("feed").getJSONArray("entry").getJSONObject(0).getJSONArray("link").getJSONObject(0).get("href").toString();
                                //Toast.makeText(getActivity(),url,Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getActivity(), TrailerActivity.class);
                                intent.putExtra("url", url);
                                getActivity().startActivity(intent);
                                movieTrailer.setClickable(true);
                            } catch (JSONException e) {
                                Toast.makeText(getActivity(),"No trailer found!",Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                                movieTrailer.setClickable(true);
                            }

                        }
                        @Override
                        public void onFailure(java.lang.Throwable error) {
                        }
                    });

                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }


        return rootView;
    }


}
