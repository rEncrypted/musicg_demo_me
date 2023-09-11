package com.example.musicgdemome;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.util.Log;

import com.musicg.api.ClapApi;
import com.musicg.api.WhistleApi;
import com.musicg.wave.WaveHeader;

import java.util.LinkedList;

public class DetectorThread extends Thread{

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


    public DetectorThread(RecorderThread recorder){
        this.recorder = recorder;
        AudioRecord audioRecord = recorder.getAudioRecord();

        int bitsPerSample = 0;
        if (audioRecord.getAudioFormat() == AudioFormat.ENCODING_PCM_16BIT){
            bitsPerSample = 16;
        }
        else if (audioRecord.getAudioFormat() == AudioFormat.ENCODING_PCM_8BIT){
            bitsPerSample = 8;
        }

        int channel = 0;
        // whistle detection only supports mono channel
        if (audioRecord.getChannelConfiguration() == AudioFormat.CHANNEL_IN_MONO){
            channel = 1;
        }

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

    public void stopDetection(){
        _thread = null;
    }

    public void run() {
        try {
            byte[] buffer;
            initBuffer();

            Thread thisThread = Thread.currentThread();
            while (_thread == thisThread) {
                // detect sound
                buffer = recorder.getFrameBytes();

                // audio analyst
                if (buffer != null) {
                    // sound detected
                    MainActivity.clapValue = numClaps;

                    // whistle detection
                    //System.out.println("*Whistle:");
//                    boolean isWhistle = whistleApi.isWhistle(buffer);
                    boolean isWhistle = clapApi.isClap(buffer);
                    if (clapResultList.getFirst()) {
                        numClaps--;
                    }

                    clapResultList.removeFirst();
                    clapResultList.add(isWhistle);

                    if (isWhistle) {
                        numClaps++;
                    }
                    //System.out.println("num:" + numClaps);

                    Log.d("TAGnn", "run: num:"+numClaps);

                    if(numClaps > 0){
                        counts++;
                        if(counts >= 2){

                            // clear buffer
                            initBuffer();
                            totalClapsDetected++;
                            counts = 0;
                            Log.d("TAGdd", "run: detect"+totalClapsDetected);
                        }
                    }

//                    if (numClaps >= ClapPassScore) {
//                        // clear buffer
//                        initBuffer();
//                        totalClapsDetected++;
//                        Log.d("TAGdd", "run: detect"+totalClapsDetected);
//                    }
                    // end whistle detection
                }
                else{
                    // no sound detected
                    if (clapResultList.getFirst()) {
                        numClaps--;
                    }
                    clapResultList.removeFirst();
                    clapResultList.add(false);

                    MainActivity.clapValue = numClaps;
                }
                // end audio analyst
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int gettotalClapsDetected(){
        return totalClapsDetected;
    }
}