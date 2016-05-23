package com.rinaldi.jesse.egather;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

/**
 * Created by Jesse on 5/15/2016.
 */
public class EventAdapter extends ArrayAdapter<Event> {

    private TextView txtNameList, txtDateList;
    ImageView ivPhotoList;

    public EventAdapter(Context context, Event[] events) {
        super(context, R.layout.event_list_item, events);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View view = layoutInflater.inflate(R.layout.event_list_item, parent, false);
        Event event = getItem(position);
        txtNameList = (TextView) view.findViewById(R.id.txtNameList);
        txtDateList = (TextView) view.findViewById(R.id.txtDateList);
        ivPhotoList = (ImageView) view.findViewById(R.id.ivPhotoList);
        txtNameList.setText(event.getName());
        txtDateList.setText(event.getMonth() + "/" + event.getDay());
        String photoURL = event.getPhotoURL();
        if(photoURL != null && !photoURL.isEmpty() && URLUtil.isValidUrl(photoURL)) {
            Picasso.with(getContext()).load(photoURL).into(ivPhotoList);
        }
        return view;
    }
}
