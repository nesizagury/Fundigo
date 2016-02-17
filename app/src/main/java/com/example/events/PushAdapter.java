package com.example.events;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mirit-binbin on 11/02/2016.
 */
public class PushAdapter extends BaseAdapter
{
    Context c;
    List<ParseObject> message=new ArrayList<>();

    public PushAdapter(Context context,List<ParseObject> message)
    {
        c=context;
        this.message=message;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View row = view;
        final PushHolder holder;

        if (row == null) {
            LayoutInflater inflator = (LayoutInflater) c.getSystemService (Context.LAYOUT_INFLATER_SERVICE);
            row = inflator.inflate (R.layout.push_list, viewGroup, false);
            holder = new PushHolder (row);
            row.setTag (holder);
        }
        else
        {
            holder = (PushHolder) row.getTag();
        }
        String mes=message.get(i).getString("pushMessage").toString();
        if(mes.length()>12)
        {
           mes=mes .substring(0, 12)+"...";
        }
        holder.message.setText(mes);
        holder.date.setText(message.get(i).getString("Date").toString());
        holder.eventName.setText(message.get(i).getString("EvendId").toString());
        return row;
    }

        @Override
    public int getCount() {
        return message.size ();
    }

    @Override
    public Object getItem(int i) {
        return message.get (i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

}
