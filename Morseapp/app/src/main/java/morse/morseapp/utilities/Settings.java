package morse.morseapp.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Vibrator;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.ViewConfiguration;

import morse.morseapp.message.Alphabet;

/**
 * Created by ekreutzman on 19/10/15.
 */
public class Settings {
    private static final String PREF_KEY = "morse.morseapp.SHARED_PREFERENCES";
    private static Context context;

    /** the different settings */

    private static Integer charsPerSecond; // how many chars per second to send with the app
    private static final String CHARS_PER_SECOND_KEY = "chars_per_second";

    private static Alphabet currentAlphabet;
    private static final String ALPHABET_KEY = "alphabet";

    private static Boolean autoExpose;
    private static final String EXPOSURE_KEY = "exposure";


    private static boolean supportsManualExposure = false;

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

    public static Alphabet getAlphabet() {
        if (currentAlphabet == null) {
            String a = sharedPrefs(context).getString(ALPHABET_KEY, "");
            currentAlphabet = Alphabet.INTERNATIONAL_MORSE;

            for (Alphabet alphabet : Alphabet.ALL) {
                if (a.equals(alphabet.name())) {
                    currentAlphabet = alphabet;
                }
            }
        }

        return currentAlphabet;
    }

    public static void setAlphabet(Alphabet alphabet) {
        Settings.currentAlphabet = alphabet;
        save(context, ALPHABET_KEY, alphabet.name());
    }


    public static boolean autoExposure() {
        if (autoExpose == null) {
            autoExpose = sharedPrefs(context).getBoolean(EXPOSURE_KEY, true);
        }

        return autoExpose;
    }

    public static void setAutoExposure(boolean autoExpose) {
        Settings.autoExpose = autoExpose;
        save(context, EXPOSURE_KEY, Settings.autoExpose);
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

    /**
     * Get the height of the on-screen navigation bar (in pixels)
     * @returns the height of the navigation bar (0 if no on-screen navbar)
     */
    public static int getNavigationBarHeight() {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0 && hasNavBar(context)) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private static Vibrator vibrator;
    public static void vibrate(int ms) {
        if (null == vibrator) {
            vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        }

        vibrator.vibrate(ms);
    }

    private static boolean hasNavBar(Context context) {
        Resources resources = context.getResources();
        int id = resources.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            return resources.getBoolean(id);
        } else {    // Check for keys
            boolean hasMenuKey = ViewConfiguration.get(context).hasPermanentMenuKey();
            boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
            return !hasMenuKey && !hasBackKey;
        }
    }

    public static boolean getSupportsManualExposure() {
        return supportsManualExposure;
    }

    public static void setSupportsManualExposure(boolean supportsManualExposure) {
        Settings.supportsManualExposure = supportsManualExposure;
    }
}
