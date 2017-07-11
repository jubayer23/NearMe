package com.creative.nearme.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.creative.nearme.R;
import com.creative.nearme.appdata.ApiUrl;
import com.creative.nearme.appdata.AppController;
import com.creative.nearme.appdata.Constant;
import com.creative.nearme.model.Result;

import java.util.List;


@SuppressLint("DefaultLocale")
public class PlaceListAdapter extends BaseAdapter {

    private List<Result> Displayedplaces;
    private List<Result> Originalplaces;
    private LayoutInflater inflater;
    private Location user_location;


    @SuppressWarnings("unused")
    private Activity activity;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public PlaceListAdapter(Activity activity, List<Result> places,Location user_location) {
        this.activity = activity;
        this.Displayedplaces = places;
        this.Originalplaces = places;
        this.user_location = user_location;

        inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return Displayedplaces.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
             convertView = inflater.inflate(R.layout.custom_layout, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.networkImageView = (NetworkImageView) convertView
                    .findViewById(R.id.product_thumb);


            viewHolder.productName = (TextView) convertView
                    .findViewById(R.id.product_name);

            viewHolder.productDetails = (TextView) convertView
                    .findViewById(R.id.product_details);
            viewHolder.destination = (TextView) convertView
                    .findViewById(R.id.distance);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Result place = Displayedplaces.get(position);

        if(user_location != null && user_location.getLongitude() != 0 && user_location.getLongitude() != 0){
            float[] results = new float[1];
            Location.distanceBetween(user_location.getLatitude(),
                    user_location.getLongitude(), place.getGeometry().getLocation().getLat(),
                    place.getGeometry().getLocation().getLng(), results);
            if ((int) results[0] <= 1000) {
                viewHolder.destination.setText(String.valueOf(results[0]).substring(0, 3) + "m");
            } else {
                viewHolder.destination.setText(String.valueOf(results[0] / 1000).substring(0, 3) + "km");
            }
        }else{
            viewHolder.destination.setVisibility(View.GONE);
        }
        //String rep = place.getPhotos().get(0).getPhoto_reference();
//AppConstant.url_photo_search + photoObj.getString("photo_reference") + "&key=" + AppConstant.API_KEY[API_COUNTER]
        if(place.getPhotos()!= null && place.getPhotos().size() > 0 && place.getPhotos().get(0).getPhotoReference() != null
                && !place.getPhotos().get(0).getPhotoReference().isEmpty()){
            viewHolder.networkImageView.setImageUrl(
                    ApiUrl.url_photo_base +
                            place.getPhotos().get(0).getPhotoReference()
                            + "&key=" + ApiUrl.API_KEY[2], imageLoader);
        }else{
            viewHolder.networkImageView.setImageUrl("http://i.imgur.com/JmurHyK.png",imageLoader);
        }


        viewHolder.productName.setText(place.getName());

      if( place.getOpeningHours() != null && place.getOpeningHours().getOpenNow() != null){
          viewHolder.productDetails.setText(place.getOpeningHours().getOpenNow() ? "Status : Open Now" : "Status : Closed Now");
      }
       else{
          viewHolder.productDetails.setText( "Status : Closed Now");

      }
        //if(place.getVicinity() != null && !place.getVicinity().isEmpty()){
        //    viewHolder.productDetails.setText(place.getVicinity());
        //}else if(place.getFormattedAddress() != null && !place.getFormattedAddress().isEmpty()){
       //     viewHolder.productDetails.setText(place.getFormattedAddress());
       // }

        return convertView;
    }

    public void addMore(List<Result> places) {
        //this.Displayedplaces.addAll(places);
        notifyDataSetChanged();
    }


    private static class ViewHolder {
        private NetworkImageView networkImageView;
        private TextView productName;
        private TextView productDetails;
        private TextView destination;
    }

}