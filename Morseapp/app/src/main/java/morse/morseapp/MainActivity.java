package morse.morseapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.graphics.Paint;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.ImageReader;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.RenderScript;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Size;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.renderscript.*;

import morse.morseapp.MessageSender.Mode;
import morse.morseapp.utilities.FastClickPreventer;

public class MainActivity extends Activity {

    static String APP_TAG = "MORSEAPP";

    Torch torch;

    private MessageSender msgSender;
    private CameraManager cameraManager;
    private Surface cameraSurface;
    private String cameraId;
    private RenderScript rs;
    private Allocation allocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.i("our awesome string:", returnedString());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // assign click listener to the cog button (to open the settings!)
        findViewById(R.id.button_open_settings).setOnClickListener(new FastClickPreventer(1000) {
            @Override
            public void onViewClick(View v) {
                openSettings();
            }
        });
        rs = RenderScript.create(this);

        //initMessageField();

        //msgSender = new MessageSender();

    }
        //Type.Builder yuvType = new Type.Builder(rs, Element.U8(rs));
        //yuvType.setYuvFormat(ImageFormat.NV21);
        //allocation = Allocation.createTyped(rs, yuvType.create());

    private void openSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void initCamera(View view) {
        msgSender = new MessageSender();

        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            String[] cameraIdList = cameraManager.getCameraIdList();
            cameraId = cameraIdList[0];

            SurfaceView cameraSurfaceView = (SurfaceView) findViewById(R.id.cameraSurface);
            SurfaceHolder cameraSurfaceHolder = cameraSurfaceView.getHolder();

            cameraSurfaceHolder.addCallback(new SurfaceHolderCallback());

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

    }

    private class ImageMaster implements ImageReader.OnImageAvailableListener {

        @Override
        public void onImageAvailable(ImageReader reader) {
            // reader.
        }
    }

    private class SurfaceHolderCallback implements SurfaceHolder.Callback {

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            initCamera(holder);
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {

        }
    }

    public void initCamera(SurfaceHolder cameraSurfaceHolder) {
        CameraCharacteristics characteristics = null;
        try {
            cameraSurface = cameraSurfaceHolder.getSurface();

            characteristics = cameraManager.getCameraCharacteristics(cameraId);
            StreamConfigurationMap configs = characteristics.get(
                    CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            Log.i("camera surface id:", "" + R.id.cameraSurface);
            int[] outputFormats = configs.getOutputFormats();
            Size[] outputSizes = configs.getOutputSizes(outputFormats[0]);

            Canvas cameraCanvas = cameraSurfaceHolder.lockCanvas();
            cameraSurfaceHolder.setFixedSize(56, 178);// cameraCanvas.getWidth() , cameraCanvas.getHeight());
            cameraSurfaceHolder.unlockCanvasAndPost(cameraCanvas);
            torch = new Torch(cameraManager, cameraSurface, cameraId);

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    public void toggleFlash(View view) {
        try {
            if (torch.builderi.get(CaptureRequest.FLASH_MODE) == CaptureRequest.FLASH_MODE_TORCH) {
                torch.builderi.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_OFF);
                torch.mSession.setRepeatingRequest(torch.builderi.build(), null, null);
            }
            else {
                torch.builderi.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_TORCH);
                torch.mSession.setRepeatingRequest(torch.builderi.build(), null, null);
            }
        } catch(CameraAccessException e) {
            e.printStackTrace();
        }

    }

    public void sendMessage(View view) {

        // Let's not interrupt if it's sending a message
        if (msgSender.isSendingMessage())
            return;

        // MessageSender extends Thread so we need to recreate it
        msgSender = new MessageSender();

        String message = ((EditText) findViewById(R.id.messageField)).getText().toString();
        int fps = Integer.parseInt(((EditText) findViewById(R.id.fpsField)).getText().toString());
        Mode morseMode = ((RadioButton) findViewById(R.id.flashOffRadio)).isChecked() ? Mode.DOT_IS_OFF : Mode.DOT_IS_SHORT_FLASH;

        msgSender.sendMessage(message, fps, morseMode, torch);
    }


    // This can be used to set maximum speed camera can be turned on
    public void setMaxSpeed(View view) {
        ((EditText) findViewById(R.id.fpsField)).setText(Integer.toString(torch.getMaxSpeed()));
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

    public native String returnedString();

    static {
        System.loadLibrary("jni_test");
    }

    public void initMessageField() {
        EditText messageField = (EditText) findViewById(R.id.messageField);
        messageField.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                messageChanged();
            }

        });
    }

}
