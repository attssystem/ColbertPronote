package pronote.colbert.fliife.com.colbertpronote;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmManagerBroadcastReceiver extends BroadcastReceiver {
    public AlarmManagerBroadcastReceiver() {
    }
    final public static String ONE_TIME = "onetime";
    @Override
    public void onReceive(Context context, Intent intent) {
        //TODO: Implement notification here
    }
    //TODO: add method to set hours. (Update every half hour || Update on specified hours)
}
