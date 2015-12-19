package pronote.colbert.fliife.com.colbertpronote;

import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public String[] arrayContent;
    private int LIGHT = 0;
    private int DARK = 1;
    public int THEME = LIGHT;
    public final static String version = "v1.2";
    public String[] arrayTitle;
    public String[] arrayDate;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    public WebView webview;
    public Context context = this;
    public boolean assumeLoggedIn = false;
    public String PREFS_NAME = "CONTENT";

    public ArrayList<String> arrayTitleSync = new ArrayList<>();
    public ArrayList<String> arrayDateSync = new ArrayList<>();
    public ArrayList<String> arrayContentSync = new ArrayList<>();
    private String username = "";
	public String defaultUrl = "http://etablissement.lyceecolbert-tg.org/pronote/mobile.eleve.html";
    private String password = "";
    private ArrayList<String> arrayClassTodaySync = new ArrayList<>();
    private ArrayList<String> arraySubjectTodaySync = new ArrayList<>();
    private ArrayList<String> arrayHoursTodaySync = new ArrayList<>();
    private String[] arrayClassToday;
    private String[] arrayHoursToday;
    private String[] arraySubjectToday;
    public AlarmManagerBroadcastReceiver alarmManagerBroadcastReceiver = new AlarmManagerBroadcastReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        THEME = (sharedPref.getBoolean("storage_settings_dark_theme", false)) ? DARK : LIGHT;

        if(settings.getBoolean("firstLaunch", true)){
            Intent introIntent = new Intent(this, LoginActivity.class);
            introIntent.putExtra("firstLaunch", true);
			
            startActivity(introIntent);
            settings.edit().putBoolean("firstLaunch", false).apply();
        }
        if(THEME == DARK){
            setTheme(R.style.DarkTheme);
        }
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);
        mSwipeRefreshLayout.setEnabled(false);
        if(THEME == DARK) {
            mSwipeRefreshLayout.setBackgroundColor(getResources().getColor(R.color.background_floating_material_dark));
        }
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);

                loginIntent.putExtra("first", false);
                startActivityForResult(loginIntent, 1);
            }
        });

        setRecyclerContent(new String[]{}, new String[]{}, new String[]{});
        SharedPreferences sp = getSharedPreferences("credentials", Context.MODE_PRIVATE);
        password = sp.getString("password", "");
        username = sp.getString("username", "");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        refreshEntries(1);
        new Thread(new Runnable() {
            @Override
            public void run() {
                URL url = null;
                try {
                    url = new URL("https://api.github.com/repos/FliiFe/ColbertPronote/releases/latest");
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                String out = "";
                try {
                    assert url != null;
                    out = new Scanner(url.openStream(), "UTF-8").useDelimiter("\\A").next();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    final JSONObject jsonObject = new JSONObject(out);
                    if(!jsonObject.getString("tag_name").equals(version)){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                new AlertDialog.Builder(context)
                                        .setTitle("Une mise à jour est disponible.")
                                        .setMessage("Voulez vous télécharger la mise à jour ?")
                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                                                DownloadManager.Request request = null;
                                                String downloadURL = null;
                                                try {
                                                    downloadURL = jsonObject.getJSONArray("assets").getJSONObject(0).getString("browser_download_url");
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                System.out.println(downloadURL);
                                                request = new DownloadManager.Request(
                                                    Uri.parse(downloadURL));

                                                dm.enqueue(request);
                                            }
                                        })
                                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        })
                                        .show();
                            }
                        });

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request it is that we're responding to
        if (requestCode == 1) {

            SharedPreferences sp = getSharedPreferences("credentials", Context.MODE_PRIVATE);
            password = sp.getString("password", "");
            username = sp.getString("username", "");
            refreshEntries(0);

        }
    }

    private void refreshEntries(int step) {

        if(webview != null){
            webview.destroy();
            webview = null;
        }
        if(step == 0) {
            mSwipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefreshLayout.setRefreshing(true);
                }
            });
            sync();
        }else if(step == 1) {
            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

            SharedPreferences sched = getSharedPreferences(Calendar.getInstance().get(Calendar.DAY_OF_WEEK) + "", 0);
            String emptyArray = "";
            try {
                emptyArray = ObjectSerializer.serialize(new ArrayList<String>());
            } catch (IOException e) {
                e.printStackTrace();
            }
            String contents = settings.getString("content", emptyArray);
            String titles = settings.getString("title", emptyArray);
            String dates = settings.getString("date", emptyArray);
            String subject = sched.getString("subject", emptyArray);
            String hours = sched.getString("hours", emptyArray);
            String claSs = sched.getString("class", emptyArray);
            try {

                arrayContent = new String[((ArrayList<String>) ObjectSerializer.deserialize(contents)).size()];
                arrayContent = ((ArrayList<String>) ObjectSerializer.deserialize(contents)).toArray(arrayContent);
                arrayTitle = new String[((ArrayList<String>) ObjectSerializer.deserialize(titles)).size()];
                arrayTitle = ((ArrayList<String>) ObjectSerializer.deserialize(titles)).toArray(arrayTitle);
                arrayDate = new String[((ArrayList<String>) ObjectSerializer.deserialize(dates)).size()];
                arrayDate = ((ArrayList<String>) ObjectSerializer.deserialize(dates)).toArray(arrayDate);

                // THIS CODE IS AWFUL. I DON'T KNOW HOW ELSE I CAN SAVE ARRAYS TO MEMORY. PS: I know the Caps Lock key.

                arraySubjectToday = new String[((ArrayList<String>) ObjectSerializer.deserialize(subject)).size()];
                arraySubjectToday = ((ArrayList<String>) ObjectSerializer.deserialize(subject)).toArray(arraySubjectToday);
                arrayHoursToday = new String[((ArrayList<String>) ObjectSerializer.deserialize(hours)).size()];
                arrayHoursToday = ((ArrayList<String>) ObjectSerializer.deserialize(hours)).toArray(arrayHoursToday);
                arrayClassToday = new String[((ArrayList<String>) ObjectSerializer.deserialize(claSs)).size()];
                arrayClassToday = ((ArrayList<String>) ObjectSerializer.deserialize(claSs)).toArray(arrayClassToday);
            } catch (IOException e) {
                e.printStackTrace();
            }

            setRecyclerContent(arrayTitle, arrayContent, arrayDate);

            mSwipeRefreshLayout.setRefreshing(false);

            //alarmManagerBroadcastReceiver.setAlarm(this, arrayHoursToday);
        }
    }

    private void sync() {
        System.out.println("Sync launched");
        SharedPreferences sp = getSharedPreferences("credentials", Context.MODE_PRIVATE);
        password = sp.getString("password", "");
        username = sp.getString("username", "");

        System.out.println("Using username: " + username);
        System.out.println("Using password: " + password);

        webview = new WebView(this);
        //TODO: What's happening on the second sync ?
        //Debug idea : show the webview with the commented code below,
        // and after the first sync, see what the second do by launching it.
        //DEBUG:setContentView(webview);
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
            public void saveDataToday(String claSs, String hours, String subject){
                arrayClassTodaySync.add(claSs);
                System.out.println("Added claSs " + claSs);
                arrayHoursTodaySync.add(hours);
                System.out.println("Added hours " + hours);
                arraySubjectTodaySync.add(subject);
                System.out.println("Added subject " + subject);
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
                } else if (!CDTDone) {
                    getCDT();
                    CDTDone = true;
                }

            }
        });
        // Simplest usage: note that an exception will NOT be thrown
        // if there is an error loading this page (see below).
		
        webview.loadUrl(getPreferences(0).getString("storage_setting_URL", defaultUrl));
    }

    public void getCDT(){
        System.out.println("Getting homeworks");

        webview.loadUrl("javascript:window.gotHomework = false;setTimeout(function(){for(var arrayDate=[],arrayTitle=[],arrayContent=[],alldiv=document.getElementsByTagName(\"div\"),requiredDiv='<div data-theme=\"a\" style=\"margin:10px;background-color:#efefef\" class=\"masquerTransition boxShadow\">',cdtdiv=null,i=0;i<alldiv.length;i++){var outHTML=alldiv[i].outerHTML.replace(alldiv[i].innerHTML,\"\").replace(\"</div>\",\"\");outHTML.indexOf(requiredDiv)>-1&&(cdtdiv=alldiv[i])}alldiv=cdtdiv.getElementsByTagName(\"div\");for(var i=0;i<alldiv.length;i++){var concernedDiv=alldiv[i];if(0===alldiv[i].innerHTML.indexOf(\"pour\")){var currentDate=alldiv[i].innerHTML;concernedDiv=alldiv[i+1];for(var j=0;j<concernedDiv.getElementsByTagName(\"div\").length;j++)if(concernedDiv.getElementsByTagName(\"div\")[j].getElementsByClassName(\"Gras\").length>0){arrayDate.push(currentDate),arrayTitle.push(concernedDiv.getElementsByTagName(\"div\")[j].getElementsByClassName(\"Gras\")[0].innerHTML);var parentDiv=concernedDiv.getElementsByTagName(\"div\")[j].getElementsByClassName(\"Gras\")[0].parentElement;parentDiv.removeChild(parentDiv.getElementsByClassName(\"Gras\")[0]);for(var contentString=parentDiv.innerHTML;\" \"===contentString.charAt(0)||\"\\n\"===contentString.charAt(0);)contentString=contentString.substring(1,contentString.length);contentString=contentString.replace(/<a(?:.|\\n)*?>.*?>/gm,\"\"),contentString=contentString.replace(/<(?:.|\\n)*?>/gm,\"\"),contentString=contentString.replace(\"&amp;\",\"&\"),contentString=contentString.replace(\"&nbsp;\",\" \"),contentString=contentString.replace(\"&gt;\",\">\"),contentString=contentString.replace(\"&lt;\",\"<\"),arrayContent.push(contentString)}}}for(var l=0;l<arrayTitle.length;l++)htmlViewer.saveData(arrayDate[l],arrayTitle[l],arrayContent[l]);window.gotHomework = true;},2250);");

        getToday();
    }

    public void getToday(){

        webview.loadUrl("javascript:var todayInterval=setInterval(function(){window.gotHomework&&(console.log(\"getting Today\"),GInterface.Instances[2].Instances[0].idMenuOnglet.surclickOnglet(1,1),setTimeout(function(){for(var e=[],n=[],t=[],o=document.getElementsByClassName(\"BandeCours\"),a=0;a<o.length;a++)for(var r=o[a].getElementsByTagName(\"tr\"),l=0;l<r.length;l++){var s=r[l].getElementsByTagName(\"span\");0!==s.length&&(e.push(s[0].innerHTML+\" - \"+s[1].innerHTML),console.log(s[0].innerHTML+\" - \"+s[1].innerHTML),n.push(s[2].innerHTML.replace(\"&\",\"&\")),console.log(s[2].innerHTML.replace(\"&amp;\",\"&\")),t.push(s[s.length-1].innerHTML),console.log(s[s.length-1].innerHTML))}for(var g=0;g<e.length;g++)htmlViewer.saveDataToday(t[g],e[g],n[g]);setTimeout(function(){htmlViewer.done()},3e3)},2e3),clearInterval(todayInterval))},500);");
    }

    public void save() throws IOException {
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
        SharedPreferences.Editor schedEditor = getSharedPreferences(Calendar.getInstance().get(Calendar.DAY_OF_WEEK) + "", Context.MODE_PRIVATE).edit();

        editor.putString("content", ObjectSerializer.serialize(arrayContentSync));
        editor.putString("title", ObjectSerializer.serialize(arrayTitleSync));
        editor.putString("date", ObjectSerializer.serialize(arrayDateSync));
        schedEditor.putString("subject", ObjectSerializer.serialize(arraySubjectTodaySync));
        schedEditor.putString("hours", ObjectSerializer.serialize(arrayHoursTodaySync));
        schedEditor.putString("class", ObjectSerializer.serialize(arrayClassTodaySync));
        editor.apply();
        schedEditor.apply();
        System.out.println("Done !");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                refreshEntries(1);
            }
        });
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
            refreshEntries(0);
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
            setRecyclerContent(arrayHoursToday,
                    arraySubjectToday,
                    arrayClassToday);
        //} else if (id == R.id.nav_week) {
            //TODO: Will this be synced ? I think it won't, and it will just save day-by-day. (See below)
            //Each day the app syncs, it saves the schedule in the memory to a day-specific emplacement.
            //Then once the week has been through, Just load schedule.
        } else if (id == R.id.nav_share) {
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareBody = "Téléchargement : https://github.com/FliiFe/ColbertPronote/releases\nPlus d'informations : http://fliife.tk/colbert-pronote\nContributeurs :\nFliife www.fliife.xyz\nATTSSystem www.attssystem.xyz";
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Colbert Pronote");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, "Partager avec"));
        } else if (id == R.id.nav_about) {
            startActivity(new Intent(this, AboutActivity.class));

        } else if(id == R.id.nav_settings){
            startActivity(new Intent(this, SettingsActivity.class));
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
        RecyclerView.Adapter mAdapter;
        if(THEME == DARK) {
            mAdapter = new CardAdapter(first, second, third, R.layout.card_base_dark);
        }else{
            mAdapter = new CardAdapter(first, second, third, R.layout.card_base_light);
        }
        mRecyclerView.setAdapter(mAdapter);
    }
}
