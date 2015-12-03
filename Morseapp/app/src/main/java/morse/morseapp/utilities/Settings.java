package morse.morseapp.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.ViewConfiguration;

import java.util.Map;

import morse.morseapp.message.Alphabet;

/**
 * Created by ekreutzman on 19/10/15.
 */
public class Settings {
    private static final String PREF_KEY = "morse.morseapp.SHARED_PREFERENCES";
    private static Context context;

    // in the user mode, a reception needs to have at least this many real letters (not *) at the start
    // to be shown at all to the user.
    public static final int REAL_LETTER_THRESHOLD = 3;

    /** the different settings */

    // alphabet
    private static Alphabet currentAlphabet;
    private static final String ALPHABET_KEY = "alphabet";

    // exposure settings
    private static Boolean autoExpose;
    private static final String EXPOSURE_KEY = "exposure";
    private static Integer exposureFraction;
    private static final String EXPOSURE_FRACTION_KEY = "exposure_fraction";

    private static boolean supportsManualExposure = false;

    // the send / receive frequency
    private static Integer frequency;
    private static final String FREQUENCY_KEY = "frequency";

    // view mode
    private static Integer viewMode;
    private static final String VIEW_MODE_KEY = "view_mode";
    public static final int DEBUG = 0;
    public static final int USER = 1;

    // detection settings
    private static Float sensitivity;
    private static final String SENSITIVITY_KEY = "sensitivity";
    private static Integer merge_distance;
    private static final String MERGE_DISTANCE_KEY = "merge_distance";
    private static Float cutoff_input;

    private static final String CUTOFF_INPUT_KEY = "cutoff_input";

    /** getters and setters for settings */



    public static void setContext(Context context) {
        Settings.context = context;

    }

    public static int getFrequency() {
        if (frequency == null) {
            int f = sharedPrefs(context).getInt(FREQUENCY_KEY, 10);
            frequency = f;
        }

        return frequency;
    }

    public static void setFrequency(int frequency) {
        Settings.frequency = frequency;
        save(context, FREQUENCY_KEY, frequency);
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

    public static void setViewMode(int viewMode) {
        if (viewMode == DEBUG || viewMode == USER) {
            Settings.viewMode = viewMode;
            save(context, VIEW_MODE_KEY, viewMode);
        }
    }

    public static int getViewMode() {
        if (Settings.viewMode == null) {
            int mode = sharedPrefs(context).getInt(VIEW_MODE_KEY, USER);
            Settings.viewMode = mode;
        }

        return Settings.viewMode;
    }


    public static boolean getAutoExposure() {
        if (autoExpose == null) {
            autoExpose = sharedPrefs(context).getBoolean(EXPOSURE_KEY, true);
        }

        return autoExpose;
    }

    public static void setAutoExposure(boolean autoExpose) {
        Settings.autoExpose = autoExpose;
        save(context, EXPOSURE_KEY, Settings.autoExpose);
    }

    public static void setExposureFraction(int fraction) {
        Settings.exposureFraction = fraction;
        save(context, EXPOSURE_FRACTION_KEY, Settings.exposureFraction);
    }

    public static int getExposureFraction() {
        if (exposureFraction == null) {
            int fraction = sharedPrefs(context).getInt(EXPOSURE_FRACTION_KEY, 300);
            Settings.exposureFraction = fraction;
        }
        return exposureFraction;
    }

    public static void setSensitivity(Float s) {
        Settings.sensitivity = s;
        save(context, SENSITIVITY_KEY, Settings.sensitivity);
    }

    public static Float getSensitivity() {
        if (sensitivity == null) {
            Float s = sharedPrefs(context).getFloat(SENSITIVITY_KEY, 0.08f);
            if (s == 0) sensitivity = 0.08f;
            else sensitivity = s;
        }

        return sensitivity;
    }

    public static void setMergeDistance(Integer md) {
        Settings.merge_distance = md;
        save(context, MERGE_DISTANCE_KEY, Settings.merge_distance);
    }

    public static Integer getMergeDistance() {
        if (merge_distance == null) {
            int md = sharedPrefs(context).getInt(MERGE_DISTANCE_KEY, 10);
            if (md == 0) merge_distance = 10;
            else merge_distance = md;
        }

        return merge_distance;
    }

    public static void setCutoffInput(Float c) {
        Settings.cutoff_input = c;
        save(context, CUTOFF_INPUT_KEY, Settings.cutoff_input);
    }

    public static Float getCutoffInput() {
        if (cutoff_input == null) {
            Float c = sharedPrefs(context).getFloat(CUTOFF_INPUT_KEY, 0.5f);
            if (c == 0) cutoff_input = 0.5f;
            else cutoff_input = c;
        }

        return cutoff_input;
    }

    public static void clearDetectionParams() {
        Log.i("clear", "clearing old detect params");
        Map<String,?> keys = sharedPrefs(context).getAll();

        for(Map.Entry<String,?> entry : keys.entrySet())
        {
            if ( entry.getValue().getClass().equals(Integer.class)) {
                if (entry.getKey().equals(SENSITIVITY_KEY)) {
                    save(context, SENSITIVITY_KEY, 0.05f);
                    Float test = sharedPrefs(context).getFloat(SENSITIVITY_KEY, 0.1f);
                }
                else if (entry.getKey().equals(MERGE_DISTANCE_KEY)) {

                }
                else if (entry.getKey().equals(CUTOFF_INPUT_KEY)) {
                    save(context, CUTOFF_INPUT_KEY, 0.5f);
                }
            }
        }
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
            else if (value instanceof Float) editor.putFloat(key, (Float) value);
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
