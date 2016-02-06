package com.example.events;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by מנהל on 02/02/2016.
 */
public class ArtistAdapter extends BaseAdapter {

    List<Artist> artistList = new ArrayList<Artist>();
    Context context;
    LayoutInflater inflater;

    public ArtistAdapter(Context c, List<Artist> artistList) {


        this.context = c;
        this.artistList = artistList;
        inflater = LayoutInflater.from(this.context);
    }

    @Override
    public int getCount() {
        return artistList .size();
    }

    @Override
    public Object getItem(int position) {
        return artistList .get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        MyViewHolder mViewHolder;


        if (convertView == null) {
            convertView = inflater.inflate(R.layout.artist_item, parent, false);
            mViewHolder = new MyViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }

        Artist artist = (Artist) getItem(position);
        mViewHolder.tvTitle.setText(artist.getName());
        mViewHolder.tvDesc.setText(artist.getTicketsSold() + " - Tickets sold");


        return convertView;
    }

    private class MyViewHolder {
        TextView tvTitle, tvDesc;


        public MyViewHolder(View item) {
            tvTitle = (TextView) item.findViewById(R.id.eventTV);
            tvDesc = (TextView) item.findViewById(R.id.tv);

        }
    }
}
