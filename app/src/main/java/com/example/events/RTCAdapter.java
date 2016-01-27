package com.example.events;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Sprintzin on 25/01/2016.
 */
public class RTCAdapter extends ArrayAdapter<Message> {
    private String customer_id;
    private String producer_id;
    private boolean isProducer;
    private static final String TAG = "RTCAdapter";
    private boolean isMe;
    //  private List<Message> messages;
    //    private Context context;

    public RTCAdapter(Context context, String customer_id, String producer_id, List<Message> messages) {
        super(context, 0, messages);
        //     this.context = context;
        this.customer_id = customer_id;
        this.producer_id = producer_id;
        //     this.messages = messages;
        Log.e(TAG, "customer_id " + customer_id);
        Log.e(TAG, "messages " + messages + " " + messages.size());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).
                    inflate(R.layout.chat_item, parent, false);
            final ViewHolder holder = new ViewHolder();
            holder.imageLeft = (ImageView) convertView.findViewById(R.id.ivProfileLeft);
            holder.imageRight = (ImageView) convertView.findViewById(R.id.ivProfileRight);
            holder.body = (TextView) convertView.findViewById(R.id.tvBody);
            holder.userId = (TextView) convertView.findViewById(R.id.user_id);
            convertView.setTag(holder);
        }
        isMe = false;
        isProducer = false;
        final Message message = getItem(position);
        final ViewHolder holder = (ViewHolder) convertView.getTag();

        if (customer_id.equals(message.getUserId()))
            isMe = true;
        if (message.getUserId().equals(producer_id))
            isProducer = true;

        if (isMe) {
            holder.imageRight.setVisibility(View.VISIBLE);
            holder.imageLeft.setVisibility(View.GONE);
            holder.body.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
            holder.userId.setVisibility(View.GONE);

        } else {
            holder.imageLeft.setVisibility(View.VISIBLE);
            holder.imageRight.setVisibility(View.GONE);
            holder.body.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
            holder.userId.setVisibility(View.VISIBLE);
            if(message.getUserId().equals("0")){
                holder.userId.setText("Guest");
            }else if(message.getUserId().length()<=2 && !message.getUserId().equals("0")){
                holder.userId.setText("Producer");
            }else{
                holder.userId.setText(message.getUserId());
            }

        }

        holder.body.setText(message.getBody());
//        if (isProducer && isMe) {
//            Log.e(TAG, "i`m here " + " isProducer " + isProducer + " isMe " + isMe);
//            holder.body.setTypeface(null, Typeface.BOLD);
//        } else {
//            Log.e(TAG, "No! i`m here " + " isProducer " + isProducer + " isMe " + isMe);
//            holder.body.setTypeface(null, Typeface.NORMAL);
//        }
        return convertView;
    }


    final class ViewHolder {
        public ImageView imageLeft;
        public ImageView imageRight;
        public TextView body;
        public TextView userId;
    }

}
