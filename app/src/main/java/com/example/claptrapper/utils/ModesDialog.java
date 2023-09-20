package com.example.claptrapper.utils;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;

import com.example.claptrapper.R;

import io.paperdb.Paper;

public class ModesDialog extends Dialog implements View.OnClickListener {

    private RadioGroup radioGroup;
    private RadioButton option1, option2, option3;
    private Button closeButton;

    private int selectedValue = -1; // Default value when nothing is selected

    Context context;

    public ModesDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.modes_dialog_layout);

        //paperdb init
        Paper.init(context);
        //init views
        radioGroup = findViewById(R.id.radio_group);
        option1 = findViewById(R.id.radio_option1);
        option2 = findViewById(R.id.radio_option2);
        option3 = findViewById(R.id.radio_option3);

        closeButton = findViewById(R.id.dialog_button);
        closeButton.setOnClickListener(this);
        //by default
        option1.setChecked(true);
        //check if it saves already and show it on dialog.
        if (Paper.book().read("mode") != null) {
            selectedValue = Paper.book().read("mode");
            if (selectedValue == 1) {
                option1.setChecked(true);
            } else if (selectedValue == 2) {
                option2.setChecked(true);
            } else if (selectedValue == 3) {
                option3.setChecked(true);
            }
        }
        //radio group listener
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radio_option1) {
                    selectedValue = 1;
                } else if (checkedId == R.id.radio_option2) {
                    selectedValue = 2;
                } else if (checkedId == R.id.radio_option3) {
                    selectedValue = 3;
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.dialog_button) {
            if (selectedValue != -1) {
                saveSelection(selectedValue);
            }

            dismiss();
        }
    }

    private void saveSelection(int selectedValue) {

        Paper.book().write("mode", selectedValue);

    }
}
