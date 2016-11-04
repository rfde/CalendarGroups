package de.return_false.calendargroups2;

import java.util.Set;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;

public class ConfigureNewShortcutActivity extends PreferenceActivity {

    private static final int MY_PERMISSIONS_REQUEST_READ_CALENDAR = 8;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(null);

        // As of Android 6, READ_CALENDAR is a so called 'dangerous' permission, so we need to
        // explicitly request it from the user (at least once at first runtime).
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            // We don't have READ_CALENDAR-Permission -> request it (Android will call onRequestPermissionsResult and tell the user's decision).
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CALENDAR}, MY_PERMISSIONS_REQUEST_READ_CALENDAR);
        } else {
            // We have READ_CALENDAR-Permission -> go ahead.
            prepareView();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CALENDAR: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay!
                    prepareView();
                } else {
                    // permission denied, boo!
                    System.exit(1);
                }
                return;
            }
        }
    }

    public void prepareView() {
        // Reset (to show clean values)
        SharedPreferences sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());
        Editor editor = sharedPrefs.edit();
        editor.clear();
        editor.commit();

        // Read Preferences from XML
        addPreferencesFromResource(R.xml.preferences);

        // We use a usual preference as OK-button
        Preference pref = findPreference("pref_ok");
        pref.setOnPreferenceClickListener(new OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {
                // Get shortcut name
                EditTextPreference etpShortcutName = (EditTextPreference) findPreference("pref_shortcut_name");
                String shortcutName = etpShortcutName.getText();

                // Get selected calendars
                SharedPreferences sharedPrefs = PreferenceManager
                        .getDefaultSharedPreferences(getBaseContext());
                Set<String> selections = sharedPrefs.getStringSet(
                        "pref_calendar_list_key", null);
                String[] selectedCalendarNames = selections.toArray(new String[] {});

                // Get selected calendar app
                String selectedCalendarApp = sharedPrefs.getString(
                        "pref_calendar_app", "(none)");

                // Build Intent (for shortcut icon)
                Intent shortcutIntent = new Intent(getBaseContext(),
                        de.return_false.calendargroups2.MainActivity.class);
                shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                // Attach extra strings (list of selected calendars, selected calendar app)

                // Lesson learned: Android deletes String *Array* Extras from Shortcuts on
                // home screen at reboot (or even earlier).
                // So we need to encode all the calendar names into a single String.
                shortcutIntent.putExtra("calendars", SerializeUtil.stringArrayToString(selectedCalendarNames));
                shortcutIntent.putExtra("cal_app", selectedCalendarApp);

                // Build Intent (for adding icon -> home screen)
                Intent addIntent = new Intent();
                addIntent
                        .putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
                addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, shortcutName);
                addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                        Intent.ShortcutIconResource.fromContext(
                                getBaseContext(), R.mipmap.ic_launcher));
                addIntent
                        .setAction("com.android.launcher.action.INSTALL_SHORTCUT");

                // finish
                getBaseContext().sendBroadcast(addIntent);
                setResult(RESULT_OK, addIntent);
                finish();

                return true;
            }
        });
    }

}
