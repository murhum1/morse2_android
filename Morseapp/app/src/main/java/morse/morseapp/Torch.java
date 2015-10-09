package morse.morseapp;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.util.Log;

import android.hardware.camera2.params.StreamConfigurationMap;
import android.util.Size;
import android.view.Surface;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Juho on 9.10.2015.
 */
public class Torch {
    private Camera cam;
    private Camera.Parameters params;
    private CameraManager cameraManager;
    private String cameraId;
    private Surface cameraSurface;
    private CameraDevice camera;


    public Torch(CameraManager cameraManager, Surface cameraSurface, String cameraId) {
        this.cameraManager = cameraManager;
        this.cameraSurface = cameraSurface;
        this.cameraId = cameraId;

        try {
            cameraManager.openCamera(cameraId, new CameraCallback(), null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    class CameraCallback extends CameraDevice.StateCallback {

        @Override
        public void onOpened(CameraDevice openedCamera) {
            camera = openedCamera;
            try {
                Log.i("camera surface:", ""+ cameraSurface);
                camera.createCaptureSession(Collections.singletonList(cameraSurface), new CameraCaptureSessionCallback(), null);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onDisconnected(CameraDevice camera) {

        }

        @Override
        public void onError(CameraDevice camera, int error) {

        }
    }

    class CameraCaptureSessionCallback extends CameraCaptureSession.StateCallback {

        @Override
        public void onConfigured(CameraCaptureSession session) {
            try {
                Log.i("WE'RE HERE!", "HOLY SHIT");
                CaptureRequest.Builder builderi = camera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                builderi.addTarget(cameraSurface);
                CaptureRequest requesti = builderi.build();
                session.setRepeatingRequest(requesti, null, null);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onConfigureFailed(CameraCaptureSession session) {

        }
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
            long endTime = System.currentTimeMillis();
            this.turnOff();

            speeds.add(1000 / (endTime - startTime));
        }

        // just in case..
        if (speeds.isEmpty())
            return 5;

        // Select the minimum FPS. It's the max speed
        return Collections.min(speeds).intValue();
    }
}
