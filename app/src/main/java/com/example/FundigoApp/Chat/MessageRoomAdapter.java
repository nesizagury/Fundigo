package com.example.FundigoApp.Chat;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.FundigoApp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MessageRoomAdapter extends BaseAdapter {

    List<MessageRoomBean> list = new ArrayList<MessageRoomBean> ();
    ArrayList<Bitmap> arr = new ArrayList<> ();
    Context context;
    Boolean comeFromMessageProducer = false;

    public MessageRoomAdapter(Context c, List list) {
        this.context = c;
        this.list = list;
    }

    public MessageRoomAdapter(Context c, List list, ArrayList<Bitmap> arr) {
        this.context = c;
        this.list = list;
        this.arr = arr;
        comeFromMessageProducer = true;
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
        MessageRoomItemHolder holder = null;

        if (row == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService (Context.LAYOUT_INFLATER_SERVICE);
            row = layoutInflater.inflate (R.layout.messages_room_item, viewGroup, false);
            holder = new MessageRoomItemHolder (row);
            row.setTag (holder);

        } else {
            holder = (MessageRoomItemHolder) row.getTag ();
        }
        MessageRoomBean message_bean = list.get (i);
        if (comeFromMessageProducer) {
            holder.customerImage.setImageBitmap (arr.get (i));
        } else if (message_bean.getCustomerImageFacebookUrl () != null &&
                           !message_bean.getCustomerImageFacebookUrl ().isEmpty ()) {
            Picasso.with (context).load (message_bean.getCustomerImageFacebookUrl ()).into (holder.customerImage);
        } else if (message_bean.getCustomerImage () != null) {
            holder.customerImage.setImageBitmap (message_bean.getCustomerImage ());
        }
        holder.body.setText (message_bean.getLastMessage ());
        holder.customer.setText (message_bean.getCustomer_id ());
        holder.customerImage.setTag (message_bean);
        holder.customer.setTag (message_bean);
        holder.body.setTag (message_bean);

        return row;
    }
}
