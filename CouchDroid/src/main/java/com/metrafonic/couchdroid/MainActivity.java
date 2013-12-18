package com.metrafonic.couchdroid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.lang.reflect.Type;
import java.util.ArrayList;


public class MainActivity extends FragmentActivity {
    SharedPreferences settings;

    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageButton buttonSettings = (ImageButton) findViewById(R.id.buttonSettings);
        final ImageButton buttonRefresh = (ImageButton) findViewById(R.id.imageButtonRefresh);
        final ImageButton buttonSearch = (ImageButton) findViewById(R.id.imageButtonSearch);
        final Button buttonManage = (Button) findViewById(R.id.buttonManage);
        final Button buttonWanted = (Button) findViewById(R.id.buttonWanted);
        final ProgressBar progressloading = (ProgressBar) findViewById(R.id.progressBarLoadingBar);
        final TextView textViewLoading = (TextView) findViewById(R.id.textViewLoading);
        //TextView lobster = (TextView) findViewById(R.id.textViewLobster);
        //Typeface font = Typeface.createFromAsset(lobster.getContext().getAssets(), "fonts/lobster.ttf");
        final String PREFS_NAME = "ServerPrefsFile";
        settings = getSharedPreferences(PREFS_NAME, 0);


        settings.edit().putString("webaddress", ("http://" + settings.getString("hostname", "127.0.0.1") + ":" + settings.getString("port", "80") + "/api/" + settings.getString("apikey", "key"))).commit();
        final String webaddress = settings.getString("webaddress", null);
        System.out.println(webaddress);


        System.out.println("something");

        System.out.println(webaddress);
        final AsyncHttpClient client = new AsyncHttpClient();
        client.setBasicAuth(settings.getString("username", ""), settings.getString("password", ""));
        client.setTimeout(0);
        //lobster.setTypeface(font);

        buttonWanted.setClickable(false);

        if (settings.getString("currentfragment", "no").toString().contains("wanted"))
            buttonWanted.setTypeface(null, Typeface.BOLD);
        if (settings.getString("currentfragment", "no").toString().contains("manage"))
            buttonManage.setTypeface(null, Typeface.BOLD);


