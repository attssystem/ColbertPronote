package pronote.colbert.fliife.com.colbertpronote;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import java.util.Calendar;

public class AlarmManagerBroadcastReceiver extends BroadcastReceiver {
    public AlarmManagerBroadcastReceiver() {

    }
    @Override
    public void onReceive(Context context, Intent intent) {
        //TODO: Logic.



        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_school_black_24dp)
                        .setContentTitle("COLBERT Vous avez cours :") //Change needed ? Awkward title...
                        .setContentText((int)Math.floor(Math.random()*100) + "");
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(0, mBuilder.build());
    }

    public void setAlarm(Context context, String[] hours){
        AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);

        String firstHour = hours[0].split(" - ")[0];
        String lastHour = hours[hours.length-1].split(" - ")[1];
        intent.putExtra("LAST", lastHour);

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, stringToHour(firstHour));


        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);

        //am.set(AlarmManager.RTC, cal.getTimeInMillis(), pi);
        am.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), 5 * 1000 * 60, pi);
    }

    public void cancelAlarm(Context context){
        Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }

    public int stringToHour(String hour){
        return Integer.parseInt(hour.split("h")[0]);
    }
    public int stringToMinute(String hour){
        return Integer.parseInt(hour.split("h")[1]);
    }
    //TODO: Copy Widget logic. Apply for notifications
}
