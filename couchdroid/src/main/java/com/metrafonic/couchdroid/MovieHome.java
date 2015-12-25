package com.metrafonic.couchdroid;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Html;
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

import org.apache.http.auth.AuthScope;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MovieHome.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MovieHome#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class MovieHome extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MovieHome.
     */
    // TODO: Rename and change types and number of parameters
    public static MovieHome newInstance(String param1, String param2) {
        MovieHome fragment = new MovieHome();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    public MovieHome() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        final View view = inflater.inflate(R.layout.fragment_movie_home, container, false);
        JSONObject jsonResponse = null;
        LinearLayout layoutsnatchedavailable = (LinearLayout) view.findViewById(R.id.layoutSnatchedAvailable);
        final EditText searchMovie = (EditText) view.findViewById(R.id.editTextSearch);
        final TextView versionText = (TextView) view.findViewById(R.id.textViewVersion);
        final SharedPreferences data = getActivity().getSharedPreferences("data", 0);
        final SharedPreferences settings = getActivity().getSharedPreferences("settings", 0);
        try {
            String versionName = getActivity().getApplicationContext().getPackageManager()
                    .getPackageInfo(getActivity().getApplicationContext().getPackageName(), 0).versionName;
            versionText.setText("Couchdroid-Android " + versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        try {
            jsonResponse = new JSONObject(data.getString("data", "none"));
            for (int i = 0; i < jsonResponse.getJSONArray("movies").length(); i++) {
                if (jsonResponse.getJSONArray("movies").getJSONObject(i).getJSONArray("releases").length() > 0) {
                    try{
                    if (jsonResponse.getJSONArray("movies").getJSONObject(i).getJSONArray("releases").getJSONObject(0).getString("status").contains("snatched")) {
                        View cell = inflater.inflate(R.layout.cell_snatched_available, container, false);
                        final AQuery aq = new AQuery(cell);
                        ImageView poster = (ImageView) cell.findViewById(R.id.imageViewPoster);
                        aq.id(R.id.imageViewPoster).image(jsonResponse.getJSONArray("movies").getJSONObject(i).getJSONObject("info").getJSONObject("images").getJSONArray("poster").getString(0));
                        final int finalI = i;
                        cell.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent myIntent = new Intent(getActivity(), MovieActivity.class);
                                myIntent.putExtra("key", finalI); //Optional parameters
                                getActivity().startActivity(myIntent);
                            }
                        });
                        layoutsnatchedavailable.addView(cell);
                    }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        
                        Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
                    }
                }

            }
            //String title = jsonResponse.getJSONArray("movies").getJSONObject(0).getJSONObject("library").getJSONObject("info").getJSONArray("titles").getString(0);
        } catch (JSONException e) {
            e.printStackTrace();
            //Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
        }
        searchMovie.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            LinearLayout searchLayout = (LinearLayout) view.findViewById(R.id.layoutSearchCellPlace);
            ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
            final AsyncHttpClient client = new AsyncHttpClient();
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                searchLayout.removeAllViews();
                progressBar.setVisibility(View.VISIBLE);
                client.setBasicAuth(settings.getString("username", null), settings.getString("password", null), new AuthScope(settings.getString("hostname", null), Integer.valueOf(settings.getString("port", null)), AuthScope.ANY_REALM));
                client.get(settings.getString("webaddress", null) + "/movie.search?q=" + URLEncoder.encode(textView.getText().toString()), new AsyncHttpResponseHandler() {
                    public void onSuccess(final String response) {
                        progressBar.setVisibility(View.GONE);
                        int l = 0;
                        int year = 0;
                        String poster = "";
                        try {
                            JSONObject jsonResponse = new JSONObject(response);

                            for (int i = 0; i < jsonResponse.getJSONArray("movies").length(); i++) {
                                l++;
                                final View cell = inflater.inflate(R.layout.cell_search, container, false);
                                TextView cellTitle = (TextView) cell.findViewById(R.id.textView);
                                final String title = jsonResponse.getJSONArray("movies").getJSONObject(i).getJSONArray("titles").getString(0);
                                final String imdb = jsonResponse.getJSONArray("movies").getJSONObject(i).getString("imdb");
                                try {
                                    jsonResponse = new JSONObject(response);
                                    year = jsonResponse.getJSONArray("movies").getJSONObject(i).getInt("year");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    jsonResponse = new JSONObject(response);
                                    poster = jsonResponse.getJSONArray("movies").getJSONObject(i).getJSONObject("images").getJSONArray("poster").getString(0);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                final AQuery aq = new AQuery(cell);
                                aq.id(R.id.imageView2).image(poster);
                                        cellTitle.setText(title + " (" + year + ")");
                                final Handler handler = new Handler();

                                cell.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        //Toast.makeText(getActivity(), imdb, Toast.LENGTH_SHORT).show();
                                        //progressBar.setVisibility(View.VISIBLE);
                                        final ProgressDialog ringProgressDialog = android.app.ProgressDialog.show(getActivity(), "Adding Movie ...", "Please wait ...", true);
                                        ringProgressDialog.setCancelable(true);
                                        client.get(settings.getString("webaddress", null) + "/movie.add?identifier=" + imdb, new AsyncHttpResponseHandler() {
                                            public void onSuccess(final String response) {
                                                Toast.makeText(getActivity(), "Added movie " + title, Toast.LENGTH_SHORT).show();
                                                try {
                                                    Thread.sleep(5000);
                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                }
                                                ringProgressDialog.dismiss();
                                                mListener.onRefreshClicked();


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

                    @Override
                    public void onFailure(java.lang.Throwable error, String response) {
                        try {
                            if (response.toString().length() > 5) {
                                Toast.makeText(getActivity(), Html.fromHtml(response.toString()), Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception c) {
                            Toast.makeText(getActivity(), "Oh no! " + error.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                });

                return false;
            }
        });

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
        public void onRefreshClicked();
    }

}
