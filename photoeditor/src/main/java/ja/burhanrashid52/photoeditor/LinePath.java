package ja.burhanrashid52.photoeditor;

import android.graphics.Paint;
import android.graphics.Path;

class LinePath {
    private final Paint mDrawPaint;
    private final Path mDrawPath;
    private final float xOffset;
    private final float yOffset;
    private final float xScale;
    private final float yScale;

    LinePath(final Path drawPath, final Paint drawPaints){
        mDrawPaint = new Paint(drawPaints);
        mDrawPath = new Path(drawPath);
        this.xOffset = 0f;
        this.yOffset = 0f;
        this.xScale = 1f;
        this.yScale = 1f;
    }

    LinePath(final Path drawPath, final Paint drawPaints, final float xOffset, final float yOffset, final float xScale, final float yScale) {
        mDrawPaint = new Paint(drawPaints);
        mDrawPath = new Path(drawPath);
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.xScale = xScale;
        this.yScale = yScale;
    }

    Paint getDrawPaint() {
        return mDrawPaint;
    }

    Path getDrawPath() {
        return new Path(mDrawPath);
    }

    public float getxOffset() {
        return xOffset;
    }

    public float getyOffset() {
        return yOffset;
    }

    public float getxScale() {
        return xScale;
    }

    public float getyScale() {
        return yScale;
    }
}