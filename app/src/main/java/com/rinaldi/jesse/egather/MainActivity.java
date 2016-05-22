package com.rinaldi.jesse.egather;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity {
    private GoogleApiClient gAPI;
    private ListView lstEvents, lstMenuOptions;
    private TextView txtMenuItem, txtNoEvents;
    private Spinner spCategoryFilter, spRadiusFilter;
    private LinearLayout filterLayout;
    private ArrayList<Event> events = new ArrayList<Event>();
    private DataSnapshot currentSnapshot;
    private AndroidApplication application;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private boolean showAddButton = false;
    private double searchRadius = 50; //-1 to ignore search radius
    private boolean showInviteOnly = false; //true to show events which are invite only (My Events, Attending, Invites)
    private enum listStates {MY_EVENTS, BROWSE_EVENTS, ATTENDING_EVENTS, INVITES};
    private listStates currentView = listStates.BROWSE_EVENTS;
    private String currentCategory = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        application = (AndroidApplication) getApplicationContext();
        gAPI = application.gAPI;

        lstEvents = (ListView) findViewById(R.id.lstEvents);
        txtNoEvents = (TextView) findViewById(R.id.txtNoEvents);

        if (application.user == null) {
            Intent intent = new Intent(this, signin.class);
            startActivity(intent);
        }

        setUpMenuBar();
        eventListClickListener();

        spCategoryFilter = (Spinner) findViewById(R.id.spCategoryFilter);
        ArrayList<CharSequence> catList = new ArrayList<CharSequence>();
        Collections.addAll(catList, getResources().getStringArray(R.array.categories));
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(MainActivity.this,
                android.R.layout.simple_spinner_item, catList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter.insert("All", 0);
        spCategoryFilter.setAdapter(adapter);
        spCategoryFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) { invalidateView(); }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });

        spRadiusFilter = (Spinner) findViewById(R.id.spRadiusFilter);
        adapter = ArrayAdapter.createFromResource(this,
                R.array.radiusValues, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRadiusFilter.setAdapter(adapter);
        spRadiusFilter.setSelection(adapter.getPosition("50 Miles"));
        spRadiusFilter.setOnItemSelectedListener(spCategoryFilter.getOnItemSelectedListener());

        filterLayout = (LinearLayout) findViewById(R.id.filterLayout);

        invalidateView();
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        invalidateView();
    }

    public void setUpMenuBar() {
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
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.getItem(0).setVisible(showAddButton);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) return true;
        if (item.getItemId() == R.id.action_create_event) {
            Intent i = new Intent(getBaseContext(), EventCreator.class);
            startActivity(i);
            currentView = listStates.MY_EVENTS;
            invalidateView();
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
                        currentView = listStates.MY_EVENTS;
                        invalidateView();
                        break;
                    case "Attending Events":
                        currentView = listStates.ATTENDING_EVENTS;
                        invalidateView();
                        break;
                    case "Browse Events":
                        currentView = listStates.BROWSE_EVENTS;
                        invalidateView();
                        break;
                    case "Logout":
                        break;
                    default:
                        if (txtMenuItem.getText().toString().startsWith("Invites")) {
                            currentView = listStates.INVITES;
                            invalidateView();
                            break;
                        }
                        Log.e("Menu", "Invalid Option Selected");
                }
                drawerLayout.closeDrawers();
            }
        });
    }

    private void invalidateView() {
        switch(currentView) {
            case MY_EVENTS:
                showInviteOnly = true;
                currentCategory = "";
                searchRadius = -1;
                populateEventList(application.mFirebaseRef.child("events").orderByChild("mod").equalTo(application.user.getId().toString()));
                setTitle("My Events");
                showAddButton = true;
                filterLayout.setVisibility(View.GONE);
                break;
            case BROWSE_EVENTS:
                setTitle("Browse Events");
                showAddButton = false;
                filterLayout.setVisibility(View.VISIBLE);
                currentCategory = (spCategoryFilter.getSelectedItem().toString().equals("All") ? "" : spCategoryFilter.getSelectedItem().toString());
                searchRadius = (spRadiusFilter.getSelectedItem().toString().equals("------") ? -1 : Double.parseDouble(spRadiusFilter.getSelectedItem().toString().split(" ")[0]));
                populateEventList(application.mFirebaseRef.child("events"));
                break;
            case ATTENDING_EVENTS:
                showInviteOnly = true;
                currentCategory = "";
                searchRadius = -1;
                setTitle("Attending Events");
                showAddButton = false;
                filterLayout.setVisibility(View.GONE);
                viewAttendingEvents();
                break;
            case INVITES:
                break;
            default:
                Log.e("INVALID VIEW", "State of 'currentView' not recognized");
                break;
        }
        invalidateOptionsMenu();
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
                            application.activeEventID = dsEvent.getKey();
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
            setEventList();
        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {

        }
    };

    private void viewAttendingEvents() {
        events.clear();
        final ArrayList<String> eventIDs = new ArrayList<String>();
        final Calendar now = Calendar.getInstance();
        final double currentDateTimeSort = (double)now.get(Calendar.YEAR)*100000000 + (double)(now.get(Calendar.MONTH)+1)*1000000 + (double)now.get(Calendar.DAY_OF_MONTH)*10000 - 1;
        application.mFirebaseRef.child("attendance").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(application.user.getId())) {
                    for (DataSnapshot dsEvent : dataSnapshot.child(application.user.getId()).getChildren()) {
                        eventIDs.add(dsEvent.getKey());
                    }
                    application.mFirebaseRef.child("events").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (String eventID : eventIDs) {
                                if (dataSnapshot.hasChild(eventID)) {
                                    events.add(dataSnapshot.child(eventID).getValue(Event.class));
                                }
                            }
                            setEventList();
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) { setEventList(); }
                    });
                }
                else {
                    setEventList();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {}
        });
    }



    private void setEventList() {
        Collections.sort(events, new Comparator<Event>(){
            public int compare(Event e1, Event e2) {
                return Double.compare(e1.getDateTimeSort(), e2.getDateTimeSort());
            }
        });

        Calendar now = Calendar.getInstance();
        double currentDateTimeSort = (double)now.get(Calendar.YEAR)*100000000 + (double)(now.get(Calendar.MONTH)+1)*1000000 + (double)now.get(Calendar.DAY_OF_MONTH)*10000 - 1;
        for (int i = events.size() - 1; i >= 0; i--) {
            Event e = events.get(i);
            if (currentView != listStates.MY_EVENTS && Double.compare(e.getDateTimeSort(), currentDateTimeSort) < 0) {
                Log.d("REMOVE OUTDATED", e.getName());
                events.remove(i);
                continue;
            }
            if (!showInviteOnly && e.getInviteOnly()) {
                Log.d("REMOVE INVITE ONLY", e.getName());
                events.remove(i);
                continue;
            }
            if (!currentCategory.equals("") && !e.getCategory().equals(currentCategory)) {
                Log.d("REMOVE CATEGORY", e.getName());
                events.remove(i);
                continue;
            }
            if (searchRadius != -1) {
                String provider;
                LocationManager lm = (LocationManager) MainActivity.this.getSystemService(Context.LOCATION_SERVICE);
                Criteria criteria = new Criteria();
                provider = lm.getBestProvider(criteria, false);
                Location currentLocation = null;
                try {
                    currentLocation = lm.getLastKnownLocation(provider);
                } catch (SecurityException se) {
                    Log.e("SECURITY EXCEPTION", se.getMessage());
                }
                if (currentLocation != null) {
                    double distance = DistanceCalculator.distance(currentLocation.getLatitude(), currentLocation.getLongitude(), e.getLatitude(), e.getLongitude(), "M");
                    if (Double.compare(searchRadius, distance) < 0) {
                        Log.d("REMOVE OUTSIDE RADIUS", e.getName());
                        events.remove(i);
                        continue;
                    }
                }
            }
        }
        txtNoEvents.setVisibility(events.size() == 0 ? View.VISIBLE : View.GONE);
        EventAdapter eventAdapter = new EventAdapter(MainActivity.this, events.toArray(new Event[events.size()]));
        lstEvents.setAdapter(eventAdapter);
        eventListClickListener();
    }

}
