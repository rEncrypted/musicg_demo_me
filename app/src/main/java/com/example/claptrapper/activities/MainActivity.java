package com.example.claptrapper.activities;

import static com.example.claptrapper.DetectorThread.flManager;
import static com.example.claptrapper.DetectorThread.mediaPlayer;
import static com.example.claptrapper.DetectorThread.vibrationEffect;
import static com.example.claptrapper.DetectorThread.vibrator;
import static com.example.claptrapper.services.ClapService.isAlarmTriggered;
import static com.example.claptrapper.services.ClapService.vibration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.claptrapper.DetectorThread;
import com.example.claptrapper.R;
import com.example.claptrapper.databinding.ActivityMainBinding;
import com.example.claptrapper.receiver.ClapReceiver;
import com.example.claptrapper.services.ClapService;
import com.example.claptrapper.utils.Constants;
import com.google.android.material.snackbar.Snackbar;

import co.mobiwise.materialintro.animation.MaterialIntroListener;
import co.mobiwise.materialintro.shape.Focus;
import co.mobiwise.materialintro.shape.FocusGravity;
import co.mobiwise.materialintro.shape.ShapeType;
import co.mobiwise.materialintro.view.MaterialIntroView;
import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {
    //binding
    ActivityMainBinding binding;
    public static final int REQUEST_AUDIO_PERMISSION_RESULT = 1;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getAction() != null) {

            if (intent.getAction().equals("YES_ACTION")) {
                stopRintone();
            }


        }

    }

    private void stopRintone() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            if (vibrationEffect != null) {
                vibrator.cancel();
            }
            if(vibrator !=null){
                vibrator.cancel();
            }
            if (flManager != null) {
                flManager.stopBlinking();
            }
            mediaPlayer.release();
            mediaPlayer = null;
            DetectorThread.counts = 0;
            isAlarmTriggered = false;
//            Toast.makeText(MainActivity.this, "Stopped!", Toast.LENGTH_SHORT).show();
            Snackbar snackbar = Snackbar.make(binding.mainLayout, "The ringing is stop now", Snackbar.LENGTH_SHORT);
            snackbar.show();
        } else {
            Snackbar snackbar = Snackbar.make(binding.mainLayout, "The ringing is already stop", Snackbar.LENGTH_SHORT);
            snackbar.show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

        } else {
            //check from db whether it's runnning
            if (Paper.book().read("service") != null) {
                if (Boolean.TRUE.equals(Paper.book().read("service"))) {
                    Intent broadcastIntent = new Intent();
                    broadcastIntent.setAction("restartservice");
                    broadcastIntent.setClass(this, ClapReceiver.class);
                    this.sendBroadcast(broadcastIntent);
                } else {

                }
            }


        }


//        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set views
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //paper init
        Paper.init(this);

//        showcase
        new MaterialIntroView.Builder(this)
                .enableDotAnimation(true)
                .enableIcon(false)
                .setFocusGravity(FocusGravity.CENTER)
                .setFocusType(Focus.NORMAL)
                .setDelayMillis(1000)

                .setListener(new MaterialIntroListener() {
                    @Override
                    public void onUserClicked(String materialIntroViewId) {
                        new MaterialIntroView.Builder(MainActivity.this)
                                .enableDotAnimation(true)
                                .enableIcon(false)
                                .setFocusGravity(FocusGravity.CENTER)
                                .setFocusType(Focus.MINIMUM)
                                .setDelayMillis(500)

                                .performClick(false)
                                .setInfoText("Tap this button to turn off ringing.")
                                .setShape(ShapeType.CIRCLE)
                                .setTarget(binding.stopRingBtn)
                                .setUsageId("intro_2") //THIS SHOULD BE UNIQUE ID
                                .setIdempotent(true)
                                .show();
                    }
                })
                .setInfoText("Tap this button to turn on/off service.")
                .setShape(ShapeType.CIRCLE)
                .setTarget(binding.onBtnView)
                .setUsageId("intro_1") //THIS SHOULD BE UNIQUE ID
                .setIdempotent(true)
                .show();


        //when app is completely close and user tap on stop button through
        // notification that will called in activity as onCreate method is that can eill execute
        if (getIntent().getExtras() != null) {

            boolean isNotificationIntent = getIntent().getExtras().getBoolean("notificationIntent", false);

            boolean isringIntent = getIntent().getExtras().getBoolean("ringIntent", false);

            if (isNotificationIntent) {
                stopRintone();
            } else if (isringIntent) {

            }
        }


        ///when app open, it'll check
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

        } else {
            if (Paper.book().read("service") != null) {
                if (Boolean.TRUE.equals(Paper.book().read("service"))) {
                    startClapService();

                } else {

                }
            }
        }

        //settings
        binding.settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(MainActivity.this, SettingsScreen.class));

            }
        });

        //stopRingButton
        binding.stopRingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopRintone();
            }
        });


