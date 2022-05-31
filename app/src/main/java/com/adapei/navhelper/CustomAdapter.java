package com.adapei.navhelper;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.List;

public class CustomAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private List<Destination> destinations;

    public CustomAdapter(Context context, List<Destination> destination) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.destinations = destination;
    }

    @Override
    public int getCount() {
        return this.destinations.size();
    }

    @Override
    public Destination getItem(int i) {
        return this.destinations.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = view;
        if (v == null) {
            // getting reference to the main layout and
            // initializing
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = new View(context);
            v = inflater.inflate(R.layout.destination_item, null);
        }
        ImageView image = v.findViewById(R.id.imageButton);
        Destination destination = this.destinations.get(i);

        if(destination.isRepresentedByIcon()) {
            int iconId = context.getResources().getIdentifier(destination.getRepresentation(), "drawable", context.getPackageName());
            image.setImageResource(iconId);
        } else {
            //if picture
            try {
                if(destination.getRepresentation().contains("content://")) { // if the picture has been taken by the camera
                    Glide.with(context).load(Uri.parse(destination.getRepresentation())).into(image);
                } else { // if the picture comes from the galery
                    Glide.with(context).load(new File(destination.getRepresentation())).into(image);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        TextView a = v.findViewById(R.id.textView);
        a.setText(this.destinations.get(i).getNickname());
        return v;
    }
}
