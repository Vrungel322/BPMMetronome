package com.nanddgroup.bpmmetronome;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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
    private Thread thread;
    private android.hardware.Camera camera;
    private android.hardware.Camera.Parameters params;
    private boolean isFlashlightOn;
    private boolean isCameraFlash;

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

        return START_NOT_STICKY;
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
        //Vibration stuff
        //>>
        if (intent.getExtras().getInt(MainActivity.sivVIBRATION_INDEX) == 1) {
            thread = new Thread(new Runnable() {
                 @Override
                 public void run() {
                         Log.e("(30000 / time )  : ", String.valueOf(30000 / time));
                         long [] paretn = {0, (30000 / time ), (30000 / time )};
                         v.vibrate(paretn, 1);
                 }
             });
            thread.start();
        }
        //>>

        //Flashlight suff
        //>>
        if (intent.getExtras().getInt(MainActivity.sivLIGHTNING_INDEX) == 1) {
            isCameraFlash = getApplicationContext().getPackageManager()
                    .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
            if(!isCameraFlash) {
                showCameraAlert();
            } else {
                camera = android.hardware.Camera.open();
                params = camera.getParameters();
            }

            if(isFlashlightOn) {
                setFlashlightOff();
            } else {
                setFlashlightOn();
            }
        }
        //>>
    }

    private void showCameraAlert() {
        new AlertDialog.Builder(this)
                .setTitle("Error: No Camera Flash!")
                .setMessage("Camera flashlight not available in this Android device!")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       stopSelf();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void setFlashlightOn() {
        params = camera.getParameters();
        params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        camera.setParameters(params);
        camera.startPreview();
        isFlashlightOn = true;
    }

    private void setFlashlightOff() {
        params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        camera.setParameters(params);
        camera.stopPreview();
        isFlashlightOn = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isServiceWork = false;
        vibrator.cancel();
        if(isFlashlightOn) {
            setFlashlightOff();
            camera.release();
            camera = null;
        }
        stopSelf();
        Toast.makeText(getApplicationContext(), "onDestroy", Toast.LENGTH_SHORT).show();
    }
}
