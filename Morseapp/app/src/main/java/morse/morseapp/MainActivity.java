package morse.morseapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import morse.morseapp.message.Alphabet;
import morse.morseapp.message.Encoder;
import morse.morseapp.message.Sender;
import morse.morseapp.utilities.Settings;

public class MainActivity extends Activity implements View.OnClickListener, ImageReader.OnImageAvailableListener {
    static String APP_TAG = "MORSEAPP";

    private CameraFragment mCameraFragment;
    private TextView mSendTextView;
    private LightPostProcessor lightProcessor = new LightPostProcessor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // immediately set settings context
        Settings.setContext(getApplicationContext());

        // set the layout for this activity
        setContentView(R.layout.activity_main);

        /**
         * Only add a new fragment if there isn't one in memory...
         * Else, reassign the mCameraFragment pointer to the existing fragment from the fragment manager.
         */
        if (null == savedInstanceState) {
            mCameraFragment = CameraFragment.newInstance();
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, mCameraFragment)
                    .commit();
        } else {
            mCameraFragment = (CameraFragment) getFragmentManager().findFragmentById(R.id.container);
        }

        mSendTextView = (TextView) findViewById(R.id.edit_text_message);

        // set button listeners
        findViewById(R.id.button_open_settings).setOnClickListener(this);
        findViewById(R.id.button_send_message).setOnClickListener(this);

        // set image processing listener
        mCameraFragment.setOnImageAvailableListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_open_settings:
                openSettings();
                break;
            case R.id.button_send_message:
                sendMessage();
                break;
        }
    }

    /**
     * Open the settings window for the app.
     * All settings are saved directly to the static Settings class, se we don't need to wait for an activity response anywhere.
     */
    private void openSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    /**
     * Sends the current message that the user typed in.
     * Sender.send() will not run if there is already a send action ongoing.
     */
    private void sendMessage() {
        Sender.setTorch(mCameraFragment);
        Sender.setAlphabet(Alphabet.INTERNATIONAL_MORSE);

        String message = mSendTextView.getText().toString().trim();
        if (!message.isEmpty())
            Sender.send(message.toLowerCase());
    }

    /**
     * Overridden method from OnImageAvailableListener.
     * We'll set this activity as the listener of our imageReader in mCameraFragment.
     */
    @Override
    public void onImageAvailable(ImageReader reader) {
        Image next = reader.acquireNextImage();
        processImage(next);
        next.close();
    }

    boolean debugVarRects = false;

    private void processImage(Image img) {
        int size = img.getHeight() * img.getWidth();
        byte[] Y = new byte[size];

        ByteBuffer plane0 = img.getPlanes()[0].getBuffer();
        plane0.get(Y);

        int[] lights = getLights(Y, img.getWidth(), img.getHeight());


        /**
         * Below, some test code for rectangle display!
         */

        /*if (debugVarRects)
            return;

        //debugVarRects = true;
        Log.d("PROCESS", "Frame: " + lightProcessor.frameNumber + " ,found: " + (lightProcessor.blinkers.size()));
        int w = img.getWidth();
        int h = img.getHeight();
        int s = 10;
        Rect mid = new Rect(0, 0, s * 3, s);
        mid.offset((w - mid.width()) / 2, (h - mid.height()) / 2);

        /* we add one rectangle that covers the Image fully, and one small one in the center.
        rects.add(new Rect(0, 0, w, h));*/

        lightProcessor.ProcessLights(lights); // Run light postprocess, merging found lights to previously seen blinkers

        ArrayList<Rect> rects = new ArrayList<>();
        for(LightPostProcessor.Blinker b : lightProcessor.blinkers) {
            rects.add(new Rect((int)b.y, (int)b.x, (int)(b.y+b.size), (int)(b.x+b.size)));
        }

        mCameraFragment.setDrawnRectangleColor(Color.RED);
        mCameraFragment.setDrawnRectangles(rects);
    }

    /**
     * Cancel ongoing send actions when the activity is destroyed!
     * (if there are any)
     */
    @Override
    protected void onDestroy() {
        Sender.cancel();
        super.onDestroy();
    }

    /**
     * -------------------------------------------------------
     * ------------- INIT AWESOME NATIVE STUFF ---------------
     * --------------------- BELOW !! ------------------------
     * -------------------------------------------------------
     */

    static {
        System.loadLibrary("jniprocess");
    }

    private native int[] getLights(byte[] Y, int width, int height);
}
