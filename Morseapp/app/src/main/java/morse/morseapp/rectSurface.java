package morse.morseapp;

import android.content.Context;
import android.graphics.Canvas;
import android.opengl.GLSurfaceView;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by Markus on 27.11.2015.
 */
public class rectSurface extends GLSurfaceView implements SurfaceHolder.Callback {

    rectRenderer renderer;

    public rectSurface(Context context) {
        super(context);
        setEGLContextClientVersion(2);
        setWillNotDraw(false);
        getHolder().addCallback(this);
        renderer = new rectRenderer();
        setRenderer(renderer);

        //setRenderMode(this.RENDERMODE_WHEN_DIRTY);
    }

    @Override
    public void onDraw(Canvas canvas) {
        requestRender();
        renderer.render();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
