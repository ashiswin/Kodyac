package com.ashiswin.kodyac;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.KeyPoint;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.features2d.FeatureDetector;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class NRICVerificationCard extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2{
    private static final int INTENT_SELFIE = 0;
    private static final int INTENT_FRONT = 1;
    private static final int INTENT_BACK = 2;

    private ImageButton btnFront, btnBack;
    private TextView txtName, txtNRIC, txtDOB, txtAddress;
    private Button btnVerify;

    //for OpenCV OCR
    private CameraBridgeViewBase mOpenCvCameraView;
    private Mat mGrey, mRgba, mByte;
    private Scalar CONTOUR_COLOR;
    private boolean isProcess = false;
    private Button btnOpenCV;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nricverification_card);

        getSupportActionBar().setTitle("Upload NRIC");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnFront = (ImageButton) findViewById(R.id.btnFront);
        btnBack = (ImageButton) findViewById(R.id.btnBack);
        txtName = (TextView) findViewById(R.id.txtName);
        txtNRIC = (TextView) findViewById(R.id.txtNRIC);
        txtDOB = (TextView) findViewById(R.id.txtDOB);
        txtAddress = (TextView) findViewById(R.id.txtAddress);
        btnVerify = (Button) findViewById(R.id.btnVerify);
        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.openCV_live_camera_frame);
        btnOpenCV = (Button) findViewById(R.id.btnOpenCV) ;

        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent selfieIntent = new Intent(NRICVerificationCard.this, NRICVerificationSelfie.class);
                selfieIntent.putExtra("methodId", getIntent().getIntExtra("methodId", 0));
                startActivityForResult(selfieIntent, INTENT_SELFIE);
            }
        });

        btnFront.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, INTENT_FRONT);
                }
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, INTENT_BACK);
                }
            }
        });

        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == INTENT_SELFIE && resultCode == RESULT_OK) {
            setResult(RESULT_OK, data);
            finish();
        }
        else if(requestCode == INTENT_FRONT && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            final Bitmap imageBitmap = (Bitmap) extras.get("data");
            btnFront.setImageBitmap(imageBitmap);

            final ProgressDialog dialog = new ProgressDialog(NRICVerificationCard.this);
            dialog.setIndeterminate(true);
            dialog.setTitle("Scanning NRIC");
            dialog.setMessage("Please wait while we scan your NRIC");
            dialog.show();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dialog.cancel();
                                TextRecognizer textRecognizer = new TextRecognizer.Builder(NRICVerificationCard.this).build();
                                if(!textRecognizer.isOperational()) {
                                    Log.w("NRICCardtextRec", "Detector dependencies are not yet available.");
                                    IntentFilter lowstorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
                                    boolean hasLowStorage = registerReceiver(null, lowstorageFilter) != null;
                                    if (hasLowStorage) {
                                        Toast.makeText(NRICVerificationCard.this, "Low Storage", Toast.LENGTH_LONG).show();
                                        Log.w("NRICCardtextRec", "Low Storage");
                                    }
                                }

                                //convert image to a frame so you can feed it into the text recognizer
                                Frame imageFrame = new Frame.Builder().setBitmap(imageBitmap).build();


                                SparseArray<TextBlock> textBlocks = textRecognizer.detect(imageFrame);
                                Log.i("NRICVER",String.valueOf(textBlocks.size()));
                                if (textBlocks.size()!=0){
                                    Toast.makeText(NRICVerificationCard.this, "text detected, check log cat", Toast.LENGTH_SHORT).show();
                                }

                                for (int i=0; i<textBlocks.size();i++){
                                    TextBlock textBlock = textBlocks.get(textBlocks.keyAt(i));
                                    if (textBlock==null){
                                        Toast.makeText(NRICVerificationCard.this, "no text", Toast.LENGTH_SHORT).show();
                                    }
                                    Log.i("NRICCardtextRec", textBlock.getValue());
                                }

                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    txtName.setText("test name");
                    txtNRIC.setText("S9999999Z");
                    txtDOB.setText("4th October 1995");
                    txtAddress.setText("Blk 59 Changi South Avenue, #09-102, S453199");
                }

            }).start();

        }
        else if(requestCode == INTENT_BACK && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            btnBack.setImageBitmap(imageBitmap);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat(height,width, CvType.CV_8UC3);
        mByte = new Mat(height, width, CvType.CV_8UC1);

    }

    @Override
    public void onCameraViewStopped() {
        mRgba.release();

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();
        mGrey = inputFrame.gray();

        CONTOUR_COLOR = new Scalar(255);
        MatOfKeyPoint keypoint = new MatOfKeyPoint();
        List<KeyPoint> listpoint = new ArrayList<KeyPoint>();
        KeyPoint kpoint = new KeyPoint();
        Mat mask = Mat.zeros(mGrey.size(), CvType.CV_8UC1);
        int rectanx1;
        int rectany1;
        int rectanx2;
        int rectany2;

        //
        Scalar zeos = new Scalar(0, 0, 0);
        List<MatOfPoint> contour2 = new ArrayList<MatOfPoint>();
        Mat kernel = new Mat(1, 50, CvType.CV_8UC1, Scalar.all(255));
        Mat morbyte = new Mat();
        Mat hierarchy = new Mat();

        Rect rectan3 = new Rect();//
        int imgsize = mRgba.height() * mRgba.width();
        //
        FeatureDetector detector = FeatureDetector
                    .create(FeatureDetector.MSER);
        detector.detect(mGrey, keypoint);
        listpoint = keypoint.toList();
            //
        for (int ind = 0; ind < listpoint.size(); ind++) {
            kpoint = listpoint.get(ind);
            rectanx1 = (int) (kpoint.pt.x - 0.5 * kpoint.size);
            rectany1 = (int) (kpoint.pt.y - 0.5 * kpoint.size);
            rectanx2 = (int) (kpoint.size);
            rectany2 = (int) (kpoint.size);
            if (rectanx1 <= 0)
                rectanx1 = 1;
            if (rectany1 <= 0)
                rectany1 = 1;
            if ((rectanx1 + rectanx2) > mGrey.width())
                rectanx2 = mGrey.width() - rectanx1;
            if ((rectany1 + rectany2) > mGrey.height())
                rectany2 = mGrey.height() - rectany1;
            Rect rectant = new Rect(rectanx1, rectany1, rectanx2, rectany2);
                try {
                    Mat roi = new Mat(mask, rectant);
                    roi.setTo(CONTOUR_COLOR);
                } catch(Exception e){
                    Log.d("openCv","mat roi error"+e.getMessage());
                }


            }

            Imgproc.morphologyEx(mask, morbyte, Imgproc.MORPH_DILATE, kernel);
            Imgproc.findContours(morbyte, contour2, hierarchy,
                    Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);
            for (int ind = 0; ind < contour2.size(); ind++) {
                rectan3 = Imgproc.boundingRect(contour2.get(ind));
                if (rectan3.area() > 0.5 * imgsize || rectan3.area() < 100
                        || rectan3.width / rectan3.height < 2) {
                    Mat roi = new Mat(morbyte, rectan3);
                    roi.setTo(zeos);

                } else
                    Imgproc.rectangle(mRgba, rectan3.br(), rectan3.tl(),
                            CONTOUR_COLOR);
            }

            return mRgba;
        }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status){
                case LoaderCallbackInterface.SUCCESS: {mOpenCvCameraView.enableView();}
                break;
                default: {super.onManagerConnected(status);}
                break;
            }
        }
    };

    @Override
    public void onPause(){
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void onDestroy() {
        super.onDestroy();

        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

}
