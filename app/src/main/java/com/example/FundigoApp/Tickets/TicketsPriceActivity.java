package com.example.FundigoApp.Tickets;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.FundigoApp.GlobalVariables;
import com.example.FundigoApp.R;

public class TicketsPriceActivity extends AppCompatActivity implements View.OnClickListener {

    Button btn_save;
    EditText et_yellow;
    EditText et_blue;
    EditText et_green;
    EditText et_pink;
    EditText et_orange;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tickets_price);

        btn_save = (Button) findViewById(R.id.btn_save_tickets_price);
        et_blue = (EditText) findViewById(R.id.et_blue);
        et_green = (EditText) findViewById(R.id.et_green);
        et_orange = (EditText) findViewById(R.id.et_orange);
        et_pink = (EditText) findViewById(R.id.et_pink);
        et_yellow = (EditText) findViewById(R.id.et_yellow);
        btn_save.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_save_tickets_price:
                String yellow = et_yellow.getText().toString();
                String pink = et_pink.getText().toString();
                String blue = et_blue.getText().toString();
                String green = et_green.getText().toString();
                String orange = et_orange.getText().toString();

                if (validateQuantity(yellow) && validateQuantity(pink) && validateQuantity(blue) && validateQuantity(green) && validateQuantity(orange)) {
                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putInt(GlobalVariables.YELLOW, Integer.parseInt(yellow));
                    editor.putInt(GlobalVariables.PINK, Integer.parseInt(pink));
                    editor.putInt(GlobalVariables.BLUE, Integer.parseInt(blue));
                    editor.putInt(GlobalVariables.GREEN, Integer.parseInt(green));
                    editor.putInt(GlobalVariables.ORANGE, Integer.parseInt(orange));
                    editor.putBoolean(GlobalVariables.SEATS, true);
                    editor.apply();
                    finish();
                } else {
                    Toast.makeText(TicketsPriceActivity.this, "Please enter valid number", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }


    public boolean validateQuantity(String str) {
        if (str.equals("0")) {
            return false;
        }
        try {
            Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return false;
        } catch (NullPointerException e) {
            return false;
        }
        return true;
    }

}
