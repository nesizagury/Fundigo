package com.example.FundigoApp.Customer.Social;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.FundigoApp.Events.EventInfo;
import com.example.FundigoApp.R;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.List;

public class NotificationAdapter extends BaseAdapter {
    Context c;
    List<ParseObject> pushObjectsList = new ArrayList<> ();
    List<EventInfo> notificationsEventList = new ArrayList<EventInfo> ();

    public NotificationAdapter(Context context,
                               List<EventInfo> notificationsEventList,
                               List<ParseObject> message) {
        c = context;
        this.notificationsEventList = notificationsEventList;
        this.pushObjectsList = message;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View row = view;
        final NotificationHolder holder;

        if (row == null) {
            LayoutInflater inflator = (LayoutInflater) c.getSystemService (Context.LAYOUT_INFLATER_SERVICE);
            row = inflator.inflate (R.layout.push_customer_list, viewGroup, false);
            holder = new NotificationHolder (row);
            row.setTag (holder);
        } else {
            holder = (NotificationHolder) row.getTag ();
        }
        String mes = pushObjectsList.get (i).getString ("pushMessage");
        if (mes.length () > 12) {
            mes = mes.substring (0, 12) + "...";
        }
        holder.message.setText (mes);
        holder.date.setText (notificationsEventList.get(i).getDateAsString ());
        holder.eventName.setText (notificationsEventList.get(i).getName ());
        return row;
    }

    @Override
    public int getCount() {
        return pushObjectsList.size ();
    }

    @Override
    public Object getItem(int i) {
        return pushObjectsList.get (i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }
}
