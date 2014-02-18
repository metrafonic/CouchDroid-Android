package com.metrafonic.couchdroid;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;

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
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        final EditText searchMovie = (EditText) rootView.findViewById(R.id.editTextSearch);
        final AsyncHttpClient client = new AsyncHttpClient();
        createlay(inflater, container, savedInstanceState, getArguments().getString("response"));

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
        searchMovie.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            LinearLayout searchLayout = (LinearLayout) rootView.findViewById(R.id.layoutSearchCellPlace);
            ProgressBar progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);


            @Override
            apublic boolean onEditorAction(final TextView v, int actionId, KeyEvent keyEvent) {
                searchLayout.removeAllViews();
                progressBar.setVisibility(View.VISIBLE);
                client.get("http://couchpotato.metrafonic.com/api/5i78ot5xybtobtptv7t87c65cie5i75cicrck67ce7cei7c"+ "/movie.search?q=" + URLEncoder.encode(v.getText().toString()), new AsyncHttpResponseHandler() {
                    public void onSuccess(final String response) {
                        progressBar.setVisibility(View.GONE);
                        int l = 0;
                        try {
                            JSONObject jsonResponse = new JSONObject(response);

                            for (int i = 0; i < jsonResponse.getJSONArray("movies").length(); i++) {
                                l++;
                                final View cell = inflater.inflate(R.layout.cell_search, container, false);
                                TextView cellTitle = (TextView) cell.findViewById(R.id.textView);
                                final String title = jsonResponse.getJSONArray("movies").getJSONObject(i).getJSONArray("titles").getString(0);
                                final String imdb = jsonResponse.getJSONArray("movies").getJSONObject(i).getString("imdb");
                                cellTitle.setText(title);
                                cell.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        //Toast.makeText(getActivity(), imdb, Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.VISIBLE);
                                        client.get("http://couchpotato.metrafonic.com/api/5i78ot5xybtobtptv7t87c65cie5i75cicrck67ce7cei7c"+ "/movie.add?identifier=" + imdb, new AsyncHttpResponseHandler() {
                                            public void onSuccess(final String response) {
                                                Toast.makeText(getActivity(), "Added movie " + title, Toast.LENGTH_SHORT).show();

                                                client.get("http://couchpotato.metrafonic.com/api/5i78ot5xybtobtptv7t87c65cie5i75cicrck67ce7cei7c/movie.list", new AsyncHttpResponseHandler() {
                                                    @Override
                                                    public void onSuccess(String response) {
                                                        searchLayout.removeAllViews();
                                                        searchMovie.setText("");
                                                        createlay(inflater, container, savedInstanceState, response);
                                                        Toast.makeText(getActivity(), "SWAAAG ", Toast.LENGTH_SHORT).show();
                                                    }
                                                    public void onFailure(java.lang.Throwable error, String response) {
                                                        System.out.println(error.toString());
                                                    }
                                                });

                                            }
                                            @Override
                                            public void onFailure(java.lang.Throwable error) {
                                                progressBar.setVisibility(View.GONE);
                                                searchLayout.removeAllViews();
                                            }
                                        });
                                    }
                                });
                                searchLayout.addView(cell);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                return false;
            }
        });

        return rootView;
    }

    public void createlay(final LayoutInflater inflater, final ViewGroup container,
                          Bundle savedInstanceState, String response){

    }

    @Override
    public void onResume() {

        super.onResume();
    }

}
