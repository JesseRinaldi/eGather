package com.rinaldi.jesse.egather;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.location.places.Places;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.Map;

/**
 * Created by Jesse on 5/22/2016.
 */
public class UserAdapter extends ArrayAdapter<Map<String, String>> {

    private TextView txtUserID, txtDisplayName, txtGMail;
    ImageView ivUserPhoto;

    public UserAdapter(Context context, Map<String, String>[] users) {
        super(context, R.layout.user_list_item, users);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View view = layoutInflater.inflate(R.layout.user_list_item, parent, false);
        Map<String, String> user = getItem(position);
        txtUserID = (TextView) view.findViewById(R.id.txtUserID);
        txtDisplayName = (TextView) view.findViewById(R.id.txtDisplayName);
        txtGMail = (TextView) view.findViewById(R.id.txtGMail);
        ivUserPhoto = (ImageView) view.findViewById(R.id.ivUserPhoto);
        txtUserID.setText(user.get("ID"));
        txtDisplayName.setText(user.get("name"));
        txtGMail.setText(user.get("email"));
        String photoURL = user.get("photoURL");
        if (photoURL != null && !photoURL.isEmpty() && URLUtil.isValidUrl(photoURL)) {
            Picasso.with(getContext()).load(photoURL).into(ivUserPhoto);
        }
        return view;
    }
}