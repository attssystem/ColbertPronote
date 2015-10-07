package pronote.colbert.fliife.com.colbertpronote;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public String[] arrayContent;
    public String[] arrayTitle;
    public String[] arrayDate;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    public WebView webview;
    public boolean assumeLoggedIn = false;
    public String PREFS_NAME = "CONTENT";

    public ArrayList<String> arrayTitleSync = new ArrayList<>();
    public ArrayList<String> arrayDateSync = new ArrayList<>();
    public ArrayList<String> arrayContentSync = new ArrayList<>();
    private String username = "";
    private String password = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        if(settings.getBoolean("firstLaunch", true)){
            Intent login = new Intent(this, LoginActivity.class);
            startActivity(login);
            settings.edit().putBoolean("firstLaunch", false).apply();
        }
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshEntries();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(loginIntent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        refreshEntries();
    }

    private void refreshEntries() {
        if(webview != null){
            webview.destroy();
        }

        sync();
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
            arrayDate = ((ArrayList<String>) ObjectSerializer.deserialize(dates)).toArray(arrayDate);
        } catch (IOException e) {
            e.printStackTrace();
        }

        setRecyclerContent(arrayTitle, arrayContent, arrayDate);

        mSwipeRefreshLayout.setRefreshing(false);
    }

    private void sync() {
        System.out.println("Sync launched");
        SharedPreferences sp = getSharedPreferences("credentials", Context.MODE_PRIVATE);
        password = sp.getString("password", "");
        username = sp.getString("username", "");

        webview = new WebView(this);
        class HTMLGetInterface{

            @JavascriptInterface
            public void saveData(String date, String title, String content) {
                arrayDateSync.add(date);
                System.out.println("Added date " + date);
                arrayTitleSync.add(title);
                System.out.println("Added title " + title);
                arrayContentSync.add(content);
                System.out.println("Added content " + content);
            }

            @JavascriptInterface
            public void done(){
                try {
                    save();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        webview.addJavascriptInterface(new HTMLGetInterface(), "htmlViewer");
        webview.getSettings().setJavaScriptEnabled(true);
        System.out.println("Logging in");
        login();
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

    public boolean CDTDone = false;
    public void login(){
        webview.setWebViewClient(new WebViewClient() {

            public void onPageFinished(WebView view, String url) {
                //view.loadUrl("javascript:setTimeout(function(){document.getElementById(\"zoneIdent\").value=\"CAILLIAU\",document.getElementById(\"zonePwd\").value=\"notreallyabot\",GInterface.traiterEvenementValidation()},2e3);");
                if (!assumeLoggedIn) {
                    view.loadUrl("javascript:var logInterval=setInterval(function(){null!==document.getElementById(\"zoneIdent\")&&null!==document.getElementById(\"zonePwd\")&&(document.getElementById(\"zoneIdent\").value=\"" + username + "\",document.getElementById(\"zonePwd\").value=\"" + password + "\",GInterface.traiterEvenementValidation(),clearInterval(logInterval))},500);");
                    assumeLoggedIn = true;
                } else if(!CDTDone){
                    getCDT();
                    CDTDone = true;
                }

            }
        });
        // Simplest usage: note that an exception will NOT be thrown
        // if there is an error loading this page (see below).
        webview.loadUrl("http://etablissement.lyceecolbert-tg.org/pronote/mobile.eleve.html");
    }

    public void getCDT(){
        System.out.println("Getting homeworks");
        System.out.println("fired");

        //webview.loadUrl("javascript:setTimeout(function(){htmlViewer.showHTML('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');},5000);");
        webview.loadUrl("javascript:setTimeout(function(){for(var arrayDate=[],arrayTitle=[],arrayContent=[],alldiv=document.getElementsByTagName(\"div\"),requiredDiv='<div data-theme=\"a\" style=\"margin:10px;background-color:#efefef\" class=\"masquerTransition boxShadow\">',cdtdiv=null,i=0;i<alldiv.length;i++){var outHTML=alldiv[i].outerHTML.replace(alldiv[i].innerHTML,\"\").replace(\"</div>\",\"\");outHTML.indexOf(requiredDiv)>-1&&(cdtdiv=alldiv[i])}alldiv=cdtdiv.getElementsByTagName(\"div\");for(var i=0;i<alldiv.length;i++){var concernedDiv=alldiv[i];if(0===alldiv[i].innerHTML.indexOf(\"pour\")){var currentDate=alldiv[i].innerHTML;concernedDiv=alldiv[i+1];for(var j=0;j<concernedDiv.getElementsByTagName(\"div\").length;j++)if(concernedDiv.getElementsByTagName(\"div\")[j].getElementsByClassName(\"Gras\").length>0){arrayDate.push(currentDate),arrayTitle.push(concernedDiv.getElementsByTagName(\"div\")[j].getElementsByClassName(\"Gras\")[0].innerHTML);var parentDiv=concernedDiv.getElementsByTagName(\"div\")[j].getElementsByClassName(\"Gras\")[0].parentElement;parentDiv.removeChild(parentDiv.getElementsByClassName(\"Gras\")[0]);for(var contentString=parentDiv.innerHTML;\" \"===contentString.charAt(0)||\"\\n\"===contentString.charAt(0);)contentString=contentString.substring(1,contentString.length);contentString=contentString.replace(/<a(?:.|\\n)*?>.*?>/gm,\"\"),contentString=contentString.replace(/<(?:.|\\n)*?>/gm,\"\"),contentString=contentString.replace(\"&\",\"&\"),contentString=contentString.replace(\"&nbsp;\",\" \"),arrayContent.push(contentString)}}}for(var l=0;l<arrayTitle.length;l++)htmlViewer.saveData(arrayDate[l],arrayTitle[l],arrayContent[l]);htmlViewer.done();},1750);");

    }

    public void save() throws IOException {
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();

        editor.putString("content", ObjectSerializer.serialize(arrayContentSync));
        editor.putString("title", ObjectSerializer.serialize(arrayTitleSync));
        editor.putString("date", ObjectSerializer.serialize(arrayDateSync));
        editor.apply();
        System.out.println("Done !");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sync) {
            Intent syncIntent = new Intent(this, SyncActivity.class);
            startActivity(syncIntent);
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
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        RecyclerView.Adapter mAdapter = new MyAdapter(first, second, third);
        mRecyclerView.setAdapter(mAdapter);
    }
}
