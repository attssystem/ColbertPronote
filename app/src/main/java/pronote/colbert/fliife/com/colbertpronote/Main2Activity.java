package pronote.colbert.fliife.com.colbertpronote;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class Main2Activity extends AppCompatActivity {
    public WebView webview;
    public boolean assumeLoggedIn = false;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        webview = new WebView(this);
        webview.getSettings().setJavaScriptEnabled(true);
        setContentView(webview);

        login();
    }

    public void login(){
        webview.setWebViewClient(new WebViewClient() {

            public void onPageFinished(WebView view, String url) {
                //view.loadUrl("javascript:setTimeout(function(){document.getElementById(\"zoneIdent\").value=\"CAILLIAU\",document.getElementById(\"zonePwd\").value=\"notreallyabot\",GInterface.traiterEvenementValidation()},2e3);");
                if (assumeLoggedIn == false) {
                    view.loadUrl("javascript:var logInterval=setInterval(function(){null!==document.getElementById(\"zoneIdent\")&&null!==document.getElementById(\"zonePwd\")&&(document.getElementById(\"zoneIdent\").value=\"CAILLIAU\",document.getElementById(\"zonePwd\").value=\"xxxxxx\",GInterface.traiterEvenementValidation(),clearInterval(logInterval))},500);");
                    assumeLoggedIn = true;
                } else {
                    getCDT();
                }

            }
        });
        // Simplest usage: note that an exception will NOT be thrown
        // if there is an error loading this page (see below).
        webview.loadUrl("http://etablissement.lyceecolbert-tg.org/pronote/mobile.eleve.html");
    }

    public void getCDT(){
        System.out.print("fired");
        /*class HTMLGetInterface{

            @JavascriptInterface
            public String showHTML(String html) {
                return "html";
            }

        }
        webview.addJavascriptInterface(new HTMLGetInterface(), "htmlViewer");
        webview.loadUrl("javascript:setTimeout(function(){htmlViewer.showHTML('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');},5000);");
        */
        class JsObject {
            @JavascriptInterface
            public String toString() { return "injectedObject"; }
        }
        webview.addJavascriptInterface(new JsObject(), "injectedObject");
        //webview.loadData("", "text/html", null);
        webview.loadUrl("javascript:console.warn(injectedObject.toString())");

    }

}
