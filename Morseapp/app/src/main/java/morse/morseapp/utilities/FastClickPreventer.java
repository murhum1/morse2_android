package morse.morseapp.utilities;

import android.view.View;

/**
 * Rejects clicks that are too close together in time.
 */
public abstract class FastClickPreventer implements View.OnClickListener {
    private final long minimumInterval;
    private long lastClick;

    /**
     * Implement this in your subclass instead of onClick
     * @param v The view that was clicked
     */
    public abstract void onViewClick(View v);

    /**
     * The one and only constructor
     * @param minimumIntervalMs The minimum allowed time between clicks - any click sooner than this after a previous click will be rejected
     */
    public FastClickPreventer(long minimumIntervalMs) {
        this.minimumInterval = minimumIntervalMs;
        lastClick = 0;
    }

    @Override public void onClick(View clickedView) {
        if (System.currentTimeMillis() - lastClick < minimumInterval) return;

        lastClick = System.currentTimeMillis();
        onViewClick(clickedView);
    }
}
