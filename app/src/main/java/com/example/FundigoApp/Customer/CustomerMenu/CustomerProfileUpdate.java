package com.example.FundigoApp.Customer.CustomerMenu;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
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
                Toast.makeText (getApplicationContext (), "User may not Registered or not Exist", Toast.LENGTH_SHORT).show ();
            }
            if (!customer.isEmpty ())
                Toast.makeText (getApplicationContext (), "User updated and now it is: " + customer, Toast.LENGTH_SHORT).show ();
            else
                Toast.makeText (getApplicationContext (), "Picture updated", Toast.LENGTH_SHORT).show ();
        } else
            Toast.makeText (getApplicationContext (), "Nothing Selected to Update", Toast.LENGTH_SHORT).show ();
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
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver ().query (selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst ();
                int columnIndex = cursor.getColumnIndex (filePathColumn[0]);
                picturePath = cursor.getString (columnIndex);
                cursor.close ();
                customerImg.setImageBitmap (BitmapFactory.decodeFile (picturePath));
                IMAGE_SELECTED = true;
                customerImg.setVisibility (View.VISIBLE);
            }
        } catch (Exception e) {
            Log.e ("On ActivityResult Error", e.toString ());
        }
    }
}
