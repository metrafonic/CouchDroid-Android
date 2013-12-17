package com.metrafonic.couchdroid;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.util.Progress;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by Mathias on 12/12/13.
 */
public class FragmentSearch extends Fragment {
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_search, container, false);
        EditText searchMovie = (EditText) rootView.findViewById(R.id.editTextSearch);
        final ProgressBar searchProgress = (ProgressBar) rootView.findViewById(R.id.progressBarSearch);
        final LinearLayout searchLayout = (LinearLayout) rootView.findViewById(R.id.LinearLayoutSearch);

        final AsyncHttpClient client = new AsyncHttpClient();
        final String PREFS_NAME = "ServerPrefsFile";
        final SharedPreferences settings = getActivity().getSharedPreferences(PREFS_NAME, 0);


        final String webaddress = settings.getString("webaddress", null);


        searchMovie.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchProgress.setVisibility(View.VISIBLE);
                    searchLayout.removeAllViews();
                    System.out.println(webaddress + "/movie.search?q=" + URLEncoder.encode(v.getText().toString()));
                    client.get(webaddress + "/movie.search?q=" + URLEncoder.encode(v.getText().toString()), new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(String response) {
                            searchProgress.setVisibility(View.GONE);


                            final ArrayList<String> nameMovie = new ArrayList<String>();
                            final ArrayList<String> imdbMovie = new ArrayList<String>();
                            final ArrayList<String> posterMovie = new ArrayList<String>();
                            final ArrayList<String> yearMovie = new ArrayList<String>();


                            //TODO: fix crash when no result
                            JSONObject jsonResponse = null;
                            try {
                                jsonResponse = new JSONObject(response);
                                JSONArray jsonMainNode = jsonResponse.optJSONArray("movies");


                                for (int i = 0; i < jsonMainNode.length(); i++) {

                                    JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                                    JSONArray jsonTitles = jsonChildNode.getJSONArray("titles");
                                    JSONObject jsonImages = jsonChildNode.getJSONObject("images");
                                    JSONArray jsonPosters = jsonImages.getJSONArray("poster");

                                    if (jsonChildNode.has("imdb")) {
                                        imdbMovie.add(jsonChildNode.getString("imdb"));
                                    } else imdbMovie.add("null");

                                    if (jsonTitles.length() >= 1) {
                                        nameMovie.add(jsonTitles.getString(0));
                                    } else nameMovie.add("unknown");

                                    if (jsonPosters.length() >= 1) {
                                        posterMovie.add(jsonPosters.get(0).toString());
                                    } else posterMovie.add("null");


                                    if (jsonChildNode.has("year")) {
                                        yearMovie.add(jsonChildNode.getString("year"));
                                    } else yearMovie.add("null");


                                    //System.out.print("JsonMainNode: " + jsonMainNode.length() + ". JsonTitles" + jsonTitles.length() + "\n");

                                }
                                //Toast.makeText(getActivity(), "JsonMainNode: " + jsonMainNode.length() + ". JsonTitles", Toast.LENGTH_SHORT).show();


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            for (int i = 0; i < nameMovie.size(); i++) {
                                View cell = inflater.inflate(R.layout.cellsearch, container, false);
                                final AQuery aq = new AQuery(cell);
                                TextView movieTitle = (TextView) cell.findViewById(R.id.textViewSearchTitle);
                                //ImageView moviePoster = (ImageView) cell.findViewById(R.id.imageViewSearchPoster);
                                aq.id(R.id.imageViewSearchPoster).image(posterMovie.get(i).toString());
                                movieTitle.setText(nameMovie.get(i).toString() + " (" + yearMovie.get(i).toString() + ")");
                                searchLayout.addView(cell);
                                final int finalI = i;
                                cell.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        launchRingDialog(rootView, imdbMovie.get(finalI));

                                    }
                                });


                            }
                            settings.edit().putString("errormessage", "").commit();
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

                    return true;
                }
                return false;
            }
        });


        return rootView;
    }

    public void launchRingDialog(View view, final String imdb) {
        final ProgressDialog ringProgressDialog = ProgressDialog.show(getActivity(), "Please wait ...", "Adding Movie...", true);
        final String PREFS_NAME = "ServerPrefsFile";
        final SharedPreferences settings = getActivity().getSharedPreferences(PREFS_NAME, 0);
        final String webaddress = settings.getString("webaddress", null);
        ringProgressDialog.setCancelable(true);
        final AsyncHttpClient client = new AsyncHttpClient();

        final Handler handler = new Handler();

        client.get(webaddress + "/movie.add?identifier=" + imdb, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            Thread.sleep(4000);
                            client.get(webaddress + "/movie.list?status=active", new AsyncHttpResponseHandler() {
                                @Override
                                public void onSuccess(String response2) {
                                    settings.edit().putString("responsewanted", response2).commit();
                                    settings.edit().putString("currentfragment", "wanted").commit();
                                    settings.edit().putString("errormessage", "");
                                    //getActivity().getSupportFragmentManager().popBackStack();
                                    ringProgressDialog.dismiss();
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
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
                                    ringProgressDialog.dismiss();
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            ((MainActivity) getActivity()).swag();
                                        }
                                    });
                                }
                            });

                        } catch (Exception e) {
                        }

                        //Toast.makeText(getActivity(), "You clicked on OK", Toast.LENGTH_SHORT).show();
                    }
                }).start();
            }

            @Override
            public void onFailure(java.lang.Throwable error) {
                System.out.println("timed out!!!!" + error);
                settings.edit().putString("responsewanted", "null").commit();
                settings.edit().putString("responsemanage", "null").commit();
                settings.edit().putString("currentfragment", "wanted").commit();
                settings.edit().putString("errormessage", error.toString()).commit();
                ringProgressDialog.dismiss();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ((MainActivity) getActivity()).swag();
                    }
                });
            }
        });

    }
}
