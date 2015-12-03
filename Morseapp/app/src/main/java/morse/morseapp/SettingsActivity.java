package morse.morseapp;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import morse.morseapp.message.Alphabet;
import morse.morseapp.utilities.Settings;

/**
 * Activity for managing settings of this app.
 */
public class SettingsActivity extends Activity {
    private EditText charsPerSecondEditor;
    private RadioGroup radioGroupAlphabet;
    private RadioGroup radioGroupExposure;
    private TextView sensitivityText;
    private TextView merge_distanceText;
    private TextView cutoff_inputText;
    private float sensitivity;
    private Integer merge_distance;
    private float cutoff_input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // give a context to the static class settings
        Settings.setContext(this);

        Settings.clearDetectionParams();

        charsPerSecondEditor = (EditText) findViewById(R.id.edit_text_chars_per_second);
        radioGroupAlphabet = (RadioGroup) findViewById(R.id.radio_group_alphabet);
        radioGroupExposure = (RadioGroup) findViewById(R.id.radio_group_exposure);

        sensitivity = Settings.getSensitivity();
        merge_distance = Settings.getMergeDistance();
        cutoff_input = Settings.getCutoffInput();

        SeekBar sensitivityBar = (SeekBar) findViewById(R.id.sensitivityBar);
        SeekBar merge_distanceBar = (SeekBar) findViewById(R.id.merge_distanceBar);
        SeekBar cutoff_inputBar = (SeekBar) findViewById(R.id.cutoff_inputBar);

        sensitivityBar.setProgress((int) (sensitivity * 100));
        merge_distanceBar.setProgress(merge_distance);
        cutoff_inputBar.setProgress((int) (cutoff_input * 10));

        sensitivityText = (TextView) findViewById(R.id.sensitivityValue);
        merge_distanceText = (TextView) findViewById(R.id.merge_distanceValue);
        cutoff_inputText = (TextView) findViewById(R.id.cutoff_inputValue);

        sensitivityText.setText(Settings.getSensitivity() + "/" + ((float) sensitivityBar.getMax() / 100));
        merge_distanceText.setText(Settings.getMergeDistance() + "/" + merge_distanceBar.getMax());
        cutoff_inputText.setText(Settings.getCutoffInput() + "/" + ((float) cutoff_inputBar.getMax() / 10));

        sensitivityBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Float actualSensitivity = (float) progress / 100;
                sensitivityText.setText(actualSensitivity + "/" + (float) seekBar.getMax() / 100);
                sensitivity = actualSensitivity;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.i("asd", "asd");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.i("asd", "asd");
            }
        });

        merge_distanceBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                merge_distanceText.setText(progress + "/" + seekBar.getMax());
                merge_distance = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        cutoff_inputBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Float actualShadowCutoff = (float) progress / 10;
                cutoff_inputText.setText(actualShadowCutoff + "/" + ((float)seekBar.getMax() / 10));
                cutoff_input = actualShadowCutoff;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });




        /** assign saved value to view for the chars per second EditText */
        charsPerSecondEditor.setText(Settings.getCharsPerSecond().toString());

        /** assign saved value to view for the radio group! */
        radioGroupAlphabet.clearCheck();
        if (Settings.getAlphabet() == Alphabet.BIT_ENCODING_ALPHABET) {
            radioGroupAlphabet.check(R.id.radio_button_bit_alphabet);
        } else {
            radioGroupAlphabet.check(R.id.radio_button_international_morse);
        }


        // check if this device supports manual exposure
        boolean supportsManual = Settings.getSupportsManualExposure();

        radioGroupExposure.clearCheck();
        if (!supportsManual || Settings.autoExposure()) {
            radioGroupExposure.check(R.id.radio_button_exposure_auto);
        } else {
            radioGroupExposure.check(R.id.radio_button_exposure_low);
        }

        if (!supportsManual) {
            radioGroupExposure.setEnabled(false);
            findViewById(R.id.radio_button_exposure_auto).setEnabled(false);
            findViewById(R.id.radio_button_exposure_low).setEnabled(false);
        }


    }

    private void saveAll() {

        Settings.setSensitivity(sensitivity);
        Settings.setMergeDistance(merge_distance);
        Settings.setCutoffInput(cutoff_input);

        String chars = charsPerSecondEditor.getText().toString();

        if (chars.matches("^\\d+$")) {
            Settings.setCharsPerSecond(Integer.parseInt(chars));
        } else {
            charsPerSecondEditor.setText(Settings.getCharsPerSecond());
        }

        boolean bitAlphabet = radioGroupAlphabet.getCheckedRadioButtonId() == R.id.radio_button_bit_alphabet;
        Settings.setAlphabet((bitAlphabet) ? Alphabet.BIT_ENCODING_ALPHABET : Alphabet.INTERNATIONAL_MORSE);

        boolean autoExposure = radioGroupExposure.getCheckedRadioButtonId() == R.id.radio_button_exposure_auto;
        Settings.setAutoExposure(autoExposure);
    }



    @Override
    protected void onPause() {
        saveAll();
        super.onPause();
    }
}
