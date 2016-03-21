package com.nanddgroup.bpmmetronome;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

/**
 * Created by Nikita on 20.03.2016.
 */
public class ListenerHelper {
    EditText etBpm;
    DiscreteSeekBar dsbBPM;
    private iOnDSCchange listener;

    public ListenerHelper(EditText etBpm, DiscreteSeekBar dsbBPM) {
        this.etBpm = etBpm;
        this.dsbBPM = dsbBPM;
    }

    public void init() {
        etBpm.setText(String.valueOf(dsbBPM.getProgress()));
        etBpm.setSelection(etBpm.getText().length());
        dsbBPM.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                etBpm.setText(String.valueOf(value));
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {
                listener.listen(seekBar.getProgress());
            }
        });
        etBpm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                etBpm.setSelection(s.length());
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                etBpm.setSelection(s.length());
                if (s.toString().equals("") || s.subSequence(0, 1).equals("0")) {
                    etBpm.setText("1");
                    dsbBPM.setProgress(1);
                } else {
                    dsbBPM.setProgress(Integer.parseInt(s.toString()));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                etBpm.setSelection(s.length());
                if (s.length() > 3) {
                    etBpm.setText("100");
                    dsbBPM.setProgress(100);
                }
            }
        });
    }

    public void setListener(iOnDSCchange listener) {
        this.listener = listener;
    }

    public DiscreteSeekBar getDsbBPM() {
        return dsbBPM;
    }

    public EditText getEtBpm() {
        return etBpm;
    }
}
