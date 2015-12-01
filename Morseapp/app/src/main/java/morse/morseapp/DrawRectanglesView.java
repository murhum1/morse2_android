package morse.morseapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Size;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * View that enables drawing of rectangles "onto" an image stream.
 * Notice that this view will have to have the same exact dimensions as its TextureView counterpart.
 *
 * [current implementation in fragment_camera.xml]
 */
public class DrawRectanglesView extends View {
    public DrawRectanglesView(Context context) {
        super(context);
        mMatrixImageToView = new Matrix();
        mPaint = new Paint();
        mDensity = context.getResources().getDisplayMetrics().density;
    }

    public DrawRectanglesView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mMatrixImageToView = new Matrix();
        mPaint = new Paint();
        mDensity = context.getResources().getDisplayMetrics().density;
    }

    public DrawRectanglesView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mMatrixImageToView = new Matrix();
        mPaint = new Paint();
        mDensity = context.getResources().getDisplayMetrics().density;
    }

    private final float LINE_WIDTH_DP = 4F;

    private final Matrix mMatrixImageToView;
    private final Paint mPaint;
    private final float mDensity;
    private ArrayList<DrawText> mDrawTexts = new ArrayList<>();

    Handler uiHandler;

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        uiHandler = new Handler(Looper.getMainLooper());
    }

    // original rectangles, in processed image coordinates
    private ArrayList<Rect> mImageRectangles;

    // mImageRectangles mapped through a transformation matrix.
    // these are the rectangles that will actually be drawn by this view.
    private ArrayList<RectF> mViewRectangles;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        List<RectF> rectangles = mViewRectangles;
        if (null != rectangles && !rectangles.isEmpty()) {

            for (RectF r : rectangles) {
                if (!canvas.quickReject(r, Canvas.EdgeType.BW))
                    drawRectOutline(canvas, r, LINE_WIDTH_DP * mDensity, mPaint);
            }
        }
        if (null != mDrawTexts) {
            for(DrawText tx : mDrawTexts) {
                canvas.drawText("TEST-------------------------------------------", (float)50, (float)50, mPaint);
                canvas.drawText(tx.str, (float) tx.x, (float) tx.y, mPaint);
            }
        }
    }

    public void setTexts(ArrayList<DrawText> texts) {
        mDrawTexts = texts;
        mapTexts();
        mPaint.setTextSize(72);
    }



    /**
     * Sets the transformation matrix of this view, so that rectangles can be drawn correctly.
     * Note: this method assumes that the size of this view (rectlayout) is the same as the corresponding texture view.
     *
     * @param cropRegion: the crop region of this stream, to which all sub-streams are center-cropped, as specified by:
     *                    http://developer.android.com/reference/android/hardware/camera2/CaptureRequest.html#SCALER_CROP_REGION
     * @param imageSize: the image size which should be mapped to view (the rectangles are in this coordinate system)
     * @param previewSize: the current size of the camera stream preview
     */
    public void setToViewTransform(Size cropRegion, Size imageSize, Size previewSize, int sensorRotation) {
        /**
         * 1. Calculate the transformation matrix from the image to the preview.
         *    The streams are center-cropped (cropped and move to the center of the crop region)
         *    (Eg if the sensor is 4:3, and our previewSize is 1920x1080 (16:9), it can't fill the whole sensor space)
         */
        float scale1 = Math.min(cropRegion.getHeight() / (float) imageSize.getHeight(), cropRegion.getWidth() / (float) imageSize.getWidth());
        float scale2 = Math.min(cropRegion.getHeight() / (float) previewSize.getHeight(), cropRegion.getWidth() / (float) previewSize.getWidth());

        float s = scale1 / scale2;

        RectF ir = new RectF(0, 0, imageSize.getWidth() * s, imageSize.getHeight() * s);
        RectF vr = new RectF(0, 0, previewSize.getWidth(), previewSize.getHeight());

        float w1 = ir.width();
        float w2 = vr.width();

        float h1 = ir.height();
        float h2 = vr.height();

        Matrix imageToPreview = new Matrix();
        imageToPreview.postScale(s, s);
        imageToPreview.postTranslate((vr.width() - ir.width()) / 2, (vr.height() - ir.height()) / 2);

        /**
         * 2. Calculate the transform from the preview to the view.
         *    This calculation assumes that the preview stream covers the whole view, and that it is center-aligned.
         */
        boolean swappedDimensions = sensorRotation % 180 != 0;
        int viewWidth = (swappedDimensions) ? this.getHeight() : this.getWidth();
        int viewHeight = (swappedDimensions) ? this.getWidth() : this.getHeight();

        Matrix previewToView = new Matrix();
        float scale3 = Math.max(viewWidth / (float) previewSize.getWidth(), viewHeight / (float) previewSize.getHeight());
        previewToView.postScale(scale3, scale3);
        previewToView.postTranslate((viewWidth - previewSize.getWidth() * scale3) / 2, (viewHeight - previewSize.getHeight() * scale3) / 2);

        Toast.makeText(getContext(), "Rotated: " + sensorRotation, Toast.LENGTH_LONG).show();

        /**
         * 3. Concatenate (matrix multiply) the two matrices to get a full image to view transform.
         */
        Matrix imageToView = new Matrix(imageToPreview);
        imageToView.postConcat(previewToView);

        int rotation = sensorRotation;
        if (rotation != 0) {
            float min = Math.min(viewHeight, viewWidth) / 2f;
            if (rotation % 180 == 90) {
                imageToView.postRotate(90, min, min);
                rotation -= 90;
            }

            if (rotation == 180) {
                imageToView.postRotate(180, this.getWidth() / 2f, this.getHeight() / 2f);
            }
        }

        this.mMatrixImageToView.set(imageToView);
        mapRectangles();
    }

    public void setColor(int color) {
        this.mPaint.setColor(color);
        invalidateOnUIThread();
    }

    /**
     * Before using this method, set the desired transformation!
     * @param rectangles
     */
    public void setRectangles(ArrayList<Rect> rectangles) {
        this.mImageRectangles = rectangles;
        mapRectangles();
    }

    private void mapRectangles() {
        ArrayList<Rect> originals = this.mImageRectangles;
        if (null == originals || originals.isEmpty()) {
            this.mViewRectangles = null;
        } else {
            ArrayList<RectF> mapped = new ArrayList<>();

            for (Rect r : originals) {
                RectF dest = new RectF(r);
                this.mMatrixImageToView.mapRect(dest);
                mapped.add(dest);
            }

            this.mViewRectangles = mapped;
        }

        invalidateOnUIThread();
    }

    private void mapTexts(){

        for (DrawText t : mDrawTexts) {
            float[] p = new float[]{t.x, t.y};
            mMatrixImageToView.mapPoints(p);
            t.x = (int)p[0];
            t.y = (int)p[1];
        }
    }

    private final Runnable invalidator = new Runnable() {
        @Override
        public void run() {
            invalidate();
        }
    };

    /**
     * Invalidate this view (force to redraw) on UI thread.
     */
    private void invalidateOnUIThread() {
        uiHandler.post(invalidator);
    }

    /**
     * UTIL methods
     */

    private static void drawRectOutline(Canvas canvas, RectF rect, float width, Paint paint) {
        float half = width / 2;
        paint.setStrokeWidth(width);
        canvas.drawLine(rect.left, rect.top - half, rect.left, rect.bottom + half, paint);
        canvas.drawLine(rect.left + half, rect.top, rect.right - half, rect.top, paint);
        canvas.drawLine(rect.left + half, rect.bottom, rect.right - half, rect.bottom, paint);
        canvas.drawLine(rect.right, rect.top - half, rect.right, rect.bottom + half, paint);
    }
}
