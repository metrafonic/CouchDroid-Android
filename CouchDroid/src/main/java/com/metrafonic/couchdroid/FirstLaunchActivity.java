package com.metrafonic.couchdroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;

/**
 * Created by hema1506 on 09.01.14.
 */
public class FirstLaunchActivity extends Activity {
    protected void onCreate(Bundle savedInstanceState) {
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_setup_1);

        Button EnterButton = (Button) findViewById(R.id.buttonSetupEnter);

        EnterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent myIntent = new Intent(FirstLaunchActivity.this, SetupActivity.class);
                //myIntent.putExtra("key", value); //Optional parameters
                myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                FirstLaunchActivity.this.startActivity(myIntent);

            }
        });


    }
}
