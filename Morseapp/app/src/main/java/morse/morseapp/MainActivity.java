package morse.morseapp;

import android.app.Activity;
import android.content.Intent;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.ImageReader;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.util.Size;
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

    private ArrayBlockingQueue imageQueue;
    private ArrayBlockingQueue anotherQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // assign click listener to the cog button (to open the settings!)
        findViewById(R.id.button_open_settings).setOnClickListener(new FastClickPreventer(1000) {
            @Override
            public void onViewClick(View v) {
                openSettings();
            }
        });

        // Queues for storing produced / to-be-consumed data
        imageQueue = new ArrayBlockingQueue(256);
        anotherQueue = new ArrayBlockingQueue(256);

        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            String[] cameraIdList = cameraManager.getCameraIdList();
            cameraId = cameraIdList[0];

            SurfaceView cameraSurfaceView = (SurfaceView) findViewById(R.id.cameraSurface);
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

            imageReader = ImageReader.newInstance(outputSizes[0].getWidth(), outputSizes[0].getHeight(), ImageFormat.YUV_420_888, 25);
            Surface imageReaderSurface = imageReader.getSurface();

            Canvas cameraCanvas = cameraSurfaceHolder.lockCanvas();

            cameraSurfaceHolder.setFixedSize(outputSizes[0].getWidth(), outputSizes[0].getHeight());
            cameraSurfaceHolder.unlockCanvasAndPost(cameraCanvas);
            cameraAndFlashHandler = new CameraAndFlashHandler(cameraManager, cameraSurface, imageReaderSurface, cameraId, imageReader, imageQueue);

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void openSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
