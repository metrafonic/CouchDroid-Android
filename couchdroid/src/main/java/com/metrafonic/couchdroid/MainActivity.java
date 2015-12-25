package com.metrafonic.couchdroid;

import java.util.Locale;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.auth.AuthScope;


public class MainActivity extends ActionBarActivity implements MovieActivity.PlaceholderFragment.OnFragmentInteractionListener,ActionBar.TabListener,MovieHome.OnFragmentInteractionListener, MovieList.OnFragmentInteractionListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;
    //final SharedPreferences settings = getSharedPreferences("test", 0);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getActionBar().setIcon(R.drawable.logo);
        getActionBar().setTitle("");

        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(3);
        final SharedPreferences settings = getSharedPreferences("settings", 0);
        if (settings.getString("webaddress", null)==null){
            Intent myIntent = new Intent(this, SettingsActivity.class);
            this.startActivity(myIntent);
        }else{
            if (savedInstanceState==null){
                onRefreshClicked();
            }
        }




        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            onRefreshClicked();
            return true;
        }
        if (id == R.id.action_settings) {
            Intent myIntent = new Intent(this, SettingsActivity.class);
            this.startActivity(myIntent);
            return true;
        }
        if (id == R.id.action_renamer) {
            simpleHttp("/renamer.scan");
            return true;
        }
        if (id == R.id.action_restart) {
            simpleHttp("/app.restart");
            return true;
        }
        if (id == R.id.action_shutdown) {
            simpleHttp("/app.shutdown");
            return true;
        }
        if (id == R.id.action_update) {
            simpleHttp("/updater.check");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        Toast.makeText(this, uri.toString(), Toast.LENGTH_SHORT).show();

    }
    @Override
    public void onRefreshClicked() {
        final ProgressDialog ringProgressDialog = ProgressDialog.show(MainActivity.this, "Refreshing data ...", "Please wait ...", true);

        ringProgressDialog.setCancelable(true);

        final AsyncHttpClient client = new AsyncHttpClient();
        final SharedPreferences data = getSharedPreferences("data", 0);
        final SharedPreferences settings = getSharedPreferences("settings", 0);
        client.setBasicAuth(settings.getString("username",null),settings.getString("password",null), new AuthScope(settings.getString("hostname", null), Integer.valueOf(settings.getString("port",null)), AuthScope.ANY_REALM));
        client.get(settings.getString("webaddress", null) + "/movie.list", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                data.edit().putString("data", response).commit();
                mSectionsPagerAdapter.notifyDataSetChanged();
                ringProgressDialog.dismiss();

            }

            public void onFailure(java.lang.Throwable error, String response) {
                try {
                    if (response.toString().length() > 5) {
                        Toast.makeText(MainActivity.this, Html.fromHtml(response.toString()), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception c) {
                    Toast.makeText(MainActivity.this, "Oh no! " + error.toString(), Toast.LENGTH_LONG).show();
                }
                System.out.println(error.toString());
                ringProgressDialog.dismiss();
            }
            });

    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            Fragment frag = null;
            switch (position){
                case 0:
                    frag = MovieHome.newInstance("home", "data");
                    break;
                case 1:
                    frag = MovieList.newInstance("wanted", "data");
                    break;
                case 2:
                    frag = MovieList.newInstance("manage", "data");
                    break;

            }
            return frag;
        }
        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }



        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }
    }
    public void simpleHttp(String extra){
        final AsyncHttpClient client = new AsyncHttpClient();
        final SharedPreferences data = getSharedPreferences("data", 0);
        final SharedPreferences settings = getSharedPreferences("settings", 0);
        final ProgressDialog ringProgressDialog = ProgressDialog.show(MainActivity.this, "Executing Command ...", "Executing: " + extra, true);
        client.setBasicAuth(settings.getString("username",null),settings.getString("password",null), new AuthScope(settings.getString("hostname", null), Integer.valueOf(settings.getString("port",null)), AuthScope.ANY_REALM));
        client.get(settings.getString("webaddress", null) + extra, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {

                Toast.makeText(MainActivity.this, Html.fromHtml(response.toString()), Toast.LENGTH_LONG).show();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ringProgressDialog.dismiss();
            }

            public void onFailure(java.lang.Throwable error, String response) {
                try {
                    if (response.toString().length() > 5) {
                        Toast.makeText(MainActivity.this, Html.fromHtml(response.toString()), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception c) {
                    Toast.makeText(MainActivity.this, "Oh no! " + error.toString(), Toast.LENGTH_LONG).show();
                }
                ringProgressDialog.dismiss();
            }
        });
    }




}
