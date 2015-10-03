package com.direyorkie.popularmovies.com.direyorkie.popularmovies.utilities;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Rachel on 10/1/2015.
 */
//This class attempts to imitate the funtionality of the ArrayAdapter class, but customized
//for ImageView
public class ImageAdapter extends BaseAdapter {

    public final String LOG_TAG = "ImageAdapter";

    Context context;
    LayoutInflater inflater;
    int resource;
    int imageViewResourceId;
    ArrayList<String> items;

    //Constructor
    // @param context The current context.
    // @param resource The resource ID for a layout file containing a layout to use when
    //                 instantiating views.
    // @param imageViewResourceId The id of the TextView within the layout resource to be populated
    // @param objects The objects to represent in the ListView.
    public ImageAdapter(Context context, int resource, int imageViewResourceId, ArrayList<String> items) {
        this.context = context;
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.resource = resource;
        this.imageViewResourceId = imageViewResourceId;
        this.items = new ArrayList<>(items);
    }

    // Adds the specified object at the end of the array.
    // @param object The object to add at the end of the array.
    public void add(String item) {
        items.add(item);
        this.notifyDataSetChanged();
    }

    public void addAll(ArrayList<String> items) {
        items.addAll(items);
        this.notifyDataSetChanged();
        for(int i = 0; i < items.size(); i++){
            Log.v(LOG_TAG, items.get(i));
        }
    }

    public void clear() {
        items.clear();
        this.notifyDataSetChanged();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        return createViewFromResource(position, convertView, parent, resource);
    }

    private View createViewFromResource(int position, View convertView, ViewGroup parent,
                                        int resource) {

        ImageView imageView;

        imageView = (ImageView) inflater.inflate(resource, parent);

        String item = getItem(position);
        Log.v(LOG_TAG, "URL for Picasso" + item);
        Picasso.with(context).load(item).into(imageView);

        return imageView;
    }

    public long getItemId(int position) {
        return position;
    }

    public String getItem(int position) {
        return items.get(position);
    }

    public int getCount() {
        Log.v(LOG_TAG, "Count is: " + items.size());
        return items.size();
    }

    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }
}
