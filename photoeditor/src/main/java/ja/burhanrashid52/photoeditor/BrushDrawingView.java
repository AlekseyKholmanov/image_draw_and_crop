package ja.burhanrashid52.photoeditor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
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

import static android.content.ContentValues.TAG;

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

    private int xOffset = 0;
    private int yOffset = 0;
    private float xScale = 1f;
    private float yScale = 1f;
    private float canvasXOffset = 1f;
    private float canvasYOffset = 1f;
    private final Matrix matrix = new Matrix();

    private BrushViewChangeListener mBrushViewChangeListener;

    public BrushDrawingView(Context context) {

        this(context, null);
        Log.d("M_BrushDrawingView", "BrushDrawingView: create view");
    }

    public BrushDrawingView(Context context, AttributeSet attrs) {

        this(context, attrs, 0);
        Log.d("M_BrushDrawingView", "BrushDrawingView: create view");
    }

    public BrushDrawingView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Log.d("M_BrushDrawingView", "BrushDrawingView: create view");
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

    public void setDrawableOffset(int x, int y, float xScale, float yScale, float canvasXOffset, float canvasYOffset) {
        xOffset = x;
        yOffset = y;
        this.xScale = xScale;
        this.yScale = yScale;
        this.canvasXOffset = canvasXOffset;
        this.canvasYOffset = canvasYOffset;
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
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.d("M_BrushDrawingView", "onDraw: canvas w: " + getWidth() + " h: " + getHeight());
        Log.d("M_BrushDrawingView", "onDraw: canvas x offset %: " + canvasXOffset + " y in %: " + canvasYOffset);
        ViewGroup parent = (ViewGroup) getParent();
//        Log.d("M_BrushDrawingView", "onDraw: parent W&H:[" + parent.getWidth() + ", " + parent.getHeight() + "]");
//        Log.d("M_D_BrushDrawingView", "onDraw: canvas W&H: [" + canvas.getWidth() + ", " + canvas.getHeight() + "] parentSize = [" + getRootView().getWidth() + ", " + getRootView().getHeight() + "]");
//        int offset = getHeight() - ((ViewGroup) getParent()).getHeight();
//        matrix.reset();
//        matrix.setTranslate(-xOffset, -yOffset);
//        matrix.setScale(xScale, yScale, getWidth()/2, getHeight()/2);
        int i = 0;
        Log.d("M_BrushDrawingView", "onDraw: ofssetX: " + xOffset + " yOffset: " + yOffset + " xScale: " + xScale + " yScale: " + yScale + "canvasXY [x,y]: " + getWidth() + ", " + getHeight());
        for (LinePath linePath : mDrawnPaths) {
            Path path = linePath.getDrawPath();
            float canvasXPixelOffset = (xOffset != 0f) ? -1 * (getWidth() * canvasXOffset * xScale) : 0f;
            float canvasYPixelOffset = (yOffset != 0f) ? -1 * (getHeight() * canvasYOffset * yScale) : 0f;
            Log.d("M_BrushDrawingView", "onDraw: canvas calculated offset XY :[" + canvasXPixelOffset + ", " + canvasYPixelOffset + "]");
//
//
            if ((xScale > 1f || yScale > 1f) && linePath.getxScale() == 1f && linePath.getyScale() == 1f) {
                matrix.reset();
                matrix.preScale(xScale, yScale);
                matrix.postTranslate(canvasXPixelOffset, canvasYPixelOffset);
//                matrix.postScale(xScale, yScale);
//                matrix.setScale(xScale, yScale);
                path.transform(matrix);
            }
            canvas.drawPath(path, linePath.getDrawPaint());
        }
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

    private void readPath(Path path, int index) {
        PathMeasure pm = new PathMeasure(path, false);
        //coordinates will be here
        float aCoordinates[] = {0f, 0f};
        pm.getPosTan(pm.getLength() * 0.5f, aCoordinates, null);
        Log.d("M_BrushDrawingView", "readPath for index:" + index + "| x: " + aCoordinates[0] + " y: " + aCoordinates[1]);
    }

    private void touchUp() {
        mPath.lineTo(mTouchX, mTouchY);
        // Commit the path to our offscreen
        mDrawCanvas.drawPath(mPath, mDrawPaint);
        // kill this so we don't double draw
        mDrawnPaths.push(new LinePath(mPath, mDrawPaint, xOffset, yOffset, xScale, yScale));
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