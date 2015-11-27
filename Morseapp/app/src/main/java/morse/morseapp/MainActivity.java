package morse.morseapp;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.nio.ByteBuffer;

import morse.morseapp.message.Alphabet;
import morse.morseapp.message.Sender;
import morse.morseapp.utilities.Settings;

public class MainActivity extends Activity implements View.OnClickListener {
    static String APP_TAG = "MORSEAPP";

    private CameraFragment mCameraFragment;
    private TextView mSendTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

        mCameraFragment.setOnImageAvailableListener(onImageAvailableListener);
        Settings.setContext(this);
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
     * This will be run on every frame of the image stream.
     * Set this to be the onImageAvailableListener of mCameraFragment!
     */
    private final ImageReader.OnImageAvailableListener onImageAvailableListener  = new ImageReader.OnImageAvailableListener() {
        @Override
        public void onImageAvailable(ImageReader reader) {
            Image next = reader.acquireNextImage();
            processImage(next);
            next.close();
        }
    };

    private void processImage(Image img) {
        int size = img.getHeight() * img.getWidth();
        byte[] Y = new byte[size];

        ByteBuffer plane0 = img.getPlanes()[0].getBuffer();
        plane0.get(Y);

        int[] lights = getLights(Y, img.getWidth(), img.getHeight());

        Log.d("PROCESS", "Found: " + (lights.length / 5));
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
