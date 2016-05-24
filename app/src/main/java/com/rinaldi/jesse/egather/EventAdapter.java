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
 * NAME
 *      EventAdapter
 * DESCRIPTION
 *      Takes an array of lists and adapts them into an adapter
 *      of event_list_item views. Inherits from ArrayAdapter
 * AUTHOR
 *      @author Jesse Rinaldi
 * DATE
 *      5/15/2016
 */
public class EventAdapter extends ArrayAdapter<Event> {

    private TextView txtNameList, txtDateList;
    ImageView ivPhotoList;

    /**
     * NAME
     *      EventAdapter (COnstructor)
     * SYNOPSIS
     *      @param context - Current activity context
     *      @param events - Array of event objects
     * DESCRIPTION
     *      Constructs the EventAdapter
     * AUTHOR
     *      @author Jesse Rinaldi
     * DATE
     *      5/15/2016
     */
    public EventAdapter(Context context, Event[] events) {
        super(context, R.layout.event_list_item, events);
    }

    /**
     * NAME
     *      EventAdapter.getView
     * SYNOPSIS
     *      @param position - Position in events array
     *      @param convertView
     *      @param parent
     * DESCRIPTION
     *      Called automatically by system. Sets the widgets for event name, date, and photo
     * RETURNS
     *      @return View - the view created
     * AUTHOR
     *      @author Jesse Rinaldi
     * DATE
     *      5/15/2016
     */
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
