package pronote.colbert.fliife.com.colbertpronote;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class Main2Activity extends AppCompatActivity {
    public WebView webview;
    public boolean assumeLoggedIn = false;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        webview = new WebView(this);
        class HTMLGetInterface{

            @JavascriptInterface
            public void showHTML(String html) {
                Toast.makeText(getApplicationContext(), html, Toast.LENGTH_SHORT).show();
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
                    view.loadUrl("javascript:var logInterval=setInterval(function(){null!==document.getElementById(\"zoneIdent\")&&null!==document.getElementById(\"zonePwd\")&&(document.getElementById(\"zoneIdent\").value=\"CAILLIAU\",document.getElementById(\"zonePwd\").value=\"notreallyabot\",GInterface.traiterEvenementValidation(),clearInterval(logInterval))},500);");
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
        webview.loadUrl("javascript:setTimeout(function(){for(var alldiv=document.getElementsByTagName(\"div\"),divHTML=alldiv[111].outerHTML.replace(alldiv[111].innerHTML,\"\"),requiredDiv='<div data-theme=\"a\" style=\"margin:10px;background-color:#efefef\" class=\"masquerTransition boxShadow\">',cdtdiv,i=0;i<alldiv.length;i++){var outHTML=alldiv[i].outerHTML.replace(alldiv[i].innerHTML,\"\").replace(\"</div>\",\"\");outHTML.indexOf(requiredDiv)>-1&&(cdtdiv=alldiv[i])}cdtdiv.removeChild(cdtdiv.getElementsByTagName(\"div\")[0]),alldiv=cdtdiv.getElementsByTagName(\"div\");for(var i=0;i<alldiv.length;i++)0===alldiv[i].innerHTML.indexOf(\"pour\")&&htmlViewer.showHTML(alldiv[i].innerHTML);},2000);");

    }

}
