package com.example.events;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

public class FilterPage extends AppCompatActivity
{
    /** Variable Decleration */
    Integer[] Images = {
            R.drawable.ic_sport, R.drawable.ic_airplane,
            R.drawable.ic_beer, R.drawable.ic_buisness,
            R.drawable.ic_camera, R.drawable.ic_education,
            R.drawable.ic_gov, R.drawable.ic_home,
            R.drawable.ic_music
    };
    private final String[] Names = {
            "Sports", "Travel",
            "Drink", "Business",
            "Fashion", "Education",
            "Government" ,"Home & LifeStyle",
            "Music"
    };

    GridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_page);
        setTitle("FilterPage");

        /** GridView gridview = (GridView) findViewById(R.id.grdSports);
        gridview.setAdapter(new ImageAdapter(this, Names, Images)); */

        /** Create the Custom Grid View*/
        ImageAdapter adapter = new ImageAdapter(FilterPage.this, Names, Images);
        gridView = (GridView) findViewById(R.id.grdFilter);
        gridView.setAdapter(adapter);

        /**  On click Event when the custom grid is clickes*/
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id)
            {
                Toast.makeText(FilterPage.this, Names[+ position], Toast.LENGTH_SHORT).show();
            }
        });

    }
}