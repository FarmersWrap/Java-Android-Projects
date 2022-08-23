package ca.uwaterloo.cs349.pdfreader;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.*;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Stack;

@SuppressLint("AppCompatCustomView")
public class PDFimage extends ImageView {
    Context c;

    // This part of the code is modified from the website:
    // https://stackoverflow.com/questions/12169905/zoom-and-panning-imageview-android
    private static final int INVALID_POINTER_ID = -1;

    private float mPosX;
    private float mPosY;

    private float mLastTouchX;
    private float mLastTouchY;
    private float mLastGestureX;
    private float mLastGestureY;
    private int mActivePointerId = INVALID_POINTER_ID;

    private ScaleGestureDetector mScaleDetector;
    private float mScaleFactor = 1.f;




    final String LOGNAME = "pdf_image";
    Region clip = new Region(0, 0, 2000, 4000);

    int mode = MainActivity.PEN;

    public void setMode(int mode) {
        this.mode = mode;
    }

    // drawing path
    Path path = null;
    ArrayList<Path> paths = new ArrayList<>();
    ArrayList<Paint> colors = new ArrayList();

    Stack<Path> pathStack = new Stack<Path>();
    Stack<Paint> paintStack = new Stack<Paint>();
    Stack<Job> redoStack = new Stack<Job>();
    Stack<Job> undoStack = new Stack<Job>();

    public ArrayList<Path> getPaths() {
        return paths;
    }
    public void setPaths(ArrayList<Path> paths) {
        this.paths = paths;
    }
    public Stack<Job> getRedoStack() {
        return redoStack;
    }
    public void setRedoStack(Stack<Job> redoStack) {
        this.redoStack = redoStack;
    }
    public Stack<Job> getUndoStack() {
        return undoStack;
    }
    public void setUndoStack(Stack<Job> undoStack) {
        this.undoStack = undoStack;
    }
    public Stack<Path> getPathStack() {
        return pathStack;
    }
    public void setPathStack(Stack<Path> pathStack) {
        this.pathStack = pathStack;
    }
    public Stack<Paint> getPaintStack() {
        return paintStack;
    }
    public void setPaintStack(Stack<Paint> paintStack) {
        this.paintStack = paintStack;
    }


    // image to display
    Bitmap bitmap;
    Paint p = new Paint(Color.BLUE);

    public ArrayList<Paint> getColors() {
        return colors;
    }

    public void setColors(ArrayList<Paint> colors) {
        this.colors = colors;
    }



