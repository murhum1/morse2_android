package morse.morseapp;

import android.hardware.Camera;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Juho on 9.10.2015.
 */
public class Torch {
    MainActivity mainActivity;

    private Camera cam;
    private Camera.Parameters params;


    private int turnOnDelay = 0;
    private int turnOffDelay = 0;

    public Torch(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }
    // Starts the camera so it can be turned on
    public void init() {
        // Initialize camera
        cam = Camera.open();
        params = cam.getParameters();
        cam.startPreview();
    }

    // Completely turn off the camera
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
            Log.e(MainActivity.APP_TAG, "Error turning on the torch: " + e.toString());
            mainActivity.displayError(e);
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

    // This can be used to get maximum speed the camera is able to flash its light
    public int getMaxSpeed() {

        List<Long> speeds = new ArrayList<Long>();

        for (int i = 0; i < 5; i++) {
            long startTime = System.currentTimeMillis();
            this.turnOn();
            this.turnOff();
            long endTime = System.currentTimeMillis();

            speeds.add(1000 / (endTime - startTime));
        }

        // just in case..
        if (speeds.isEmpty())
            return 5;

        // Select the minimum FPS. It's the max speed
        return Collections.min(speeds).intValue();
    }


    public void updateTurnOnDelay() {
        List<Long> speeds = new ArrayList<Long>();

        for (int i = 0; i < 5; i++) {
            long startTime = System.currentTimeMillis();
            this.turnOn();
            long endTime = System.currentTimeMillis();
            this.turnOff();

            speeds.add(endTime - startTime);
        }

        // Select the minimum FPS. It's the max speed
        this.turnOnDelay = Collections.min(speeds).intValue();
    }

    public void updateTurnOffDelay() {
        List<Long> speeds = new ArrayList<Long>();

        for (int i = 0; i < 5; i++) {
            this.turnOn();
            long startTime = System.currentTimeMillis();
            this.turnOff();
            long endTime = System.currentTimeMillis();
            speeds.add(endTime - startTime);
        }

        // Select the minimum FPS. It's the max speed
        this.turnOffDelay = Collections.min(speeds).intValue();
    }
    public int getTurnOnDelay() {
        if(this.turnOnDelay == 0)
            updateTurnOnDelay();
        return this.turnOnDelay;
    }

    public int getTurnOffDelay() {
        if(this.turnOffDelay == 0)
            updateTurnOffDelay();
        return this.turnOffDelay;
    }
}
