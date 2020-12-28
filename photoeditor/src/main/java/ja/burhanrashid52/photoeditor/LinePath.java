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

    private final int canvasWidth;
    private final int canvasHeight;

    LinePath(final Path drawPath, final Paint drawPaints){
        mDrawPaint = new Paint(drawPaints);
        mDrawPath = new Path(drawPath);
        this.xOffset = 0f;
        this.yOffset = 0f;
        this.xScale = 1f;
        this.yScale = 1f;
        this.canvasWidth = 1;
        this.canvasHeight = 1;
    }

    LinePath(final Path drawPath, final Paint drawPaints, final float xOffset, final float yOffset, final float xScale, final float yScale, final int canvasWidth, final int canvasHeight) {
        mDrawPaint = new Paint(drawPaints);
        mDrawPath = new Path(drawPath);
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.xScale = xScale;
        this.yScale = yScale;
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;
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

    public int getcanvasWidth() {
        return canvasWidth;
    }

    public int getcanvasHeight() {
        return canvasHeight;
    }
}