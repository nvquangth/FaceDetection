package com.example.admin.adapter;

import android.app.Activity;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.admin.facedetectiondemo.R;
import com.example.admin.model.Detection;

import java.util.List;

/**
 * Created by Admin on 4/21/2017.
 */

public class DetectionAdapter extends ArrayAdapter<Detection> {
    Activity context;
    int resource;
    List<Detection> objects;
    public DetectionAdapter(@NonNull Activity context, @LayoutRes int resource, @NonNull List<Detection> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.objects = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = this.context.getLayoutInflater();
        View row = inflater.inflate(this.resource, null);
        Detection detection = this.objects.get(position);

        ImageView imgPhotoScaned = (ImageView) row.findViewById(R.id.imgPhotoScaned);
        TextView txtType = (TextView) row.findViewById(R.id.txtType);

        imgPhotoScaned.setImageBitmap(detection.getPhotoScaned());
        txtType.setText(detection.getDetectType() + " - " + detection.getColorType() + "");

        return row;
    }
}
