package de.return_false.calendargroups2;

import android.content.Context;
import android.preference.EditTextPreference;
import android.util.AttributeSet;

public class SEditTextPreference extends EditTextPreference {

    public SEditTextPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public CharSequence getSummary() {
        return this.getText();
    }
}
