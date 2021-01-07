package ja.burhanrashid52.photoeditor;

import android.graphics.Paint;
import android.graphics.Path;

class LinePath {
    private final Paint mDrawPaint;
    private final Path mDrawPath;
    private final float xPixelScale;
    private final float yPixelScale;
    private final float topXRatioPositionInOriginal;
    private final float topYRatioPositionInOriginal;

    private final int canvasWidth;
    private final int canvasHeight;

    LinePath(final Path drawPath, final Paint drawPaints){
        mDrawPaint = new Paint(drawPaints);
        mDrawPath = new Path(drawPath);
        this.xPixelScale = 1f;
        this.yPixelScale = 1f;
        this.topXRatioPositionInOriginal = 0f;
        this.topYRatioPositionInOriginal = 0f;
        this.canvasWidth = 1;
        this.canvasHeight = 1;
    }

    LinePath(final Path drawPath, final Paint drawPaints, final float xPixelScale, final float yPixelScale, final float topXRatioPositionInOriginal, final float topYRatioPositionInOriginal, final int canvasWidth, final int canvasHeight) {
        mDrawPaint = new Paint(drawPaints);
        mDrawPath = new Path(drawPath);
        this.xPixelScale = xPixelScale;
        this.yPixelScale = yPixelScale;
        this.topXRatioPositionInOriginal = topXRatioPositionInOriginal;
        this.topYRatioPositionInOriginal = topYRatioPositionInOriginal;
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;
    }

    Paint getDrawPaint() {
        return mDrawPaint;
    }

    Path getDrawPath() {
        return new Path(mDrawPath);
    }

    public float getxPixelScale() {
        return xPixelScale;
    }

    public float getyPixelScale() {
        return yPixelScale;
    }

    public float getTopXRatioPositionInOriginal() {
        return topXRatioPositionInOriginal;
    }

    public float gettopYRatioPositionInOriginal() {
        return topYRatioPositionInOriginal;
    }

    public int getcanvasWidth() {
        return canvasWidth;
    }

    public int getcanvasHeight() {
        return canvasHeight;
    }
}