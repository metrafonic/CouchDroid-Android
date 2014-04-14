package com.metrafonic.couchdroid;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MovieList.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MovieList#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class MovieList extends Fragment {
    //final SharedPreferences settings = getActivity().getSharedPreferences("test", 0);
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "type";
    private static final String ARG_PARAM2 = "data";

    // TODO: Rename and change types of parameters
    private String type;
    private String data;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MovieList.
     */
    // TODO: Rename and change types and number of parameters
    public static MovieList newInstance(String param1, String param2) {
        MovieList fragment = new MovieList();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    public MovieList() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            type = getArguments().getString(ARG_PARAM1);
            data = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final SharedPreferences settings = getActivity().getSharedPreferences("data", 0);
        if (getArguments() != null) {
            type = getArguments().getString(ARG_PARAM1);
            data = getArguments().getString(ARG_PARAM2);
        }

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_movie_list, container, false);
        TextView text = (TextView) view.findViewById(R.id.textView);
        //text.setText(type + "\n\n" + settings.getString("test", "none"));

        boolean memCache = false;
        boolean fileCache = true;
        String title = "none";
        String plot = "none";
        String poster = "none";
        JSONObject jsonResponse;
        try {
            jsonResponse = new JSONObject(settings.getString("data", "none"));
            title = jsonResponse.getJSONArray("movies").getJSONObject(0).getJSONObject("library").getJSONObject("info").getJSONArray("titles").getString(0);
            LinearLayout movieLayout = (LinearLayout) view.findViewById(R.id.homecell);

            for (int i = 0; i < jsonResponse.getJSONArray("movies").length(); i++) {
                discard:
                if (1+1==2) {

                    keep:
                    if (type.contains("wanted")) {
                        if (jsonResponse.getJSONArray("movies").getJSONObject(i).getJSONArray("releases").length() > 0) {
                            switch (jsonResponse.getJSONArray("movies").getJSONObject(i).getJSONArray("releases").getJSONObject(0).getInt("status_id")) {
                                case 8:
                                    break discard;
                                case 6:
                                    break discard;
                                case 5:
                                    break discard;
                                case 4:
                                    break discard;
                                case 3:
                                    break discard;
                                case 2:
                                    break discard;
                            }
                            break keep;
                        }
                    } else {
                        if (jsonResponse.getJSONArray("movies").getJSONObject(i).getJSONArray("releases").length() == 0) {
                            break discard;
                        }
                        if (jsonResponse.getJSONArray("movies").getJSONObject(i).getJSONArray("releases").length() > 0) {
                            switch (jsonResponse.getJSONArray("movies").getJSONObject(i).getJSONArray("releases").getJSONObject(0).getInt("status_id")) {
                                case 8:
                                    break keep;
                                case 6:
                                    break keep;
                                case 5:
                                    break keep;
                                case 4:
                                    break keep;
                                case 3:
                                    break keep;
                                case 2:
                                    break keep;
                            }
                            break discard;
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
                        aq.id(R.id._image).image(poster, memCache, fileCache, 100, 0, null, AQuery.FADE_IN);
                    } catch (Exception e) {
                        e.printStackTrace();

                    }

                    if (jsonResponse.getJSONArray("movies").getJSONObject(i).getJSONArray("releases").length() > 0) {
                        int status = jsonResponse.getJSONArray("movies").getJSONObject(i).getJSONArray("releases").getJSONObject(0).getInt("status_id");
                        switch (jsonResponse.getJSONArray("movies").getJSONObject(i).getJSONArray("releases").getJSONObject(0).getInt("status_id")) {
                            case 1: {
                                movieStatusId.setText("Snatched");
                                movieStatusId.setBackgroundColor(Color.parseColor("#578bc3"));
                                break;
                            }
                            case 2: {
                                movieStatusId.setText("Downloaded");
                                break;
                            }
                            case 3: {
                                movieStatusId.setText("Downloaded #3");
                                break;
                            }
                            case 4: {
                                movieStatusId.setText("Downloaded #4");
                                break;
                            }
                            case 5: {
                                movieStatusId.setText("Downloaded #5");
                                break;
                            }
                            case 6: {
                                movieStatusId.setText("Downloaded");
                                movieStatusId.setBackgroundColor(Color.parseColor("#369545"));
                                break;
                            }
                            case 7: {
                                movieStatusId.setText("Snatched");
                                movieStatusId.setBackgroundColor(Color.parseColor("#a2a232"));
                                break;
                            }
                            case 8: {
                                movieStatusId.setText("Downloaded #8");
                                break;
                            }
                        }
                        //movieStatusId.setBackgroundColor(Color.parseColor("#578bc3"));
                    }
                    movieTitle.setText(title);
                    moviePlot.setText(plot);
                    final String finalTitle = title;
                    final int finalI = i;
                    final int finalI1 = i;
                    cell.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            final ProgressDialog ringProgressDialog = android.app.ProgressDialog.show(getActivity(), "Please wait ...", "Refreshing data ...", true);
                            ringProgressDialog.setCancelable(true);
                           //Toast.makeText(getActivity(), finalTitle, Toast.LENGTH_SHORT).show();
                            Intent myIntent = new Intent(getActivity(), MovieActivity.class);
                            myIntent.putExtra("key", finalI1); //Optional parameters
                            getActivity().startActivity(myIntent);
                            ringProgressDialog.dismiss();


                        }
                    });
                    movieLayout.addView(cell);
                }
            }

        }catch (JSONException e) {
            e.printStackTrace();
        }

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
