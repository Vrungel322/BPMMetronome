package com.nanddgroup.bpmmetronome;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

public class BPMService extends Service {
    public static boolean isServiceWork;
    private Intent intent;
    private String sbpm;
    private Vibrator vibrator;
    private Thread threadV;
    private Thread threadF;
    private android.hardware.Camera camera;
    private android.hardware.Camera.Parameters params;
    public static boolean isFlashlightOn;
    public static boolean isFlashlightWorks;
    // private boolean isCameraFlash;
    public static boolean isVibrateOn;

    public BPMService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sbpm = intent.getExtras().getString(MainActivity.sBPM);
        if (!sbpm.equals("0")) {
            startAction(intent, vibrator, Integer.parseInt(sbpm));
        } else {
            Toast.makeText(getApplicationContext(), "BPM can't be 0.", Toast.LENGTH_SHORT).show();
            stopSelf();
        }
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        isServiceWork = true;
        Toast.makeText(getApplicationContext(), "onCreate", Toast.LENGTH_SHORT).show();
        vibrator = (Vibrator) getApplicationContext()
                .getSystemService(Context.VIBRATOR_SERVICE);
    }

    private void startAction(Intent intent, final Vibrator v, final int time) {

        initThreads(time, v);
        //Vibration stuff
        //>>
        if (intent.getExtras().getInt(MainActivity.sivVIBRATION_INDEX) == 1) {
            isVibrateOn = true;
            threadV.start();
        }
        //>>

        //Flashlight suff
        //>>
        if (intent.getExtras().getInt(MainActivity.sivLIGHTNING_INDEX) == 1) {
//            isCameraFlash = getApplicationContext().getPackageManager()
//                    .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
            initCamera();

            Log.e("flashlight", String.valueOf(isFlashlightOn));

            threadF.start();
        }
        //>>
    }

    private void initCamera() {

        if (camera == null) {
            BPMService.isFlashlightWorks = true;
            camera = Camera.open();
            params = camera.getParameters();
        } else {
            return;
        }
    }

    private void initThreads(final int time, final Vibrator v) {

        threadV = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.e("(30000 / time )  : ", String.valueOf(30000 / time));
                long[] paretn = {0, (30000 / time), (30000 / time)};
                v.vibrate(paretn, 1);
            }
        });

        threadF = new Thread(new Runnable() {
            @Override
            public void run() {
                while (isFlashlightWorks) {
                    if (isFlashlightOn) {
                        setFlashlightOff();
                    } else {
                        setFlashlightOn();
                    }
                    try {
                        Thread.sleep(30000 / time);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

        });
    }

    private void setFlashlightOn() {
        if (camera == null || params == null) {
            return;
        } else {
            //params = camera.getParameters();
            params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            camera.setParameters(params);
            camera.startPreview();
            isFlashlightOn = true;
        }
    }

    private void setFlashlightOff() {
        if (camera == null || params == null) {
            return;
        } else {
            params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            camera.setParameters(params);
            camera.stopPreview();
            isFlashlightOn = false;
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isServiceWork = false;
        vibrator.cancel();
        isVibrateOn = false;
        setFlashlightOff();
        BPMService.isFlashlightWorks = false;
        if (camera != null) {
            camera.release();
            camera = null;
        }
        Toast.makeText(getApplicationContext(), "onDestroy", Toast.LENGTH_SHORT).show();
    }
}
