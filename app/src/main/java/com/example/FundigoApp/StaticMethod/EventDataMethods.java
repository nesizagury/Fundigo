package com.example.FundigoApp.StaticMethod;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.FundigoApp.Events.Event;
import com.example.FundigoApp.Events.EventInfo;
import com.example.FundigoApp.GlobalVariables;
import com.example.FundigoApp.MyLocation.CityMenu;
import com.example.FundigoApp.Producer.Artists.Artist;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class EventDataMethods {

    public static void onEventItemClick(int positionViewItem,
                                        List<EventInfo> eventsList,
                                        Intent intent) {
        intent.putExtra ("eventDate", eventsList.get (positionViewItem).getDate ());
        intent.putExtra ("eventName", eventsList.get (positionViewItem).getName ());
        intent.putExtra ("eventTags", eventsList.get (positionViewItem).getTags ());
        intent.putExtra ("eventPrice", eventsList.get (positionViewItem).getPrice ());
        intent.putExtra ("eventInfo", eventsList.get (positionViewItem).getInfo ());
        intent.putExtra ("eventPlace", eventsList.get (positionViewItem).getPlace ());
        intent.putExtra ("toilet", eventsList.get (positionViewItem).getToilet ());
        intent.putExtra ("parking", eventsList.get (positionViewItem).getParking ());
        intent.putExtra ("capacity", eventsList.get (positionViewItem).getCapacity ());
        intent.putExtra ("atm", eventsList.get (positionViewItem).getAtm ());
        intent.putExtra ("index", eventsList.get (positionViewItem).getIndexInFullList ());
        intent.putExtra ("i", String.valueOf (positionViewItem));
        intent.putExtra ("artist", eventsList.get (positionViewItem).getArtist ());
        intent.putExtra ("fbUrl", eventsList.get (positionViewItem).getFbUrl ());
    }

    public interface GetEventsDataCallback {
        void eventDataCallback();
    }

    public static void downloadEventsData(final GetEventsDataCallback ic,
                                          String producerId,
                                          final Context context,
                                          final Intent intent) {
        final ArrayList<EventInfo> tempEventsList = new ArrayList<> ();
        ParseQuery<Event> query = new ParseQuery ("Event");
        if (producerId != null && producerId != "") {
            query.whereEqualTo ("producerId", producerId);
        }
        query.orderByDescending ("createdAt");
        List<Event> eventParse = null;
        try {
            eventParse = query.find ();
            for (int i = 0; i < eventParse.size (); i++) {
                Event event = eventParse.get (i);
                tempEventsList.add (new EventInfo (event.getPic ().getUrl (),
                                                          event.getRealDate (),
                                                          getEventDateAsString (event.getRealDate ()),
                                                          event.getName (),
                                                          event.getTags (),
                                                          event.getPrice (),
                                                          event.getDescription (),
                                                          event.getPlace (),
                                                          event.getAddress (),
                                                          event.getCity (),
                                                          event.getEventToiletService (),
                                                          event.getEventParkingService (),
                                                          event.getEventCapacityService (),
                                                          event.getEventATMService (),
                                                          event.getFilterName (),
                                                          false,
                                                          event.getProducerId (),
                                                          i,
                                                          event.getX (),
                                                          event.getY (),
                                                          event.getArtist (),
                                                          event.getNumOfTickets (),
                                                          event.getObjectId (),
                                                          event.getFbUrl (),
                                                          event.getIsStadium ()
                ));
            }
            GeneralStaticMethods.updateSavedEvents (tempEventsList, context);
            GlobalVariables.ALL_EVENTS_DATA.clear ();
            GlobalVariables.ALL_EVENTS_DATA.addAll (tempEventsList);

            if (!GlobalVariables.IS_PRODUCER) {
                GlobalVariables.cityMenuInstance = new CityMenu (tempEventsList, context);
                GlobalVariables.namesCity = GlobalVariables.cityMenuInstance.getCityNames ();
                if (!GlobalVariables.deepLinkEventObjID.equals ("")) {
                    for (int i = 0; i < GlobalVariables.ALL_EVENTS_DATA.size (); i++) {
                        if (GlobalVariables.deepLinkEventObjID.equals (GlobalVariables.ALL_EVENTS_DATA.get (i).getParseObjectId ())) {
                            Bundle b = new Bundle ();
                            onEventItemClick (i, GlobalVariables.ALL_EVENTS_DATA, intent);
                            intent.putExtras (b);
                            context.startActivity (intent);
                            ic.eventDataCallback ();
                            return;
                        }
                    }
                }
            }
            ic.eventDataCallback ();
        } catch (ParseException e) {
            e.printStackTrace ();
        }
    }

    public static EventInfo getEventFromObjID(String parseObjID, List<EventInfo> eventsList) {
        for (EventInfo eventInfo : eventsList) {
            if (eventInfo.getParseObjectId ().equals (parseObjID)) {
                return eventInfo;
            }
        }
        return null;
    }

    public static void uploadArtistData(List<Artist> artist_list) {
        artist_list.clear ();
        List<String> temp_artist_list = new ArrayList<String> ();
        for (int i = 0; i < GlobalVariables.ALL_EVENTS_DATA.size (); i++) {
            EventInfo eventInfo = GlobalVariables.ALL_EVENTS_DATA.get (i);
            if (eventInfo.getArtist () != null &&
                        !eventInfo.getArtist ().equals ("") &&
                        !temp_artist_list.contains (eventInfo.getArtist ())) {
                temp_artist_list.add (eventInfo.getArtist ());
                artist_list.add (new Artist (eventInfo.getArtist ()));
            }
        }
        artist_list.add (new Artist (GlobalVariables.No_Artist_Events));
    }

    public static String getEventDateAsString(Date eventDate) {
        long time = eventDate.getTime ();
        Calendar calendar = Calendar.getInstance ();
        calendar.setTimeInMillis (time);
        String dayOfWeek = null;
        switch (calendar.get (Calendar.DAY_OF_WEEK)) {
            case 1:
                dayOfWeek = "SUN";
                break;
            case 2:
                dayOfWeek = "MON";
                break;
            case 3:
                dayOfWeek = "TUE";
                break;
            case 4:
                dayOfWeek = "WED";
                break;
            case 5:
                dayOfWeek = "THU";
                break;
            case 6:
                dayOfWeek = "FRI";
                break;
            case 7:
                dayOfWeek = "SAT";
                break;
        }
        String month = null;
        switch (calendar.get (Calendar.MONTH)) {
            case 0:
                month = "JAN";
                break;
            case 1:
                month = "FEB";
                break;
            case 2:
                month = "MAR";
                break;
            case 3:
                month = "APR";
                break;
            case 4:
                month = "MAY";
                break;
            case 5:
                month = "JUN";
                break;
            case 6:
                month = "JUL";
                break;
            case 7:
                month = "AUG";
                break;
            case 8:
                month = "SEP";
                break;
            case 9:
                month = "OCT";
                break;
            case 10:
                month = "NOV";
                break;
            case 11:
                month = "DEC";
                break;
        }
        int day = calendar.get (Calendar.DAY_OF_MONTH);
        int hour = calendar.get (Calendar.HOUR_OF_DAY);
        int minute = calendar.get (Calendar.MINUTE);
        String ampm = null;
        if (calendar.get (Calendar.AM_PM) == Calendar.AM)
            ampm = "AM";
        else if (calendar.get (Calendar.AM_PM) == Calendar.PM)
            ampm = "PM";

        String min;
        if (minute < 10) {
            min = "0" + minute;
        } else {
            min = "" + minute;
        }
        return dayOfWeek + ", " + month + " " + day + ", " + hour + ":" + min + " " + ampm;
    }

    public static String getDisplayedEventPrice(String eventPrice) {
        if (eventPrice.contains ("-")) {
            String[] prices = eventPrice.split ("-");
            return prices[0] + "₪-" + prices[1] + "₪";
        } else if (!eventPrice.equals ("FREE")) {
            return eventPrice + "₪";
        } else {
            return eventPrice;
        }
    }

    public static void updateEventInfoDromParseEvent(EventInfo eventInfo,
                                                     Event event) {
        eventInfo.setPrice (event.getPrice ());
        eventInfo.setAddress (event.getAddress ());
        eventInfo.setIsStadium (event.getIsStadium ());
        eventInfo.setParseObjectId (event.getObjectId ());
        Date currentDate = new Date ();
        eventInfo.setIsFutureEvent (event.getRealDate ().after (currentDate));
        eventInfo.setArtist (event.getArtist ());
        eventInfo.setAtm (event.getEventATMService ());
        eventInfo.setCapacity (event.getEventCapacityService ());
        eventInfo.setDate (event.getRealDate ());
        eventInfo.setDateAsString (getEventDateAsString (event.getRealDate ()));
        eventInfo.setFilterName (event.getFilterName ());
        eventInfo.setDescription (event.getDescription ());
        eventInfo.setName (event.getName ());
        eventInfo.setNumOfTickets (event.getNumOfTickets ());
        eventInfo.setPlace (event.getPlace ());
        eventInfo.setProducerId (event.getProducerId ());
        eventInfo.setParking (event.getEventParkingService ());
        eventInfo.setTags (event.getTags ());
        eventInfo.setToilet (event.getEventToiletService ());
        eventInfo.setX (event.getX ());
        eventInfo.setY (event.getY ());
    }
}
