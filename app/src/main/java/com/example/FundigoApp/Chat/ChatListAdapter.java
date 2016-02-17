package com.example.FundigoApp.Chat;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.FundigoApp.*;

import java.util.List;

public class ChatListAdapter extends ArrayAdapter<Message> {
    private String mUserId;

    public ChatListAdapter(Context context, List<Message> messages, String userId) {
        super (context, 0, messages);
        this.mUserId = userId;
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
        boolean isMe = false;
        final Message message = (Message) getItem (position);
        final ViewHolder holder = (ViewHolder) convertView.getTag ();
        holder.userId.setVisibility (View.GONE);

        if (mUserId.equals (message.getUserId ()))
            isMe = true;

        if (isMe) {
            holder.imageRight.setVisibility (View.VISIBLE);
            holder.imageLeft.setVisibility (View.GONE);
            holder.body.setGravity (Gravity.CENTER_VERTICAL | Gravity.RIGHT);
        } else {
            holder.imageLeft.setVisibility (View.VISIBLE);
            holder.imageRight.setVisibility (View.GONE);
            holder.body.setGravity (Gravity.CENTER_VERTICAL | Gravity.LEFT);
        }
        holder.body.setText (message.getBody ());
        return convertView;
    }

    final class ViewHolder {
        public ImageView imageLeft;
        public ImageView imageRight;
        public TextView body;
        public TextView userId;
    }
}