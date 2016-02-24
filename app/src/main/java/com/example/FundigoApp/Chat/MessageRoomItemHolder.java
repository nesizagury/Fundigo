package com.example.FundigoApp.Chat;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.FundigoApp.R;

public class MessageRoomItemHolder {
    ImageView customerImage;
    TextView body;
    TextView customer;

    public MessageRoomItemHolder(View v) {
        customerImage = (ImageView) v.findViewById (R.id.message_itemIV);
        customer = (TextView) v.findViewById (R.id.message_item_nameTV);
        body = (TextView) v.findViewById (R.id.message_item_bodyTV);
    }
}
