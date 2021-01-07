package ja.burhanrashid52.photoeditor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

import androidx.annotation.ColorInt;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.Stack;

/**
 * <p>
 * This is custom drawing view used to do painting on user touch events it it will paint on canvas
 * as per attributes provided to the paint
 * </p>
 *
 * @author <a href="https://github.com/burhanrashid52">Burhanuddin Rashid</a>
 * @version 0.1.1
 * @since 12/1/18
 */
public class BrushDrawingView extends View {

    static final float DEFAULT_BRUSH_SIZE = 25.0f;
    static final float DEFAULT_ERASER_SIZE = 50.0f;
    static final int DEFAULT_OPACITY = 255;

    private float mBrushSize = DEFAULT_BRUSH_SIZE;
    private float mBrushEraserSize = DEFAULT_ERASER_SIZE;
    private int mOpacity = DEFAULT_OPACITY;

    private final Stack<LinePath> mDrawnPaths = new Stack<>();
    private final Stack<LinePath> mRedoPaths = new Stack<>();
    private final Paint mDrawPaint = new Paint();

    private Canvas mDrawCanvas;
    private boolean mBrushDrawMode;

    private Path mPath;
    private float mTouchX, mTouchY;
    private static final float TOUCH_TOLERANCE = 4;

    private float xPixelScale = 1f;
    private float yPixelScale = 1f;
    private float topXRatioPositionInOriginal = 1f;
    private float topYRatioPositionInOriginal = 1f;
    private final Matrix matrix = new Matrix();

    private BrushViewChangeListener mBrushViewChangeListener;

    public BrushDrawingView(Context context) {

        this(context, null);
        Log.d("M_BrushDrawingView", "0BrushDrawingView: create view canvas w: " + getWidth() + " h: " + getHeight());
    }

    public BrushDrawingView(Context context, AttributeSet attrs) {

        this(context, attrs, 0);
        Log.d("M_BrushDrawingView", "1BrushDrawingView: create view canvas w: " + getWidth() + " h: " + getHeight());
    }

