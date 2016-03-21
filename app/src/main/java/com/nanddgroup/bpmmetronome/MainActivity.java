package com.nanddgroup.bpmmetronome;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    @Bind(R.id.ivVibration)
    ImageView ivVibration;
    @Bind(R.id.ivLightning)
    ImageView ivLightning;
    @Bind(R.id.ivSound)
    ImageView ivSound;
    @Bind(R.id.etBPM)
    EditText etBpm;
    @Bind(R.id.dsbBPM)
    DiscreteSeekBar dsbBPM;
    @Bind(R.id.bService)
    Button bService;
    private int ivVibration_index;
    private int ivLightning_index;
    private int ivSound_index;
    public static final String sivVIBRATION_INDEX = "sivVibration_index";
    public static final String sivLIGHTNING_INDEX = "sivLightning_index";
    public static final String sivSOUND_INDEX = "sivSound_index";
    public static final String sBPM = "sBPM";
    private ListenerHelper listenerHelper;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        ivVibration_index = 0;
        ivLightning_index = 0;
        ivSound_index = 0;
        listenerHelper = new ListenerHelper(etBpm, dsbBPM);
        listenerHelper.init();
        checkServiceEnabled();
        listenerHelper.setListener(new iOnDSCchange() {
            @Override
            public void listen(int value) {
                putInIntent();
                stopService(intent);
                if (bService.getText().equals("STOP")) {
                    startService(intent);
                }
            }
        });
    }


    @OnClick(R.id.ivVibration)
    public void ivVibrationClicked() {
        if (ivVibration_index == 0) {
            ivVibration.setImageResource(R.drawable.vibration_black);
            ivVibration_index = 1;
            putInIntent();
            if (BPMService.isServiceWork) {
                reloadService();
                checkServiceEnabled();
            }
        } else {
            ivVibration.setImageResource(R.drawable.vibration_white);
            ivVibration_index = 0;
            putInIntent();
            if (BPMService.isServiceWork) {
                reloadService();
                checkServiceEnabled();
            }
        }
    }

    @OnClick(R.id.ivLightning)
    public void ivLightningClicked() {
        if (ivLightning_index == 0) {
            ivLightning.setImageResource(R.drawable.lightning_black);
            ivLightning_index = 1;
            putInIntent();
            if (BPMService.isServiceWork) {
                reloadService();
                checkServiceEnabled();
            }
        } else {
            ivLightning.setImageResource(R.drawable.lightning_white);
            ivLightning_index = 0;
            putInIntent();
            if (BPMService.isServiceWork) {
                reloadService();
                checkServiceEnabled();
            }
        }
    }

    @OnClick(R.id.ivSound)
    public void ivSoundClicked() {
        if (ivSound_index == 0) {
            ivSound.setImageResource(R.drawable.sound_black);
            ivSound_index = 1;
        } else {
            ivSound.setImageResource(R.drawable.sound_white);
            ivSound_index = 0;
        }
    }

    @OnClick(R.id.bService)
    public void bServiceClicked() {
        putInIntent();
        if (ivVibration_index == 0 && ivLightning_index == 0 && ivSound_index == 0) {
            Toast.makeText(getApplicationContext(),
                    "Choose some type of action.", Toast.LENGTH_SHORT).show();
        } else {
            checkServiceEnabledStraight();
        }
    }

    private void reloadService() {
        stopService(intent);
        startService(intent);
    }

    private void putInIntent() {
        intent = new Intent(this, BPMService.class);
        intent.putExtra(sivVIBRATION_INDEX, ivVibration_index);
        intent.putExtra(sivLIGHTNING_INDEX, ivLightning_index);
        intent.putExtra(sivSOUND_INDEX, ivSound_index);
        intent.putExtra(sBPM, listenerHelper.etBpm.getText().toString());

    }

    private void checkServiceEnabled() {
        if (BPMService.isServiceWork) {
            bService.setText("STOP");
        } else {
            bService.setText("START");
        }
    }

    private void checkServiceEnabledStraight() {
        if (!BPMService.isServiceWork) {
            bService.setText("STOP");
            startService(intent);
        } else {
            bService.setText("START");
            stopService(intent);
        }
    }
}
