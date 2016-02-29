package com.example.FundigoApp.Customer.CustomerMenu;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.devmarvel.creditcardentry.library.CardValidCallback;
import com.devmarvel.creditcardentry.library.CreditCard;
import com.devmarvel.creditcardentry.library.CreditCardForm;
import com.example.FundigoApp.Events.Event;
import com.example.FundigoApp.GlobalVariables;
import com.example.FundigoApp.R;
import com.example.FundigoApp.Verifications.VerifyCard;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.stripe.android.*;
import com.stripe.android.Stripe.*;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.exception.StripeException;

import org.apache.http.impl.client.HttpClients;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.StringTokenizer;

import javax.net.ssl.HttpsURLConnection;

public class SaveCreditCard extends AppCompatActivity implements View.OnClickListener
{

    EditText cardNumber,year,moth,cvc;
    Button applay,delete;
    private CreditCardForm noZipForm;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_credit_card);
      /*  cardNumber=(EditText)findViewById(R.id.saveCrditCardCardNumber);
        year=(EditText)findViewById(R.id.saveCrditCardYear);
        moth=(EditText)findViewById(R.id.saveCrditCardMoth);
        cvc=(EditText)findViewById(R.id.saveCrditCardCVC);*/
        applay=(Button)findViewById(R.id.saveCrditCard_buttonApplay);
        delete=(Button)findViewById(R.id.saveCrditCard_buttonDelete);
        applay.setVisibility(View.INVISIBLE);
        delete.setVisibility(View.INVISIBLE);
        //readFromFile();
        noZipForm = (CreditCardForm) findViewById (R.id.form_no_zip_SaveCard);
        noZipForm.setOnCardValidCallback (cardValidCallback);

    }


    CardValidCallback cardValidCallback = new CardValidCallback () {
        @Override
        public void cardValid(CreditCard card) {
            applay.setVisibility(View.VISIBLE);

            Toast.makeText (SaveCreditCard.this, "Card valid and complete", Toast.LENGTH_SHORT).show();
            //finish ();
        }
    };



    @Override
    public void onClick(View v)
    {
        Boolean flag=true;
        if(v.getId()==delete.getId())
        {
            File file=new File(SaveCreditCard.this.getFilesDir().getAbsolutePath(),"card.txt");
            if(file.exists())
            {
                if (file.delete())
                {
                    Toast.makeText(this, "Delete succsse", Toast.LENGTH_SHORT).show();
         /*           cardNumber.setText("");
                    moth.setText("");
                    year.setText("");
                    cvc.setText("");
         */       }
                else
                {
                    Toast.makeText(this, "Delete not succsse", Toast.LENGTH_SHORT).show();
                }
            }
            else
            {
                Toast.makeText(this, "file not exsits", Toast.LENGTH_SHORT).show();
            }
        }
        if(v.getId()==applay.getId())
        {
            Card card = new Card(noZipForm.getCreditCard().getCardNumber(),noZipForm.getCreditCard().getExpMonth(),noZipForm.getCreditCard().getExpYear(),noZipForm.getCreditCard().getSecurityCode());
            if (!card.validateCard())
            {
                Toast.makeText(this, "unValidateCard", Toast.LENGTH_SHORT).show();
                flag = false;
            }
            else if (!card.validateCVC())
            {
                Toast.makeText(this, "unValidateCVC", Toast.LENGTH_SHORT).show();
                flag = false;
            }
            else if (!card.validateExpMonth())
            {
                Toast.makeText(this, "unValidateMoth", Toast.LENGTH_SHORT).show();
                flag = false;
            }
            else if (!card.validateExpYear())
            {
                Toast.makeText(this, "unValidateYear", Toast.LENGTH_SHORT).show();
                flag = false;
            }
            else if(!card.validateExpiryDate())
            {
                Toast.makeText(this, "unValidateDate", Toast.LENGTH_SHORT).show();
                flag = false;
            }
            if (flag)
            {
                try
                {
                    final Stripe stripe = new Stripe("pk_test_YyMy1mvHItPsHftS4iKcoO3O");
                    stripe.createToken(card, new TokenCallback() {
                                public void onSuccess(Token token)
                                {
                                    token.getCard().setNumber(noZipForm.getCreditCard().getCardNumber());
                                    // Send token to your server
                                    com.example.FundigoApp.Customer.CustomerMenu.CreditCard parseObject =new com.example.FundigoApp.Customer.CustomerMenu.CreditCard();

                                    /*URL url;
                                    try
                                    {
                                        // Add your data
                                        url = new URL("https://A0F3SEBpsYzx5v68Q9W2C5qWz6A9X7fQVPVyq2j7:javascript-key=9bqA64wQfgiHLpxkyaPKFbHw4SLg3tZIgeYdG69t@api.parse.com/1/functions/");

                                        // Execute HTTP Post Request
                                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                                        conn.setReadTimeout(15000);
                                        conn.setConnectTimeout(15000);
                                        conn.setRequestMethod("Get");
                                        conn.setDoInput(true);
                                        conn.setDoOutput(true);

                                    OutputStream os = conn.getOutputStream();
                                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                                    writer.write(getPostDataString(postDataParams));

                                    writer.flush();
                                    writer.close();
                                    os.close();
                                    int responseCode=conn.getResponseCode();

                                    if (responseCode == HttpsURLConnection.HTTP_OK) {
                                        String line;
                                        BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                                        while ((line=br.readLine()) != null) {
                                            response+=line;
                                        }
                                    }
                                    else {
                                        response="";

                                    }
                                } catch (Exception e) {
                            e.printStackTrace();
                        }*/

                        try
                                    {

                                        if(GlobalVariables.CUSTOMER_PHONE_NUM==null)parseObject.put("IdCostumer","0");
                                        else parseObject.put("IdCostumer", GlobalVariables.CUSTOMER_PHONE_NUM);
                                        parseObject.put("number", token.getCard().getNumber());
                                        parseObject.put("month",00);
                                        parseObject.put("year", 00);
                                        parseObject.put("cvc", "XXX");
                                        parseObject.save();
                                    }
                                    catch (ParseException e)
                                    {
                                        e.printStackTrace();
                                    }
                                   /* cardNumber.setText("XXXX-XXXX-XXXX-" + token.getCard().getLast4());
                                    cvc.setText("XXX");
                                    moth.setText("XX");
                                    year.setText("XXXX");*/
                                    //Toast.makeText(SaveCreditCard.this, token.toString(), Toast.LENGTH_LONG).show();
                                    Toast.makeText(SaveCreditCard.this, "Success card save!", Toast.LENGTH_LONG).show();
                                    String s = noZipForm.getCreditCard().getCardNumber()+"$"+noZipForm.getCreditCard().getExpMonth()+"$"+noZipForm.getCreditCard().getExpYear()+"$"+noZipForm.getCreditCard().getSecurityCode();
                                    saveToFIle(s);
                                    finish();
                                }

                                public void onError(Exception error)
                                {
                                    // Show localized error message
                                    Toast.makeText(SaveCreditCard.this, error.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                    );
                }
                catch (StripeException e)
                {
                    e.getMessage();
                }
            }
        }
        /*else
        {
            if(cardNumber.getText().toString().length()!=0)
            {
                if(year.getText().toString().length()!=0)
                {
                    if(moth.getText().toString().length()!=0)
                    {
                        if(cvc.getText().toString().length()!=0)
                        {
                            if (checkIsNumber(cardNumber.getText().toString(), moth.getText().toString(), year.getText().toString(), cvc.getText().toString()))
                            {
                                Card card = new Card(cardNumber.getText().toString(), Integer.parseInt(moth.getText().toString()), Integer.parseInt(year.getText().toString()), cvc.getText().toString());
                                if (!card.validateCard())
                                {
                                    Toast.makeText(this, "unValidateCard", Toast.LENGTH_SHORT).show();
                                    flag = false;
                                }
                                else if (!card.validateCVC())
                                {
                                    Toast.makeText(this, "unValidateCVC", Toast.LENGTH_SHORT).show();
                                    flag = false;
                                }
                                else if (!card.validateExpMonth())
                                {
                                    Toast.makeText(this, "unValidateMoth", Toast.LENGTH_SHORT).show();
                                    flag = false;
                                }
                                else if (!card.validateExpYear())
                                {
                                    Toast.makeText(this, "unValidateYear", Toast.LENGTH_SHORT).show();
                                    flag = false;
                                }
                                else if(!card.validateExpiryDate())
                                {
                                    Toast.makeText(this, "unValidateDate", Toast.LENGTH_SHORT).show();
                                    flag = false;
                                }
                                if (flag)
                                {
                                    try
                                    {
                                        final Stripe stripe = new Stripe("pk_test_YyMy1mvHItPsHftS4iKcoO3O");
                                        stripe.createToken(card, new TokenCallback() {
                                                    public void onSuccess(Token token)
                                                    {
                                                        token.getCard().setNumber(cardNumber.getText().toString());
                                                        // Send token to your server

                                                    *//**//*ParseObject parseObject =new ParseObject("creditCards");
                                                    try
                                                    {
                                                        parseObject.put("Token", token);
                                                        parseObject.save();
                                                    }
                                                    catch (ParseException e)
                                                    {
                                                        e.printStackTrace();
                                                    }*//**//*
                                                        cardNumber.setText("XXXX-XXXX-XXXX-" + token.getCard().getLast4());
                                                        cvc.setText("XXX");
                                                        moth.setText("XX");
                                                        year.setText("XXXX");
                                                        Toast.makeText(SaveCreditCard.this, token.toString(), Toast.LENGTH_LONG).show();
                                                        Toast.makeText(SaveCreditCard.this, "Success", Toast.LENGTH_LONG).show();
                                                        String s = cardNumber.getText().toString() + "$" + moth.getText().toString() + "$" + year.getText().toString() + "$" + cvc.getText().toString();
                                                        saveToFIle(s);
                                                    }

                                                    public void onError(Exception error)
                                                    {
                                                        // Show localized error message
                                                        Toast.makeText(SaveCreditCard.this, error.getMessage(), Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                        );
                                    }
                                    catch (StripeException e)
                                    {
                                        e.getMessage();
                                    }
                                }
                            }
                        }
                        else
                        {
                            Toast.makeText(this,"Enter CVC",Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        Toast.makeText(this,"Enter MotheExp",Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(this,"Enter YearExp",Toast.LENGTH_SHORT).show();
                }
            }
            else
            {
                Toast.makeText(this,"Enter Card Number",Toast.LENGTH_SHORT).show();
            }
        }*/
    }

    private void saveToFIle(String s)
    {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(this.openFileOutput("card.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(s);
            outputStreamWriter.close();
        }
        catch (IOException e) {
             e.printStackTrace();
        }
    }

    private void readFromFile()
    {
        String ret = "";
        try
        {
            InputStream inputStream = openFileInput("card.txt");
            if ( inputStream != null )
            {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null )
                {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
                StringTokenizer tokenizer=new StringTokenizer(ret,"$");
                String []arr=new String[4];
                int i=0;
                while(tokenizer.hasMoreElements())
                {
                    arr[i++]=tokenizer.nextToken();
                }
                cardNumber.setText(arr[0]);
                moth.setText(arr[1]);
                year.setText(arr[2]);
                cvc.setText(arr[3]);
            }
        }
        catch (FileNotFoundException e)
        {
            Log.e("login activity", "File not found: " + e.toString());
        }
        catch (IOException e)
        {
            Log.e("login activity", "Can not read file: " + e.toString());
        }
    }

    private Boolean checkIsNumber(String s1,String s2,String s3,String s4)
    {
        try{
            Double.parseDouble(s1);
        }
        catch (NumberFormatException e)
        {
            Toast.makeText(this,"CardNumber is not number:",Toast.LENGTH_SHORT).show();
            return false;
        }

        try{
            Integer.parseInt(s2);
        }
        catch (NumberFormatException e)
        {
            Toast.makeText(this,"MothExp is not number:",Toast.LENGTH_SHORT).show();
            return false;
        }
        try{
            Integer.parseInt(s3);
        }
        catch (NumberFormatException e)
        {
            Toast.makeText(this,"yearExp is not number:",Toast.LENGTH_SHORT).show();
            return false;
        }
        try{
            Integer.parseInt(s4);
        }
        catch (NumberFormatException e)
        {
            Toast.makeText(this,"CVC is not number:",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
