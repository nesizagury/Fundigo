package com.example.FundigoApp;

import android.Manifest.permission;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.FundigoApp.Customer.CustomerDetails;
import com.example.FundigoApp.Customer.SavedEvents.SavedEventActivity;
import com.example.FundigoApp.Events.Event;
import com.example.FundigoApp.Events.EventInfo;
import com.example.FundigoApp.Events.EventsListAdapter;
import com.example.FundigoApp.MyLocation.CityMenu;
import com.example.FundigoApp.Producer.Artists.Artist;
import com.example.FundigoApp.Verifications.Numbers;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StaticMethods {
    //in millis
    private static final int GPS_UPDATE_TIME_INTERVAL = 10000;

    private static LocationManager locationManager;
    private static LocationListener locationListener;

    public static void uploadEventsData(final GetEventsDataCallback ic,
                                        String producerId,
                                        final Context context,
                                        final Intent intent) {
        final ArrayList<EventInfo> tempEventsList = new ArrayList<> ();
        ParseQuery<Event> query = new ParseQuery ("Event");
        if (producerId != null && producerId != "") {
            query.whereEqualTo ("producerId", producerId);
        }
        query.orderByDescending ("createdAt");
        query.findInBackground (new FindCallback<Event> () {
            public void done(List<Event> eventParse, ParseException e) {
                if (e == null) {
                    ParseFile imageFile;
                    byte[] data = null;
                    Bitmap bmp;
                    for (int i = 0; i < eventParse.size (); i++) {
                        imageFile = (ParseFile) eventParse.get (i).get ("ImageFile");
                        if (imageFile != null) {
                            try {
                                data = imageFile.getData ();
                            } catch (ParseException e1) {
                                e1.printStackTrace ();
                            }
                            bmp = BitmapFactory.decodeByteArray (data, 0, data.length);
                        } else {
                            bmp = null;
                        }
                        Event event = eventParse.get (i);
                        tempEventsList.add (new EventInfo (bmp,
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
                    updateSavedEvents (tempEventsList, context);
                    GlobalVariables.ALL_EVENTS_DATA.clear ();
                    GlobalVariables.ALL_EVENTS_DATA.addAll (tempEventsList);

                    GlobalVariables.cityMenuInstance = new CityMenu (tempEventsList, context);
                    GlobalVariables.namesCity = GlobalVariables.cityMenuInstance.getCityNames ();
                    if (!GlobalVariables.deepLinkEventObjID.equals ("") || GlobalVariables.deepLinkEventObjID != "") {
                        for (int i = 0; i < GlobalVariables.ALL_EVENTS_DATA.size (); i++) {
                            if (GlobalVariables.deepLinkEventObjID.equals (GlobalVariables.ALL_EVENTS_DATA.get (i).getParseObjectId ())) {
                                ic.eventDataCallback ();
                                Bundle b = new Bundle ();
                                onEventItemClick (i, GlobalVariables.ALL_EVENTS_DATA, intent);
                                intent.putExtras (b);
                                context.startActivity (intent);
                                Toast.makeText (context.getApplicationContext (), "found", Toast.LENGTH_SHORT).show ();
                                return;
                            }
                        }
                    }
                    ic.eventDataCallback ();
                } else {
                    e.printStackTrace ();
                }
            }
        });
    }

    public static EventInfo getEventFromObjID(String parseObjID, List<EventInfo> eventsList) {
        for (EventInfo eventInfo : eventsList) {
            if (eventInfo.getParseObjectId ().equals (parseObjID)) {
                return eventInfo;
            }
        }
        return null;
    }

    public interface GetEventsDataCallback {
        void eventDataCallback();
    }

    public interface GpsICallback {
        void gpsCallback();
    }

    private static void updateSavedEvents(List<EventInfo> eventsList, Context context) {
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
    }

    public static void updateDeviceLocationGPS(Context context, GpsICallback iCallback) {
        boolean gps_enabled = false;
        boolean network_enabled = false;
        boolean passive_enabled = false;

        locationManager = (LocationManager) context.getSystemService (Context.LOCATION_SERVICE);
        locationListener = new MyLocationListener (iCallback, context);
        try {
            gps_enabled = locationManager.isProviderEnabled (LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
            ex.printStackTrace ();
        }
        try {
            network_enabled = locationManager.isProviderEnabled (LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
            ex.printStackTrace ();
        }
        try {
            passive_enabled = locationManager.isProviderEnabled (LocationManager.PASSIVE_PROVIDER);
        } catch (Exception ex) {
            ex.printStackTrace ();
        }
        if (gps_enabled) {
            if (ActivityCompat.checkSelfPermission (context, permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission (context, permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //do none
            } else {
                locationManager.requestLocationUpdates (LocationManager.GPS_PROVIDER, GPS_UPDATE_TIME_INTERVAL, 0, locationListener);
            }
        }
        if (network_enabled) {
            locationManager.requestLocationUpdates (LocationManager.NETWORK_PROVIDER, GPS_UPDATE_TIME_INTERVAL, 0, locationListener);
        }
        if (passive_enabled) {
            locationManager.requestLocationUpdates (LocationManager.PASSIVE_PROVIDER, GPS_UPDATE_TIME_INTERVAL, 0, locationListener);
        }
    }

    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt (context.getContentResolver (), Settings.Secure.LOCATION_MODE);
            } catch (SettingNotFoundException e) {
                e.printStackTrace ();
            }
            return locationMode != Settings.Secure.LOCATION_MODE_OFF;
        } else {
            locationProviders = Settings.Secure.getString (context.getContentResolver (), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty (locationProviders);
        }
    }

    private static class MyLocationListener implements LocationListener {
        GpsICallback ic;
        Context context;

        MyLocationListener(GpsICallback iCallback, Context context) {
            ic = iCallback;
            this.context = context;
        }

        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                String cityGPS = findCurrentCityGPS (location);
                GlobalVariables.MY_LOCATION = location;
                if (!cityGPS.isEmpty ()) {
                    GlobalVariables.CITY_GPS = cityGPS;
                    ic.gpsCallback ();
                }
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        public String findCurrentCityGPS(Location loc) {
            Geocoder gcd = new Geocoder (context, Locale.ENGLISH);
            if (loc != null) {
                List<Address> addresses = null;
                try {
                    addresses = gcd.getFromLocation (loc.getLatitude (), loc.getLongitude (), 1);
                } catch (IOException e) {
                    e.printStackTrace ();
                }
                if (addresses != null && addresses.size () > 0) {
                    return addresses.get (0).getLocality ();
                }
            }
            return "";
        }
    }

    public static String getCustomerPhoneNumFromFile(Context context) {
        String phone_number = "";
        try {
            InputStream inputStream = context.openFileInput ("verify.txt");
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
            e.printStackTrace ();
        } catch (IOException e) {
            e.printStackTrace ();
        }
        return phone_number;
    }

    public static void filterListsAndUpdateListAdapter(List<EventInfo> eventsListToFilter,
                                                       EventsListAdapter eventsListAdapter,
                                                       String[] namesCity,
                                                       int indexCityChosen) {
        if (GlobalVariables.USER_CHOSEN_CITY_MANUALLY) {
            ArrayList<EventInfo> tempEventsList =
                    StaticMethods.filterByCityAndFilterName (
                                                                    namesCity[indexCityChosen],
                                                                    GlobalVariables.CURRENT_FILTER_NAME,
                                                                    GlobalVariables.ALL_EVENTS_DATA);
            eventsListToFilter.clear ();
            eventsListToFilter.addAll (tempEventsList);
            eventsListAdapter.notifyDataSetChanged ();
        } else if (GlobalVariables.CITY_GPS != null) {
            ArrayList<EventInfo> tempEventsList =
                    StaticMethods.filterByCityAndFilterName (
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

    public static CustomerDetails getUserDetailsFromParseInMainThread(String customerPhoneNum) {
        String faceBookId = null;
        String picUrl = null;
        String customerName = null;
        Bitmap customerImage = null;
        ParseQuery<Numbers> query = ParseQuery.getQuery (Numbers.class);
        query.whereEqualTo ("number", customerPhoneNum);
        List<Numbers> numbers = null;
        try {
            numbers = query.find ();
            return getUserDetails (numbers);
        } catch (ParseException e) {
            e.printStackTrace ();
        }
        //all null
        return new CustomerDetails (faceBookId, picUrl, customerImage, customerName);
    }

    public static CustomerDetails getUserDetails(List<Numbers> numbers) {
        String faceBookId = null;
        String customerPicFacebookUrl = null;
        Bitmap customerImage = null;
        String customerName = null;
        if (numbers.size () > 0) {
            Numbers number = numbers.get (0);
            faceBookId = number.getFbId ();
            customerPicFacebookUrl = number.getFbUrl ();
            customerName = number.getName ();
            ParseFile imageFile;
            byte[] data = null;
            imageFile = (ParseFile) number.getImageFile ();
            if (imageFile != null) {
                try {
                    data = imageFile.getData ();
                } catch (ParseException e1) {
                    e1.printStackTrace ();
                }
                customerImage = BitmapFactory.decodeByteArray (data, 0, data.length);
            }
        }
        return new CustomerDetails (faceBookId, customerPicFacebookUrl, customerImage, customerName);
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

    public static void onEventItemClick(int positionViewItem,
                                        List<EventInfo> eventsList,
                                        Intent intent) {
        if (eventsList.get (positionViewItem).getImageBitmap () != null) {
            Bitmap bmp = eventsList.get (positionViewItem).getImageBitmap ();
            ByteArrayOutputStream stream = new ByteArrayOutputStream ();
            bmp.compress (Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] byteArray = stream.toByteArray ();
            intent.putExtra ("eventImage", byteArray);
        } else {
            intent.putExtra ("eventImage", "");
        }
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

    public static void handleSaveEventClicked(final EventInfo event,
                                              ImageView save,
                                              final Context context,
                                              int savedImageId,
                                              int unsavedImageId) {
        if (event.getIsSaved ()) {
            event.setIsSaved (false);
            save.setImageResource (unsavedImageId);
            Toast.makeText (context, "You unSaved this event", Toast.LENGTH_SHORT).show ();
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
            Toast.makeText (context, "You Saved this event", Toast.LENGTH_SHORT).show ();
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
        if (GlobalVariables.SAVED_ACTIVITY_RUNNING) {
            SavedEventActivity.getSavedEventsFromJavaList ();
        }
    }

    public static Bitmap getImageFromDevice(Intent data, Context context) {
        Uri selectedImage = data.getData ();
        ParcelFileDescriptor parcelFileDescriptor =
                null;
        try {
            parcelFileDescriptor = context.getContentResolver ().openFileDescriptor (selectedImage, "r");
        } catch (FileNotFoundException e) {
            e.printStackTrace ();
        }
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor ();
        Bitmap image = BitmapFactory.decodeFileDescriptor (fileDescriptor);
        try {
            parcelFileDescriptor.close ();
        } catch (IOException e) {
            e.printStackTrace ();
        }
        Matrix matrix = new Matrix ();
        int angleToRotate = getOrientation (selectedImage, context);
        matrix.postRotate (angleToRotate);
        Bitmap rotatedBitmap = Bitmap.createBitmap (image,
                                                           0,
                                                           0,
                                                           image.getWidth (),
                                                           image.getHeight (),
                                                           matrix,
                                                           true);
        return rotatedBitmap;
    }

    private static int getOrientation(Uri selectedImage, Context context) {
        int orientation = 0;
        final String[] projection = new String[]{MediaStore.Images.Media.ORIENTATION};
        final Cursor cursor = context.getContentResolver ().query (selectedImage, projection, null, null, null);
        if (cursor != null) {
            final int orientationColumnIndex = cursor.getColumnIndex (MediaStore.Images.Media.ORIENTATION);
            if (cursor.moveToFirst ()) {
                orientation = cursor.isNull (orientationColumnIndex) ? 0 : cursor.getInt (orientationColumnIndex);
            }
            cursor.close ();
        }
        return orientation;
    }

    private static String getEventDateAsString(Date eventDate) {
        long time = eventDate.getTime();
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
                                                     Event event){
        eventInfo.setPrice (event.getPrice ());
        eventInfo.setAddress (event.getAddress ());
        eventInfo.setIsStadium (event.getIsStadium ());
        eventInfo.setParseObjectId (event.getObjectId ());
        Date currentDate = new Date();
        eventInfo.setIsFutureEvent (event.getRealDate ().after (currentDate));
        eventInfo.setArtist (event.getArtist ());
        eventInfo.setAtm (event.getEventATMService ());
        eventInfo.setCapacity (event.getEventCapacityService ());
        eventInfo.setDate (event.getRealDate ());
        eventInfo.setDateAsString (StaticMethods.getEventDateAsString (event.getRealDate ()));
        eventInfo.setFilterName (event.getFilterName ());
        ParseFile imageFile;
        byte[] data = null;
        Bitmap bmp;
        imageFile = (ParseFile) event.get ("ImageFile");
        if (imageFile != null) {
            try {
                data = imageFile.getData ();
            } catch (ParseException e1) {
                e1.printStackTrace ();
            }
            bmp = BitmapFactory.decodeByteArray (data, 0, data.length);
        } else {
            bmp = null;
        }
        eventInfo.setBitmap (bmp);
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
