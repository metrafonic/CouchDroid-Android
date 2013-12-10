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
        View rootView = inflater.inflate(R.layout.fragment_movieinfo, container, false);
        TextView movieTitle = (TextView) rootView.findViewById(R.id._imageName);
        TextView moviePlot = (TextView) rootView.findViewById(R.id.textViewMoviePlot);

        movieTitle.setText(String.valueOf(movieId));

        JSONObject jsonResponse = null;
        final AQuery aq = new AQuery(rootView);

        try {
            jsonResponse = new JSONObject(response);
            JSONArray jsonMainNode = jsonResponse.optJSONArray("movies");
            for (int i = 0; i < jsonMainNode.length(); i++) {
                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                JSONObject jsonLibrary = jsonChildNode.getJSONObject("library");
                JSONObject jsonInfo = jsonLibrary.getJSONObject("info");
                JSONArray jsonTitles = jsonInfo.optJSONArray("titles");
                //textview1.append("\n" + jsonChildNode.getInt("library_id"));
                //textview1.append("-" + movieID);
                if (jsonChildNode.getInt("library_id") == movieId) {
                    JSONObject jsonImages = jsonInfo.getJSONObject("images");
                    JSONArray jsonPoster = jsonImages.getJSONArray("poster");
                    JSONArray jsonBacdrop = jsonImages.getJSONArray("backdrop");

                    aq.id(R.id._image).image(jsonPoster.get(0).toString());
                    aq.id(R.id.imageViewBackdrop).image(jsonBacdrop.get(0).toString());

                    movieTitle.setText(jsonTitles.get(0).toString() + " (" + jsonInfo.getInt("year") + ")");
                    moviePlot.setText(jsonInfo.getString("plot").toString());
                    //dialog.cancel();

                    break;
                }
            }
            //idMovie.add(jsonChildNode.getInt("library_id"));
            //list.add(jsonTitles.getString(0));
            final int finalMovieId1 = movieId;
            /*
            buttonDeleteMovie.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    client.get(webaddress+ "/movie.delete" + "?id=" + finalMovieId1, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(String response) {
                            Toast.makeText(getActivity(),
                                    "Successfully deleted movie", Toast.LENGTH_SHORT)
                                    .show();

                        }
                    });
                }
            });
            */
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return rootView;
    }
}
