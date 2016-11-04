package de.return_false.calendargroups2;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.preference.ListPreference;
import android.util.AttributeSet;

public class AppListPreference extends ListPreference {

    public AppListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Text, visible to the user ("human readable" names go here)
        List<CharSequence> entries = new ArrayList<>();
        // Internal value to save (package names go here)
        List<CharSequence> entriesValues = new ArrayList<>();

        // Add default/dummy entry: No calendar app
        entries.add(getContext().getString(R.string.str_calendar_app_none));
        entriesValues.add("(none)");

        // List all applications on the device
        // The following code is based on http://stackoverflow.com/a/9359389
        final PackageManager pm = context.getPackageManager();

        List<ApplicationInfo> packages = pm
                .getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo pInfo : packages) {
            if ((pm.getLaunchIntentForPackage(pInfo.packageName) != null)
                    && (!pm.getLaunchIntentForPackage(pInfo.packageName)
                    .equals(""))) {

                entries.add(pm.getApplicationLabel(pInfo));
                entriesValues.add(pInfo.packageName);
            }
        }
        // <end stackoverflow>

        // Use list of apps as list for the ListPreference
        setEntries(entries.toArray(new CharSequence[entries.size()]));
        setEntryValues(entriesValues.toArray(new CharSequence[entriesValues.size()]));
    }

    @Override
    public CharSequence getSummary() {
        // Summary shows display name of selected app
        final CharSequence entry = getEntry();
        if (entry == null) {
            return null;
        } else {
            return entry;
        }
    }
}
