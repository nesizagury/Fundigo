package com.example.FundigoApp;

import android.location.Location;

import com.example.FundigoApp.Events.EventInfo;
import com.example.FundigoApp.MyLocation.CityMenu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GlobalVariables {
    public static final String FB_PIC_URL = "fb_pic_url";
    public static final String FB_NAME = "fb_name";
    public static final String FB_ID = "fb_id";
    public static final String All_Events = "NoArtist Events";
    public static final int REQUEST_CODE_MY_PICK = 1;
    public static final String QR = "qr";
    public static final String GEO_API_ADDRESS = "https://maps.googleapis.com/maps/api/geocode/json?";
    public static final String GEO_API_KEY = "AIzaSyAO_BADR0qgh5i6oirplDXw0wCCxCZoLe8";
    public static final String YELLOW = "yellow";
    public static final String GREEN = "green";
    public static final String BLUE = "blue";
    public static final String PINK = "pink";
    public static final String ORANGE = "orange";
    public static final String SEATS = "seats";
    public static final String OBJECTID = "objectId";


    public static boolean IS_CUSTOMER_GUEST = false;
    public static boolean IS_CUSTOMER_REGISTERED_USER = false;
    public static boolean IS_PRODUCER = false;
    public static String CUSTOMER_PHONE_NUM = null;
    public static String PRODUCER_PARSE_OBJECT_ID = null;

    public static List<EventInfo> ALL_EVENTS_DATA = new ArrayList<EventInfo> ();
    public static String CITY_GPS = null;
    public static Location MY_LOCATION = null;
    public static String CURRENT_FILTER_NAME = "";
    public static String CURRENT_CITY_NAME = "";
    public static boolean SAVED_ACTIVITY_RUNNING = false;
    public static boolean USER_CHOSEN_CITY_MANUALLY = false;

    public static CityMenu cityMenuInstance;
    public static String[] namesCity;
    public static int indexCityGPS = 0;
    public static HashMap<Integer, Integer> popUpIDToCityIndex = new HashMap<Integer, Integer> ();
    public static int indexCityChosen = 0;
    public static String deepLink_params = "";
}
