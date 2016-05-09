package com.rinaldi.jesse.egather;

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

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private GoogleApiClient gAPI;
    private ListView lstManageEvent;
    private ArrayList<String> eventNames = new ArrayList<>();
    private DataSnapshot currentSnapshot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplication application = (AndroidApplication) getApplicationContext();
        gAPI = application.gAPI;

        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this, signin.class);
        startActivity(intent);

        application.mFirebaseRef.addValueEventListener(valueEventListener);
    /*
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(this, R.array.ManageEventsOptions, android.R.layout.simple_list_item_1);
        lstManageEvent = (ListView) findViewById(R.id.lstManageEvents);
        lstManageEvent.setAdapter(arrayAdapter);
        listViewClickListener(); */
            ///DELETE
       //Intent i = new Intent(getBaseContext(), EventCreator.class);
        //startActivity(i);
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void listViewClickListener(){
        lstManageEvent.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> paret, View viewClicked, int position, long id) {
                TextView textView = (TextView) viewClicked;
                if (textView.getText().equals("New Event")) {
                    Intent intent = new Intent(getBaseContext(), EventCreator.class);
                    startActivity(intent);
                }
            }
        });
    }

    private ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            currentSnapshot = dataSnapshot;
            for (DataSnapshot child : dataSnapshot.child("events").getChildren()) {
                Log.d("EVENT", child.child("name").getValue().toString());
                eventNames.add(child.child("name").getValue().toString());
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