        if (savedInstanceState == null) if (settings.getBoolean("serverstatus", false) == true) {
            buttonWanted.setTypeface(null, Typeface.BOLD);
            buttonManage.setTypeface(null, Typeface.NORMAL);
            buttonWanted.setClickable(false);
            buttonManage.setClickable(false);
            buttonRefresh.setClickable(false);
            System.out.println("starting web request");
            System.out.println(webaddress);
            progressloading.setProgress(0);
            textViewLoading.setText("Loading... (" + 0 + "%)");
            //webaddress + "/movie.list?status=active"
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
                            client.get(webaddress + "/quality.list", new AsyncHttpResponseHandler() {
                                @Override
                                public void onSuccess(String response) {
                                    settings.edit().putString("responsequality", response).commit();

                                    client.get(webaddress + "/profile.list", new AsyncHttpResponseHandler() {
                                        @Override
                                        public void onSuccess(String response) {

                                            settings.edit().putString("responseprofile", response).commit();
                                            settings.edit().putString("currentfragment", "wanted").commit();
                                            settings.edit().putString("errormessage", "null").commit();

                                            swag();
                                        }

                                        @Override
                                        public void onFailure(java.lang.Throwable error) {
                                            System.out.println("timed out!!!!" + error);
                                            settings.edit().putString("responsewanted", "null").commit();
                                            settings.edit().putString("responseprofile", "null").commit();
                                            settings.edit().putString("responsemanage", "null").commit();
                                            settings.edit().putString("responsequality", "null").commit();
                                            settings.edit().putString("currentfragment", "wanted").commit();
                                            settings.edit().putString("errormessage", error.toString()).commit();
                                            swag();
                                        }

                                        @Override
                                        public void onProgress(int bytesWritten, int totalSize) {
                                            int prosent = 99;
                                            progressloading.setProgress(prosent);
                                            textViewLoading.setText("Loading... (" + prosent + "%)");

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
                                    swag();
                                }

                                @Override
                                public void onProgress(int bytesWritten, int totalSize) {
                                    int prosent = 75;
                                    progressloading.setProgress(prosent);
                                    textViewLoading.setText("Loading... (" + prosent + "%)");

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
                            swag();
                        }

                        @Override
                        public void onProgress(int bytesWritten, int totalSize) {
                            int prosent = 50;
                            progressloading.setProgress(prosent);
                            textViewLoading.setText("Loading... (" + prosent + "%)");

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
                    swag();
                }

                @Override
                public void onProgress(int bytesWritten, int totalSize) {
                    int prosent = 25;
                    progressloading.setProgress(prosent);
                    textViewLoading.setText("Loading... (" + prosent + "%)");

                }
            });


        } else {
            settings.edit().putString("responsewanted", "null").commit();
            settings.edit().putString("responsemanage", "null").commit();
            settings.edit().putString("currentfragment", "wanted").commit();
            settings.edit().putString("errormessage", "not set up");
            android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
            final android.support.v4.app.FragmentTransaction transaction = fm.beginTransaction();
            ActivitySettings newFragment = new ActivitySettings();
            transaction.add(R.id.fragmentLayout, newFragment, "search");
            //fm.popBackStack(null, fm.POP_BACK_STACK_INCLUSIVE);
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            transaction.commit();
        }
        buttonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
                final android.support.v4.app.FragmentTransaction transaction = fm.beginTransaction();
                ActivitySettings newFragment = new ActivitySettings();
                transaction.replace(R.id.fragmentLayout, newFragment, "wanted");
                fm.popBackStack(null, fm.POP_BACK_STACK_INCLUSIVE);
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                transaction.commit();
            }
        });
        buttonManage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (settings.getBoolean("serverstatus", false) == true) {
                    android.support.v4.app.FragmentManager fm2 = getSupportFragmentManager();
                    final android.support.v4.app.FragmentTransaction transaction2 = fm2.beginTransaction();
                    transaction2.remove(fm2.findFragmentById(R.id.fragmentLayout));
                    transaction2.commit();
                    buttonWanted.setTypeface(null, Typeface.NORMAL);
                    buttonManage.setTypeface(null, Typeface.BOLD);
                    settings.edit().putString("currentfragment", "manage").commit();
                    swag();
                }

            }
        });
        buttonWanted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (settings.getBoolean("serverstatus", false) == true) {
                    android.support.v4.app.FragmentManager fm2 = getSupportFragmentManager();
                    final android.support.v4.app.FragmentTransaction transaction2 = fm2.beginTransaction();
                    transaction2.remove(fm2.findFragmentById(R.id.fragmentLayout));
                    transaction2.commit();
                    buttonWanted.setTypeface(null, Typeface.BOLD);
                    buttonManage.setTypeface(null, Typeface.NORMAL);
                    settings.edit().putString("currentfragment", "wanted").commit();
                    swag();
                }

            }
        });

        buttonRefresh.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (settings.getBoolean("serverstatus", false) == true) {
                    final AsyncHttpClient clientnew = new AsyncHttpClient();
                    clientnew.setBasicAuth(settings.getString("username", ""), settings.getString("password", ""));
                    final String webaddressnew = settings.getString("webaddress", null);
                    System.out.println("pressed refresh");
                    System.out.println(webaddressnew);
                    buttonWanted.setClickable(false);
                    buttonWanted.setTypeface(null, Typeface.BOLD);
                    buttonManage.setTypeface(null, Typeface.NORMAL);
                    buttonManage.setClickable(false);
                    buttonRefresh.setClickable(false);
                    android.support.v4.app.FragmentManager fm2 = getSupportFragmentManager();
                    final android.support.v4.app.FragmentTransaction transaction2 = fm2.beginTransaction();
                    transaction2.remove(fm2.findFragmentById(R.id.fragmentLayout));
                    transaction2.commit();
                    progressloading.setProgress(0);
                    textViewLoading.setText("Loading... (" + 0 + "%)");

                    clientnew.get(webaddressnew + "/movie.list?status=active", new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(String response) {
                            System.out.println("it worked!");
                            settings.edit().putString("responsewanted", response).commit();
                            clientnew.get(webaddressnew + "/movie.list?status=done", new AsyncHttpResponseHandler() {
                                @Override
                                public void onSuccess(String response) {
                                    settings.edit().putString("responsemanage", response).commit();
                                    clientnew.get(webaddressnew + "/quality.list", new AsyncHttpResponseHandler() {
                                        @Override
                                        public void onSuccess(String response) {
                                            settings.edit().putString("responsequality", response).commit();
                                            clientnew.get(webaddressnew + "/profile.list", new AsyncHttpResponseHandler() {
                                                @Override
                                                public void onSuccess(String response) {
                                                    settings.edit().putString("responseprofile", response).commit();
                                                    settings.edit().putString("currentfragment", "wanted").commit();
                                                    settings.edit().putString("errormessage", "null").commit();
                                                    buttonWanted.setClickable(true);
                                                    buttonManage.setClickable(true);
                                                    buttonRefresh.setClickable(true);
                                                    swag();
                                                }

                                                @Override
                                                public void onFailure(java.lang.Throwable error) {
                                                    System.out.println("timed out!!!!" + error);
                                                    settings.edit().putString("responsewanted", "null").commit();
                                                    settings.edit().putString("responsemanage", "null").commit();
                                                    settings.edit().putString("currentfragment", "wanted").commit();
                                                    settings.edit().putString("errormessage", error.toString()).commit();
                                                    buttonWanted.setClickable(true);
                                                    buttonManage.setClickable(true);
                                                    buttonRefresh.setClickable(true);
                                                    swag();
                                                }

                                                @Override
                                                public void onProgress(int bytesWritten, int totalSize) {
                                                    int prosent = 99;
                                                    progressloading.setProgress(prosent);
                                                    textViewLoading.setText("Loading... (" + prosent + "%)");

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
                                            buttonWanted.setClickable(true);
                                            buttonManage.setClickable(true);
                                            buttonRefresh.setClickable(true);
                                            swag();
                                        }

                                        @Override
                                        public void onProgress(int bytesWritten, int totalSize) {
                                            int prosent = 75;
                                            progressloading.setProgress(prosent);
                                            textViewLoading.setText("Loading... (" + prosent + "%)");

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
                                    buttonWanted.setClickable(true);
                                    buttonManage.setClickable(true);
                                    buttonRefresh.setClickable(true);
                                    swag();
                                }

                                @Override
                                public void onProgress(int bytesWritten, int totalSize) {
                                    int prosent = 50;
                                    progressloading.setProgress(prosent);
                                    textViewLoading.setText("Loading... (" + prosent + "%)");

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
                            buttonWanted.setClickable(true);
                            buttonManage.setClickable(true);
                            buttonRefresh.setClickable(true);
                            swag();

                        }

                        @Override
                        public void onProgress(int bytesWritten, int totalSize) {
                            int prosent = 25;
                            progressloading.setProgress(prosent);
                            textViewLoading.setText("Loading... (" + prosent + "%)");

                        }
                    });
                }

            }
        });
        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonSearch.setClickable(false);
                if (settings.getBoolean("serverstatus", false) == true) {
                    android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
                    final android.support.v4.app.FragmentTransaction transaction = fm.beginTransaction();
                    FragmentSearch newFragment = new FragmentSearch();
                    transaction.add(R.id.fragmentLayout, newFragment, "search");
                    fm.popBackStack(null, fm.POP_BACK_STACK_INCLUSIVE);
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    transaction.addToBackStack(null);
                    transaction.commit();
                    settings.edit().putString("currentfragment", "wanted").commit();
                    buttonWanted.setClickable(true);
                    buttonWanted.setTypeface(null, Typeface.NORMAL);
                    buttonManage.setClickable(true);
                    buttonManage.setTypeface(null, Typeface.NORMAL);
                    buttonSearch.setClickable(true);
                }

            }
        });


    }

    public void swag() {
        System.out.println("running SWAG");
        final String PREFS_NAME = "ServerPrefsFile";
        Bundle data = new Bundle();
        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        final android.support.v4.app.FragmentTransaction transaction = fm.beginTransaction();
        FragmentHome newFragment = new FragmentHome();


        settings = getSharedPreferences(PREFS_NAME, 0);
        if (settings.getString("currentfragment", null).contains("wanted")) {
            data.putString("movielist", settings.getString("responsewanted", "null").toString());
            data.putString("listtype", "Wanted Movies:");
            data.putString("error", settings.getString("errormessage", null));
            transaction.replace(R.id.fragmentLayout, newFragment, "wanted");
        } else if (settings.getString("currentfragment", null).contains("manage")) {
            data.putString("movielist", settings.getString("responsemanage", "null").toString());
            data.putString("listtype", "Manage Movies:");
            data.putString("error", settings.getString("errormessage", null));
            transaction.replace(R.id.fragmentLayout, newFragment, "manage");
        }


        newFragment.setArguments(data);

        fm.popBackStack(null, fm.POP_BACK_STACK_INCLUSIVE);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.commit();


    }

}
