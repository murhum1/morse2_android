package morse.morseapp;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.Parameters.*;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Juho on 9.10.2015.
 */
public class Torch {
    static Camera cam;
    static Camera.Parameters params;

    public void init() {
        // Initialize camera
        cam = Camera.open();
        params = cam.getParameters();
        cam.startPreview();
    }

    public void clean() {
        cam.stopPreview();
        cam.release();
    }
    public void turnOff() {
        params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        cam.setParameters(params);

    }

    public void turnOn() {
        try {

            String cameraParam = getFlashOnSetting();
            if (cameraParam == null)
                return;

            params.setFlashMode(cameraParam);
            cam.setParameters(params);

        } catch (Exception e) {
            Log.e(MainActivity.APP_TAG, "Error turning on torch: " + e.toString());
        }
    }


    private String getFlashOnSetting() {
        List<String> flashModes = cam.getParameters().getSupportedFlashModes();

        if (flashModes.contains(Camera.Parameters.FLASH_MODE_TORCH)) {
            return Camera.Parameters.FLASH_MODE_TORCH;
        } else if (flashModes.contains(Camera.Parameters.FLASH_MODE_ON)) {
            return Camera.Parameters.FLASH_MODE_ON;
        } else if (flashModes.contains(Camera.Parameters.FLASH_MODE_AUTO)) {
            return Camera.Parameters.FLASH_MODE_AUTO;
        }

        return null;
    }

    public int getMaxSpeed() {

        List<Long> speeds = new ArrayList<Long>();

        for (int i = 0; i < 5; i++) {
            long startTime = System.currentTimeMillis();
            this.turnOn();
            long endTime = System.currentTimeMillis();
            this.turnOff();

            speeds.add(1000 / (endTime - startTime));
        }

        // just in case..
        if(speeds.isEmpty())
            return 5;

        // Select the minimum FPS. It's the max speed
        return Collections.min(speeds).intValue();
    }
}
