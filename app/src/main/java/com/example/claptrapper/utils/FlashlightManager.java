package com.example.claptrapper.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import com.example.claptrapper.R;

public class FlashlightManager {

    private static final String TAG = "FlashlightManager";

    private Context context;
    private CameraManager cameraManager;
    private Handler handler = new Handler();

    final boolean[] flashOn = {true};

    public FlashlightManager(Context context) {
        this.context = context;
        this.cameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
    }

    private Runnable onEverySecond = new Runnable() {
        @Override
        public void run() {
//            Log.d(TAG, "isPause: " + isPause);


            flashOn[0] = !flashOn[0];
            handler.postDelayed(onEverySecond, 1000);
            turnFlash(flashOn[0]);
            Log.d(TAG, "on");

        }
    };


    public void startBlinking() {
        handler.post(onEverySecond);
    }

    public void stopBlinking() {
        turnFlash(false);
        handler.removeCallbacks(onEverySecond);
    }

    public void turnFlash(boolean isCheck) {
        Log.d(TAG, "running flash");

        try {
            String cameraId = cameraManager.getCameraIdList()[0];
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                cameraManager.setTorchMode(cameraId, isCheck);
//                    isFlashOn = isCheck;
            }

        } catch (CameraAccessException e) {
            logError(e);
        }

    }

    private void showToast(Context context, String message) {
        // Implement your toast logic here
    }

    private void logError(Exception e) {
        // Implement your error logging logic here
    }
}
