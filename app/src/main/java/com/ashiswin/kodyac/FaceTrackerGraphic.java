package com.ashiswin.kodyac;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;

import com.ashiswin.kodyac.camera.GraphicOverlay;
import com.google.android.gms.vision.face.Face;


/**
 * Created by Jing Yun on 8/3/2018.
 */

public class FaceTrackerGraphic extends GraphicOverlay.Graphic {
    private static final float EYE_RADIUS_PROPORTION = 0.45f;
    private static final float IRIS_RADIUS_PROPORTION = EYE_RADIUS_PROPORTION / 2.0f;
    private static final float ID_TEXT_SIZE = 40.0f;
    private static final float INS_TEXT_SIZE = 100.0f;
    private static final float BOX_STROKE_WIDTH = 5.0f;

    //Boolean is update in real time! static int is to correct that
    private int winkedLeft = 0;
    private int rotated = 0;
    private int smiled = 0;

    private volatile Face mFace;

    private Paint mFacePositionPaint;
    private Paint mIdInstructions;
    private Paint mBoxPaint;
    private Paint mIdPaint;

    private Context context;
    //==============================================================================================
    // Methods
    //==============================================================================================

    FaceTrackerGraphic(GraphicOverlay overlay) {
        super(overlay);

        mFacePositionPaint = new Paint();
        mFacePositionPaint.setColor(Color.GREEN);

        mIdPaint = new Paint();
        mIdPaint.setColor(Color.BLACK);
        mIdPaint.setTextSize(ID_TEXT_SIZE);

        mIdInstructions = new Paint();
        mIdInstructions.setColor(Color.BLACK);
        mIdInstructions.setTextSize(INS_TEXT_SIZE);

        mBoxPaint = new Paint();
        mBoxPaint.setColor(Color.YELLOW);
        mBoxPaint.setStyle(Paint.Style.STROKE);
        mBoxPaint.setStrokeWidth(BOX_STROKE_WIDTH);

        context = overlay.getContext();
    }

    /**
     * Updates the eye positions and state from the detection of the most recent frame.  Invalidates
     * the relevant portions of the overlay to trigger a redraw.
     */
    void updateItem(Face face, int rotateRight, int wink,  int smile) {
        mFace = face;
        winkedLeft = wink;
        rotated = rotateRight;
        smiled = smile;

        if(smile == 1) {
            ((VideoVerificationVideoActivity) context).verifyVideo();
        }
        postInvalidate();
    }

    /**
     * Draws the current eye state to the supplied canvas.  This will draw the eyes at the last
     * reported position from the tracker, and the iris positions according to the physics
     * simulations for each iris given motion and other forces.
     */
    @Override
    public void draw(Canvas canvas) {


        Face face = mFace;
        if (face == null) {
            return;
        }
        /*draw border around face*/
        float cx = translateX(face.getPosition().x + face.getWidth() / 2);
        float cy = translateY(face.getPosition().y + face.getHeight() / 2);
        Log.e("testing",String.valueOf(cx));
        Log.e("testing",String.valueOf(cy));

        float xOffset = scaleX(face.getWidth() / 2.0f);
        float yOffset = scaleY(face.getHeight() / 2.0f);
        float left = cx - xOffset;
        float top = cy - yOffset;
        float right = cx + xOffset;
        float bottom = cy + yOffset;
        canvas.drawRect(left, top, right, bottom, mBoxPaint);

        String text = "";
        //get person to rotate head
        if(rotated == 0 && winkedLeft == 0 && smiled == 0){
            text = "Tilt your head right";
        }
        else if (rotated > 0 && winkedLeft == 0 && smiled == 0) {
            text = "Wink your left eye";
        }
        else if(rotated > 0 && winkedLeft > 0&& smiled == 0){
            //take a picture? (fucking low res tho)
            text = "Smile!";
        }

        TextPaint textPaint = new TextPaint();
        textPaint.setColor(Color.parseColor("#FFFFFF"));
        textPaint.setTextSize(100);
        StaticLayout sl = new StaticLayout(text, textPaint, canvas.getClipBounds().width(), Layout.Alignment.ALIGN_CENTER, 1, 1, true);
        canvas.save();

        float textHeight = getTextHeight(text, textPaint);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        float textYCoordinate =  (int)TypedValue.applyDimension( TypedValue.COMPLEX_UNIT_DIP, 16, displaymetrics );

        //text will be drawn from left
        float textXCoordinate = canvas.getClipBounds().left;

        canvas.translate(textXCoordinate, textYCoordinate);

        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#66000000"));
        canvas.drawRect(textXCoordinate, -textYCoordinate, textXCoordinate + sl.getWidth(), 2 * textYCoordinate + textHeight, paint);
        //draws static layout on canvas
        sl.draw(canvas);
        canvas.restore();
    }
    private float getTextHeight(String text, Paint paint) {
        Rect rect = new Rect();
        paint.getTextBounds(text, 0, text.length(), rect);
        return rect.height();
    }


}
