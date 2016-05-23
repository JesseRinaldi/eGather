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

/**
 * NAME
 *      MainActivity
 * DESCRIPTION
 *      The Main Activity. The first activity to be opened by the application.
 *      Inherits from AppCombatActivity (activity with menu bar).
 *      It revolves around switching the "view" state of the ListView lstEvents.
 * AUTHOR
 *      @author Jesse Rinaldi
 */
public class MainActivity extends AppCompatActivity {

    private ListView lstEvents, lstMenuOptions;
    private TextView txtMenuItem, txtNoEvents;
    private Spinner spCategoryFilter, spRadiusFilter;
    private LinearLayout filterLayout;
    private ArrayList<Event> events = new ArrayList<Event>();
    private AndroidApplication application;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private boolean showAddButton = false;
    private double searchRadius = 50; //-1 to ignore search radius
    private boolean showInviteOnly = false; //true to show events which are invite only (My Events, Attending, Invites)
    private enum listStates {MY_EVENTS, BROWSE_EVENTS, ATTENDING_EVENTS, INVITES};
    private listStates currentView = listStates.BROWSE_EVENTS;
    private String currentCategory = "";

    /**
     * NAME
     *      MainActivity.onCreate
     * SYNOPSIS
     *      @param savedInstanceState - Used by Android to restore Activity to previous state
     * DESCRIPTION
     *      Ran when MainActivity is created. Binds views from XML file and populates ListViews and Spinners.
     *      If no user is found, the signin activity is ran immediately
     * AUTHOR
     *      @author Jesse Rinaldi
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        application = (AndroidApplication) getApplicationContext();

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

    /**
     * NAME
     *      MainActivity.onResume
     * DESCRIPTION
     *      Ran when MainActivity resumes itself from the app being reopened or
     *      another activity finishing. Resets lstEvents to its current view.
     * AUTHOR
     *      Jesse Rinaldi
     */
    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        invalidateView();
    }

    /**
     * NAME
     *      MainActivity.setUpMenuBar
     * SYNOPSIS
     *      Sets up the sliding menu drawer and action bar
     * DESCRIPTION
     *      Binds the drawer layout, sets its toggle button in the action bar, and
     *      populates the listview in the drawer layout
     * AUTHOR
     *      @author Jesse Rinaldi
     */
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

    /**
     * NAME
     *       MainActivity.onCreateOptionsMenu
     * SYNOPSIS
     *      @param menu - The menu used by the action bar
     * DESCRIPTION
     *      Called by Android to inflate the Android menu with its options. Only option
     *      is the Add Event button which is only visible when in view MY_EVENTS
     *      Note - Can be called with invalidateOptionsMenu()
     * RETURNS
     *     @return boolean always true
     * AUTHOR
     *      @author Jesse Rinaldi
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.getItem(0).setVisible(showAddButton);
        return true;
    }

    /**
     * NAME
     *      MainActivity.onOptionsItemSelected
     * SYNOPSIS
     *      @param item - The item clicked from the Action Bar Menu
     * DESCRIPTION
     *      Handles the click events for the only 2 things in the Action Bar,
     *      the drawer layout toggle and create event button
     * RETURNS
     *      @return boolean true if valid item clicked
     * AUTHOR
     *      @author Jesse Rinaldi
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) return true;
        if (item.getItemId() == R.id.action_create_event) {
            Intent i = new Intent(getBaseContext(), EventCreator.class);
            startActivity(i);
            currentView = listStates.MY_EVENTS;
            invalidateView();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * NAME
     *      MainActivity.onPostCreate
     * SYNOPSIS
     *      @param savedInstanceState - Used by Android to restore Activity to previous state
     * DESCRIPTION
     *      Needed for drawer layout toggle button functionality
     * AUTHOR
     *      @author Jesse Rinaldi
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    /**
     * NAME
     *      MainActivity.onConfigurationChanged
     * SYNOPSIS
     *      @param newConfig
     * DESCRIPTION
     *      Needed for drawer layout toggle button functionality
     * AUTHOR
     *      @author Jesse Rinaldi
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    /**
     * NAME
     *      MainActivity.menuListClickListener
     * DESCRIPTION
     *      Sets the on item click listener for lstMenuOptions in the sliding menu drawer
     *      Each item sets the currentView enum and repops the listview of events
     * AUTHOR
     *      @author Jesse Rinaldi
     */
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
                        txtMenuItem.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                        Intent intent = new Intent(MainActivity.this, signin.class);
                        intent.putExtra("PERFORM_LOGOUT", true);
                        startActivity(intent);
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

    /**
     * NAME
     *      MainActivity.invalidateView
     * DESCRIPTION
     *      Based on the currentView enum, changes MainActivity to reflect the current view
     *      Certain properties are changed in each like the ability to see InviteOnly events,
     *      and also whether certain filters will be applied to the event listview
     * AUTHOR
     *      @author Jesse Rinaldi
     */
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
                showInviteOnly = false;
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
                showInviteOnly = true;
                currentCategory = "";
                searchRadius = -1;
                setTitle("Invites");
                showAddButton = false;
                filterLayout.setVisibility(View.GONE);
                viewInvites();
                break;
            default:
                Log.e("INVALID VIEW", "State of 'currentView' not recognized");
                break;
        }
        invalidateOptionsMenu();
    }

    /**
     * NAME
     *      MainActivity.eventListClickListener
     * DESCRIPTION
     *      Sets the click listener for lstEvents. When an event item is clicked,
     *      the event is retrieved from firebase, then it and its ID is stored in
     *      AndroidApplication and it is opened in the EventView activity
     * AUTHOR
     *      @author Jesse Rinaldi
     */
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
                            return;
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

    /**
     * NAME
     *      MainActivity.populateEventList
     * SYNOPSIS
     *      @param qRef - A Firebase query for events to be put in lstEvents
     * DESCRIPTION
     *      Sets a listener to retrieve events from the qRef query
     * AUTHOR
     *      @author Jesse Rinaldi
     */
    private void populateEventList(Query qRef){
        qRef.addValueEventListener(valueEventListener);
    }

    /**
     * NAME
     *      MainActivity.valueEventListener
     * DESCRIPTION
     *      A ValueEventListener object which stores events retrieved from
     *      Firebase in an Arraylist of events
     * AUTHOR
     *      @author Jesse Rinaldi
     */
    private ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
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

    /**
     * NAME
     *      MainActivity.viewAttendingEvents
     * DESCRIPTION
     *      Sets MainActivity to the ATTENDING_EVENTS view. Retrieves
     *      the IDs for each event a user is attending from Firebase
     *      and reconciles each ID to a Firebase Event
     * AUTHOR
     *      @author Jesse Rinaldi
     */
    private void viewAttendingEvents() {
        events.clear();
        final ArrayList<String> eventIDs = new ArrayList<String>();
        final Calendar now = Calendar.getInstance();
        final double currentDateTimeSort = (double)now.get(Calendar.YEAR)*100000000 + (double)(now.get(Calendar.MONTH)+1)*1000000 + (double)now.get(Calendar.DAY_OF_MONTH)*10000 - 1;
        application.mFirebaseRef.child("attendance").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(application.user.getId())) {
                    //Get each attending event ID
                    for (DataSnapshot dsEvent : dataSnapshot.child(application.user.getId()).getChildren()) {
                        eventIDs.add(dsEvent.getKey());
                    }
                    application.mFirebaseRef.child("events").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (String eventID : eventIDs) {
                                //Pair each ID with its corresponding event object
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

    /**
     * NAME
     *      MainActivity.viewInvites
     * DESCRIPTION
     *      Sets MainActivity to INVITES view. Very similar to viewAttendingEvents
     *      except it gets the Event IDs from the servers "invites" child
     * AUTHOR
     *      @author Jesse Rinaldi
     */
    private void viewInvites() {
        events.clear();
        final ArrayList<String> eventIDs = new ArrayList<String>();
        final Calendar now = Calendar.getInstance();
        final double currentDateTimeSort = (double)now.get(Calendar.YEAR)*100000000 + (double)(now.get(Calendar.MONTH)+1)*1000000 + (double)now.get(Calendar.DAY_OF_MONTH)*10000 - 1;
        application.mFirebaseRef.child("invites").addListenerForSingleValueEvent(new ValueEventListener() {
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


    /**
     * NAME
     *      MainActivity.setEventList
     * DESCRIPTION
     *      Sets the contents of the ArrayList events to lstEvents. Beforehand though,
     *      it removes some events which don't fulfill certain criteria set before.
     * AUTHOR
     *      @author Jesse Rinaldi
     */
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
            //Remove outdated events if needed
            if (currentView != listStates.MY_EVENTS && Double.compare(e.getDateTimeSort(), currentDateTimeSort) < 0) {
                Log.d("REMOVE OUTDATED", e.getName());
                events.remove(i);
                continue;
            }
            //Remove inviteOnly events if needed
            if (!showInviteOnly && e.getInviteOnly()) {
                Log.d("REMOVE INVITE ONLY", e.getName());
                events.remove(i);
                continue;
            }
            //Remove events filtered out of category if needed
            if (!currentCategory.equals("") && !e.getCategory().equals(currentCategory)) {
                Log.d("REMOVE CATEGORY", e.getName());
                events.remove(i);
                continue;
            }
            //Remove events outside of search radius if needed
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
