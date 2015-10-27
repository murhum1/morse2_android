package morse.morseapp;

import android.app.Activity;
import android.content.Intent;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.View;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import morse.morseapp.utilities.FastClickPreventer;

public class MainActivity extends Activity {

    static {
        System.loadLibrary("opencv_java3");
    }

    static String APP_TAG = "MORSEAPP";

    Torch torch;
    private JavaCameraView openCvCameraView;

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

        openCvCameraView = (JavaCameraView) findViewById(R.id.open_cv_view);
        openCvCameraView.setVisibility(View.VISIBLE);
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i("TAG", "OpenCV loaded successfully");
                    openCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    private void openSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
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

    @Override
    protected void onResume() {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_10, this, mLoaderCallback);
    }
}
