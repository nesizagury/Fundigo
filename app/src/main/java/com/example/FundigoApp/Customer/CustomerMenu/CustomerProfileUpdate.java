package com.example.FundigoApp.Customer.CustomerMenu;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.FundigoApp.Customer.CustomerDetails;
import com.example.FundigoApp.GlobalVariables;
import com.example.FundigoApp.R;
import com.example.FundigoApp.StaticMethods;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_customer_profile_update);
        customerName = (EditText) findViewById (R.id.userEdit);
        customerImg = (ImageView) findViewById (R.id.customerImage);
        getCurrentUserProfile ();
    }

    public void updateProfile(View view) {
        customer = customerName.getText ().toString ();
        byte[] imageToUpdate;
        List<ParseObject> list;
        if (!customer.isEmpty () || IMAGE_SELECTED) {
            String _userPhoneNumber = GlobalVariables.CUSTOMER_PHONE_NUM;
            if (!_userPhoneNumber.isEmpty ()) {
                try {
                    ParseQuery<ParseObject> query = ParseQuery.getQuery ("MipoProfile");
                    query.whereEqualTo ("number", _userPhoneNumber);
                    list = query.find ();
                    for (ParseObject obj : list) {
                        obj.put ("name", customer);
                        if (IMAGE_SELECTED) {
                            imageToUpdate = imageUpdate ();
                            ParseFile picFile = new ParseFile (imageToUpdate);
                            obj.put ("pic", picFile);
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
        if (i.resolveActivity (getPackageManager ()) != null) {
            startActivityForResult (i, GlobalVariables.SELECT_PICTURE);
        }
    }

    public byte[] imageUpdate() {
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
            if (requestCode == GlobalVariables.SELECT_PICTURE && resultCode == RESULT_OK && null != data) {
                Bitmap image = StaticMethods.getImageFromDevice (data, this);
                customerImg.setImageBitmap (image);
                customerImg.setVisibility (View.VISIBLE);
                IMAGE_SELECTED = true;
            }
        } catch (Exception e) {
            Log.e ("On ActivityResult Error", e.toString ());
        }
    }

    public void getCurrentUserProfile() {
        String phoneNum = GlobalVariables.CUSTOMER_PHONE_NUM;
        CustomerDetails customerDetails = StaticMethods.getUserDetailsFromParseInMainThread (phoneNum);
        String currentUserName = customerDetails.getCustomerName ();
        if(customerName.getText ().toString ().isEmpty ()){
            customerName.setText (currentUserName);
        }
        Bitmap userImage = customerDetails.getCustomerImage ();
        if(userImage != null && !IMAGE_SELECTED){
            customerImg.setImageBitmap (userImage);
            customerImg.setVisibility (View.VISIBLE);
            IMAGE_SELECTED = true;
        }
    }
}
