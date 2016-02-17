package com.example.FundigoApp.Chat;

import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.FundigoApp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RTCAdapter extends ArrayAdapter<MsgRealTime> {
    private String customer_id;
    private String producer_id;
    private boolean isProducer;
    private static final String TAG = "RTCAdapter";
    private boolean isMe;
    private Context context;
    private String pic_url;

    public RTCAdapter(Context context, String customer_id, String producer_id, List<MsgRealTime> messages) {
        super (context, 0, messages);
        this.context = context;
        this.customer_id = customer_id;
        this.producer_id = producer_id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from (getContext ()).
                                                                     inflate (R.layout.chat_item, parent, false);
            final ViewHolder holder = new ViewHolder ();
            holder.imageLeft = (ImageView) convertView.findViewById (R.id.ivProfileLeft);
            holder.imageRight = (ImageView) convertView.findViewById (R.id.ivProfileRight);
            holder.body = (TextView) convertView.findViewById (R.id.tvBody);
            holder.userId = (TextView) convertView.findViewById (R.id.user_id);
            convertView.setTag (holder);
        }
        isMe = false;
        final MsgRealTime message = getItem (position);
        final ViewHolder holder = (ViewHolder) convertView.getTag ();
        String fbName = message.getSenderName ();
        isProducer = message.isProducer ();
        pic_url = message.getPicUrl ();
        if (customer_id.equals (message.getUserId ())) {
            isMe = true;
        }

        if (isMe) {
            holder.imageRight.setVisibility (View.VISIBLE);
            holder.imageLeft.setVisibility (View.GONE);
            holder.body.setGravity (Gravity.CENTER_VERTICAL | Gravity.RIGHT);
            holder.userId.setVisibility (View.GONE);
            if (pic_url != null) {
                Picasso.with (context).load (pic_url).into (holder.imageRight);
            }
        } else {
            holder.imageLeft.setVisibility (View.VISIBLE);
            holder.imageRight.setVisibility (View.GONE);
            holder.body.setGravity (Gravity.CENTER_VERTICAL | Gravity.LEFT);
            holder.userId.setVisibility (View.VISIBLE);
            if (pic_url != null) {
                Picasso.with (context).load (pic_url).into (holder.imageLeft);
            }

            if (fbName != null) {
                holder.userId.setText (fbName);
            } else if (message.getUserId ().equals ("0")) {
                holder.userId.setText ("Guest");
            } else if (message.getUserId ().length () <= 2 && !message.getUserId ().equals ("0")) {
                holder.userId.setText ("Producer");
            } else {
                holder.userId.setText (message.getUserId ());
            }
        }
        holder.body.setText (message.getBody ());
        if (isProducer) {
            holder.body.setTypeface (null, Typeface.BOLD);
        } else {
            holder.body.setTypeface (null, Typeface.NORMAL);
        }
        return convertView;
    }

    final class ViewHolder {
        public ImageView imageLeft;
        public ImageView imageRight;
        public TextView body;
        public TextView userId;
    }
}
