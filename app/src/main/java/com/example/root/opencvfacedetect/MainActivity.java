package com.example.root.opencvfacedetect;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.hardware.Camera;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.root.cameramodule.CameraApi;
import com.example.root.cameramodule.ICameraApiCallback;

import org.opencv.android.InstallCallbackInterface;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback, ICameraApiCallback {
    private static final String TAG = "MainActivity";
    private final int PERMISSION_CAMERA_REQUEST_CODE = 0x10;
    private boolean isCheckPermissionOk = false;

    private SurfaceView mSurfaceView;
    private ViewGroup.LayoutParams layoutParams;

    private int previewWidth = 1920;
    private int previewHeight = 1080;
    private float ratio;


    private int mSurfaceViewWidth;
    private int mSurfaceViewHeight;
    private org.opencv.core.Size mMinSize = new org.opencv.core.Size(Math.round(mSurfaceViewHeight * 0.2), Math.round(mSurfaceViewHeight * 0.2));
    private org.opencv.core.Size mMaxSize = new org.opencv.core.Size();

    private File mCascadeFile;
    private CascadeClassifier mFaceCascade;
    private Mat mSrcMat;
    private Mat mDesMat;
    private MatOfRect matOfRect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSurfaceView = findViewById(R.id.preview_surface);
        mSurfaceView.getHolder().addCallback(this);

        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_CAMERA_REQUEST_CODE);
        } else {
            isCheckPermissionOk = true;
        }


        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getRealSize(size);
        mSurfaceViewWidth = size.x;
        mSurfaceViewHeight = size.y;
        Log.e(TAG, "onCreate: " + size.x + "--" + size.y);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CAMERA_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                isCheckPermissionOk = true;
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (isCheckPermissionOk) {
            Log.e(TAG, "surfaceCreated: ");
            CameraApi.getInstance().setCameraId(CameraApi.CAMERA_INDEX_BACK);
            CameraApi.getInstance().initCamera(this, this);
            CameraApi.getInstance().setPreviewSize(new Size(previewWidth, previewHeight));
            CameraApi.getInstance().setFps(30).configCamera();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        ratio = (float) (previewWidth) / (float) (previewHeight);
        layoutParams = mSurfaceView.getLayoutParams();
        if (mSurfaceViewHeight > mSurfaceViewWidth) {
            //竖屏
            mSurfaceViewHeight = (int) (mSurfaceViewWidth * ratio);
        } else {
            //横屏
            mSurfaceViewWidth = (int) (mSurfaceViewHeight * ratio);
        }

        Log.e(TAG, "surfaceChanged:mSurfaceViewWidth= " + mSurfaceViewWidth);
        Log.e(TAG, "surfaceChanged:mSurfaceViewHeight= " + mSurfaceViewHeight);
        layoutParams.width = mSurfaceViewWidth;
        layoutParams.height = mSurfaceViewHeight;
        mSurfaceView.setLayoutParams(layoutParams);
        CameraApi.getInstance().startPreview(holder);

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.e(TAG, "surfaceDestroyed: ");
        CameraApi.getInstance().stopCamera();
    }

    @Override
    public void onPreviewFrameCallback(byte[] data, Camera camera) {
        mSrcMat.put(0, 0, data);
        Imgproc.cvtColor(mSrcMat, mDesMat, Imgproc.COLOR_YUV420sp2GRAY);
        mFaceCascade.detectMultiScale(mDesMat, matOfRect, 1.1, 5
                , 2, mMinSize, mMaxSize);
        if (matOfRect.toArray().length != 0)
            Log.e(TAG, "onPreviewFrameCallback: " + matOfRect.toArray()[0].x + "--" + matOfRect.toArray()[0].y);
        camera.addCallbackBuffer(data);
    }

    @Override
    public void onNotSupportErrorTip(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private LoaderCallbackInterface mLoaderCallback = new LoaderCallbackInterface() {
        @Override
        public void onManagerConnected(int status) {
            if (status == LoaderCallbackInterface.SUCCESS) {
                mSrcMat = new Mat(previewHeight, previewWidth, CvType.CV_8UC1);
                mDesMat = new Mat(previewHeight, previewWidth, CvType.CV_8UC1);
                matOfRect = new MatOfRect();

                try {
                    // load cascade file from application resources
                    InputStream is = getResources().openRawResource(R.raw.lbpcascade_frontalface);
                    File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
                    mCascadeFile = new File(cascadeDir, "lbpcascade_frontalface.xml");
                    FileOutputStream os = new FileOutputStream(mCascadeFile);

                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = is.read(buffer)) != -1) {
                        os.write(buffer, 0, bytesRead);
                    }
                    is.close();
                    os.close();

                    mFaceCascade = new CascadeClassifier(mCascadeFile.getAbsolutePath());
                    if (mFaceCascade.empty()) {
                        Log.e(TAG, "Failed to load cascade classifier");
                        mFaceCascade = null;
                    } else {
                        Log.e(TAG, "Loaded cascade classifier from " + mCascadeFile.getAbsolutePath());
                    }


                    cascadeDir.delete();

                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, "Failed to load cascade. Exception thrown: " + e);
                }


            }
        }

        @Override
        public void onPackageInstall(int operation, InstallCallbackInterface callback) {

        }
    };
}