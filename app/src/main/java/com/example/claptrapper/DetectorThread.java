package com.example.claptrapper;

import static com.example.claptrapper.services.ClapService.flash;
import static com.example.claptrapper.services.ClapService.isAlarmTriggered;
import static com.example.claptrapper.services.ClapService.mode;
import static com.example.claptrapper.services.ClapService.vibration;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

import com.example.claptrapper.utils.FlashlightManager;
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

    public static int counts = 0;

//


    public static MediaPlayer mediaPlayer;

    Context context;
    private boolean isRingtonePlaying = false;
    AudioManager audioManager;

    //vibrations
    public static Vibrator vibrator;
    public static VibrationEffect vibrationEffect;

    //flashlight
    public static FlashlightManager flManager;

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

        //vib
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

        //flashLight
        flManager = new FlashlightManager(context);

        //set Vol to maximum
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
        audioManager.setStreamVolume(AudioManager.STREAM_RING, maxVolume, AudioManager.FLAG_PLAY_SOUND);

        mediaPlayer = new MediaPlayer();

        waveHeader = new WaveHeader();
        waveHeader.setChannels(channel);
        waveHeader.setBitsPerSample(bitsPerSample);
        waveHeader.setSampleRate(audioRecord.getSampleRate());
        if (mode == 1) {
            clapApi = new ClapApi(waveHeader);
        } else if (mode == 2) {

            whistleApi = new WhistleApi(waveHeader);

        } else if (mode == 3) {
            clapApi = new ClapApi(waveHeader);
            whistleApi = new WhistleApi(waveHeader);

        }

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
                byte[] buffer = recorder.getFrameBytes();


                // audio analyst
                if (buffer != null) {
                    // sound detected

                    if (mode == 1) {

                        boolean isClap = clapApi.isClap(buffer);
                        Log.d("TAGpp", "run: " + isClap);

                        if (isClap) {
//                            Log.d("TAGcc", "run: " + isClap);
                            counts++;
                            if (counts >= 6) {
                                Log.d("TAGcc", "run after counts: " + isClap);
                                playRingtone();
                                counts = 0;
                            }

//                        alarm();

                        } else {
                            isRingtonePlaying = false;
                            counts = 0;
                        }
                    } else if (mode == 2) {
                        boolean isWhistle = whistleApi.isWhistle(buffer);
                        if (isWhistle) {
                            counts++;
                            if (counts >= 6) {
                                Log.d("TAGcc", "run: " + isWhistle);
                                playRingtone();
                                counts = 0;
                            }
//                        alarm();

                        } else {
                            isRingtonePlaying = false;
                            counts = 0;
                        }
                    } else if (mode == 3) {
                        boolean isWhistle = whistleApi.isWhistle(buffer);
                        boolean isClap = clapApi.isClap(buffer);
                        if (isClap) {

                            counts++;
                            if (counts >= 6) {
                                playRingtone();
                                counts = 0;
                            }

//                        alarm();

                        } else if (isWhistle) {
                            counts++;
                            if (counts >= 6) {
                                playRingtone();
                                counts = 0;
                            }
                        } else {
                            counts = 0;

                        }

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

                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
                    mediaPlayer.setDataSource(context, notification);
                    mediaPlayer.setLooping(true);
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    if (vibration) {


                        // create vibrator effect with the constant EFFECT_TICK
                        // it is safe to cancel other vibrations currently taking place
                        vibrator.cancel();

                        long[] pattern = {0, 100, 1000};
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            vibrator.vibrate(VibrationEffect.createWaveform(pattern, 0));
                        } else {
                            vibrator.vibrate(pattern, 0);
                        }
                    }


                        if (flash) {
                            flManager.startBlinking();
                        }
                        isAlarmTriggered = true;
                        //intent to open the mainscreen
//                    Intent yesIntent = new Intent(context, MainActivity.class);
////                    yesIntent.addCategory(Intent.CATEGORY_LAUNCHER);
////                    yesIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                    yesIntent.putExtra("ringIntent", true);
//                    yesIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    context.startActivity(yesIntent);
//                    PendingIntent.getActivity(context, 0, yesIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//                isRingtonePlaying = true;
                    }
                }


            } catch(Exception e){
                e.printStackTrace();
            }

        }

//    private void stopRingtone() {
//        if (ringtone != null && ringtone.isPlaying()) {
//            ringtone.stop();
//        }
//    }

        public int gettotalClapsDetected () {
            return totalClapsDetected;
        }


    }