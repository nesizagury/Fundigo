package com.example.FundigoApp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import io.branch.referral.SharingHelper;
import io.branch.referral.util.LinkProperties;
import io.branch.referral.util.ShareSheetStyle;

public class DeepLinkActivity extends Activity {
    String index;
    static final int REQUEST_CODE_MY_PICK = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_deeplink);
        Intent in = getIntent ();
    }


    public void AppPage(View v) {
        BranchUniversalObject branchUniversalObject = new BranchUniversalObject ()
                                                              .setCanonicalIdentifier ("item/1234")
                                                              .setTitle (""+R.string.my_content_title)
                                                              .setContentDescription (""+R.string.my_content_description)
                                                              .setContentIndexingMode (BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
                                                              .addContentMetadata ("objectId", getIntent ().getStringExtra ("objectId"));

        LinkProperties linkProperties = new LinkProperties ()
                                                .setChannel ("My Application")
                                                .setFeature ("sharing");

        ShareSheetStyle shareSheetStyle = new ShareSheetStyle (this, "Check this out!", "This stuff is awesome: ")
                                                  .setCopyUrlStyle (getResources ().getDrawable (android.R.drawable.ic_menu_send), "Copy", "Added to clipboard")
                                                  .setMoreOptionStyle (getResources ().getDrawable (android.R.drawable.ic_menu_search), "Show more")
                                                  .addPreferredSharingOption (SharingHelper.SHARE_WITH.FACEBOOK)
                                                  .addPreferredSharingOption (SharingHelper.SHARE_WITH.EMAIL)
                                                  .addPreferredSharingOption (SharingHelper.SHARE_WITH.WHATS_APP);
        branchUniversalObject.showShareSheet (this,
                                                     linkProperties,
                                                     shareSheetStyle,
                                                     new Branch.BranchLinkShareListener () {
                                                         @Override
                                                         public void onShareLinkDialogLaunched() {
                                                         }

                                                         @Override
                                                         public void onShareLinkDialogDismissed() {
                                                         }

                                                         @Override
                                                         public void onLinkShareResponse(String sharedLink, String sharedChannel, BranchError error) {
                                                         }

                                                         @Override
                                                         public void onChannelSelected(String channelName) {
                                                             finish ();
                                                         }
                                                     });
        branchUniversalObject.generateShortUrl (this, linkProperties, new Branch.BranchLinkCreateListener () {
            @Override
            public void onLinkCreate(String url, BranchError error) {
                if (error == null) {
                    Toast.makeText (getApplicationContext (), url, Toast.LENGTH_LONG).show ();


                } else
                    Toast.makeText (getApplication (), error.getMessage () + "", Toast.LENGTH_SHORT).show ();

            }
        });

    }

    public void WebPage(View v) {

        try {
            Bitmap largeIcon = BitmapFactory.decodeResource (getResources (), R.mipmap.pic0);
            ByteArrayOutputStream bytes = new ByteArrayOutputStream ();
            largeIcon.compress (Bitmap.CompressFormat.JPEG, 40, bytes);
            File f = new File (Environment.getExternalStorageDirectory () + File.separator + "test.jpg");
            f.createNewFile ();
            FileOutputStream fo = new FileOutputStream (f);
            fo.write (bytes.toByteArray ());
            fo.close ();
        } catch (IOException e) {
            e.printStackTrace ();
        }
        Intent intent = new Intent (Intent.ACTION_SEND);
        intent.setType ("image/jpeg");
        intent.putExtra (Intent.EXTRA_TEXT, "I`m going to " + getIntent ().getStringExtra ("name") +
                                                    "\n" + "C u there at " + getIntent ().getStringExtra ("place") + " !" +
                                                    "\n" + "At " + getIntent ().getStringExtra ("date") +
                                                    "\n" + "http://eventpageURL.com/here");
        String imagePath = Environment.getExternalStorageDirectory () + File.separator + "test.jpg";
        File imageFileToShare = new File (imagePath);
        Uri uri = Uri.fromFile (imageFileToShare);
        intent.putExtra (Intent.EXTRA_STREAM, uri);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences (getApplicationContext ());
        SharedPreferences.Editor editor = sp.edit ();
        editor.putString ("name", getIntent ().getStringExtra ("name"));
        editor.putString ("date", getIntent ().getStringExtra ("date"));
        editor.putString ("place", getIntent ().getStringExtra ("place"));
        editor.apply ();
        Intent intentPick = new Intent ();
        intentPick.setAction (Intent.ACTION_PICK_ACTIVITY);
        intentPick.putExtra (Intent.EXTRA_TITLE, "Launch using");
        intentPick.putExtra (Intent.EXTRA_INTENT, intent);
        //startActivityForResult(intentPick, REQUEST_CODE_MY_PICK);
        startActivity (intentPick.createChooser (intent, "Share With:"));
        finish ();
    }
}
