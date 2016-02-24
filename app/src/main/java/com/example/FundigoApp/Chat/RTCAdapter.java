package com.example.FundigoApp.Chat;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.FundigoApp.R;

import java.util.List;

public class RTCAdapter extends ArrayAdapter<MsgRealTime> {
    private String current_user_id;
    private Context context;

    public RTCAdapter(Context context, String current_user_id, List<MsgRealTime> messages) {
        super (context, 0, messages);
        this.context = context;
        this.current_user_id = current_user_id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final MsgRealTime message = getItem (position);
        if (convertView == null) {
            if (current_user_id.equals (message.getUserId ())) {
                convertView = LayoutInflater.from (context).inflate (R.layout.real_time_chat_item_right, null);
            } else {
                convertView = LayoutInflater.from (context).inflate (R.layout.real_time_chat_item_left, null);
            }
            final ViewHolder holder = new ViewHolder ();

            holder.userId = (TextView) convertView.findViewById (R.id.user_id_1);
            holder.body = (TextView) convertView.findViewById (R.id.tvBody_1);
            convertView.setTag (holder);
        }

        final ViewHolder holder = (ViewHolder) convertView.getTag ();
        holder.body.setText (message.getBody ());

        if (!current_user_id.equals (message.getUserId ())) {
            holder.userId.setText (message.getUserId ());
            if (message.isProducer ()) {
                holder.body.setTypeface (null, Typeface.BOLD);
                holder.userId.setText ("Producer: " + message.getUserId ());
            }
        } else{
            holder.userId.setVisibility (View.GONE);
        }
        return convertView;
    }

    final class ViewHolder {
        public TextView body;
        public TextView userId;
    }
}
