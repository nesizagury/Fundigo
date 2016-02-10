package com.example.events;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class EventsTickets extends AppCompatActivity {


    private int eventId;
    private int ticketId;
    private TextView event;
    private TextView tickets;
    private HashMap<String, String> mapOfEventTickets;
    private ArrayList<HashMap<String, String>> arrayOfEventTicketHashMap;
    boolean TICKETS=false;
    final ArrayList<EventInfo> tempEventsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_tickets);
        ListView listT = (ListView) findViewById(R.id.listOfEventsTickets);
        arrayOfEventTicketHashMap = new ArrayList<>();

        this.getListOfEventsTickets();

        if (TICKETS)
        {
            this.uploadEventsData();// Upload Events data from "Events" in Parse for Tickets Selected
            this.getView((ViewGroup) findViewById(android.R.id.content));// send the ViewGroup Parent

            String[] from = {"Event", "Tickets"};
            int[] to = {event.getId(), tickets.getId()};

            ListAdapter _adapter = new SimpleAdapter(EventsTickets.this, arrayOfEventTicketHashMap, R.layout.content_events_tickets, from, to);

            listT.setAdapter(_adapter);

        } else
            Toast.makeText(getApplicationContext(), "No Tickets to Display", Toast.LENGTH_SHORT).show();


        final Intent intent = new Intent(this, EventPage.class); // When click on one of the events that in the list, it will be presented
        listT.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               Bundle b = new Bundle();
               try {
                   if (tempEventsList.get(position).getImageId() != null) {
                       Bitmap bmp = tempEventsList.get(position).getImageId();
                       ByteArrayOutputStream stream = new ByteArrayOutputStream();
                       bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                       byte[] byteArray = stream.toByteArray();
                       intent.putExtra("eventImage", byteArray);
                   } else
                       intent.putExtra("eventImage", "");
                   intent.putExtra("eventDate", tempEventsList.get(position).getDate());
                   intent.putExtra("eventName", tempEventsList.get(position).getName());
                   intent.putExtra("eventTags", tempEventsList.get(position).getTags());
                   intent.putExtra("eventPrice", tempEventsList.get(position).getPrice());
                   intent.putExtra("eventInfo", tempEventsList.get(position).getInfo());
                   intent.putExtra("eventPlace", tempEventsList.get(position).getPlace());
                   intent.putExtra("toilet", tempEventsList.get(position).getToilet());
                   intent.putExtra("parking", tempEventsList.get(position).getParking());
                   intent.putExtra("capacity", tempEventsList.get(position).getCapacity());
                   intent.putExtra("atm", tempEventsList.get(position).getAtm());
                   intent.putExtra("index", tempEventsList.get(position).getIndexInFullList());

                   startActivity(intent);
               } catch (Exception e)
               {
                   Log.e(e.toString(),"Error in SetOnItemClick listener");
               }
           }

        });
    }

    private void uploadEventsData() { // Upload  Events profile for those that customer buy tickets. and buidl Evnets Info Objects
        ParseQuery<Event> query = new ParseQuery("Event");
        List<Event>list;
        try {
            list = query.orderByDescending("date").whereContainedIn("Name", Arrays.asList(getNameofEvents())).find();
            ParseFile imageFile;
            byte[] data = null;
            Bitmap bmp;
            for (Event obj:list)
              {
                  imageFile = (ParseFile) obj.get("ImageFile");
                  if (imageFile != null) {
                    try {
                        data = imageFile.getData();
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }
                    bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                } else {
                    bmp = null;
                }
                  tempEventsList.add(new EventInfo(
                          bmp,
                          obj.getDate(),
                          obj.getName(),
                          obj.getTags(),
                          obj.getPrice(),
                          obj.getDescription(),
                          obj.getAddress(),
                          obj.getEventToiletService(),
                          obj.getEventParkingService(),
                          obj.getEventCapacityService(),
                          obj.getEventATMService(),
                          obj.getCity(),
                          list.indexOf(obj)));
                  tempEventsList.get(list.indexOf(obj)).setProducerId(obj.getProducerId());
            }
        }
    catch (Exception e)
        {
            Log.e(e.toString(),"Error");
         }
    }


    public void getView(ViewGroup parent) {
        LayoutInflater customListInflate = LayoutInflater.from(this);
        View customView = customListInflate.inflate(R.layout.content_events_tickets, parent, false);

        event = (TextView) customView.findViewById(R.id.eventName);
        tickets = (TextView) customView.findViewById(R.id.numberOfTickets);
    }

    public void getListOfEventsTickets() // Assaf- this method build a Arraylist of HashMaps from "Tickets" list in parse
    {
        String _userPhoneNumber = readFromFile();
        List<ParseObject> list;
        arrayOfEventTicketHashMap = new ArrayList<>();
        mapOfEventTickets = new HashMap<>();
            try {
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Tickets");
                query.orderByDescending("EventDate").whereEqualTo("Number", _userPhoneNumber);
                list = query.find();
                if(list.size()!=0) {
                    for (ParseObject obj : list) {
                       // arrayOfHashMap = (ArrayList<HashMap<String,String>>)obj.get("EventsTickets");
                        mapOfEventTickets.put("Event",obj.getString("Event"));
                        mapOfEventTickets.put("Tickets",obj.getString("Tickets"));
                        arrayOfEventTicketHashMap.add(mapOfEventTickets);
                        mapOfEventTickets = new HashMap<>();
                      }
                    TICKETS = true;
                     }
                else
                    TICKETS = false;
                }
              catch (ParseException e) {
                 Log.e("Exception catch", e.toString());
             } catch (Exception e) {
                 Log.e("Exception catch", e.toString());
            }

            }


    private String readFromFile() {
        String phone_number = "";
        try {
            InputStream inputStream = openFileInput ("verify.txt");
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader (inputStream);
                BufferedReader bufferedReader = new BufferedReader (inputStreamReader);
                String receiveString = "";
                while ((receiveString = bufferedReader.readLine ()) != null) {
                    phone_number = receiveString;
                }
                inputStream.close ();
            }
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }
        return phone_number;
        //return "1-555-521-5554";
    }

    public String[] getNameofEvents ()// return the Name of the Events that saved in Ticket Table
        {
            String[] eventsNames = new String[arrayOfEventTicketHashMap.size()];
            int i =0;
            for(HashMap<String,String> map:arrayOfEventTicketHashMap)
            {
              eventsNames[i] = map.get("Event").toString();
              i++;
            }
            return eventsNames;
        }
      }

