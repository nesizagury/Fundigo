package com.example.events;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

public class ImageAdapter extends BaseAdapter
{
    /** Variables Decleration */
    private Context mContext;
    private String[] mNames;
    private Integer[] mImages;


    public ImageAdapter(Context c, String[]names, Integer[]images)
    {
        mContext = c;
        this.mImages = images;
        this.mNames = names;
    }

    @Override
    public int getCount()
    {
        return mImages.length;
    }

    @Override
    public Object getItem(int position)
    {
        return null;
    }

    @Override
    public long getItemId(int position)
    {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View grid;
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if( convertView == null )
        {
            grid = new View(this.mContext);
            grid = inflater.inflate(R.layout.grid_layout, null);
            TextView textView = (TextView) grid.findViewById(R.id.grid_text);
            ImageView imgView = (ImageView) grid.findViewById(R.id.grid_image);
            textView.setText(this.mNames[position]);
            imgView.setImageResource(this.mImages[position]);
        }
        else
        {
            grid = (View) convertView;
        }

        return grid;
    }


}
