package com.creative.nearme;

import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.creative.nearme.adapter.IconGridAdapter;
import com.creative.nearme.appdata.Constant;
import com.creative.nearme.utils.DeviceInfoUtils;
import com.creative.nearme.utils.LastLocationOnly;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.io.File;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private GridView gridView;
    private IconGridAdapter iconGridAdapter;
    private String[] iconSet;

    public static final String KEY_QUERYTYPE = "query_type";
    public static final String KEY_SEARCHTEXT = "query_search";
    public static final String KEY_IS_PLACESEARCH = "is_place_search";
    public static final String KEY_USER_LOC = "user_loc";
    public static final String KEY_TITLE = "title";

    private MaterialSearchView searchView;

    private Location desire_location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setTitle("");
        //  getSupportActionBar().setIcon(R.mipmap.icon);

        desire_location =  getIntent().getParcelableExtra(HomeActivityFragment.EXTRA_LOCATION);

        Log.d("DEBUG",String.valueOf(desire_location.getLatitude()));
        Log.d("DEBUG",String.valueOf(desire_location.getLongitude()));

        init();

        this.iconSet = Constant.IconSet;
        iconGridAdapter = new IconGridAdapter(this, this.iconSet);
        gridView.setAdapter(iconGridAdapter);

        setSearchView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (this.searchView.isSearchOpen()) {
            this.searchView.closeSearch();
            return;
        }
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

    private void setSearchView() {
        searchView = ((MaterialSearchView) findViewById(R.id.search_view));

        this.searchView.setVoiceSearch(false);
        this.searchView.setEllipsize(true);
        this.searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener()
        {
            public boolean onQueryTextChange(String paramAnonymousString)
            {
                return false;
            }

            public boolean onQueryTextSubmit(String paramAnonymousString)
            {

                openPlaceList("",paramAnonymousString,true);
                String search_text = paramAnonymousString;
                //MainActivity.this.openPlaceList(MainActivity.this.name, "");
                return true;
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        String value = iconSet[i];

        String levelParams[] = value.split("/");

        openPlaceList(levelParams[2],"",false);



    }

    private void openPlaceList(String query_type, String search_text, boolean isSearch){

        Intent intent = new Intent(MainActivity.this, PlaceList.class);

        intent.putExtra(KEY_IS_PLACESEARCH, isSearch);
        intent.putExtra(KEY_USER_LOC, desire_location);
        intent.putExtra(KEY_SEARCHTEXT, search_text);
        intent.putExtra(KEY_QUERYTYPE, query_type);
        intent.putExtra(KEY_TITLE, "ALL LIST");

        startActivity(intent);
    }


    public boolean onCreateOptionsMenu(Menu paramMenu) {
        getMenuInflater().inflate(R.menu.category_menu, paramMenu);
        MenuItem localMenuItem = paramMenu.findItem(R.id.action_search);
        searchView.setMenuItem(localMenuItem);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem paramMenuItem) {
        switch (paramMenuItem.getItemId()) {
            case R.id.share:
                //shareLocation();
                break;
            case R.id.more_apps:
                // startActivity(new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=com.ydoodle.mymoneymanager")));
                Toast.makeText(MainActivity.this,"Please publish your app on play store first!",Toast.LENGTH_LONG).show();
                break;
            case R.id.wikipedia:
                startActivity(new Intent(getApplicationContext(), WikipedidaActivity.class));
                break;
            case R.id.cshareApp:
                String str = getApplicationContext().getApplicationInfo().sourceDir;
                Intent localIntent = new Intent("android.intent.action.SEND");
                localIntent.setType("*/*");
                localIntent.putExtra("android.intent.extra.STREAM", Uri.fromFile(new File(str)));
                startActivity(Intent.createChooser(localIntent, "Share app"));
                break;
        }

        return false;
    }

    public void onBackPressed() {
        if (this.searchView.isSearchOpen()) {
            this.searchView.closeSearch();
            return;
        }
        super.onBackPressed();
    }

}
