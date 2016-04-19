package com.example.FundigoApp.StaticMethod;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.FundigoApp.Customer.CustomerDetails;
import com.example.FundigoApp.Customer.Social.Profile;
import com.example.FundigoApp.Events.EventInfo;
import com.example.FundigoApp.GlobalVariables;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;

import java.util.List;

public class UserDetailsMethod {

    public static CustomerDetails getUserDetailsFromParseInMainThread(String customerPhoneNum) {
        String faceBookId = null;
        String picUrl = null;
        String customerName = null;
        String customerImage = null;
        ParseQuery<Profile> query = ParseQuery.getQuery (Profile.class);
        query.whereEqualTo ("number", customerPhoneNum);
        List<Profile> profile = null;
        try {
            profile = query.find ();
            return getUserDetails (profile);
        } catch (ParseException e) {
            e.printStackTrace ();
        }
        //all null
        return new CustomerDetails (faceBookId, picUrl, customerImage, customerName);
    }

    public static CustomerDetails getUserDetails(List<Profile> profiles) {
        String faceBookId = null;
        String customerPicFacebookUrl = null;
        String customerImage = null;
        String customerName = null;
        if (profiles.size () > 0) {
            Profile profile = profiles.get (0);
            faceBookId = profile.getFbId ();
            customerPicFacebookUrl = profile.getFbUrl ();
            customerName = profile.getName ();
            customerImage = profile.getPic ().getUrl ();
        }
        return new CustomerDetails (faceBookId, customerPicFacebookUrl, customerImage, customerName);
    }

    public static CustomerDetails getUserDetailsWithBitmap(List<Profile> numbers) {
        String faceBookId = null;
        String customerPicFacebookUrl = null;
        Bitmap customerImage = null;
        String customerName = null;
        if (numbers.size () > 0) {
            Profile number = numbers.get (0);
            faceBookId = number.getFbId ();
            customerPicFacebookUrl = number.getFbUrl ();
            customerName = number.getName ();
            ParseFile imageFile;
            byte[] data = null;
            imageFile = (ParseFile) number.getPic ();
            if (imageFile != null) {
                try {
                    data = imageFile.getData ();
                } catch (ParseException e1) {
                    e1.printStackTrace ();
                }
                customerImage = BitmapFactory.decodeByteArray (data, 0, data.length);
            }
        }
        CustomerDetails customerDetails = new CustomerDetails (faceBookId,
                                                                      customerPicFacebookUrl,
                                                                      null,
                                                                      customerName);
        customerDetails.setBitmap (customerImage);
        return customerDetails;
    }

    public static CustomerDetails getUserDetailsFromParseInMainThreadWithBitmap(String customerPhoneNum) {
        String faceBookId = null;
        String picUrl = null;
        String customerName = null;
        String customerImage = null;
        ParseQuery<Profile> query = ParseQuery.getQuery (Profile.class);
        query.whereEqualTo ("number", customerPhoneNum);
        List<Profile> numbers = null;
        try {
            numbers = query.find ();
            return getUserDetailsWithBitmap (numbers);
        } catch (ParseException e) {
            e.printStackTrace ();
        }
        //all null
        return new CustomerDetails (faceBookId, picUrl, customerImage, customerName);
    }

    public static void canclePush(EventInfo eventInfo) {
        ParseInstallation installation = ParseInstallation.getCurrentInstallation ();
        ParsePush.unsubscribeInBackground ("a" + eventInfo.getParseObjectId ());
        installation.saveInBackground ();
        while (GlobalVariables.userChanels.contains (eventInfo.getParseObjectId ())) {
            GlobalVariables.userChanels.remove (eventInfo.getParseObjectId ());
        }
        ParseQuery<Profile> query = ParseQuery.getQuery ("Profile");
        query.whereEqualTo ("number", GlobalVariables.CUSTOMER_PHONE_NUM);

        query.findInBackground (new FindCallback<Profile> () {
            @Override
            public void done(List<Profile> objects, ParseException e) {
                if (e == null) {
                    objects.get (0).removeAll ("Chanels", objects.get (0).getChanels ());
                    objects.get (0).saveInBackground ();

                    objects.get (0).addAllUnique ("Chanels", GlobalVariables.userChanels);
                    objects.get (0).saveInBackground ();
                } else {
                    e.printStackTrace ();
                }
            }

        });
    }

    public static void registerToPush(EventInfo eventInfo) {
        ParseInstallation installation = ParseInstallation.getCurrentInstallation ();
        ParsePush.subscribeInBackground ("a" + eventInfo.getParseObjectId ());
        installation.saveInBackground ();
        if (!GlobalVariables.userChanels.contains (eventInfo.getParseObjectId ())) {
            GlobalVariables.userChanels.add (eventInfo.getParseObjectId ());
        }

        ParseQuery<Profile> query = ParseQuery.getQuery ("Profile");
        query.whereEqualTo ("number", GlobalVariables.CUSTOMER_PHONE_NUM);
        query.findInBackground (new FindCallback<Profile> () {
            @Override
            public void done(List<Profile> objects, ParseException e) {
                if (e == null) {
                    if (objects.get (0).getChanels () != null) {
                        objects.get (0).getChanels ().removeAll ((objects.get (0).getChanels ()));
                        objects.get (0).saveInBackground ();
                    }
                    objects.get (0).addAllUnique ("Chanels", GlobalVariables.userChanels);
                    objects.get (0).saveInBackground ();
                } else {
                    e.printStackTrace ();
                }
            }

        });
    }
}
