package pronote.colbert.fliife.com.colbertpronote;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class SettingsActivity extends AppCompatActivity {
    public static final String SILENT_NOTIFICATIONS = "storage_settings_silent_notifications";
    public static final String NOTIFICATIONS_ENABLED = "storage_settings_schedule_notifications";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getFragmentManager().beginTransaction()
                .replace(R.id.relativelayout_settings, new SettingsFragment())
                .commit();
        // Display the fragment as the main content.*/
    }

    public static class SettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preferences);
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
            Boolean silentNotifications = sharedPref.getBoolean(SILENT_NOTIFICATIONS, true);
            Boolean enableNotifications = sharedPref.getBoolean(NOTIFICATIONS_ENABLED, true);
            System.out.println("enabled: " + enableNotifications + "; silent: " + silentNotifications);
        }

    }
}