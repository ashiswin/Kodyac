package com.ashiswin.kodyac;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;

import com.ashiswin.kodyac.camera.GraphicOverlay;
import com.google.android.gms.vision.face.Face;

/**
 * Created by Jing Yun on 8/3/2018.
 */

public class FaceTrackerGraphic extends GraphicOverlay.Graphic {
    private static final float FACE_POSITION_RADIUS = 10.0f;
    private static final float EYE_RADIUS_PROPORTION = 0.45f;
    private static final float IRIS_RADIUS_PROPORTION = EYE_RADIUS_PROPORTION / 2.0f;
    private static final float ID_TEXT_SIZE = 40.0f;
    private static final float INS_TEXT_SIZE = 100.0f;
    private static final float ID_Y_OFFSET = 50.0f;
    private static final float ID_X_OFFSET = -50.0f;
    private static final float BOX_STROKE_WIDTH = 5.0f;

    //Boolean is update in real time! static int is to correct that
    private int winkedLeft =0;
    private int rotated =0;
    private int smiled =0;



    private volatile Face mFace;


    private Paint mFacePositionPaint;
    private Paint mIdInstructions;
    private Paint mBoxPaint;
    private Paint mIdPaint;


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

        float cx = translateX(face.getPosition().x + face.getWidth() / 2);
        float cy = translateY(face.getPosition().y + face.getHeight() / 2);
        canvas.drawCircle(cx, cy, FACE_POSITION_RADIUS, mFacePositionPaint);
        canvas.drawText("id: " + face.getId() + "rotation: "+face.getEulerZ()+"smiling "+face.getIsSmilingProbability(),  cx + ID_X_OFFSET, cy + ID_Y_OFFSET, mIdPaint);

        float xOffset = scaleX(face.getWidth() / 2.0f);
        float yOffset = scaleY(face.getHeight() / 2.0f);
        float left = cx - xOffset;
        float top = cy - yOffset;
        float right = cx + xOffset;
        float bottom = cy + yOffset;
        canvas.drawRect(left, top, right, bottom, mBoxPaint);



        //get person to rotate head
        if(rotated==0 && winkedLeft==0 && smiled==0){
            canvas.drawText("Rotate your head right", ID_X_OFFSET, ID_Y_OFFSET, mIdInstructions);
        }else if (rotated>0 && winkedLeft==0 && smiled==0) {
            canvas.drawText("Wink Your Left Eye", ID_X_OFFSET, ID_Y_OFFSET, mIdInstructions);
        }else if(rotated>0 && winkedLeft>0&& smiled==0){
            canvas.drawText("Smile!", ID_X_OFFSET, ID_Y_OFFSET, mIdInstructions);
            //take a picture? (fucking low res tho)
        }else if (rotated>0 && winkedLeft>0&& smiled>0) {
            canvas.drawText("Verification Successful!", ID_X_OFFSET, ID_Y_OFFSET, mIdInstructions);
        }else {
            //TODO: debugging
            canvas.drawText("oh shit waddup check ur code bitch", ID_X_OFFSET, ID_Y_OFFSET, mIdInstructions);
        }
    }


}