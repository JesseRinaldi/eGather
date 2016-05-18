package com.rinaldi.jesse.egather;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private GoogleApiClient gAPI;
    private ListView lstEvents, lstMenuOptions;
    private TextView txtMenuItem;
    private ArrayList<Event> events = new ArrayList<Event>();
    private DataSnapshot currentSnapshot;
    private AndroidApplication application;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        application = (AndroidApplication) getApplicationContext();
        gAPI = application.gAPI;

        lstEvents = (ListView) findViewById(R.id.lstEvents);

        if (application.user == null) {
            Intent intent = new Intent(this, signin.class);
            startActivity(intent);
        }

        setTitle("Events");
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, R.string.drawerOpen, R.string.drawerClose) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();
            }
        };

        drawerLayout.addDrawerListener(actionBarDrawerToggle);

        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(this, R.array.MenuOptions, R.layout.menu_list_item);
        lstMenuOptions = (ListView) findViewById(R.id.lstMenuOptions);
        lstMenuOptions.setAdapter(arrayAdapter);

        menuListClickListener();
        eventListClickListener();

        viewBrowseEvents();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) return true;
        int id = item.getItemId();

        switch(id) {
            case R.id.action_settings:
                return true;
            case R.id.action_create_event:
                Intent i = new Intent(getBaseContext(), EventCreator.class);
                startActivity(i);
                viewMyEvents();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // This method should always be called by your Activity's
        // onConfigurationChanged method.
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    private void menuListClickListener(){
        lstMenuOptions.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> paret, View viewClicked, int position, long id) {
                if (txtMenuItem != null) {
                    txtMenuItem.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                }
                txtMenuItem = (TextView) viewClicked;
                txtMenuItem.setBackgroundColor(getResources().getColor(R.color.colorPrimaryLight));
                switch (txtMenuItem.getText().toString()) {
                    case "My Events":
                        viewMyEvents();
                        setTitle("My Events");
                        drawerLayout.closeDrawers();
                        break;
                    case "Attending Events":
                        //viewAttendingEvents();
                        //
                        drawerLayout.closeDrawers();
                        break;
                    case "Browse Events":
                        viewBrowseEvents();
                        setTitle("Select a Category");
                        drawerLayout.closeDrawers();
                        break;
                    case "Logout":
                        break;
                    default:
                        Log.e("Menu", "Invalid Option Selected");
                }
            }
        });
    }

    private void viewMyEvents() {
        populateEventList(application.mFirebaseRef.child("events").orderByChild("mod").equalTo(application.user.getId().toString()));
    }

    private void viewBrowseEvents() {
        lstEvents.setAdapter(ArrayAdapter.createFromResource(this, R.array.categories, android.R.layout.simple_list_item_1));
        categoryListClickListener();
    }

    private void viewEventCategory(String category) {
        populateEventList(application.mFirebaseRef.child("events").orderByChild("category").equalTo(category));
        setTitle(category);
    }

    private void eventListClickListener(){
        lstEvents.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> paret, View viewClicked, int position, long id) {
                final TextView textView = (TextView) viewClicked.findViewById(R.id.txtNameList);
                Query qRef = application.mFirebaseRef.child("events").orderByChild("name").equalTo(textView.getText().toString());
                qRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot dsEvent : dataSnapshot.getChildren()) {
                            Event event = dsEvent.getValue(Event.class);
                            Log.d("EVENT", event.getLocationAddress());
                            application.activeEvent = event;
                            Intent intent = new Intent(MainActivity.this, EventView.class);
                            startActivity(intent);

                        } //else onCancelled(null);
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                        Log.e("FIREBASE ERROR", "Failed to open '" + textView.getText().toString() + "'");
                    }
                });
            }
        });
    }

    private void categoryListClickListener(){
        lstEvents.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> paret, View viewClicked, int position, long id) {
                TextView txtCategory = (TextView) viewClicked;
                viewEventCategory(txtCategory.getText().toString());
            }
        });
    }


    private void populateEventList(Query qRef){
        qRef.addValueEventListener(valueEventListener);
    }

    private ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            currentSnapshot = dataSnapshot;
            events.clear();
            for (DataSnapshot dsEvent : dataSnapshot.getChildren()) {
                events.add(dsEvent.getValue(Event.class));
            }
            EventAdapter eventAdapter = new EventAdapter(MainActivity.this, events.toArray(new Event[events.size()]));
            lstEvents.setAdapter(eventAdapter);
            eventListClickListener();
        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {

        }
    };
}
