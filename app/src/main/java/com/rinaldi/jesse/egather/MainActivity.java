package com.rinaldi.jesse.egather;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;

public class MainActivity extends Activity {
    private GoogleApiClient gAPI;
    private ListView lstManageEvent;
    private ArrayList<String> eventNames = new ArrayList<>();
    private DataSnapshot currentSnapshot;
    private AndroidApplication application;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        application = (AndroidApplication) getApplicationContext();
        gAPI = application.gAPI;

        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this, signin.class);
        startActivity(intent);


        setTitle("Events");
        populateListView();
    /*
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(this, R.array.ManageEventsOptions, android.R.layout.simple_list_item_1);
        lstManageEvent = (ListView) findViewById(R.id.lstManageEvents);
        lstManageEvent.setAdapter(arrayAdapter);
        listViewClickListener(); */
            ///DELETE
       //Intent i = new Intent(getBaseContext(), EventCreator.class);
        //startActivity(i);
        setTitle("Events");
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
        int id = item.getItemId();

        switch(id) {
            case R.id.action_settings:
                return true;
            case R.id.action_create_event:
                Intent i = new Intent(getBaseContext(), EventCreator.class);
                startActivity(i);
                populateListView();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void listViewClickListener(){
        lstManageEvent.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> paret, View viewClicked, int position, long id) {
                final TextView textView = (TextView) viewClicked;
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

    private void populateListView(){
        application.mFirebaseRef.addValueEventListener(valueEventListener);
    }

    private ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            currentSnapshot = dataSnapshot;
            eventNames.clear();
            for (DataSnapshot dsEvent : dataSnapshot.child("events").getChildren()) {
                Log.d("EVENT", dsEvent.child("name").getValue().toString());
                eventNames.add(dsEvent.child("name").getValue().toString());
                ArrayAdapter arrayAdapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, eventNames);
                lstManageEvent = (ListView) findViewById(R.id.lstManageEvents);
                lstManageEvent.setAdapter(arrayAdapter);
                listViewClickListener();
            }
        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {

        }
    };
}
