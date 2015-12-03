package morse.morseapp;

import android.app.Activity;
import android.os.Bundle;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import morse.morseapp.message.Alphabet;
import morse.morseapp.utilities.Settings;

/**
 * Activity for managing settings of this app.
 */
public class SettingsActivity extends Activity implements OnSeekBarChangeListener, RadioGroup.OnCheckedChangeListener {
    private RadioGroup radioGroupAlphabet, radioGroupExposure, radioGroupViewMode;
    private TextView sensitivityText, merge_distanceText, cutoff_inputText, exposureFractionText, frequencyText;
    private float sensitivity, cutoff_input;
    private int merge_distance, frequency, exposureFraction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // give a context to the static class settings
        Settings.setContext(this);

        Settings.clearDetectionParams();

        radioGroupAlphabet = (RadioGroup) findViewById(R.id.radio_group_alphabet);
        radioGroupExposure = (RadioGroup) findViewById(R.id.radio_group_exposure);
        radioGroupViewMode = (RadioGroup) findViewById(R.id.radio_group_view_mode);

        sensitivityText = (TextView) findViewById(R.id.sensitivityValue);
        merge_distanceText = (TextView) findViewById(R.id.merge_distanceValue);
        cutoff_inputText = (TextView) findViewById(R.id.cutoff_inputValue);
        exposureFractionText = (TextView) findViewById(R.id.exposure_time_text);
        frequencyText = (TextView) findViewById(R.id.frequency_text);

        sensitivity = Settings.getSensitivity();
        merge_distance = Settings.getMergeDistance();
        cutoff_input = Settings.getCutoffInput();
        frequency = Settings.getFrequency();
        exposureFraction = Settings.getExposureFraction();

        SeekBar exposureSeekBar = (SeekBar) findViewById(R.id.exposure_fraction_seekbar);
        SeekBar sensitivityBar = (SeekBar) findViewById(R.id.sensitivityBar);
        SeekBar merge_distanceBar = (SeekBar) findViewById(R.id.merge_distanceBar);
        SeekBar cutoff_inputBar = (SeekBar) findViewById(R.id.cutoff_inputBar);
        SeekBar frequencySeekBar = (SeekBar) findViewById(R.id.frequency_seekbar);

        sensitivityBar.setOnSeekBarChangeListener(this);
        merge_distanceBar.setOnSeekBarChangeListener(this);
        cutoff_inputBar.setOnSeekBarChangeListener(this);
        frequencySeekBar.setOnSeekBarChangeListener(this);
        exposureSeekBar.setOnSeekBarChangeListener(this);

        sensitivityBar.setProgress((int) (sensitivity * 100));
        merge_distanceBar.setProgress(merge_distance);
        cutoff_inputBar.setProgress((int) (cutoff_input * 10));
        frequencySeekBar.setProgress(frequency - 1);
        exposureSeekBar.setProgress(-(exposureFraction / 100 - 10));

        radioGroupViewMode.clearCheck();
        if (Settings.getViewMode() == Settings.DEBUG) {
            radioGroupViewMode.check(R.id.radio_button_view_mode_debug);
        } else {
            radioGroupViewMode.check(R.id.radio_button_view_mode_user);
        }

        /** assign saved value to view for the radio group! */
        radioGroupAlphabet.clearCheck();
        if (Settings.getAlphabet() == Alphabet.BIT_ENCODING_ALPHABET) {
            radioGroupAlphabet.check(R.id.radio_button_bit_alphabet);
        } else {
            radioGroupAlphabet.check(R.id.radio_button_international_morse);
        }

        // check if this device supports manual exposure
        boolean supportsManual = Settings.getSupportsManualExposure();

        // exposure
        radioGroupExposure.clearCheck();
        radioGroupExposure.setOnCheckedChangeListener(this);
        if (!supportsManual || Settings.getAutoExposure()) {
            radioGroupExposure.check(R.id.radio_button_exposure_auto);
        } else {
            radioGroupExposure.check(R.id.radio_button_manual_exposure);
        }

        if (!supportsManual) {
            radioGroupExposure.setEnabled(false);
            findViewById(R.id.radio_button_exposure_auto).setEnabled(false);
            findViewById(R.id.radio_button_manual_exposure).setEnabled(false);
            setManualExposureEnabled(false);
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()) {
            case R.id.cutoff_inputBar:
                Float actualShadowCutoff = (float) progress / 10;
                cutoff_inputText.setText(actualShadowCutoff + "/" + ((float) seekBar.getMax() / 10));
                cutoff_input = actualShadowCutoff;
                break;
            case R.id.merge_distanceBar:
                merge_distanceText.setText(progress + "/" + seekBar.getMax());
                merge_distance = progress;
                break;
            case R.id.sensitivityBar:
                Float actualSensitivity = (float) progress / 100;
                sensitivityText.setText(actualSensitivity + "/" + (float) seekBar.getMax() / 100);
                sensitivity = actualSensitivity;
                break;
            case R.id.frequency_seekbar:
                frequency = progress + 1;
                frequencyText.setText(String.format("%d units/s", frequency));
                break;
            case R.id.exposure_fraction_seekbar:
                exposureFraction = (10 - progress) * 100;
                exposureFractionText.setText(String.format("1/%ds", exposureFraction));
                break;
            default: break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (group == radioGroupExposure) {
            setManualExposureEnabled(checkedId == R.id.radio_button_manual_exposure);
        }
    }

    private void setManualExposureEnabled(boolean enabled) {
        findViewById(R.id.exposure_fraction_seekbar).setEnabled(enabled);
        findViewById(R.id.exposure_time_text).setEnabled(enabled);
        findViewById(R.id.exposure_time_title_text).setEnabled(enabled);
    }

    private void saveAll() {
        boolean bitAlphabet = radioGroupAlphabet.getCheckedRadioButtonId() == R.id.radio_button_bit_alphabet;
        Settings.setAlphabet((bitAlphabet) ? Alphabet.BIT_ENCODING_ALPHABET : Alphabet.INTERNATIONAL_MORSE);

        boolean autoExposure = radioGroupExposure.getCheckedRadioButtonId() == R.id.radio_button_exposure_auto;
        Settings.setAutoExposure(autoExposure);

        boolean viewModeDebug = radioGroupViewMode.getCheckedRadioButtonId() == R.id.radio_button_view_mode_debug;
        Settings.setViewMode((viewModeDebug) ? Settings.DEBUG : Settings.USER);

        Settings.setSensitivity(sensitivity);
        Settings.setMergeDistance(merge_distance);
        Settings.setCutoffInput(cutoff_input);
        Settings.setFrequency(frequency);
        Settings.setExposureFraction(exposureFraction);
    }


    @Override
    protected void onPause() {
        saveAll();
        super.onPause();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
