package de.return_false.calendargroups2;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.preference.MultiSelectListPreference;
import android.provider.CalendarContract;
import android.util.AttributeSet;

// This class is based on code I found on stackoverflow: http://stackoverflow.com/a/21880947, author: mynameistodd

public class CalendarListPreference extends MultiSelectListPreference {

    public CalendarListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Text, visible to the user (calendar's display names)
        List<CharSequence> entries = new ArrayList<>();
        // Internal value to save (will also be display names -- TODO: Use any unique identifier instead)
        List<CharSequence> entriesValues = new ArrayList<>();

        // Get list of all calendars
        ContentResolver cr = context.getContentResolver();
        String[] projection = new String[]{CalendarContract.Calendars.NAME,
                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME};

        // We should have permissions here (ConfigureNewShortcutActivity handles that)
        Cursor cursor = cr.query(CalendarContract.Calendars.CONTENT_URI, projection, null, null, null);
        // Step through the calendars and collect their display names
        while (cursor.moveToNext()) {
            String displayName = cursor.getString(1);

            entries.add(displayName);
            entriesValues.add(displayName);
        }

        // Use list of calendars as list for the MultiSelectListPreference
        setEntries(entries.toArray(new CharSequence[entries.size()]));
        setEntryValues(entriesValues.toArray(new CharSequence[entriesValues.size()]));
    }

    @Override
    public CharSequence getSummary() {
        // Summary shows comma separated list of selected calendars.
        final Set<String> entries = getValues();
        if (entries == null) {
            return null;
        } else {
            String listOfEntries = "";
            for (CharSequence entry : entries) {
                listOfEntries += entry + ", ";
            }
            if (listOfEntries.endsWith(", ")) {
                listOfEntries = (String) listOfEntries.subSequence(0,
                        listOfEntries.length() - 2);
            }
            return listOfEntries;
        }
    }

    @Override
    public void setValues(final Set<String> values) {
        super.setValues(values);
        notifyChanged();
    }
}
