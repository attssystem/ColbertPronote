package pronote.colbert.fliife.com.colbertpronote;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Implementation of App Widget functionality.
 */
public class ColbertWidget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }


    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        SharedPreferences sp = context.getSharedPreferences("CONTENT", 0);
        String emptyArray = "";
        try {
            emptyArray = ObjectSerializer.serialize(new ArrayList<String>());
        } catch (IOException e) {
            e.printStackTrace();
        }
        String claSs = sp.getString("class", emptyArray);
        String hours = sp.getString("hours", emptyArray);
        String[] arrayClassToday = null;
        String[] arrayHoursToday = null;
        try {
            arrayHoursToday = new String[((ArrayList<String>) ObjectSerializer.deserialize(hours)).size()];
            arrayHoursToday = ((ArrayList<String>) ObjectSerializer.deserialize(hours)).toArray(arrayHoursToday);
            arrayClassToday = new String[((ArrayList<String>) ObjectSerializer.deserialize(claSs)).size()];
            arrayClassToday = ((ArrayList<String>) ObjectSerializer.deserialize(claSs)).toArray(arrayClassToday);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int count=0;
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.colbert_widget);
        for(String hour: arrayHoursToday){
            String firstHour = hour.split(" - ")[0];
            String endHour = hour.split(" - ")[1];
            System.out.println(firstHour + " - " + endHour);

            Calendar cal1 = Calendar.getInstance();
            cal1.set(Calendar.HOUR_OF_DAY, Integer.parseInt(firstHour.split("h")[0]));
            cal1.set(Calendar.MINUTE, Integer.parseInt(firstHour.split("h")[1]));
			cal1 = removeMinutes(cal1, 15);
            System.out.println(cal1.getTime());
            Date firstHourTimeInMillis = cal1.getTime();

            Calendar cal2 = Calendar.getInstance();
            cal2.set(Calendar.HOUR_OF_DAY, Integer.parseInt(endHour.split("h")[0]));
            cal2.set(Calendar.MINUTE, Integer.parseInt(endHour.split("h")[1]));
			cal2 = removeMinutes(cal2, 15);
            System.out.println(cal2.getTime());
            Date endHourTimeInMillis = cal2.getTime();

            Date nowTimeInMillis = new Date();
            /*Following is for debug
            Calendar cal3 = Calendar.getInstance();
            cal3.set(Calendar.HOUR_OF_DAY, 16);
            cal3.set(Calendar.MINUTE, 20);*/
            System.out.println(nowTimeInMillis);
            //Date nowTimeInMillis = cal3.getTime();

            if(nowTimeInMillis.compareTo(firstHourTimeInMillis) >= 0 && nowTimeInMillis.compareTo(endHourTimeInMillis) <= 0){
                views.setTextViewText(R.id.appwidget_text, arrayClassToday[count]);
            }
            count++;
        }

        //CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        //views.setTextViewText(R.id.appwidget_text, widgetText);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
	public static Calendar removeMinutes(Calendar calObject, int offset){
		if(calObject.getTime().getMinutes() >= offset){
			calObject.set(Calendar.MINUTE, calObject.getTime().getMinutes()-offset);
			return calObject;
		}else{
			calObject.set(Calendar.HOUR_OF_DAY, calObject.getTime().getHours()-1);
			calObject.set(Calendar.MINUTE, calObject.getTime().getMinutes()+60-offset);
			return calObject;
		}
	}
}