    // constructor
    public PDFimage(Context context) {
        super(context);
        c = context;
        mScaleDetector = new ScaleGestureDetector(getContext(), new ScaleListener());
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();

            // Don't let the object get too small or too large.
            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 10.0f));

            invalidate();
            return true;
        }
    }

    // capture touch events (down/move/up) to create a path
    // and use that to create a stroke that we can draw
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (mode) {
            case MainActivity.ERASER:
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Log.d(LOGNAME, "Action down");
                        path = new Path();
                        path.moveTo(event.getX(), event.getY());
                        break;
                    case MotionEvent.ACTION_MOVE:
                        Log.d(LOGNAME, "Action move");
                        path.lineTo(event.getX(), event.getY());
                        break;
                    case MotionEvent.ACTION_UP:
                        Log.d(LOGNAME, "Action up");
                        System.out.println("noPaths: " + paths.size());
                        int counter = 0;
                        for (int i = 0; i < paths.size(); i ++) {

                            // The code below is inspired by the link here:
                            // https://stackoverflow.com/questions/34374921/how-to-detect-if-two-paths-intersect-in-android
                            Region region1 = new Region();
                            region1.setPath(path, clip);
                            Region region2 = new Region();
                            region2.setPath(paths.get(i), clip);
                            System.out.println("im here "+ i);
                            if (!region1.quickReject(region2) && region1.op(region2, Region.Op.INTERSECT)) {
                                System.out.println("Intersect");
                                pathStack.push(paths.get(i));
                                paintStack.push(colors.get(i));
                                paths.remove(i);
                                colors.remove(i);
                                i --;
                                counter ++;
                            }
                            System.out.println("shit " + i);
                        }
                        undoStack.push(new Job(counter, true));
                        break;
                }
                break;
            case MainActivity.PEN:
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Log.d(LOGNAME, "Action down");
                        path = new Path();
                        path.moveTo(event.getX(), event.getY());

                        break;
                    case MotionEvent.ACTION_MOVE:
                        Log.d(LOGNAME, "Action move");
                        path.lineTo(event.getX(), event.getY());
                        break;
                    case MotionEvent.ACTION_UP:
                        Log.d(LOGNAME, "Action up");
                        paths.add(path);
                        colors.add(p);
                        undoStack.push(new Job(1, false));
                        break;
                }
                break;
            case MainActivity.ZOOM:
                // This part of the code is modified from the website:
                // https://stackoverflow.com/questions/12169905/zoom-and-panning-imageview-android
                System.out.println("zooming");
                mScaleDetector.onTouchEvent(event);
                final int action = event.getAction();
                switch (action & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN: {
                        System.out.println("MotionEvent.ACTION_DOWN");
                        if (!mScaleDetector.isInProgress()) {
                            final float x = event.getX();
                            final float y = event.getY();
                            mLastTouchX = x;
                            mLastTouchY = y;
                            mActivePointerId = event.getPointerId(0);
                        }
                        break;
                    }
                    case MotionEvent.ACTION_POINTER_DOWN: {
                        System.out.println("MotionEvent.ACTION_POINTER_1_DOWN");
                        mLastGestureX = mScaleDetector.getFocusX();
                        mLastGestureY = mScaleDetector.getFocusY();
                        break;
                    }
                    case MotionEvent.ACTION_MOVE: {
                        System.out.println("MotionEvent.ACTION_MOVE");
                        // Only move if the ScaleGestureDetector isn't processing a gesture.
                        if (!mScaleDetector.isInProgress()) {
                            final int pointerIndex = event.findPointerIndex(mActivePointerId);
                            final float x = event.getX(pointerIndex);
                            final float y = event.getY(pointerIndex);
                            final float dx = x - mLastTouchX;
                            final float dy = y - mLastTouchY;
                            mPosX += dx;
                            mPosY += dy;
                            invalidate();
                            mLastTouchX = x;
                            mLastTouchY = y;
                        } else {
                            final float gx = mScaleDetector.getFocusX();
                            final float gy = mScaleDetector.getFocusY();
                            final float gdx = gx - mLastGestureX;
                            final float gdy = gy - mLastGestureY;
                            mPosX += gdx;
                            mPosY += gdy;
                            invalidate();
                            mLastGestureX = gx;
                            mLastGestureY = gy;
                        }

                        break;
                    }
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL: {
                        System.out.println("MotionEvent.ACTION_UP, ACTION_CANCEL");
                        mActivePointerId = INVALID_POINTER_ID;
                        break;
                    }
                    case MotionEvent.ACTION_POINTER_UP: {
                        System.out.println("MotionEvent.ACTION_POINTER_UP");
                        final int pointerIndex = (event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK)
                                >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                        final int pointerId = event.getPointerId(pointerIndex);
                        if (pointerId == mActivePointerId) {
                            // This was our active pointer going up. Choose a new
                            // active pointer and adjust accordingly.
                            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                            mLastTouchX = event.getX(newPointerIndex);
                            mLastTouchY = event.getY(newPointerIndex);
                            mActivePointerId = event.getPointerId(newPointerIndex);
                        } else {
                            final int tempPointerIndex = event.findPointerIndex(mActivePointerId);
                            mLastTouchX = event.getX(tempPointerIndex);
                            mLastTouchY = event.getY(tempPointerIndex);
                        }
                        break;
                    }
                }
                break;
        }
        return true;
    }

    // set image as background
    public void setImage(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    // set brush characteristics
    // e.g. color, thickness, alpha
    public void setBrush(Paint paint) {
        this.p = paint;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        canvas.save();

        // draw background
        if (bitmap != null) {
            this.setImageBitmap(bitmap);
        }
        Matrix translateMatrix = new Matrix();
        Matrix scaleMatrix = new Matrix();
        translateMatrix.setTranslate(mPosX, mPosY);
        if (mScaleDetector.isInProgress()) {
            scaleMatrix.setScale(mScaleFactor, mScaleFactor, mScaleDetector.getFocusX(), mScaleDetector.getFocusY());
        } else{
            scaleMatrix.setScale(mScaleFactor, mScaleFactor, mLastGestureX, mLastGestureY);
        }
        for (int i = 0; i < paths.size(); i ++) {
            Path temp = new Path(paths.get(i));

            temp.transform(scaleMatrix);
            temp.transform(translateMatrix);
            canvas.drawPath(temp, colors.get(i));
        }

        canvas.translate(mPosX, mPosY);

        if (mScaleDetector.isInProgress()) {
            canvas.scale(mScaleFactor, mScaleFactor, mScaleDetector.getFocusX(), mScaleDetector.getFocusY());
        } else{
            canvas.scale(mScaleFactor, mScaleFactor, mLastGestureX, mLastGestureY);
        }
        super.onDraw(canvas);
        canvas.restore();
    }
}
