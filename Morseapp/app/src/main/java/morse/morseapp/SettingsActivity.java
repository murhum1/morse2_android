package morse.morseapp;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.RadioGroup;

import morse.morseapp.message.Alphabet;
import morse.morseapp.utilities.Settings;

/**
 * Activity for managing settings of this app.
 */
public class SettingsActivity extends Activity {

    private EditText charsPerSecondEditor;
    private RadioGroup radioGroupAlphabet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // give a context to the static class settings
        Settings.setContext(this);

        charsPerSecondEditor = (EditText) findViewById(R.id.edit_text_chars_per_second);
        radioGroupAlphabet = (RadioGroup) findViewById(R.id.radio_group_alphabet);

        /** assign saved value to view for the chars per second EditText */
        charsPerSecondEditor.setText(Settings.getCharsPerSecond().toString());

        /** assign saved value to view for the radio group! */
        radioGroupAlphabet.clearCheck();
        if (Settings.getAlphabet() == Alphabet.BIT_ENCODING_ALPHABET) {
            radioGroupAlphabet.check(R.id.radio_button_bit_alphabet);
        } else {
            radioGroupAlphabet.check(R.id.radio_button_international_morse);
        }
    }

    private void saveAll() {
        String chars = charsPerSecondEditor.getText().toString();

        if (chars.matches("^\\d+$")) {
            Settings.setCharsPerSecond(Integer.parseInt(chars));
        } else {
            charsPerSecondEditor.setText(Settings.getCharsPerSecond());
        }

        boolean bitAlphabet = radioGroupAlphabet.getCheckedRadioButtonId() == R.id.radio_button_bit_alphabet;
        Settings.setAlphabet((bitAlphabet) ? Alphabet.BIT_ENCODING_ALPHABET : Alphabet.INTERNATIONAL_MORSE);
    }

    @Override
    protected void onPause() {
        saveAll();
        super.onPause();
    }
}
