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
import android.util.Log;
import android.util.Size;
import android.view.View;

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

    Handler uiHandler;

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        uiHandler = new Handler(Looper.getMainLooper());
    }

    public void setTexts(ArrayList<DrawText> texts) {
        mDrawTexts = texts;
        mPaint.setTextSize(72);
    }

    // original rectangles, in processed image coordinates
    private ArrayList<Rect> mImageRectangles;

    // mImageRectangles mapped through a transformation matrix.
    // these are the rectangles that will actually be drawn by this view.
    private ArrayList<RectF> mViewRectangles;

    private ArrayList<DrawText> mDrawTexts;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        List<RectF> rectangles = mViewRectangles;
        if (rectangles != null && !rectangles.isEmpty()) {

            for (RectF r : rectangles) {
                if (!canvas.quickReject(r, Canvas.EdgeType.BW));
                    drawRectOutline(canvas, r, LINE_WIDTH_DP * mDensity, mPaint);
            }
        }

        if (null != mDrawTexts) {
            for(DrawText tx : mDrawTexts) {
                canvas.drawText("TEST-------------------------------------------", (float)50, (float)50, mPaint);
                canvas.drawText(tx.str, (float)tx.x, (float)tx.y, mPaint);
                Log.d("asdasd", tx.str + " " + tx.x + ", " + tx.y);
            }
        }
    }

    /**
     * Sets the transformation matrix of this view, so that rectangles can be drawn correctly.
     * Note: this method assumes that the size of this view (rectlayout) is the same as the corresponding texture view.
     *
     * @param sensorSize: the maximum image size produces by the sensor (both imageSize and viewSize fit into the sensor's aspect ratio)
     * @param imageSize: the image size which should be mapped to view (the rectangles are in this coordinate system)
     * @param previewSize: the current size of the camera stream preview
     */
    public void setToViewTransform(Size sensorSize, Size imageSize, Size previewSize) {

        Matrix m = new Matrix();

        float scale_x = (float)imageSize.getWidth() / getHeight();
        float scale_y = (float)imageSize.getHeight() / getWidth();
        Log.d("asd", getWidth() + "x" + getHeight());
        m.postScale(1.0f / scale_x, 1.0f / scale_y);
        //this.mMatrixImageToView.set(m);
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
                //this.mMatrixImageToView.mapRect(dest);
                mapped.add(dest);
            }

            this.mViewRectangles = mapped;
        }

        invalidateOnUIThread();
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
