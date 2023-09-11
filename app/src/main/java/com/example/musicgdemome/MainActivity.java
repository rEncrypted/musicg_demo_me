package com.example.musicgdemome;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
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

    public static final int  REQUEST_AUDIO_PERMISSION_RESULT = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set views
        LayoutInflater inflater = LayoutInflater.from(this);
        mainView = inflater.inflate(R.layout.activity_main, null);
        listeningView = inflater.inflate(R.layout.listening, null);
        setContentView(mainView);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.RECORD_AUDIO}, REQUEST_AUDIO_PERMISSION_RESULT);

        }else{
            Toast.makeText(this, "already permission", Toast.LENGTH_SHORT).show();
        }

        clapButton = (Button) this.findViewById(R.id.clapButton);
        clapButton.setOnClickListener(new ClickEvent());


    }

    private void goHomeView() {
        setContentView(mainView);
        if (recorderThread != null) {
            recorderThread.stopRecording();
            recorderThread = null;
        }
        if (detectorThread != null) {
            detectorThread.stopDetection();
            detectorThread = null;
        }
        selectedDetection = DETECT_NONE;
    }

    private void goListeningView(){
        setContentView(listeningView);

        if (totalClapsDetectedNumberText == null){
            totalClapsDetectedNumberText = (TextView) this.findViewById(R.id.detectedNumberText);
        }

        // thread for detecting environmental noise
        if (detectedTextThread == null){
            detectedTextThread = new Thread() {
                public void run() {
                    try {
                        while (recorderThread != null && detectorThread != null) {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    if (detectorThread != null){
                                        totalClapsDetectedNumberText.setText(String.valueOf(detectorThread.gettotalClapsDetected()));
                                    }
                                }
                            });
                            sleep(100);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        detectedTextThread = null;
                    }
                }
            };
            detectedTextThread.start();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 0, 0, "Quit demo");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                finish();
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            goHomeView();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    class ClickEvent implements View.OnClickListener {
        public void onClick(View view) {
            if (view == clapButton) {
                selectedDetection = DETECT_WHISTLE;
                recorderThread = new RecorderThread();
                recorderThread.start();
                detectorThread = new DetectorThread(recorderThread);
                detectorThread.start();
                goListeningView();
            }
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_AUDIO_PERMISSION_RESULT:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    startRecordingDirectly();
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    Log.e("Permission Error", "Audio recording permission denied");
                }
                break;
        }
    }
}
