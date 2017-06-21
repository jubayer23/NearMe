package com.creative.nearme;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.creative.nearme.adapter.IconGridAdapter;
import com.creative.nearme.appdata.Constant;
import com.creative.nearme.utils.DeviceInfoUtils;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private GridView gridView;
    private IconGridAdapter iconGridAdapter;
    private String[] iconSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setTitle("");
        //  getSupportActionBar().setIcon(R.mipmap.icon);

        init();


        this.iconSet = Constant.IconSet;
        iconGridAdapter = new IconGridAdapter(this, this.iconSet);
        gridView.setAdapter(iconGridAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        DeviceInfoUtils.checkMarshMallowPermission(this);
        DeviceInfoUtils.checkInternetConnectionAndGps(this);

    }

    private void init() {

        gridView = (GridView) findViewById(R.id.gridview);
        gridView.setOnItemClickListener(this);

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        String value = iconSet[i];

        String levelParams[] = value.split("/");

        Log.d("DEBUG",levelParams[2]);

    }
}
