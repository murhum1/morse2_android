package morse.morseapp;

import android.content.Context;
import android.graphics.Paint;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.media.Image;
import android.media.ImageReader;
import android.util.Log;

import android.hardware.camera2.params.StreamConfigurationMap;
import android.util.Size;
import android.view.Surface;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by Juho on 9.10.2015.
 */
public class CameraAndFlashHandler {
    private CameraManager cameraManager;
    private String cameraId;
    private Surface cameraSurface;
    private CameraDevice camera;
    private Surface imageReaderSurface;
    private ArrayBlockingQueue imageQueue;
    public CaptureRequest.Builder builderi;
    public CameraCaptureSession mSession;

    public CameraAndFlashHandler(CameraManager cameraManager, Surface cameraSurface, Surface imageReaderSurface, String cameraId, ImageReader imageReader, ArrayBlockingQueue imageQueue) {
        this.cameraManager = cameraManager;
        this.cameraSurface = cameraSurface;
        this.imageReaderSurface = imageReaderSurface;
        this.cameraId = cameraId;
        this.imageQueue = imageQueue;
        ImageReader.OnImageAvailableListener imageAvailableListener = new OnImageAvailableListener();
        imageReader.setOnImageAvailableListener(imageAvailableListener, null);

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
                Log.i("camera surface:", "" + cameraSurface);
                List<Surface> surfaceList = new ArrayList<>();
                surfaceList.add(cameraSurface);
                surfaceList.add(imageReaderSurface);
                camera.createCaptureSession(surfaceList, new CameraCaptureSessionCallback(), null);
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
                mSession = session;
                builderi = camera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                builderi.addTarget(cameraSurface);
                builderi.addTarget(imageReaderSurface);
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

    class OnImageAvailableListener implements ImageReader.OnImageAvailableListener {

        @Override
        public void onImageAvailable(ImageReader reader) {
            Image img = reader.acquireNextImage();
            processImage(img);
            Log.i("Got image!", "" + img);
            img.close();
        }
    }

    private native int[] getLights(byte[] Y, int width, int height);

    private void processImage(Image img) {

        byte[] Y = new byte[img.getHeight() * img.getWidth()];

        img.getPlanes()[0].getBuffer().get(Y);

        int[] lights = getLights(Y, img.getWidth(), img.getHeight());

        Log.i("höhö", "" + lights[0]);

        /*
        try {
            Log.i("Image received", "");

            imageQueue.put(img);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        */
    }

    // Completely turn off the camera
    public void clean() {
        mSession.close();
        camera.close();
        mSession = null;
        camera = null;
    }

    public boolean flashIsOn() {
        return builderi.get(CaptureRequest.FLASH_MODE) == CaptureRequest.FLASH_MODE_TORCH;
    }

    public void turnOff() {
        try {
            builderi.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_OFF);
            mSession.setRepeatingRequest(builderi.build(), null, null);
        } catch (Exception e) {
            Log.e(MainActivity.APP_TAG, "Error turning on the cameraAndFlashHandler: " + e.toString());
        }
    }

    public void turnOn() {
        try {
            builderi.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_TORCH);
            mSession.setRepeatingRequest(builderi.build(), null, null);
        } catch (Exception e) {
            Log.e(MainActivity.APP_TAG, "Error turning on the cameraAndFlashHandler: " + e.toString());
        }
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
