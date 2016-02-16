package com.example.events;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;


public class GetQRCode extends AppCompatActivity {
    private String googleUrl="https://chart.googleapis.com/chart?chs=150x150&cht=qr&chl=";
    private Button qr_down_butt;
    private ImageView qr_image;
    private String phoneNumber;
    private String eventName="";
    private EditText enterPhone;
    private String fileName;
    private TextView congrad;
    private static int codeNum=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_rqcode);
        qr_down_butt= (Button) findViewById(R.id.qr_down_butt);
        qr_image    = (ImageView) findViewById(R.id.qr_image);
        enterPhone=(EditText)findViewById(R.id.edit_text);
        enterPhone.setInputType(InputType.TYPE_CLASS_PHONE);
        enterPhone.setBackgroundColor(Color.GRAY);
        qr_down_butt.setBackgroundColor(Color.GRAY);
        congrad=(TextView)findViewById(R.id.textView9);
        congrad.setText("Congratulations, Enjoy!!!");
        congrad.setVisibility(View.GONE);

        final Intent myIntent=getIntent();

        eventName=myIntent.getStringExtra("eventName");
        Log.d("mmmm1",eventName);
        eventName=eventName.replace(" ", "");

        fileName="qr"+eventName+"_"+codeNum;

        qr_down_butt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!enterPhone.getText().toString().isEmpty()) {
                    DownlandTask downlandTask = new DownlandTask();
                    downlandTask.execute(googleUrl + "972" + enterPhone.getText().toString() + eventName+""+System.currentTimeMillis());
                    Log.d("mmmm1", googleUrl + "972" + enterPhone.getText().toString() + eventName + System.currentTimeMillis());
                } else {
                    Toast.makeText(getApplicationContext(), "Enter Phone Number", Toast.LENGTH_LONG).show();
                }
            }
        });

        enterPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterPhone.beginBatchEdit();
            }
        });

    }

    private class DownlandTask extends AsyncTask<String,Integer,String>{
        ProgressDialog progressDialog;
        File myFile;
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            progressDialog=new ProgressDialog(GetQRCode.this);
            progressDialog.setTitle("Downland In Progress...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setMax(100);
            progressDialog.setProgress(0);
            progressDialog.show();

        }



        @Override
        protected String doInBackground(String... params) {
            Log.d("mmmm1", "doInBackground");
            String path=params[0];
            int fileLength=0;
            try {
                URL url=new URL(path);
                URLConnection urlConnection= url.openConnection();
                urlConnection.connect();
                fileLength=urlConnection.getContentLength();
                File newFolder=new File("sdcard/fundigo_qr_code");
                if(!newFolder.exists()){
                    newFolder.mkdir();
                    Log.d("mmmm1", "mkdir");
                }

                File input_file=new File(newFolder,fileName+".jpg");
                InputStream inputStream=new BufferedInputStream(url.openStream(),8192);
                byte[] data=new byte[1024];
                int total=0;
                int count=0;
                OutputStream outputStream=new FileOutputStream(input_file);
                while ((count=inputStream.read(data))!=-1){
                    total+=count;
                    outputStream.write(data,0,count);
                    int progress=(int)(total*100/fileLength);
                    publishProgress(progress);
                }
                Log.d("mmmm1","while");
                inputStream.close();
                outputStream.close();
                codeNum++;

                myFile=input_file;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return "Downlaod Complete...";
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progressDialog.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            progressDialog.hide();
            congrad.setVisibility(View.VISIBLE);
            Toast.makeText(getApplicationContext(),result,Toast.LENGTH_LONG).show();
            String parh="sdcard/fundigo_qr_code/"+fileName+".jpg";
            qr_image.setImageDrawable(Drawable.createFromPath(parh));
            Intent myIntent=getIntent();
            String seatNumber=myIntent.getStringExtra("seatNumber");
            String choose=myIntent.getStringExtra("isChoose");
            String seatKey=myIntent.getStringExtra("seatKey");
            if(choose!=null) {
                ParseQuery<ParseObject> query = ParseQuery.getQuery("EventsSeats");

                    // Retrieve the object by id
                query.getInBackground(seatKey, new GetCallback<ParseObject>() {
                    public void done(ParseObject updateData, ParseException e) {
                        if (e == null) {
                            // Now let's update it with some new data. In this case, only cheatMode and score
                            // will get sent to the Parse Cloud. playerName hasn't changed.
                            Date myDate = new Date();
                            ParseFile file = new ParseFile(myFile);
                            try {
                                file.save();
                                updateData.put("QR_Code", file);
                            } catch (ParseException e1) {
                                e1.printStackTrace();
                            }
                            updateData.put("buyer_phone", Integer.parseInt(enterPhone.getText().toString()));
                            updateData.put("purchase_date",myDate);

                            updateData.saveInBackground();
                        }
                    }
                });


            }else{
                ParseQuery<ParseObject> query = ParseQuery.getQuery("EventsSeats");

                // Retrieve the object by id
                try {
                    query.getInBackground(query.getFirst().getObjectId(), new GetCallback<ParseObject>() {
                        public void done(ParseObject updateData, ParseException e) {
                            if (e == null) {
                                // Now let's update it with some new data. In this case, only cheatMode and score
                                // will get sent to the Parse Cloud. playerName hasn't changed.
                                Date myDate = new Date();
                                ParseFile file = new ParseFile(myFile);
                                try {
                                    file.save();
                                    updateData.put("QR_Code",file);
                                } catch (ParseException e1) {
                                    e1.printStackTrace();
                                }
                                updateData.put("buyer_phone", Integer.parseInt(enterPhone.getText().toString()));
                                updateData.put("purchase_date",myDate);

                                updateData.saveInBackground();
                            }
                        }
                    });
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }


        }
    }

}
