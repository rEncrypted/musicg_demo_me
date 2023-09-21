package com.example.claptrapper.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;

import com.example.claptrapper.R;
import com.example.claptrapper.databinding.ActivityFlashLightScreenBinding;
import com.example.claptrapper.utils.FlashlightManager;
import com.google.android.material.snackbar.Snackbar;

import io.paperdb.Paper;

public class FlashLightScreen extends AppCompatActivity {
    ActivityFlashLightScreenBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFlashLightScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //paper init
        Paper.init(this);

        if (Paper.book().read("flash") != null) {
            boolean ff = Paper.book().read("flash");
            if (ff) {
                binding.flashLightSwitch.setChecked(true);
            }
        }

        //flashlight
        boolean isFlashAvailableOnDevice = this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

        //back
        binding.icBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //switch impl.
        binding.flashLightSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {

                    if (!isFlashAvailableOnDevice) {
                        Snackbar snackbar = Snackbar.make(binding.fLayout, getResources().getString(R.string.device_doesn_t_support_flash_light), Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    } else {
                        Paper.book().write("flash", true);
                        Snackbar snackbar = Snackbar
                                .make(binding.fLayout, "Flashlight feature is enable now", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }


                } else {
                    Paper.book().write("flash", false);
                    Snackbar snackbar = Snackbar
                            .make(binding.fLayout, "Flashlight feature is disable now", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        });
    }
}