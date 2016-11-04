package de.return_false.calendargroups2;

import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/*
 * Util class to encode String arrays as a single string and vice versa.
 * Used to store a list of calendars in shortcut's intents.
 */

public class SerializeUtil {

    public static String[] stringArrayFromString(String encoded) {
        byte [] data = android.util.Base64.decode(encoded, Base64.DEFAULT);
        try {
            ObjectInputStream ois = new ObjectInputStream(
                    new ByteArrayInputStream(  data ) );
            String[] strArr  = (String[])ois.readObject();
            ois.close();
            return strArr;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return new String[]{};
    }

    public static String stringArrayToString( String[] strArr ) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(strArr);
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
    }

}
