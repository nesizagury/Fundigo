package com.example.FundigoApp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.FundigoApp.Events.EventInfo;
import com.example.FundigoApp.Events.EventPageActivity;
import com.example.FundigoApp.Events.EventsListAdapter;
import com.example.FundigoApp.StaticMethod.EventDataMethods;
import com.example.FundigoApp.StaticMethod.GeneralStaticMethods;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements SearchView.OnClickListener, AdapterView.OnItemClickListener {
    AutoCompleteTextView autoCompleteTextView;

    SearchView search;
    ListView listView;
    EventsListAdapter listAdpter;
    Button b_history, b_clear;
    String wordSearch;
    ArrayList<String> history = new ArrayList<> ();
    ArrayAdapter<String> autoStrings;
    PopupMenu historyPop;
    List<EventInfo> eventsResultList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_search);
        readHistory ();

        search = (SearchView) findViewById (R.id.b_search);
        b_history = (Button) findViewById (R.id.b_history);
        b_clear = (Button) findViewById (R.id.b_clear);
        listView = (ListView) findViewById (R.id.listView_Search);

        b_clear.setOnClickListener (this);
        search.setOnClickListener (this);

        String[] auto = getResources ().getStringArray (R.array.autoComplateStrings);
        autoStrings = new ArrayAdapter<String> (this, android.R.layout.simple_list_item_1, auto);
        eventsResultList = new ArrayList<EventInfo> ();
        listAdpter = new EventsListAdapter (this, eventsResultList, false);
        listView.setAdapter (listAdpter);
        listView.setSelector (new ColorDrawable (Color.TRANSPARENT));
        listView.setOnItemClickListener (this);

        //Creating the instance of PopupMenu
        historyPop = new PopupMenu (SearchActivity.this, b_history);
        //Inflating the Popup using xml file
        historyPop.getMenuInflater ().inflate (R.menu.popup_city, historyPop.getMenu ());
        b_history = (Button) findViewById (R.id.b_history);

        b_history.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                if (history.size () > 0) {
                    //Creating the instance of PopupMenu
                    historyPop = new PopupMenu (SearchActivity.this, b_history);
                    //Inflating the Popup using xml file
                    historyPop.getMenuInflater ().inflate (R.menu.popup_history, historyPop.getMenu ());
                    //registering popup with OnMenuItemClickListener
                    for (int i = 0; i < history.size (); i++) {
                        historyPop.getMenu ().add (history.get (i));
                    }
                    historyPop.setOnMenuItemClickListener (new PopupMenu.OnMenuItemClickListener () {
                        public boolean onMenuItemClick(MenuItem item) {
                            autoCompleteTextView.setText (item.getTitle ());
                            return true;
                        }
                    });

                    historyPop.show ();//showing popup menu
                } else {
                    Toast.makeText (SearchActivity.this, R.string.not_have_history, Toast.LENGTH_SHORT).show ();
                }
            }
        });
        search.setOnQueryTextListener (new SearchView.OnQueryTextListener () {
            @Override
            public boolean onQueryTextSubmit(String query) {
                wordSearch = search.getQuery ().toString ();
                if (wordSearch.length () == 0) {
                    Toast.makeText (SearchActivity.this, R.string.input_word_to_search, Toast.LENGTH_SHORT).show ();
                } else {
                    history.add (0, wordSearch);
                    saveHistory ();
                    eventsResultList.clear ();
                    eventsResultList.addAll (searchInfo (wordSearch));
                    listAdpter.notifyDataSetChanged ();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                wordSearch = newText;
                eventsResultList.clear ();
                eventsResultList.addAll (searchInfo (wordSearch));
                listAdpter.notifyDataSetChanged ();
                return false;
            }
        });

    }

    @Override
    public void onClick(View view) {
        if (view.getId () == search.getId ()) {
            wordSearch = search.getQuery ().toString ();
            if (wordSearch.length () > 0) {
                history.add (0, wordSearch);
                saveHistory ();
            }
        }
        if (view.getId () == b_clear.getId ()) {
            clearHistory ();
        }
    }

    private ArrayList<EventInfo> searchInfo(String search) {
        boolean flag = true;
        ArrayList<EventInfo> ans = new ArrayList<> ();
        ArrayList<String> checkIfInside = new ArrayList<> ();
        for (int i = 0; i < GlobalVariables.ALL_EVENTS_DATA.size (); i++) {
            if (!checkIfInside.contains (GlobalVariables.ALL_EVENTS_DATA.get (i).getParseObjectId ())) {
                if (GlobalVariables.ALL_EVENTS_DATA.get (i).getInfo ().toLowerCase ().contains (search.toLowerCase ())) {
                    ans.add (GlobalVariables.ALL_EVENTS_DATA.get (i));
                    checkIfInside.add (GlobalVariables.ALL_EVENTS_DATA.get (i).getParseObjectId ());
                    flag = false;
                }

                if (flag && GlobalVariables.ALL_EVENTS_DATA.get (i).getName ().toLowerCase ().contains (search.toLowerCase ())) {
                    ans.add (GlobalVariables.ALL_EVENTS_DATA.get (i));
                    checkIfInside.add (GlobalVariables.ALL_EVENTS_DATA.get (i).getParseObjectId ());
                    flag = false;
                }

                if (flag && GlobalVariables.ALL_EVENTS_DATA.get (i).getFilterName ().toLowerCase ().contains (search.toLowerCase ())) {
                    ans.add (GlobalVariables.ALL_EVENTS_DATA.get (i));
                    checkIfInside.add (GlobalVariables.ALL_EVENTS_DATA.get (i).getParseObjectId ());
                    flag = false;
                }

                if (flag && GlobalVariables.ALL_EVENTS_DATA.get (i).getTags ().toLowerCase ().contains (search.toLowerCase ())) {
                    ans.add (GlobalVariables.ALL_EVENTS_DATA.get (i));
                    checkIfInside.add (GlobalVariables.ALL_EVENTS_DATA.get (i).getParseObjectId ());
                }
                flag = true;
            }
        }
        return ans;
    }

    private void clearHistory() {
        history.clear ();
        File inputFile = new File ("history");
        File tempFile = new File ("myTempFile");
        tempFile.renameTo (inputFile);
    }

    private void readHistory() {
        FileInputStream inputStream;
        String readLine = "";
        int index = 0;
        try {
            inputStream = openFileInput ("history.txt");
            InputStreamReader inputStreamReader = new InputStreamReader (inputStream);
            BufferedReader bufferedReader = new BufferedReader (inputStreamReader);
            while ((readLine = bufferedReader.readLine ()) != null) {
                history.add (index++, readLine);
            }
            inputStream.close ();
        } catch (Exception e) {
            e.printStackTrace ();
        }
    }

    private void saveHistory() {
        FileOutputStream outputStream;
        try {
            outputStream = openFileOutput ("history.txt", Context.MODE_PRIVATE);
            for (int i = 0; i < history.size (); i++) {
                outputStream.write (history.get (i).getBytes ());
            }
            outputStream.close ();
        } catch (Exception e) {
            e.printStackTrace ();
        }
    }

    @Override
    public void onBackPressed() {
        if (history.size () > 0) {
            saveHistory ();
        }
        finish();
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        GeneralStaticMethods.onActivityResult (requestCode,
                                                      data,
                                                      this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Bundle b = new Bundle ();
        Intent intent = new Intent (this, EventPageActivity.class);
        EventDataMethods.onEventItemClick (position, eventsResultList, intent);
        intent.putExtras (b);
        startActivity (intent);
    }
}
