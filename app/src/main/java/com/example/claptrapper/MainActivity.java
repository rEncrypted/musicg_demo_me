package com.example.claptrapper;

import static com.example.claptrapper.DetectorThread.mediaPlayer;
import static com.example.claptrapper.services.ClapService.isAlarmTriggered;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.claptrapper.databinding.ActivityMainBinding;
import com.example.claptrapper.receiver.ClapReceiver;
import com.example.claptrapper.services.ClapService;

public class MainActivity extends AppCompatActivity {
    //binding
    ActivityMainBinding binding;
    public static final int DETECT_NONE = 0;
    public static final int DETECT_WHISTLE = 1;
    public static int selectedDetection = DETECT_NONE;

    // detection parameters
    private DetectorThread detectorThread;
    private RecorderThread recorderThread;
    private Thread detectedTextThread;
    public static int clapValue = 0;

    // views
    private View mainView, listeningView;
    private Button clapButton;
    private TextView totalClapsDetectedNumberText;

    public static final int REQUEST_AUDIO_PERMISSION_RESULT = 1;

    int permission = 0;

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
            mediaPlayer.release();
            mediaPlayer = null;
            isAlarmTriggered = false;
            Toast.makeText(MainActivity.this, "Stopped!", Toast.LENGTH_SHORT).show();
        } else {
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("restartservice");
        broadcastIntent.setClass(this, ClapReceiver.class);
        this.sendBroadcast(broadcastIntent);
//        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set views
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.RECORD_AUDIO}, REQUEST_AUDIO_PERMISSION_RESULT);

        } else {
            Intent serviceIntent = new Intent(MainActivity.this, ClapService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent);
            } else {
                startService(serviceIntent);
            }
        }

        //btn listener
//        binding.clapButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (permission == 1) {
//                    Intent serviceIntent = new Intent(MainActivity.this, ClapService.class);
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                        startForegroundService(serviceIntent);
//                    } else {
//                        startService(serviceIntent);
//                    }
//                } else {
//                    Toast.makeText(MainActivity.this, "Please give record audio permission to the App", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });


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
                    permission = 1;
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    Log.e("Permission Error", "Audio recording permission denied");
                }
                break;
        }
    }
}
