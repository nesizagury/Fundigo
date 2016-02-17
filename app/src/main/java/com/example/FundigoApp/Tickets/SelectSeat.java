package com.example.FundigoApp.Tickets;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.FundigoApp.R;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class SelectSeat extends AppCompatActivity {

    private String eventObjectId;
    private ListView mylist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_seat);
        mylist=(ListView)findViewById(R.id.listView2);

        Intent intentHere1 = getIntent();
        eventObjectId =intentHere1.getStringExtra("eventObjectId" );

        ParseQuery<ParseObject> query = ParseQuery.getQuery("EventsSeats").whereMatches("EventName", eventObjectId).whereDoesNotExist("QR_Code");

        ArrayList<ParseObject>seatsList=new ArrayList<>();

        try {
            List<ParseObject> commentList=query.find();
            seatsList.addAll(commentList);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        mylist.setAdapter(new SelectSeatAdapter(this, seatsList));

    }



    class SelectSeatAdapter extends BaseAdapter {


        Context context;
        ArrayList<ParseObject> seat;
        ArrayList<SeatRow> list;



        SelectSeatAdapter(Context c,ArrayList<ParseObject> seat){
            list=new ArrayList<SeatRow>();
            this.seat=seat;

            this.context=c;
            int images= R.drawable.seat_ldpi;
            Log.d("xxx"," SelectSeatAdapter(Context c,ArrayL...");
            for(int i=0;i<seat.size();i++){
                String price;
                if(seat.get(i).get("price")==null || seat.get(i).get("price")==""){
                    price="Free";
                }else{
                    price=seat.get(i).get("price").toString()+"$";
                }
                list.add(new SeatRow(seat.get(i).getString("seatNumber"),price,images,seat.get(i).getObjectId()));

            }


        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {

            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater layoutInfla= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View row=layoutInfla.inflate(R.layout.seat_row, parent, false);
            final TextView title= (TextView) row.findViewById(R.id.textView7);
            TextView description= (TextView) row.findViewById(R.id.textView8);
            ImageView image= (ImageView) row.findViewById(R.id.imageView6);
            Button buyTicket=(Button)row.findViewById(R.id.button3);
            buyTicket.setText("Buy Ticket");
            buyTicket.setVisibility(View.GONE);

            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (row.findViewById(R.id.button3).getVisibility() == View.GONE) {

                        row.findViewById(R.id.button3).setVisibility(View.VISIBLE);
                    } else {
                        row.findViewById(R.id.button3).setVisibility(View.GONE);
                    }
                }

            });
            final SeatRow temp=list.get(position);
            title.setText(temp.title);
            description.setText("Price: "+temp.description);
            image.setImageResource(temp.image);
            buyTicket.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intentQr = new Intent(SelectSeat.this, GetQRCode.class);
                    intentQr.putExtra("seatNumber",temp.title);
                    intentQr.putExtra("eventObjectId", eventObjectId);
                    intentQr.putExtra("isChoose","yes");
                    intentQr.putExtra("seatKey",temp.getSeatKey());


                    startActivity(intentQr);
                }
            });

            return row;
        }


        class SeatRow{
            String title;
            String description;
            int image;
            String seatKey;
            SeatRow(String title,String description,int image,String seatKey){
                this.title=title;
                this.description=description;
                this.image=image;
                this.seatKey=seatKey;
            }
            public String getSeatKey(){
                return seatKey;
            }

        }
    }



}
