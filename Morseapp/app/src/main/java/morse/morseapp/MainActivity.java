package morse.morseapp;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.ImageReader;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.util.Size;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import java.util.concurrent.ArrayBlockingQueue;

import morse.morseapp.utilities.FastClickPreventer;

public class MainActivity extends Activity {

    static String APP_TAG = "MORSEAPP";

    CameraAndFlashHandler cameraAndFlashHandler;
    private MessageSender msgSender;
    private CameraManager cameraManager;
    private Surface cameraSurface;
    private String cameraId;
    private ImageReader imageReader;

    private LinearLayout layout;
    rectSurface rectSurface;
    SurfaceView cameraSurfaceView;

    private ArrayBlockingQueue imageQueue;
    private ArrayBlockingQueue anotherQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FrameLayout layout = (FrameLayout) findViewById(R.id.frameLayout);

        rectSurface = new rectSurface(this);
        rectSurface.getHolder().setFormat(PixelFormat.TRANSPARENT);
        setContentView(rectSurface, new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT ));

        cameraSurfaceView = new SurfaceView(this);
        cameraSurfaceView.setVisibility(View.GONE);
        addContentView(cameraSurfaceView, new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT ));

        msgSender = new MessageSender();

        // assign click listener to the cog button (to open the settings!)
        /*findViewById(R.id.button_open_settings).setOnClickListener(new FastClickPreventer(1000) {
            @Override
            public void onViewClick(View v) {
                openSettings();
            }
        });

        findViewById(R.id.button_send_message).setOnClickListener(new FastClickPreventer(1000) {
            @Override
            public void onViewClick(View v) {
                EditText messageField = (EditText) findViewById(R.id.edit_text_message);
                int fps = 20;
                msgSender.sendMessage(messageField.getText().toString(), 20, MessageSender.Mode.DOT_IS_SHORT_FLASH, cameraAndFlashHandler);
            }
        });*/

        // Queues for storing produced / to-be-consumed data
        imageQueue = new ArrayBlockingQueue(256);
        anotherQueue = new ArrayBlockingQueue(256);

        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            String[] cameraIdList = cameraManager.getCameraIdList();
            cameraId = cameraIdList[0];
            SurfaceHolder cameraSurfaceHolder = cameraSurfaceView.getHolder();
            cameraSurfaceView.setVisibility(View.VISIBLE);

            cameraSurfaceHolder.addCallback(new SurfaceHolderCallback());

        } catch (CameraAccessException e) {
            e.printStackTrace();
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

    Surface imageReaderSurface;

    public void initCamera(SurfaceHolder cameraSurfaceHolder) {
        CameraCharacteristics characteristics = null;
        try {
            cameraSurface = cameraSurfaceHolder.getSurface();
//            rectHolder.setFormat(PixelFormat.TRANSPARENT);

            characteristics = cameraManager.getCameraCharacteristics(cameraId);
            StreamConfigurationMap configs = characteristics.get(
                    CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            Log.i("camera surface id:", "" + R.id.cameraSurface);
            int[] outputFormats = configs.getOutputFormats();
            Size[] outputSizes = configs.getOutputSizes(outputFormats[0]);

            imageReader = ImageReader.newInstance(300, 400, ImageFormat.YUV_420_888, 25);
            imageReaderSurface = imageReader.getSurface();

//            Canvas cameraCanvas = cameraSurfaceHolder.lockCanvas();

            cameraSurfaceHolder.setFixedSize(300, 400);
//            cameraSurfaceHolder.unlockCanvasAndPost(cameraCanvas);
            cameraAndFlashHandler = new CameraAndFlashHandler(rectSurface, cameraManager, cameraSurface, imageReaderSurface, cameraId, imageReader, imageQueue);

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void openSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }


    static {
        System.loadLibrary("jniprocess");
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
