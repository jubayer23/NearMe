package com.creative.nearme.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.creative.nearme.R;


/**
 * Created by comsol on 9/10/2015.
 */
public class IconGridAdapter extends BaseAdapter {

    private String[] displayedplaces;
    private String[] originalplaces;
    private LayoutInflater inflater;
    @SuppressWarnings("unused")
    private Activity activity;

    public IconGridAdapter(Activity activity, String[] venues) {
        this.activity = activity;
        this.displayedplaces = venues;
        this.originalplaces = venues;

        inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return displayedplaces.length;
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
            convertView = inflater.inflate(R.layout.row_grid_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.img_icon = (ImageView) convertView
                    .findViewById(R.id.img_icon);
            viewHolder.tv_title = (TextView) convertView
                    .findViewById(R.id.tv_title);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        String venue = displayedplaces[position];

        String[] levelParams = venue.split("/");

        viewHolder.img_icon.setImageResource(activity.getResources().getIdentifier(
                levelParams[0], "drawable", activity.getPackageName()));
        viewHolder.tv_title.setText(levelParams[1]);

        return convertView;
    }

    private static class ViewHolder {

        private ImageView img_icon;
        private TextView tv_title;
        // private TextView destination;
    }


}
