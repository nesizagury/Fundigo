package com.example.FundigoApp.StaticMethod;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.FundigoApp.Customer.Social.Profile;
import com.example.FundigoApp.Events.EventInfo;
import com.example.FundigoApp.Events.EventsListAdapter;
import com.example.FundigoApp.GlobalVariables;
import com.example.FundigoApp.R;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class GeneralStaticMethods {

    ///////////////////////////////////////
    //Saved events
    ///////////////////////////////////////
    public static void updateSavedEvents(final List<EventInfo> eventsList, final Context context) {
        try {
            InputStream inputStream = context.openFileInput ("saves");
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader (inputStream);
                BufferedReader bufferedReader = new BufferedReader (inputStreamReader);
                String receiveString = "";
                while ((receiveString = bufferedReader.readLine ()) != null) {
                    for (int i = 0; i < eventsList.size (); i++) {
                        if (eventsList.get (i).getParseObjectId ().equals (receiveString)) {
                            eventsList.get (i).setIsSaved (true);
                        }
                    }
                }
                inputStream.close ();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace ();
        } catch (IOException e) {
            e.printStackTrace ();
        }
        if (GlobalVariables.IS_CUSTOMER_REGISTERED_USER && GlobalVariables.userChanels.size () == 0) {
            ParseQuery<Profile> query = ParseQuery.getQuery ("Profile");
            query.whereEqualTo ("number", GlobalVariables.CUSTOMER_PHONE_NUM);
            query.findInBackground (new FindCallback<Profile> () {
                @Override
                public void done(List<Profile> objects, ParseException e) {
                    if (e == null) {
                        if (objects.get (0).getChanels () != null) {
                            if (GlobalVariables.userChanels.size () == 0) {
                                GlobalVariables.userChanels.addAll (objects.get (0).getChanels ());
                            }
                            for (String eventObjId : GlobalVariables.userChanels) {
                                EventInfo event = EventDataMethods.getEventFromObjID (eventObjId, eventsList);
                                if (event != null && !event.getIsSaved ()) {
                                    event.setIsSaved (true);
                                    saveEvent (context, event);
                                }
                            }
                        }
                    } else {
                        e.printStackTrace ();
                    }
                }

            });
        } else {
            for (String eventObjId : GlobalVariables.userChanels) {
                EventInfo event = EventDataMethods.getEventFromObjID (eventObjId, eventsList);
                if (event != null && !event.getIsSaved ()) {
                    event.setIsSaved (true);
                    saveEvent (context, event);
                }
            }
        }
    }

    public static void handleSaveEventClicked(final EventInfo event,
                                              ImageView save,
                                              final Context context,
                                              int savedImageId,
                                              int unsavedImageId) {
        if (event.getIsSaved ()) {
            event.setIsSaved (false);
            save.setImageResource (unsavedImageId);
            Toast.makeText (context, R.string.you_unsaved_this_event, Toast.LENGTH_SHORT).show ();
            if (GlobalVariables.IS_CUSTOMER_REGISTERED_USER) {
                UserDetailsMethod.canclePush (event);
            }
            AsyncTask.execute (new Runnable () {
                @Override
                public void run() {
                    try {
                        context.deleteFile ("temp");
                        InputStream inputStream = context.openFileInput ("saves");
                        OutputStream outputStreamTemp = context.openFileOutput ("temp", Context.MODE_PRIVATE);
                        BufferedReader bufferedReader = new BufferedReader (new InputStreamReader (inputStream));
                        BufferedWriter bufferedWriter = new BufferedWriter (new OutputStreamWriter (outputStreamTemp));
                        String lineToRemove = event.getParseObjectId ();
                        String currentLine;
                        while ((currentLine = bufferedReader.readLine ()) != null) {
                            // trim newline when comparing with lineToRemove
                            String trimmedLine = currentLine.trim ();
                            if (trimmedLine.equals (lineToRemove)) continue;
                            else {
                                bufferedWriter.write (currentLine);
                                bufferedWriter.write (System.getProperty ("line.separator"));
                            }
                        }
                        bufferedReader.close ();
                        bufferedWriter.close ();
                        context.deleteFile ("saves");
                        inputStream = context.openFileInput ("temp");
                        outputStreamTemp = context.openFileOutput ("saves", Context.MODE_PRIVATE);
                        bufferedReader = new BufferedReader (new InputStreamReader (inputStream));
                        bufferedWriter = new BufferedWriter (new OutputStreamWriter (outputStreamTemp));
                        while ((currentLine = bufferedReader.readLine ()) != null) {
                            bufferedWriter.write (currentLine);
                            bufferedWriter.write (System.getProperty ("line.separator"));
                        }
                        bufferedReader.close ();
                        bufferedWriter.close ();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace ();
                    } catch (IOException e) {
                        e.printStackTrace ();
                    }
                }
            });
        } else {
            event.setIsSaved (true);
            save.setImageResource (savedImageId);
            Toast.makeText (context, R.string.you_saved_this_event, Toast.LENGTH_SHORT).show ();
            if(GlobalVariables.IS_CUSTOMER_REGISTERED_USER) {
                UserDetailsMethod.registerToPush (event);
            }
            saveEvent (context, event);
        }
    }

    public static void saveEvent(final Context context, final EventInfo event) {
        AsyncTask.execute (new Runnable () {
            @Override
            public void run() {
                try {
                    OutputStream outputStream = context.openFileOutput ("saves", Context.MODE_APPEND + Context.MODE_PRIVATE);
                    outputStream.write (event.getParseObjectId ().getBytes ());
                    outputStream.write (System.getProperty ("line.separator").getBytes ());
                    outputStream.close ();
                } catch (IOException e) {
                    e.printStackTrace ();
                }
            }
        });
    }

    ///////////////////////////////////////
    //Filter
    ///////////////////////////////////////
    public static void filterListsAndUpdateListAdapter(List<EventInfo> eventsListToFilter,
                                                       EventsListAdapter eventsListAdapter,
                                                       String[] namesCity,
                                                       int indexCityChosen) {
        if (GlobalVariables.USER_CHOSEN_CITY_MANUALLY) {
            ArrayList<EventInfo> tempEventsList =
                    GeneralStaticMethods.filterByCityAndFilterName (
                                                                           namesCity[indexCityChosen],
                                                                           GlobalVariables.CURRENT_FILTER_NAME,
                                                                           GlobalVariables.ALL_EVENTS_DATA);
            eventsListToFilter.clear ();
            eventsListToFilter.addAll (tempEventsList);
            eventsListAdapter.notifyDataSetChanged ();
        } else if (GlobalVariables.CITY_GPS != null) {
            ArrayList<EventInfo> tempEventsList =
                    GeneralStaticMethods.filterByCityAndFilterName (
                                                                           GlobalVariables.CITY_GPS,
                                                                           GlobalVariables.CURRENT_FILTER_NAME,
                                                                           GlobalVariables.ALL_EVENTS_DATA);
            eventsListToFilter.clear ();
            eventsListToFilter.addAll (tempEventsList);
            eventsListAdapter.notifyDataSetChanged ();
        }
    }

    public static List<EventInfo> filterByFilterName(String filterName, List<EventInfo> eventsListToFilter) {
        ArrayList<EventInfo> tempEventsList = new ArrayList<> ();
        if (filterName.isEmpty ()) {
            return eventsListToFilter;
        } else {
            for (int i = 0; i < eventsListToFilter.size (); i++) {
                if (filterName.isEmpty () ||
                            (filterName.equals (eventsListToFilter.get (i).getFilterName ()))) {
                    tempEventsList.add (eventsListToFilter.get (i));
                }
            }
        }
        return tempEventsList;
    }

    public static ArrayList<EventInfo> filterByCityAndFilterName(String cityName,
                                                                 String currentFilterName,
                                                                 List<EventInfo> eventsListToFilter) {
        ArrayList<EventInfo> tempEventsList = new ArrayList<> ();
        if (cityName.equals ("All Cities") && currentFilterName.isEmpty ()) {
            tempEventsList.addAll (eventsListToFilter);
            return tempEventsList;
        } else {
            for (int i = 0; i < eventsListToFilter.size (); i++) {
                String cityEvent = eventsListToFilter.get (i).getCity ();
                if (cityName.equals ("All Cities") || (cityEvent != null && cityEvent.equals (cityName))) {
                    if (currentFilterName.isEmpty () ||
                                (currentFilterName.equals (eventsListToFilter.get (i).getFilterName ()))) {
                        tempEventsList.add (eventsListToFilter.get (i));
                    }
                }
            }
        }
        return tempEventsList;
    }

    public static void filterEventsByArtist(String artistName, List<EventInfo> eventsListFiltered) {
        eventsListFiltered.clear ();
        for (EventInfo eventInfo : GlobalVariables.ALL_EVENTS_DATA) {
            if (eventInfo.getArtist () == null || eventInfo.getArtist ().isEmpty ()) {
                if (artistName.equals (GlobalVariables.No_Artist_Events)) {
                    eventsListFiltered.add (eventInfo);
                }
            } else if (eventInfo.getArtist ().equals (artistName)) {
                eventsListFiltered.add (eventInfo);
            }
        }
    }

    ///////////////////////////////////////
    //General
    ///////////////////////////////////////
    public static void onActivityResult(final int requestCode, final Intent data, Activity activity) {
        if (data != null && requestCode == GlobalVariables.REQUEST_CODE_MY_PICK) {
            String appName = data.getComponent ().flattenToShortString ();
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences (activity);
            String name = sp.getString ("name", null);
            String date = sp.getString ("date", null);
            String place = sp.getString ("place", null);
            if (appName.equals ("com.facebook.katana/com.facebook.composer.shareintent.ImplicitShareIntentHandlerDefaultAlias")) {
                ShareDialog shareDialog;
                shareDialog = new ShareDialog (activity);
                ShareLinkContent linkContent = new ShareLinkContent.Builder ()
                                                       .setContentTitle ("I`m going to " + name)
                                                       .setImageUrl (Uri.parse ("https://lh3.googleusercontent.com/-V5wz7jKaQW8/VpvKq0rwEOI/AAAAAAAAB6Y/cZoicmGpQpc/s279-Ic42/pic0.jpg"))
                                                       .setContentDescription (
                                                                                      "C u there at " + date + " !" + "\n" + "At " + place)
                                                       .setContentUrl (Uri.parse ("http://eventpageURL.com/here"))
                                                       .build ();
                shareDialog.show (linkContent);
            } else {
                activity.startActivity (data);
            }
        }
    }
}
