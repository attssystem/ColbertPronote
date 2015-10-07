package pronote.colbert.fliife.com.colbertpronote;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.IOException;
import java.util.ArrayList;

public class SyncActivity extends AppCompatActivity {
    public WebView webview;
    public boolean assumeLoggedIn = false;
    public String PREFS_NAME = "CONTENT";

    public ArrayList<String> arrayTitle = new ArrayList<>();
    public ArrayList<String> arrayDate = new ArrayList<>();
    public ArrayList<String> arrayContent = new ArrayList<>();
    private String username = "";
    private String password = "";


    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sp = getSharedPreferences("credentials", Context.MODE_PRIVATE);
        password = sp.getString("password", "");
        username = sp.getString("username", "");

        setContentView(R.layout.activity_main2);
        webview = new WebView(this);
        class HTMLGetInterface{

            @JavascriptInterface
            public void saveData(String date, String title, String content) {
                arrayDate.add(date);
                System.out.println("Added date " + date);
                arrayTitle.add(title);
                System.out.println("Added title " + title);
                arrayContent.add(content);
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
        setContentView(webview);

        login();
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
        System.out.println("fired");

        //webview.loadUrl("javascript:setTimeout(function(){htmlViewer.showHTML('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');},5000);");
        webview.loadUrl("javascript:setTimeout(function(){for(var arrayDate=[],arrayTitle=[],arrayContent=[],alldiv=document.getElementsByTagName(\"div\"),requiredDiv='<div data-theme=\"a\" style=\"margin:10px;background-color:#efefef\" class=\"masquerTransition boxShadow\">',cdtdiv=null,i=0;i<alldiv.length;i++){var outHTML=alldiv[i].outerHTML.replace(alldiv[i].innerHTML,\"\").replace(\"</div>\",\"\");outHTML.indexOf(requiredDiv)>-1&&(cdtdiv=alldiv[i])}alldiv=cdtdiv.getElementsByTagName(\"div\");for(var i=0;i<alldiv.length;i++){var concernedDiv=alldiv[i];if(0===alldiv[i].innerHTML.indexOf(\"pour\")){var currentDate=alldiv[i].innerHTML;concernedDiv=alldiv[i+1];for(var j=0;j<concernedDiv.getElementsByTagName(\"div\").length;j++)if(concernedDiv.getElementsByTagName(\"div\")[j].getElementsByClassName(\"Gras\").length>0){arrayDate.push(currentDate),arrayTitle.push(concernedDiv.getElementsByTagName(\"div\")[j].getElementsByClassName(\"Gras\")[0].innerHTML);var parentDiv=concernedDiv.getElementsByTagName(\"div\")[j].getElementsByClassName(\"Gras\")[0].parentElement;parentDiv.removeChild(parentDiv.getElementsByClassName(\"Gras\")[0]);for(var contentString=parentDiv.innerHTML;\" \"===contentString.charAt(0)||\"\\n\"===contentString.charAt(0);)contentString=contentString.substring(1,contentString.length);contentString=contentString.replace(/<a(?:.|\\n)*?>.*?>/gm,\"\"),contentString=contentString.replace(/<(?:.|\\n)*?>/gm,\"\"),contentString=contentString.replace(\"&\",\"&\"),contentString=contentString.replace(\"&nbsp;\",\" \"),arrayContent.push(contentString)}}}for(var l=0;l<arrayTitle.length;l++)htmlViewer.saveData(arrayDate[l],arrayTitle[l],arrayContent[l]);htmlViewer.done();},1750);");

    }

    public void save() throws IOException {
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();

        editor.putString("content", ObjectSerializer.serialize(arrayContent));
        editor.putString("title", ObjectSerializer.serialize(arrayTitle));
        editor.putString("date", ObjectSerializer.serialize(arrayDate));
        editor.apply();
    }

}
