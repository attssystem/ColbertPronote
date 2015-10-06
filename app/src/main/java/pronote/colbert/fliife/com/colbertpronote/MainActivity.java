package pronote.colbert.fliife.com.colbertpronote;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;

    public String PREFS_NAME = "CONTENT";
    public String[] arrayContent;
    public String[] arrayTitle;
    public String[] arrayDate;
    private RecyclerView.LayoutManager mLayoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        String emptyArray = "";
        try {
             emptyArray = ObjectSerializer.serialize(new ArrayList<String>());
        } catch (IOException e) {
            e.printStackTrace();
        }
        String contents = settings.getString("content", emptyArray);
        String titles = settings.getString("title", emptyArray);
        String dates = settings.getString("date", emptyArray);
        try {

            arrayContent = new String[((ArrayList<String>) ObjectSerializer.deserialize(contents)).size()];
            arrayContent = ((ArrayList<String>) ObjectSerializer.deserialize(contents)).toArray(arrayContent);
            arrayTitle = new String[((ArrayList<String>) ObjectSerializer.deserialize(titles)).size()];
            arrayTitle = ((ArrayList<String>) ObjectSerializer.deserialize(titles)).toArray(arrayTitle);
            arrayDate = new String[((ArrayList<String>) ObjectSerializer.deserialize(dates)).size()];
            arrayDate = ((ArrayList<String>) ObjectSerializer.deserialize(dates)).toArray(arrayTitle);
        } catch (IOException e) {
            e.printStackTrace();
        }

        setRecyclerContent(arrayTitle, arrayContent, arrayDate);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_cdt) {
            setRecyclerContent(arrayTitle, arrayContent, arrayDate);
        } else if (id == R.id.nav_today) {
            setRecyclerContent(new String[]{"8h", "10h"},
                    new String[]{"Histoire-géographie", "Mathématiques"},
                    new String[]{"C007", "E309"});
        } else if (id == R.id.nav_week) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_about) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void setRecyclerContent(String[] first, String[] second, String[] third){
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new MyAdapter(first, second, third);
        mRecyclerView.setAdapter(mAdapter);
    }
}
