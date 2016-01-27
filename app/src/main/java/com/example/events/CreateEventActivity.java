package com.example.events;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;

import java.io.ByteArrayOutputStream;

/**
 * Created by מנהל on 21/01/2016.
 */
public class CreateEventActivity extends Activity{

    String picturePath;
    private static final int SELECT_PICTURE = 1;
    private ProgressDialog progressDialog;
    ImageView imageV;
    TextView imageTV;
    EditText nameET;
    TextView nameTV;
    TextView descriptionTV;
    Button browse_button;
    EditText descriptionET;

    TextView priceTV;
    TextView addressTV;
    TextView quantityTV;
    TextView xTV;
    TextView yTV;
    EditText priceET;
    EditText addressET;
    EditText quantityET;
    EditText xET;
    EditText yET;

    Button done_button;
    TextView tagsTV;
    EditText tagsET;
    boolean pictureSelected = false;
    TextView dateTV;
    EditText dateET;
    TextView placeTV;
    EditText placeET;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);

        componentInit();

        descriptionET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {

                    HideFirstStage(null);

                }
                return false;
            }
        });

        placeET.setOnEditorActionListener (new TextView.OnEditorActionListener () {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode () == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {

                    showThirdStage(null);

                }
                return false;
            }
        });

    }


    public void imageUpload(View view) {
        Intent i = new Intent (
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, SELECT_PICTURE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == SELECT_PICTURE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData ();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver ().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst ();
            int columnIndex = cursor.getColumnIndex (filePathColumn[0]);
            picturePath = cursor.getString (columnIndex);
            cursor.close();
            imageV.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            pictureSelected = true;


        }
    }





    public void saveEvent(View view){


     Event event = new Event();

        if(tagsET.getText().length() != 0) {

            event.setName(nameET.getText().toString());
            event.setDescription(descriptionET.getText().toString());
            event.setPrice(priceET.getText().toString());
            event.setNumOfTicketsLeft(quantityET.getText().toString());
            event.setAddress(addressET.getText().toString());
            event.setX(Double.parseDouble(xET.getText().toString()));
            event.setY(Double.parseDouble(yET.getText().toString()));
            event.setTags(tagsET.getText().toString());
            event.setProducerId(MainActivity.producerId);
            event.setDate(dateET.getText().toString());
            event.setPlace(placeET.getText().toString());

            if (pictureSelected) {
                imageV.buildDrawingCache();
                Bitmap bitmap = imageV.getDrawingCache();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] image = stream.toByteArray();
                ParseFile file = new ParseFile("picturePath", image);
                try {
                    file.save();
                } catch (ParseException e) {
                    e.printStackTrace();
                }


                event.put("ImageFile", file);
            }

            try {
                event.save();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Toast.makeText(getApplicationContext(), "Event has created successfully!", Toast.LENGTH_SHORT).show ();
            finish();
        }

        else
            Toast.makeText(getApplicationContext(), "Please fill the  empty fields", Toast.LENGTH_SHORT).show ();

    }

    @Override
    public void onBackPressed() {

        if(nameET.getVisibility() == View.VISIBLE)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure you want to exit?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            CreateEventActivity.this.finish();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }

        // at stage two
        if(priceET.getVisibility() == View.VISIBLE)
        {
            showFirstStage(null);
            hideSecondStage(null);
        }

        // at stage three
        if(tagsET.getVisibility() == View.VISIBLE)
        {
            hideThirdStage(null);
            showSecondStage(null);
        }


    }

    public void showFirstStage(View view){

        nameTV.setVisibility(View.VISIBLE);
        descriptionTV.setVisibility(View.VISIBLE);
        nameET.setVisibility(View.VISIBLE);
        descriptionET.setVisibility(View.VISIBLE);
        dateTV.setVisibility(View.VISIBLE);
        dateET.setVisibility(View.VISIBLE);

    }

    public void HideFirstStage(View view){

        if(nameET.getText().length() != 0 && descriptionET.getText().length() != 0
                && dateET.getText().length() != 0) {
            nameTV.setVisibility(View.INVISIBLE);
            descriptionTV.setVisibility(View.INVISIBLE);
            nameET.setVisibility(View.INVISIBLE);
            descriptionET.setVisibility(View.INVISIBLE);
            dateTV.setVisibility(View.INVISIBLE);
            dateET.setVisibility(View.INVISIBLE);

            showSecondStage(null);

        }

    }

    public void showSecondStage(View view){


        priceTV.setVisibility(View.VISIBLE);
        priceET.setVisibility(View.VISIBLE);
        quantityTV.setVisibility(View.VISIBLE);
        quantityET.setVisibility(View.VISIBLE);
        addressTV.setVisibility(View.VISIBLE);
        addressET.setVisibility(View.VISIBLE);
        placeTV.setVisibility(View.VISIBLE);
        placeET.setVisibility(View.VISIBLE);
        xTV.setVisibility(View.VISIBLE);
        xET.setVisibility(View.VISIBLE);
        yTV.setVisibility(View.VISIBLE);
        yET.setVisibility(View.VISIBLE);

    }

    public void hideSecondStage(View view){


        priceTV.setVisibility(View.INVISIBLE);
        priceET.setVisibility(View.INVISIBLE);
        quantityTV.setVisibility(View.INVISIBLE);
        quantityET.setVisibility(View.INVISIBLE);
        placeET.setVisibility(View.INVISIBLE);
        placeTV.setVisibility(View.INVISIBLE);
        addressTV.setVisibility(View.INVISIBLE);
        addressET.setVisibility(View.INVISIBLE);
        xTV.setVisibility(View.INVISIBLE);
        xET.setVisibility(View.INVISIBLE);
        yTV.setVisibility(View.INVISIBLE);
        yET.setVisibility(View.INVISIBLE);

    }

    public void showThirdStage(View view){

        if(priceET.getText().length() != 0 && quantityET.getText().length() != 0 &&
                addressET.getText().length() != 0 && xET.getText().length() != 0 &&
                yET.getText().length() != 0 && placeET.getText().length() != 0) {
            imageTV.setVisibility(View.VISIBLE);
            imageV.setVisibility(View.VISIBLE);
            browse_button.setVisibility(View.VISIBLE);
            tagsTV.setVisibility(View.VISIBLE);
            tagsET.setVisibility(View.VISIBLE);
            done_button.setVisibility(View.VISIBLE);

            hideSecondStage(null);
        }
        else
            Toast.makeText(getApplicationContext(), "Please fill the  empty fields", Toast.LENGTH_SHORT).show ();


    }

    public void hideThirdStage(View view){

        imageTV.setVisibility(View.INVISIBLE);
        imageV.setVisibility(View.INVISIBLE);
        browse_button.setVisibility(View.INVISIBLE);
        tagsTV.setVisibility(View.INVISIBLE);
        tagsET.setVisibility(View.INVISIBLE);
        done_button.setVisibility(View.INVISIBLE);

    }

    private void componentInit() {

        nameTV = (TextView) findViewById(R.id.nameTV);
        descriptionTV = (TextView) findViewById(R.id.descriptionTV);
        nameET = (EditText) findViewById(R.id.nameET);
        descriptionET = (EditText) findViewById(R.id.descriptionET);
        priceTV = (TextView) findViewById(R.id.priceTV);
        priceET = (EditText) findViewById(R.id.priceET);
        quantityTV = (TextView) findViewById(R.id.quantityTV);
        quantityET = (EditText) findViewById(R.id.quantityET);
        addressTV = (TextView) findViewById(R.id.addressTV);
        addressET = (EditText) findViewById(R.id.addressET);
        placeTV = (TextView) findViewById(R.id.placeTV);
        placeET = (EditText) findViewById(R.id.placeET);
        xTV = (TextView) findViewById(R.id.xTV);
        xET = (EditText) findViewById(R.id.xET);
        yTV = (TextView) findViewById(R.id.yTV);
        yET = (EditText) findViewById(R.id.yET);
        dateTV = (TextView) findViewById(R.id.dateTV);
        dateET = (EditText) findViewById(R.id.dateET);
        imageTV = (TextView) findViewById(R.id.imageTV);
        imageV = (ImageView) findViewById(R.id.create_imageV);
        browse_button = (Button) findViewById(R.id.browse_button);
        tagsTV = (TextView) findViewById(R.id.tagsTV);
        tagsET = (EditText) findViewById(R.id.tagsET);
        done_button = (Button) findViewById(R.id.done_button);

    }

}
