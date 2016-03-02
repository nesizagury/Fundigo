package com.example.FundigoApp.Customer.CustomerMenu;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.FundigoApp.GlobalVariables;
import com.example.FundigoApp.R;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class CustomerProfileUpdate extends AppCompatActivity {

    String customer;
    EditText customerName;
    ImageView customerImg;
    boolean IMAGE_SELECTED = false;
    private static final int SELECT_PICTURE = 1;
    String picturePath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_customer_profile_update);
        customerName = (EditText) findViewById (R.id.userEdit);
        customerImg = (ImageView) findViewById (R.id.customerImage);
    }

    public void updateProfile(View view) {
        customer = customerName.getText ().toString ();
        byte[] imageToUpdate;
        List<ParseObject> list;
        if (!customer.isEmpty () || IMAGE_SELECTED) {
            String _userPhoneNumber = GlobalVariables.CUSTOMER_PHONE_NUM;
            if (!_userPhoneNumber.isEmpty ()) {
                try {
                    ParseQuery<ParseObject> query = ParseQuery.getQuery ("Numbers");
                    query.whereEqualTo ("number", _userPhoneNumber);
                    list = query.find ();
                    for (ParseObject obj : list) {
                        obj.put ("name", customer);
                        if (IMAGE_SELECTED) {
                            imageToUpdate = ImageUpdate (view);
                            ParseFile picFile = new ParseFile (imageToUpdate);
                            obj.put ("ImageFile", picFile);
                        }
                        obj.save ();
                        finish ();
                    }
                } catch (Exception e) {
                    Log.e ("Exception catch", e.toString ());
                }
            } else {
                Toast.makeText (getApplicationContext (), R.string.user_may_not_registered_or_not_exist, Toast.LENGTH_SHORT).show ();
            }
            if (!customer.isEmpty ())
                Toast.makeText (getApplicationContext (), R.string.user_updated_and_now_it_is + customer, Toast.LENGTH_SHORT).show ();
            else
                Toast.makeText (getApplicationContext (), R.string.picture_updated, Toast.LENGTH_SHORT).show ();
        } else
            Toast.makeText (getApplicationContext (), R.string.nothing_selected_to_update, Toast.LENGTH_SHORT).show ();
    }

    public void imageUpload(View view) {
        Intent i = new Intent (Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (i.resolveActivity (getPackageManager ()) != null)
            startActivityForResult (i, SELECT_PICTURE);
    }

    public byte[] ImageUpdate(View v) {
        byte[] image;
        try {
            customerImg.buildDrawingCache ();
            Bitmap bitmap = customerImg.getDrawingCache ();
            ByteArrayOutputStream stream = new ByteArrayOutputStream ();
            bitmap.compress (Bitmap.CompressFormat.JPEG, 100, stream);
            image = stream.toByteArray ();
            return image;
        } catch (Exception e) {
            Log.e ("Exceptpion in In Image", e.toString ());
            return null;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (requestCode == SELECT_PICTURE && resultCode == RESULT_OK && null != data) {
                Uri selectedImage = data.getData ();
                ParcelFileDescriptor parcelFileDescriptor =
                        null;
                try {
                    parcelFileDescriptor = getContentResolver ().openFileDescriptor (selectedImage, "r");
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
                int angleToRotate = getOrientation (selectedImage);
                matrix.postRotate (angleToRotate);
                Bitmap rotatedBitmap = Bitmap.createBitmap (image,
                                                                   0,
                                                                   0,
                                                                   image.getWidth (),
                                                                   image.getHeight (),
                                                                   matrix,
                                                                   true);
                customerImg.setImageBitmap (rotatedBitmap);
                customerImg.setVisibility (View.VISIBLE);
                IMAGE_SELECTED = true;
            }
        } catch (Exception e) {
            Log.e ("On ActivityResult Error", e.toString ());
        }
    }

    public int getOrientation(Uri selectedImage) {
        int orientation = 0;
        final String[] projection = new String[]{MediaStore.Images.Media.ORIENTATION};
        final Cursor cursor = this.getContentResolver ().query (selectedImage, projection, null, null, null);
        if (cursor != null) {
            final int orientationColumnIndex = cursor.getColumnIndex (MediaStore.Images.Media.ORIENTATION);
            if (cursor.moveToFirst ()) {
                orientation = cursor.isNull (orientationColumnIndex) ? 0 : cursor.getInt (orientationColumnIndex);
            }
            cursor.close ();
        }
        return orientation;
    }
}
