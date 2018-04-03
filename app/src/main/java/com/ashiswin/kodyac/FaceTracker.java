package com.ashiswin.kodyac;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.icu.util.VersionInfo;
import android.util.Log;
import android.view.View;

import com.ashiswin.kodyac.camera.GraphicOverlay;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.Landmark;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jing Yun on 8/3/2018.
 */

public class FaceTracker extends Tracker<Face> {

    //TODO: use a less hackermanz method -jy

    private static final float EYE_CLOSED_THRESHOLD = 0.4f;
    private static final float SMILE_THRESHOLD = 0.6f;

    private GraphicOverlay mOverlay;
    private FaceTrackerGraphic mEyesGraphic;

    private Map<Integer, PointF> mPreviousProportions = new HashMap<>();

    // Similarly, keep track of the previous eye open state so that it can be reused for
    // intermediate frames which lack eye landmarks and corresponding eye state.

    //you dont want it to be updated in real time
    //int is to keep track
    private int winked =0;
    private int smile =0;
    private int rotated =0;
    private boolean isLeftOpen = true;
    private boolean isRightOpen = true;
    private boolean winkLeft = false;
    private boolean isSmile = false;
    private boolean isRotateRight = false;


    //==============================================================================================
    // Methods
    //==============================================================================================

    FaceTracker(GraphicOverlay overlay) {
        mOverlay = overlay;
    }

    /**
     * Resets the underlying googly eyes graphic and associated physics state.
     */
    @Override
    public void onNewItem(int id, Face face) {
        mEyesGraphic = new FaceTrackerGraphic(mOverlay);
    }

    /**
     * Updates the positions and state of eyes to the underlying graphic, according to the most
     * recent face detection results.  The graphic will render the eyes and simulate the motion of
     * the iris based upon these changes over time.
     */
    @Override
    public void onUpdate(FaceDetector.Detections<Face> detectionResults, Face face) {
        mOverlay.add(mEyesGraphic);

        updatePreviousProportions(face);

        float leftOpenScore = face.getIsLeftEyeOpenProbability();
        if (leftOpenScore == Face.UNCOMPUTED_PROBABILITY) {
            //cannot be computed nothing is done
        } else {
            isLeftOpen = (leftOpenScore > EYE_CLOSED_THRESHOLD);
        }

        float rightOpenScore = face.getIsRightEyeOpenProbability();
        if (rightOpenScore == Face.UNCOMPUTED_PROBABILITY) {
            //false nothing done
        } else {
            isRightOpen = (rightOpenScore > EYE_CLOSED_THRESHOLD);
        }


        if(face.getEulerZ()>20){
            isRotateRight = true;
            if (rotated==0){
            }
            rotated++;
            //security measure only true after you've passed the prev one
            if (winked>0){
                rotated++;
            }

        }

        winkLeft = !isLeftOpen && isRightOpen;
        if (winkLeft && rotated>0){
            winked++;
        }

        if (face.getIsSmilingProbability()>SMILE_THRESHOLD){
            isSmile = true;
            if (winked>0 && rotated>0){
                smile++;
            }

        }
        /*
        Log.i("test","Y rotation is" +face.getEulerY());
        Log.i("test","Z rotation is" +face.getEulerZ());
        Log.i("test","smilin prob is" +face.getIsSmilingProbability());
*/
        mEyesGraphic.updateItem(face, rotated, winked, smile);
    }

    /**
     * Hide the graphic when the corresponding face was not detected.  This can happen for
     * intermediate frames temporarily (e.g., if the face was momentarily blocked from
     * view).
     */
    @Override
    public void onMissing(FaceDetector.Detections<Face> detectionResults) {
        mOverlay.remove(mEyesGraphic);
    }

    /**
     * Called when the face is assumed to be gone for good. Remove the googly eyes graphic from
     * the overlay.
     */
    @Override
    public void onDone() {
        mOverlay.remove(mEyesGraphic);
    }

    //==============================================================================================
    // Private
    //==============================================================================================

    private void updatePreviousProportions(Face face) {
        for (Landmark landmark : face.getLandmarks()) {
            PointF position = landmark.getPosition();
            float xProp = (position.x - face.getPosition().x) / face.getWidth();
            float yProp = (position.y - face.getPosition().y) / face.getHeight();
            mPreviousProportions.put(landmark.getType(), new PointF(xProp, yProp));
        }
    }

    /**
     * Finds a specific landmark position, or approximates the position based on past observations
     * if it is not present.
     */
    private PointF getLandmarkPosition(Face face, int landmarkId) {
        for (Landmark landmark : face.getLandmarks()) {
            if (landmark.getType() == landmarkId) {
                return landmark.getPosition();
            }
        }

        PointF prop = mPreviousProportions.get(landmarkId);
        if (prop == null) {
            return null;
        }

        float x = face.getPosition().x + (prop.x * face.getWidth());
        float y = face.getPosition().y + (prop.y * face.getHeight());
        return new PointF(x, y);
    }

    public int getRotated(){
        return this.rotated;
    }

    public int getWinked(){
        return this.winked;
    }

    public int getSmile(){
        return this.smile;
    }

}
