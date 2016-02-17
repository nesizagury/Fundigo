package com.example.events;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

public class MessageRoomAdapter extends BaseAdapter {

    List<MessageRoomBean> list = new ArrayList<MessageRoomBean> ();
    ArrayList<Bitmap> arr=new ArrayList<>();
    Context context;
    Boolean comeFromMessageProducer=false;

    public MessageRoomAdapter(Context c, List list) {
        this.context = c;
        this.list = list;
    }

    public MessageRoomAdapter(Context c, List list,ArrayList<Bitmap> arr) {
        this.context = c;
        this.list = list;
        this.arr=arr;
        comeFromMessageProducer=true;
    }

    @Override
    public int getCount() {
        return list.size ();
    }

    @Override
    public Object getItem(int i) {
        return list.get (i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View row = view;
        MessageItemHolder holder = null;

        if (row == null) {
            LayoutInflater inflator = (LayoutInflater) context.getSystemService (Context.LAYOUT_INFLATER_SERVICE);
            row = inflator.inflate (R.layout.messages_room_item, viewGroup, false);
            holder = new MessageItemHolder (row);
            row.setTag (holder);

        } else {
            holder = (MessageItemHolder) row.getTag ();
        }
        MessageRoomBean message_bean = list.get (i);
        if(comeFromMessageProducer)
        {
            holder.image.setImageBitmap(arr.get(i));
        }
        else
        {
            holder.image.setImageResource (message_bean.imageId);
        }

        holder.body.setText (message_bean.body);
        holder.customer.setText (message_bean.getCustomer_id ());

        holder.image.setTag (message_bean);
        holder.customer.setTag (message_bean);
        holder.body.setTag (message_bean);

        return row;
    }
}
