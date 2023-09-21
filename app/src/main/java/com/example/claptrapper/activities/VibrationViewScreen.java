package com.example.claptrapper.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;

import com.example.claptrapper.R;
import com.example.claptrapper.databinding.ActivityVibrationViewScreenBinding;
import com.google.android.material.snackbar.Snackbar;

import io.paperdb.Paper;

public class VibrationViewScreen extends AppCompatActivity {

    ActivityVibrationViewScreenBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVibrationViewScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //paper int
        Paper.init(this);

        //check if it saves then
        if (Paper.book().read("vibration") != null) {
            boolean vv = Paper.book().read("vibration");
            if (vv) {
                binding.vibrationSwitch.setChecked(true);
            }
        }

        //back
        binding.icBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //vibr switch iml.

        binding.vibrationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    Paper.book().write("vibration", true);
                    Snackbar snackbar = Snackbar.make(binding.vLayout, "Vibration is enable now", Snackbar.LENGTH_LONG);
                    snackbar.show();
                } else {
                    Paper.book().write("vibration", false);
                    Snackbar snackbar = Snackbar.make(binding.vLayout, "Vibration is disable now", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        });
    }
}