    public BrushDrawingView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Log.d("M_BrushDrawingView", "2BrushDrawingView: create view canvas w: " + getWidth() + " h: " + getHeight());
        setupBrushDrawing();
    }

    private void setupBrushDrawing() {
        //Caution: This line is to disable hardware acceleration to make eraser feature work properly
        setLayerType(LAYER_TYPE_HARDWARE, null);
        mDrawPaint.setColor(Color.BLACK);
        setupPathAndPaint();
        setVisibility(View.GONE);
    }

    private void setupPathAndPaint() {
        mPath = new Path();
        mDrawPaint.setAntiAlias(true);
        mDrawPaint.setDither(true);
        mDrawPaint.setStyle(Paint.Style.STROKE);
        mDrawPaint.setStrokeJoin(Paint.Join.ROUND);
        mDrawPaint.setStrokeCap(Paint.Cap.ROUND);
        mDrawPaint.setStrokeWidth(mBrushSize);
        mDrawPaint.setAlpha(mOpacity);
        mDrawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
    }

    private void refreshBrushDrawing() {
        mBrushDrawMode = true;
        setupPathAndPaint();
    }

    public void setDrawableOffset(float xPixelScale, float yPixelScale, float topXRatioPositionInOriginal, float topYRatioPositionInOriginal) {

        this.xPixelScale = xPixelScale;
        this.yPixelScale = yPixelScale;
        this.topXRatioPositionInOriginal = topXRatioPositionInOriginal;
        this.topYRatioPositionInOriginal = topYRatioPositionInOriginal;
        invalidate();
    }

    void brushEraser() {
        mBrushDrawMode = true;
        mDrawPaint.setStrokeWidth(mBrushEraserSize);
        mDrawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }

    void setBrushDrawingMode(boolean brushDrawMode) {
        this.mBrushDrawMode = brushDrawMode;
        if (brushDrawMode) {
            this.setVisibility(View.VISIBLE);
            refreshBrushDrawing();
        }
    }

    void setOpacity(@IntRange(from = 0, to = 255) int opacity) {
        this.mOpacity = opacity;
        setBrushDrawingMode(true);
    }

    int getOpacity() {
        return mOpacity;
    }

    boolean getBrushDrawingMode() {
        return mBrushDrawMode;
    }

    void setBrushSize(float size) {
        mBrushSize = size;
        setBrushDrawingMode(true);
    }

    void setBrushColor(@ColorInt int color) {
        mDrawPaint.setColor(color);
        setBrushDrawingMode(true);
    }

    void setBrushEraserSize(float brushEraserSize) {
        this.mBrushEraserSize = brushEraserSize;
        setBrushDrawingMode(true);
    }

    void setBrushEraserColor(@ColorInt int color) {
        mDrawPaint.setColor(color);
        setBrushDrawingMode(true);
    }

    float getEraserSize() {
        return mBrushEraserSize;
    }

    float getBrushSize() {
        return mBrushSize;
    }

    int getBrushColor() {
        return mDrawPaint.getColor();
    }

    void clearAll() {
        mDrawnPaths.clear();
        mRedoPaths.clear();
        if (mDrawCanvas != null) {
            mDrawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        }
        invalidate();
    }

    void setBrushViewChangeListener(BrushViewChangeListener brushViewChangeListener) {
        mBrushViewChangeListener = brushViewChangeListener;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Bitmap canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mDrawCanvas = new Canvas(canvasBitmap);
        Log.d("M_BrushDrawingView", "onSizeChanged: ");
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.d("M_Size", "onDraw: canvas w: " + getWidth() + " h: " + getHeight() + " rootHeight: " + getRootView().getHeight() + " rootWidth: " + getRootView().getWidth());

        ViewGroup parent = (ViewGroup) getParent();
//        Log.d("M_BrushDrawingView", "onDraw: parent W&H:[" + parent.getWidth() + ", " + parent.getHeight() + "]");
//        Log.d("M_D_BrushDrawingView", "onDraw: canvas W&H: [" + canvas.getWidth() + ", " + canvas.getHeight() + "] parentSize = [" + getRootView().getWidth() + ", " + getRootView().getHeight() + "]");
//        int offset = getHeight() - ((ViewGroup) getParent()).getHeight();
//        matrix.reset();
//        matrix.setTranslate(-xOffset, -yOffset);
//        matrix.setScale(xScale, yScale, getWidth()/2, getHeight()/2);


        for (LinePath linePath : mDrawnPaths) {
            Path path = linePath.getDrawPath();
            Paint newPaint = new Paint(linePath.getDrawPaint());
            float currentStrokeWidth = newPaint.getStrokeWidth();
            matrix.reset();
            if (xPixelScale > 1f || yPixelScale > 1f) {
                if (linePath.getxPixelScale() == 1f && linePath.getyPixelScale() == 1f) {

                    float moreX = (float) getWidth() / linePath.getcanvasWidth();
                    float moreY = (float) getHeight() / linePath.getcanvasHeight();
                    float canvasXRatio = moreX * xPixelScale;
                    float canvasYratio = moreY * yPixelScale;
                    float currentXOffset = linePath.getcanvasWidth() * topXRatioPositionInOriginal * -1;
                    float currentYOffset = linePath.getcanvasHeight() * topYRatioPositionInOriginal * -1;

                    Log.d("M_BrushDrawingView", "            w,h:[" + getWidth() + ", " + getHeight() + "] drawW,H: [" + linePath.getcanvasWidth() + ", " + linePath.getcanvasHeight());
                    Log.d("M_BrushDrawingView", "   topXYRatPos: [" + topXRatioPositionInOriginal + ", " + topYRatioPositionInOriginal + "]");
                    Log.d("M_BrushDrawingView", "        moreXY: [" + moreX + ", " + moreY + "]");
                    Log.d("M_BrushDrawingView", "XY_Pixel_Scale: [" + xPixelScale + ", " + yPixelScale + "]");
                    Log.d("M_BrushDrawingView", " canvasRatioXY: [" + canvasXRatio + ", " + canvasYratio + "]");
                    Log.d("M_BrushDrawingView", "    X_Y_Offset: [" + currentXOffset + ", " + currentYOffset + "]");
                    Log.d("M_BrushDrawingView", "moreX: " + moreX + "moreY: " + moreY);

                    matrix.preTranslate(currentXOffset, currentYOffset);
                    matrix.postScale(canvasXRatio, canvasYratio);
                    path.transform(matrix);
                } else {
                    float moreX = (float) getWidth() / linePath.getcanvasWidth();
                    float moreY = (float) getHeight() / linePath.getcanvasHeight();
                    float canvasXRatio = moreX * xPixelScale/linePath.getxPixelScale();
                    float canvasYratio = moreY * yPixelScale/ linePath.getyPixelScale();
                    float currentXOffset = linePath.getcanvasWidth() * Math.abs(topXRatioPositionInOriginal - linePath.getTopXRatioPositionInOriginal()) * -1;
                    float currentYOffset = linePath.getcanvasHeight() * Math.abs(topYRatioPositionInOriginal - linePath.gettopYRatioPositionInOriginal()) * -1;
                    Log.d("M_BrushDrawingView", "onDraw: ");
                    matrix.postScale(canvasXRatio, canvasYratio);
                    matrix.preTranslate(currentXOffset, currentYOffset);
                    path.transform(matrix);
                }
                double currentSize = currentStrokeWidth * Math.sqrt(xPixelScale * yPixelScale);
                newPaint.setStrokeWidth((float) currentSize);
                Log.d("M_BrushDrawingView_SIZE", "onDraw: parentSize: " + linePath.getDrawPaint().getStrokeWidth() + ", currentSIze: " + currentSize);
                canvas.drawPath(path, newPaint);
            } else {
                canvas.drawPath(path, linePath.getDrawPaint());
            }

        }
        Log.d("M_BrushDrawingView", "||||||||||||||||||||||||||||||||||||||");
        canvas.drawPath(mPath, mDrawPaint);
    }

    /**
     * Handle touch event to draw paint on canvas i.e brush drawing
     *
     * @param event points having touch info
     * @return true if handling touch events
     */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        if (mBrushDrawMode) {
            float touchRawX = event.getRawX();
            float touchRawY = event.getRawY();
            float touchX = event.getX();
            float touchY = event.getY();

            Log.d("M_BrushDrawingView", "onTouchEvent: [x, rawX]:{" + touchX + "," + touchRawX + "] [y, rawY]:[" + touchY + ", " + touchRawY + "]");
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    Log.d("M_BrushDrawingView", "onTouchEvent_Start: x:" + touchX + " y: " + touchY);
                    touchStart(touchX, touchY);
                    break;
                }
                case MotionEvent.ACTION_MOVE: {
                    Log.d("M_BrushDrawingView", "onTouchEvent_MOVE: x:" + touchX + " y: " + touchY);
                    touchMove(touchX, touchY);
                    break;
                }
                case MotionEvent.ACTION_UP:
                    touchUp();
                    break;
            }
            invalidate();
            return true;
        } else {
            return false;
        }
    }

    boolean undo() {
        if (!mDrawnPaths.empty()) {
            mRedoPaths.push(mDrawnPaths.pop());
            invalidate();
        }
        if (mBrushViewChangeListener != null) {
            mBrushViewChangeListener.onViewRemoved(this);
        }
        return !mDrawnPaths.empty();
    }

    boolean redo() {
        if (!mRedoPaths.empty()) {
            mDrawnPaths.push(mRedoPaths.pop());
            invalidate();
        }

        if (mBrushViewChangeListener != null) {
            mBrushViewChangeListener.onViewAdd(this);
        }
        return !mRedoPaths.empty();
    }


    private void touchStart(float x, float y) {
        mRedoPaths.clear();
        mPath.reset();
        mPath.moveTo(x, y);
        mTouchX = x;
        mTouchY = y;
        if (mBrushViewChangeListener != null) {
            mBrushViewChangeListener.onStartDrawing();
        }
    }

    private void touchMove(float x, float y) {
        float dx = Math.abs(x - mTouchX);
        float dy = Math.abs(y - mTouchY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mTouchX, mTouchY, (x + mTouchX) / 2, (y + mTouchY) / 2);
            mTouchX = x;
            mTouchY = y;
        }
    }

    private void touchUp() {
        mPath.lineTo(mTouchX, mTouchY);
        // Commit the path to our offscreen
        mDrawCanvas.drawPath(mPath, mDrawPaint);
        // kill this so we don't double draw

        mDrawnPaths.push(new LinePath(mPath, mDrawPaint, xPixelScale, yPixelScale, topXRatioPositionInOriginal, topYRatioPositionInOriginal, getWidth(), getHeight()));
        mPath = new Path();
        if (mBrushViewChangeListener != null) {
            mBrushViewChangeListener.onStopDrawing();
            mBrushViewChangeListener.onViewAdd(this);
        }
    }

    @VisibleForTesting
    Paint getDrawingPaint() {
        return mDrawPaint;
    }

    @VisibleForTesting
    Pair<Stack<LinePath>, Stack<LinePath>> getDrawingPath() {
        return new Pair<>(mDrawnPaths, mRedoPaths);
    }
}