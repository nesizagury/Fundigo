package com.example.FundigoApp.Chat;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.FundigoApp.R;

public class MessageItemHolder {

    ImageView image;
    TextView body;
    TextView customer;

    public MessageItemHolder(View v) {
        image = (ImageView) v.findViewById (R.id.message_itemIV);
        customer = (TextView) v.findViewById (R.id.message_item_nameTV);
        body = (TextView) v.findViewById (R.id.message_item_bodyTV);
    }
}
