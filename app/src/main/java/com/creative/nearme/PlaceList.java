package com.creative.nearme;

import android.content.Intent;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.creative.nearme.adapter.PlaceListAdapter;
import com.creative.nearme.alertbanner.AlertDialogForAnything;
import com.creative.nearme.appdata.ApiUrl;
import com.creative.nearme.appdata.AppController;
import com.creative.nearme.model.PlaceInfo;
import com.creative.nearme.model.Result;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class PlaceList extends AppCompatActivity {


    List<Result> places = new ArrayList<>();

    private ListView listView;

    private PlaceListAdapter placeListAdapter;


    private Gson gson;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        init();



        final Intent intent = getIntent();

        String searchText = intent.getStringExtra(MainActivity.KEY_SEARCHTEXT);
        boolean isPlaceSearch = intent.getBooleanExtra(MainActivity.KEY_IS_PLACESEARCH, false);
        String query_type = intent.getStringExtra(MainActivity.KEY_QUERYTYPE);
        final Location user_loc =  intent.getParcelableExtra(MainActivity.KEY_USER_LOC);

        placeListAdapter = new PlaceListAdapter(this,places,user_loc);
        listView.setAdapter(placeListAdapter);

        String url = getApiUrl(isPlaceSearch, query_type, searchText, user_loc);


        sendRequestToGetPlaceList(url);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Result result = places.get(i);

                Intent intent1 = new Intent(PlaceList.this,PlaceDetailsActivity.class);


                intent1.putExtra("ref", result.getReference());
                intent1.putExtra("place_id", result.getPlaceId());
                intent1.putExtra("api_counter", 2);



                startActivity(intent1);

            }
        });


    }


    private void init() {

        gson = new Gson();

        listView = (ListView) findViewById(R.id.place_list);

    }

    private String getApiUrl(boolean isPlaceSearch, String query_type, String searchText, Location user_loc) {
        String url = "";

        if (isPlaceSearch) {

            String query = "";

            try {
                query = "query=" + URLEncoder.encode(searchText, "utf-8");
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }

            // place type to be searched


            String radius = "radius=50000";

            String location = "location=" + user_loc.getLatitude() + "," + user_loc.getLongitude();

            String key = "key=" + ApiUrl.API_KEY[2];

            // Building the parameters to the web service
            String parameters = query + "&" + location + "&" + radius + "&" + key;


            return ApiUrl.url_place_search + parameters;

        } else {

            url = ApiUrl.url_seach_nearby + "type=" + query_type + "&rankby=distance" + "&location=" +
                    user_loc.getLatitude() + "," + user_loc.getLongitude() +
                    "&sensor=false" + "&key=" + ApiUrl.API_KEY[2];
        }
        return url;
    }


    public void sendRequestToGetPlaceList(String url) {

        // TODO Auto-generated method stub
        // final ProgressBar progressBar = (ProgressBar)dialog_add_tag.findViewById(R.id.dialog_progressbar);
        //progressBar.setVisibility(View.VISIBLE);
        showProgressDialog();

        final StringRequest req = new StringRequest(Request.Method.GET, url,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dismissProgressDialog();

                        PlaceInfo placeInfo = gson.fromJson(response, PlaceInfo.class);
                        if(placeInfo.getStatus().equals("OK")){
                            places.addAll(placeInfo.getResults());
                            placeListAdapter.notifyDataSetChanged();
                        }
                        // progressBar.setVisibility(View.GONE);
                        // Log.d("DEBUG", response);

                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                dismissProgressDialog();
                //progressBar.setVisibility(View.GONE);
                AlertDialogForAnything.showAlertDialogWhenComplte(PlaceList.this,
                        "ERROR",
                        "Something went wrong!!",
                        false);

            }
        });

        req.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // TODO Auto-generated method stub
        AppController.getInstance().addToRequestQueue(req);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();
        switch (itemId) {
            case android.R.id.home:

                onBackPressed();
                break;

        }

        return true;
    }


    SpotsDialog dialog;
    public void showProgressDialog() {


       /**/
        if (dialog == null) {
            dialog = new SpotsDialog(PlaceList.this, R.style.Custom);
        }
        if (dialog.isShowing()) {
           dialog.dismiss();
        }

        dialog.show();

    }

    public void dismissProgressDialog() {
        if (dialog == null) {
            return;
        }
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
