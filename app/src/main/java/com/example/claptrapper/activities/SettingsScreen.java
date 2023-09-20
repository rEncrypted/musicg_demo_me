package com.example.claptrapper.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.example.claptrapper.databinding.ActivitySettingsScreenBinding;
import com.example.claptrapper.utils.ModesDialog;

public class SettingsScreen extends AppCompatActivity {

    ActivitySettingsScreenBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //back
        binding.icBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //
        binding.changeModeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ModesDialog customDialog = new ModesDialog(SettingsScreen.this);
                customDialog.show();
            }
        });
    }
}