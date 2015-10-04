package morse.morseapp;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import morse.morseapp.MessageSender.Mode;

public class MainActivity extends Activity {

    static String APP_TAG = "MORSEAPP";

    static Camera cam;
    static Camera.Parameters params;

    private MessageSender msgSender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initMessageField();

        msgSender = new MessageSender();
    }

    public void sendMessage(View view) {

        // Let's not interrupt if it's sending a message
        if(msgSender.isSendingMessage())
            return;

        // MessageSender extends Thread so we need to recreate it
        msgSender = new MessageSender();

        String message = ((EditText) findViewById(R.id.messageField)).getText().toString();
        int fps = Integer.parseInt(((EditText) findViewById(R.id.fpsField)).getText().toString());
        Mode morseMode = ((RadioButton) findViewById(R.id.flashOffRadio)).isChecked() ? Mode.DOT_IS_OFF : Mode.DOT_IS_SHORT_FLASH;

        msgSender.sendMessage(message, fps, morseMode);
    }


    // This can be used to set maximum speed camera can be turned on
    public void setMaxSpeed(View view) {
        String speed = "5";

        List<Long> speeds = new ArrayList<Long>();

        for(int i = 0; i < 5; i++) {
            long startTime = System.currentTimeMillis();
            turnCameraOn();
            long endTime = System.currentTimeMillis();
            turnCameraOff();

            speeds.add(1000 / (endTime - startTime));
        }

        // Select the minimum FPS. It's the max speed
        speed = Long.toString(Collections.min(speeds));

        ((EditText) findViewById(R.id.fpsField)).setText(speed);
    }

    // Sets the encoded message to the encodedMsg field
    public void messageChanged() {
        String message = ((EditText) findViewById(R.id.messageField)).getText().toString();
        ((TextView) findViewById(R.id.encodedMsg)).setText(MessageSender.convertToMorse(message));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Initialize camera
        cam = Camera.open();
        params = cam.getParameters();
        cam.startPreview();
    }

    @Override
    protected void onPause() {
        super.onDestroy();
        cam.stopPreview();
        cam.release();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static void turnCameraOff() {
        params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        MainActivity.cam.setParameters(params);
    }

    public static void turnCameraOn() {
        params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        MainActivity.cam.setParameters(params);
    }

    public void initMessageField() {
        EditText messageField = (EditText) findViewById(R.id.messageField);
        messageField.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                messageChanged();
            }

        });
    }
}
