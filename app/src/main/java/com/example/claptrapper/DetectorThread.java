package com.example.claptrapper;

import static com.example.claptrapper.services.ClapService.isAlarmTriggered;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;

import com.musicg.api.ClapApi;
import com.musicg.api.WhistleApi;
import com.musicg.wave.WaveHeader;

import java.util.LinkedList;

public class DetectorThread extends Thread {

    private RecorderThread recorder;
    private WaveHeader waveHeader;
    private WhistleApi whistleApi;
    ClapApi clapApi;
    private volatile Thread _thread;

    private LinkedList<Boolean> clapResultList = new LinkedList<Boolean>();
    private int numClaps;
    private int totalClapsDetected = 0;
    private int CheckClapLength = 3;
    private int ClapPassScore = 1;

    int counts = 0;

//

    private Ringtone ringtone;

    public static MediaPlayer mediaPlayer;

    Context context;
    private boolean isRingtonePlaying = false;
    byte[] buffer;

    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);


    public DetectorThread(RecorderThread recorder, Context context) {
        this.recorder = recorder;
        this.context = context;
        AudioRecord audioRecord = recorder.getAudioRecord();

        int bitsPerSample = 0;
        if (audioRecord.getAudioFormat() == AudioFormat.ENCODING_PCM_16BIT) {
            bitsPerSample = 16;
        } else if (audioRecord.getAudioFormat() == AudioFormat.ENCODING_PCM_8BIT) {
            bitsPerSample = 8;
        }

        int channel = 0;
        // whistle detection only supports mono channel
        if (audioRecord.getChannelConfiguration() == AudioFormat.CHANNEL_IN_MONO) {
            channel = 1;
        }
        mediaPlayer = new MediaPlayer();

        waveHeader = new WaveHeader();
        waveHeader.setChannels(channel);
        waveHeader.setBitsPerSample(bitsPerSample);
        waveHeader.setSampleRate(audioRecord.getSampleRate());
//        whistleApi = new WhistleApi(waveHeader);
        clapApi = new ClapApi(waveHeader);
    }

    private void initBuffer() {
        numClaps = 0;
        clapResultList.clear();

        // init the first frames
        for (int i = 0; i < CheckClapLength; i++) {
            clapResultList.add(false);
        }
        // end init the first frames
    }

    public void start() {
        _thread = new Thread(this);
        _thread.start();
    }

    public void stopDetection() {
        _thread = null;
    }

    public void run() {
        try {

//            initBuffer();

            Thread thisThread = Thread.currentThread();


            while (_thread == thisThread) {
                // detect sound
                buffer = recorder.getFrameBytes();


                // audio analyst
                if (buffer != null) {
                    // sound detected

                    boolean isWhistle = clapApi.isClap(buffer);

                    if (isWhistle) {

                        playRingtone();
//                        alarm();

                    } else {
                        isRingtonePlaying = false;
                    }


                    // end whistle detection
                }
                // end audio analyst
//                Thread.sleep(50);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void playRingtone() {

        try {

            // Get the default notification ringtone
            if (!isAlarmTriggered) {
                if (mediaPlayer != null) {

                    mediaPlayer.setDataSource(context, notification);
                    mediaPlayer.setLooping(true);
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    isAlarmTriggered = true;
//                isRingtonePlaying = true;
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void stopRingtone() {
        if (ringtone != null && ringtone.isPlaying()) {
            ringtone.stop();
        }
    }

    public int gettotalClapsDetected() {
        return totalClapsDetected;
    }


}