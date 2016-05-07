package com.rinaldi.jesse.egather;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;

public class MainActivity extends AppCompatActivity {
    private GoogleApiClient gAPI;
    private ListView lstManageEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplication application = (AndroidApplication) getApplicationContext();
        gAPI = application.gAPI;

        setContentView(R.layout.activity_main);
        setupTabHost();
        Intent intent = new Intent(this, signin.class);
        startActivity(intent);

        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(this, R.array.ManageEventsOptions, R.layout.da_item);
        lstManageEvent = (ListView) findViewById(R.id.lstManageEvents);
        lstManageEvent.setAdapter(arrayAdapter);
        listViewClickListener();

            ///DELETE
        Intent i = new Intent(getBaseContext(), EventEditor.class);
        startActivity(i);
    }

    public void setupTabHost(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TabHost tabHost = (TabHost)findViewById(R.id.tabHost);
        tabHost.setup();

        TabHost.TabSpec tabSpec = tabHost.newTabSpec("browse");
        tabSpec.setContent(R.id.tabBrowse);
        tabSpec.setIndicator("Browse Events");
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("tabManageEvents");
        tabSpec.setContent(R.id.tabManageEvents);
        tabSpec.setIndicator("Manage Events");
        tabHost.addTab(tabSpec);
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
                if (textView.getText() == "New Event") {
                    Intent intent = new Intent(getBaseContext(), EventEditor.class);
                    startActivity(intent);
                }
            }
        });
    }
}
