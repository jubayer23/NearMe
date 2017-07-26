package com.creative.nearme;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.creative.nearme.alertbanner.AlertDialogForAnything;
import com.creative.nearme.appdata.ApiUrl;
import com.creative.nearme.appdata.AppController;
import com.creative.nearme.model.PlaceInfo;
import com.creative.nearme.model.Result;
import com.creative.nearme.utils.DeviceInfoUtils;
import com.creative.nearme.utils.UserLastKnownLocation;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class HomeActivityFragment extends Fragment implements View.OnClickListener {

    public static final String EXTRA_LOCATION = "desire_location";
    private Button btn_current_location, btn_other_location;
    private EditText ed_other_location;
    private Gson gson;
    List<Result> places = new ArrayList<>();

    public HomeActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        init(view);
        return view;
    }

    private void init(View view) {
        gson = new Gson();
        btn_current_location = (Button) view.findViewById(R.id.btn_current_location);
        btn_current_location.setOnClickListener(this);
        btn_other_location = (Button) view.findViewById(R.id.btn_other_location);
        btn_other_location.setOnClickListener(this);
        ed_other_location = (EditText) view.findViewById(R.id.ed_other_location);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (!DeviceInfoUtils.isConnectingToInternet(getActivity())) {
            AlertDialogForAnything.showAlertDialogWhenComplte(getActivity(), "Error", "No internet connections", false);
            return;
        }

        if (id == R.id.btn_other_location) {
            if (!isInputFiledEmpty()) {

                String address = ed_other_location.getText().toString();
                String url = getLocationDetailsUrl(address);
                sendRequestToGetUserCustomLocationCoordinates(url);
            }

        }

        if (!DeviceInfoUtils.checkInternetConnectionAndGps(getActivity())) {
            return;
        }

        if (!DeviceInfoUtils.checkMarshMallowPermission(getActivity())) {
            return;
        }


        if (id == R.id.btn_current_location) {
            showProgressDialog("please wait..",true,false);
            UserLastKnownLocation.LocationResult locationResult = new UserLastKnownLocation.LocationResult(){
                @Override
                public void gotLocation(Location location){
                    //Got the location!
                    dismissProgressDialog();
                    proceedToMainActivity(location);
                }
            };
            UserLastKnownLocation myLocation = new UserLastKnownLocation();
            myLocation.getLocation(getActivity(), locationResult);
        }

    }

    private String getLocationDetailsUrl(String address) {
        return ApiUrl.url_location_details_using_name +
                "address=" + address
                + "&sensor=false";
    }

    private boolean isInputFiledEmpty() {
        if (ed_other_location.getText().toString().isEmpty()) {
            ed_other_location.setError("Please give a valid location!");
            return true;
        }

        ed_other_location.setError(null);
        return false;
    }


    public void sendRequestToGetUserCustomLocationCoordinates(String url) {

        // TODO Auto-generated method stub
        showProgressDialog("Loading", true, false);

        final StringRequest req = new StringRequest(Request.Method.GET, url,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        dismissProgressDialog();

                        PlaceInfo placeInfo = gson.fromJson(response, PlaceInfo.class);
                        if (placeInfo.getStatus().equals("OK")) {
                            places.addAll(placeInfo.getResults());

                            if (!places.isEmpty()) {

                                Location location = new Location("desire_location");
                                location.setLatitude(places.get(0).getGeometry().getLocation().getLat());
                                location.setLongitude(places.get(0).getGeometry().getLocation().getLng());

                                proceedToMainActivity(location);

                                return;
                            }
                        } else {
                            if (places.isEmpty()) {
                                AlertDialogForAnything.showAlertDialogWhenComplte(getActivity(), "ERROR", "There is no location with such name! Please give a valid location name!", false);
                            }
                        }

                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                dismissProgressDialog();

                AlertDialogForAnything.showAlertDialogWhenComplte(getActivity(), "ERROR", "Something went wrong please try again later!", false);

            }
        }) {

        };

        req.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // TODO Auto-generated method stub
        AppController.getInstance().addToRequestQueue(req);
    }


    private void proceedToMainActivity(Location location) {
        Intent intent = new Intent(getActivity(), MainActivity.class);

        intent.putExtra(EXTRA_LOCATION, location);

        startActivity(intent);
    }


    private ProgressDialog progressDialog;

    public void showProgressDialog(String message, boolean isIntermidiate, boolean isCancelable) {
       /**/
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
        }
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        progressDialog.setIndeterminate(isIntermidiate);
        progressDialog.setCancelable(isCancelable);
        progressDialog.setMessage(message);
        progressDialog.show();
    }

    public void dismissProgressDialog() {
        if (progressDialog == null) {
            return;
        }
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
