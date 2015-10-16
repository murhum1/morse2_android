package morse.morseapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Bundle;
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


import morse.morseapp.MessageSender.Mode;

public class MainActivity extends Activity {

    static String APP_TAG = "MORSEAPP";

    Torch torch;

    private MessageSender msgSender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initMessageField();

        msgSender = new MessageSender();

    }

    public void initCamera(View view) {

        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        String cameraId;
        try {
            String[] cameraIdList = cameraManager.getCameraIdList();
            cameraId = cameraIdList[0];
            CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);
            StreamConfigurationMap configs = characteristics.get(
                    CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            Log.i("camera surface id:", "" + R.id.cameraSurface);
            Size[] outputSizes = configs.getOutputSizes(R.id.cameraSurface);
            Log.i("outputSizes", "" + outputSizes);
            SurfaceView cameraSurfaceView = (SurfaceView) findViewById(R.id.cameraSurface);
            SurfaceHolder cameraSurfaceHolder = cameraSurfaceView.getHolder();

            cameraSurfaceHolder.setFixedSize(100, 100);

            Surface cameraSurface = cameraSurfaceHolder.getSurface();

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
    protected void onPause() {
        super.onDestroy();
        torch.clean();
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
