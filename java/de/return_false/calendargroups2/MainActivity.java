package de.return_false.calendargroups2;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract.Calendars;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

/*
 * This Activity has no UI; it is called by user-defined shortcuts on the home screen, sets the
 * calendar's visibilities according to the extra information hidden behind those shortcuts
 * and starts the user's favorite calendar app.
 */

public class MainActivity extends Activity {

    // Projection array. Creating indices for this array instead of doing
    // dynamic lookups improves performance.
    public static final String[] EVENT_PROJECTION = new String[]{
            Calendars._ID, // 0
            Calendars.CALENDAR_DISPLAY_NAME, // 1
            Calendars.VISIBLE // 2
    };

    // The indices for the projection array above.
    private static final int PROJECTION_ID_INDEX = 0;
    private static final int PROJECTION_DISPLAY_NAME_INDEX = 1;
    private static final int PROJECTION_VISIBLE_INDEX = 2;

    private static final int MY_PERMISSIONS_REQUEST_RW_CALENDAR = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!readPermission() || !writePermission()) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR}, MY_PERMISSIONS_REQUEST_RW_CALENDAR);
        } else {
            doCalendarChange();
        }

        finish();
        System.exit(0);
    }

    private boolean readPermission() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean writePermission() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_RW_CALENDAR: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay!
                    doCalendarChange();
                } else {
                    // permission denied, boo!
                    System.exit(1);
                }
            }
        }
    }

    private void doCalendarChange() {
        Intent intent = getIntent();
        //intent = getIntent();
        if (intent != null) {
            // Get calendar display names (< intent)
            String[] calendars = SerializeUtil.stringArrayFromString(intent.getStringExtra("calendars"));
            // Get calendar app (< intent)
            String calAppPackageName = intent.getStringExtra("cal_app");

            Log.w("calg2", "APP PACKAGE NAME:" + calAppPackageName);

            // Run query
            Cursor cur;
            ContentResolver cr = getContentResolver();
            Uri uri = Calendars.CONTENT_URI;

            // We should have sufficient permissions (i.e. READ_CALENDAR) at this point
            cur = cr.query(uri, EVENT_PROJECTION, null, null, null);

            // Use the cursor to step through the returned records
            while (cur.moveToNext()) {
                long calID = 0;

                String displayName;
                String visible;

                // Get the field values
                calID = cur.getLong(PROJECTION_ID_INDEX);
                displayName = cur.getString(PROJECTION_DISPLAY_NAME_INDEX);
                visible = cur.getString(PROJECTION_VISIBLE_INDEX);

                int shallBeVisible = 0;
                for (String calendar : calendars) {
                    if (calendar.equals(displayName)) {
                        shallBeVisible = 1;
                    }
                }
                if (shallBeVisible != Integer.parseInt(visible)) {
                    ContentValues values = new ContentValues();
                    // The new visibility for the calendar
                    values.put(Calendars.VISIBLE, shallBeVisible);
                    Uri updateUri = ContentUris.withAppendedId(
                            Calendars.CONTENT_URI, calID);
                    getContentResolver().update(updateUri,
                            values, null, null);
                }
            }

            // Start calendar app (if user selected one)
            if ((calAppPackageName != null)
                    && (!calAppPackageName.equals("(none)"))) {
                Intent i;
                PackageManager manager = getPackageManager();

                i = manager.getLaunchIntentForPackage(calAppPackageName);
                i.addCategory(Intent.CATEGORY_LAUNCHER);
                startActivity(i);
            }
        }
    }
}