//        btn listener
        binding.onBtnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.RECORD_AUDIO}, REQUEST_AUDIO_PERMISSION_RESULT);

                } else {
                    if (isServiceRunning(ClapService.class)) {
                        stopClapService();
//                        Toast.makeText(MainActivity.this, "Stop Service", Toast.LENGTH_SHORT).show();
                        Snackbar snackbar = Snackbar.make(binding.mainLayout, "Service is stop now!", Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    } else {
                        startClapService();
//                        Toast.makeText(MainActivity.this, "Start Service", Toast.LENGTH_SHORT).show();
                        Snackbar snackbar = Snackbar.make(binding.mainLayout, "Service is start now!", Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    }

                }

            }
        });


    }


    private boolean isServiceRunning(Class<ClapService> clapServiceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (clapServiceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void stopClapService() {
        Intent serviceIntent = new Intent(MainActivity.this, ClapService.class);
        serviceIntent.setAction(Constants.STOPFOREGROUND_ACTION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }

        binding.btnLayout.setBackgroundResource(R.drawable.gradient);

        //wave 1 the top wave
        binding.waveHeader.setCloseColor(Color.parseColor("#F64D4D"));
        binding.waveHeader.setStartColor(Color.parseColor("#AD0000"));

        //wave 2, bottom down wave
        binding.waveHeader2.setStartColor(Color.parseColor("#AD0000"));
        binding.waveHeader2.setCloseColor(Color.parseColor("#F64D4D"));

    }

    private void startClapService() {

        Intent serviceIntent = new Intent(MainActivity.this, ClapService.class);
        serviceIntent.setAction(Constants.STARTFOREGROUND_ACTION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }

        binding.btnLayout.setBackgroundResource(R.drawable.gradient_green);

        //wave 1 the top wave
        binding.waveHeader.setCloseColor(Color.parseColor("#3BA900"));
        binding.waveHeader.setStartColor(Color.parseColor("#257C00"));

        //wave 2, bottom down wave
        binding.waveHeader2.setStartColor(Color.parseColor("#257C00"));
        binding.waveHeader2.setCloseColor(Color.parseColor("#3BA900"));

    }

//    private void goHomeView() {
//        setContentView(mainView);
//        if (recorderThread != null) {
//            recorderThread.stopRecording();
//            recorderThread = null;
//        }
//        if (detectorThread != null) {
//            detectorThread.stopDetection();
//            detectorThread = null;
//        }
//        selectedDetection = DETECT_NONE;
//    }

//    private void goListeningView() {
//        setContentView(listeningView);
//
//        if (totalClapsDetectedNumberText == null) {
//            totalClapsDetectedNumberText = (TextView) this.findViewById(R.id.detectedNumberText);
//        }
//
//        // thread for detecting environmental noise
//        if (detectedTextThread == null) {
//            detectedTextThread = new Thread() {
//                public void run() {
//                    try {
//                        while (recorderThread != null && detectorThread != null) {
//                            runOnUiThread(new Runnable() {
//                                public void run() {
//                                    if (detectorThread != null) {
//                                        totalClapsDetectedNumberText.setText(String.valueOf(detectorThread.gettotalClapsDetected()));
//                                    }
//                                }
//                            });
//                            sleep(100);
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    } finally {
//                        detectedTextThread = null;
//                    }
//                }
//            };
//            detectedTextThread.start();
//        }
//    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        menu.add(0, 0, 0, "Quit demo");
//        return super.onCreateOptionsMenu(menu);
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case 0:
//                finish();
//                break;
//            default:
//        }
//        return super.onOptionsItemSelected(item);
//    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
//            goHomeView();
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }
//
//    class ClickEvent implements View.OnClickListener {
//        public void onClick(View view) {
//            if (view == clapButton) {
//                selectedDetection = DETECT_WHISTLE;
//                recorderThread = new RecorderThread();
//                recorderThread.start();
//                detectorThread = new DetectorThread(recorderThread);
//                detectorThread.start();
//                goListeningView();
//            }
//        }
//    }

//    protected void onDestroy() {
//        super.onDestroy();
//        android.os.Process.killProcess(android.os.Process.myPid());
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_AUDIO_PERMISSION_RESULT:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    startRecordingDirectly();
                    startClapService();
                    Snackbar snackbar = Snackbar.make(binding.mainLayout, "Service is start now!", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    Log.e("Permission Error", "Audio recording permission denied");
                }
                break;
        }
    }
}
