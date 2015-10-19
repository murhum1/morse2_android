package morse.morseapp.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;

/**
 * Created by ekreutzman on 19/10/15.
 */
public class Settings {
    private static final String PREF_KEY = "morse.morseapp.SHARED_PREFERENCES";
    private static Context context;

    public enum DOT_TYPE {
        SHORT_FLASH, FLASH_OFF
    }


    /** the different settings */

    private static Integer charsPerSecond; // how many chars per second to send with the app
    private static final String CHARS_PER_SECOND_KEY = "chars_per_second";

    private static DOT_TYPE dotType; // is the dot ('.' in morse) flash off, or is it just a shorter flash?
    private static final String DOT_TYPE_KEY = "dot_type";


    /** getters and setters for settings */

    public static void setContext(Context context) {
        Settings.context = context;
    }

    public static Integer getCharsPerSecond() {
        if (charsPerSecond == null) {
            Integer chars = sharedPrefs(context).getInt(CHARS_PER_SECOND_KEY, 0);
            if (chars == 0) charsPerSecond = 5;
            else charsPerSecond = chars;
        }

        return charsPerSecond;
    }

    public static void setCharsPerSecond(Integer charsPerSecond) {
        Settings.charsPerSecond = charsPerSecond;
        save(context, CHARS_PER_SECOND_KEY, charsPerSecond);
    }

    public static DOT_TYPE getDotType() {
        if (dotType == null) {
            boolean isFlashOff = sharedPrefs(context).getBoolean(DOT_TYPE_KEY, false);
            dotType = (isFlashOff) ? DOT_TYPE.FLASH_OFF : DOT_TYPE.SHORT_FLASH;
        }

        return dotType;
    }

    public static void setDotType(DOT_TYPE dotType) {
        Settings.dotType = dotType;
        save(context, DOT_TYPE_KEY, dotType == DOT_TYPE.FLASH_OFF);

    }


    /** from here on down, utility methods for storing settings to disc */

    private static SharedPreferences sharedPrefs(Context context) {
        return context.getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE);
    }

    private static void save(Context context, String key, Object value) {
        SharedPreferences sharedPref = sharedPrefs(context);

        SharedPreferences.Editor editor = sharedPref.edit();

        if (sharedPref.contains(key))
            editor.remove(key);

        if (value != null) {
            if (value instanceof String) editor.putString(key,(String) value);
            else if (value instanceof Integer) editor.putInt(key, (Integer) value);
            else if (value instanceof Boolean) editor.putBoolean(key, (Boolean) value);
        }
        editor.commit();
    }
}
