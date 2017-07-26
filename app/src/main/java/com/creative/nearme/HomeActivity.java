package com.creative.nearme;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.creative.nearme.utils.DeviceInfoUtils;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG_MY_FRAGMENT = "myFragment";

    private HomeActivityFragment mFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        if (savedInstanceState == null) {
            // The Activity is NOT being re-created so we can instantiate a new Fragment
            // and add it to the Activity
            mFragment = new HomeActivityFragment();

            getSupportFragmentManager()
                    .beginTransaction()
                    // It's almost always a good idea to use .replace instead of .add so that
                    // you never accidentally layer multiple Fragments on top of each other
                    // unless of course that's your intention
                    .replace(R.id.content_layout, mFragment, TAG_MY_FRAGMENT)
                    .commit();
        } else {
            // The Activity IS being re-created so we don't need to instantiate the Fragment or add it,
            // but if we need a reference to it, we can use the tag we passed to .replace
            mFragment = (HomeActivityFragment) getSupportFragmentManager().findFragmentByTag(TAG_MY_FRAGMENT);
        }

    }


    @Override
    protected void onResume() {
        super.onResume();

        DeviceInfoUtils.checkMarshMallowPermission(this);
        DeviceInfoUtils.checkInternetConnectionAndGps(this);


    }
}
