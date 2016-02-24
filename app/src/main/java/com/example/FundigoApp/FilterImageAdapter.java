package com.example.FundigoApp;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FilterImageAdapter extends BaseAdapter {
    private Context mContext;
    private String[] mNames;
    private Integer[] mImages;

    public FilterImageAdapter(Context c, String[] names, Integer[] images) {
        mContext = c;
        this.mImages = images;
        this.mNames = names;
    }

    @Override
    public int getCount() {
        return mImages.length;
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
        View grid;
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService (Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            grid = inflater.inflate (R.layout.grid_layout, null);
            TextView textView = (TextView) grid.findViewById (R.id.grid_text);
            ImageView imgView = (ImageView) grid.findViewById (R.id.grid_image);
            textView.setText (this.mNames[position]);
            imgView.setImageResource (this.mImages[position]);
        } else {
            grid = (View) convertView;
        }
        if (!GlobalVariables.CURRENT_FILTER_NAME.isEmpty () && this.mNames[position].equals (GlobalVariables.CURRENT_FILTER_NAME)) {
            grid.setBackgroundColor (Color.RED);
        }
        return grid;
    }


}
