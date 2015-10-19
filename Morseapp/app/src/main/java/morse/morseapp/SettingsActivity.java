package morse.morseapp;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import morse.morseapp.utilities.Settings;

/**
 * Activity for managing settings of this app.
 */
public class SettingsActivity extends Activity {

    private EditText charsPerSecondEditor;
    private RadioGroup radioGroupDotType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // give a context to the static class settings
        Settings.setContext(this);

        charsPerSecondEditor = (EditText) findViewById(R.id.edit_text_chars_per_second);
        radioGroupDotType = (RadioGroup) findViewById(R.id.radio_group_dot_type);

        /** assign saved value to view for the chars per second EditText */
        charsPerSecondEditor.setText(Settings.getCharsPerSecond().toString());

        /** assign saved value to view for the radio group! */
        radioGroupDotType.clearCheck();
        if (Settings.getDotType() == Settings.DOT_TYPE.FLASH_OFF) {
            radioGroupDotType.check(R.id.radio_button_flash_off);
        } else {
            radioGroupDotType.check(R.id.radio_button_short_flash);
        }
    }

    private void saveAll() {
        String chars = charsPerSecondEditor.getText().toString();

        if (chars.matches("^\\d+$")) {
            Settings.setCharsPerSecond(Integer.parseInt(chars));
        } else {
            charsPerSecondEditor.setText(Settings.getCharsPerSecond());
        }

        boolean flashOff = radioGroupDotType.getCheckedRadioButtonId() == R.id.radio_button_flash_off;
        Settings.setDotType((flashOff) ? Settings.DOT_TYPE.FLASH_OFF : Settings.DOT_TYPE.SHORT_FLASH);
    }

    @Override
    protected void onPause() {
        saveAll();
        super.onPause();
    }
}
