package com.creative.nearme;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.creative.nearme.alertbanner.AlertDialogForAnything;
import com.creative.nearme.appdata.ApiUrl;
import com.creative.nearme.appdata.AppController;
import com.creative.nearme.model.PlaceDetailInfo;
import com.creative.nearme.utils.LastLocationOnly;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import dmax.dialog.SpotsDialog;

public class PlaceDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {

    FloatingActionButton direction;
    GoogleMap mMap;
    TextView tv_name;
    TextView tv_address;
    TextView tv_distance;
    TextView tv_phoneno;
    TextView tv_website;
    NetworkImageView img_placeimg;
    Location user_loc;

    Gson gson;

    PlaceDetailInfo.Result result;

    int api_counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_details);

        init();

        initMap();

        Intent intent = getIntent();


        String place_reference = intent.getStringExtra("ref");
         api_counter = intent.getIntExtra("api_counter", 0);


        String url = getApiUrl(place_reference, api_counter);


        sendRequestToGetPlaceDetails(url);


    }

    private void init() {

        gson = new Gson();

        tv_name = (TextView) findViewById(R.id.tvpname);
        tv_address = (TextView) findViewById(R.id.tvpaddress);
        tv_distance = (TextView) findViewById(R.id.tvpdistance);
        tv_phoneno = (TextView) findViewById(R.id.tvpphoneNo);
        tv_website = (TextView) findViewById(R.id.tvpwebSite2);

        img_placeimg = (NetworkImageView) findViewById(R.id.imgplace);


    }

    private void updateUi(PlaceDetailInfo.Result result) {


        tv_name.setText(result.getName());
        tv_address.setText(result.getFormattedAddress() == null ? "" : result.getFormattedAddress());
        tv_phoneno.setText(result.getFormattedPhoneNumber() == null ? "" : result.getFormattedPhoneNumber());
        tv_website.setText(result.getWebsite() == null ? "" : result.getWebsite());
        if (result.getPhotos() != null && result.getPhotos().size() > 0 && result.getPhotos().get(0).getPhotoReference() != null
                && !result.getPhotos().get(0).getPhotoReference().isEmpty()) {
            ImageLoader imageLoader = AppController.getInstance().getImageLoader();
            img_placeimg.setImageUrl(
                    ApiUrl.url_photo_base +
                            result.getPhotos().get(0).getPhotoReference()
                            + "&key=" + ApiUrl.API_KEY[api_counter], imageLoader);
        }

        LatLng destination_latlng = new LatLng(result.getGeometry().getLocation().getLat(),
                result.getGeometry().getLocation().getLng());

        mMap.addMarker(new MarkerOptions()
                .position(destination_latlng)
                .title(result.getName()));

        LastLocationOnly lastLocationOnly = new LastLocationOnly(this);
        if(lastLocationOnly.canGetLocation()){
            LatLng user_latlng = new LatLng(lastLocationOnly.getLatitude(),
                    lastLocationOnly.getLongitude());

            com.google.android.gms.maps.model.LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
            boundsBuilder.include(destination_latlng);
            boundsBuilder.include(user_latlng);
            final LatLngBounds bounds = boundsBuilder.build();
            mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 120));
                }
            });



                float[] results = new float[1];
                Location.distanceBetween(lastLocationOnly.getLatitude(),
                        lastLocationOnly.getLongitude(), result.getGeometry().getLocation().getLat(),
                        result.getGeometry().getLocation().getLng(), results);
                if ((int) results[0] <= 1000) {
                    tv_distance.setText(String.valueOf(results[0]).substring(0, 3) + "m");
                } else {
                    tv_distance.setText(String.valueOf(results[0] / 1000).substring(0, 3) + "km");
                }

        }else{
            mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    mapZoomToDestinationLoc();
                }
            });
        }

    }
    private void mapZoomToDestinationLoc() {
        LatLng latLng = new LatLng(result.getGeometry().getLocation().getLat(),
                result.getGeometry().getLocation().getLng());
        CameraPosition cameraPosition = new CameraPosition.Builder().
                target(latLng).
                tilt(0).
                zoom(15).
                bearing(0).
                build();
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    public void openDialPad(View paramView)
    {
        String str = tv_phoneno.getText().toString();
        if (str.equals(""))
        {
            Toast.makeText(getApplicationContext(), "no number found...", Toast.LENGTH_LONG).show();
            return;
        }
        Intent localIntent = new Intent("android.intent.action.DIAL");
        localIntent.setData(Uri.parse("tel:" + str));
        startActivity(localIntent);
    }

    public void openUrl(View paramView)
    {
        String str = tv_website.getText().toString();
        if (str.equals("Website not found."))
        {
            Toast.makeText(getApplicationContext(), "no url found..", Toast.LENGTH_LONG).show();
            return;
        }
        Intent localIntent = new Intent("android.intent.action.VIEW");
        localIntent.setData(Uri.parse(str));
        startActivity(localIntent);
    }

    public void showDirection(View paramView)
    {
        LastLocationOnly lastLocationOnly = new LastLocationOnly(this);
        if(result == null || !lastLocationOnly.canGetLocation() || (result.getGeometry().getLocation().getLat() <= 0.0D &&
                        result.getGeometry().getLocation().getLng() <= 0.0D)){
            Toast.makeText(getApplicationContext(), "No address found try again later", Toast.LENGTH_LONG).show();
            return;
        }

        Intent localIntent = new Intent("android.intent.action.VIEW", Uri.parse("http://maps.google.com/maps?saddr=" +
                lastLocationOnly.getLatitude() + "," + lastLocationOnly.getLongitude() +
                "&daddr=" + result.getGeometry().getLocation().getLat() + "," +result.getGeometry().getLocation().getLng()));
        localIntent.addFlags(268435456);
        localIntent.addCategory("android.intent.category.LAUNCHER");
        localIntent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
        startActivity(localIntent);
    }

    private void initMap() {
        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (mMap != null) {
            return;
        }
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
    }

    private String getApiUrl(String reference, int api_counter) {
        // TODO Auto-generated method stub

        String placeDetails_url = ApiUrl.url_place_details + "reference="
                + reference + "&key=" + ApiUrl.API_KEY[api_counter];

        Log.d("DEBUG_placeDetails_url", placeDetails_url);

        return placeDetails_url;

    }


    public void sendRequestToGetPlaceDetails(String url) {

        // TODO Auto-generated method stub
        // final ProgressBar progressBar = (ProgressBar)dialog_add_tag.findViewById(R.id.dialog_progressbar);
        //progressBar.setVisibility(View.VISIBLE);
        showProgressDialog();

        final StringRequest req = new StringRequest(Request.Method.GET, url,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dismissProgressDialog();

                        PlaceDetailInfo placeDetailInfo = gson.fromJson(response, PlaceDetailInfo.class);
                        if (placeDetailInfo.getStatus().equals("OK")) {
                            result = placeDetailInfo.getResult();
                            updateUi(result);
                        }
                        // progressBar.setVisibility(View.GONE);
                        // Log.d("DEBUG", response);

                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                dismissProgressDialog();
                //progressBar.setVisibility(View.GONE);
                AlertDialogForAnything.showAlertDialogWhenComplte(PlaceDetailsActivity.this,
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
            dialog = new SpotsDialog(PlaceDetailsActivity.this, R.style.Custom);
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
