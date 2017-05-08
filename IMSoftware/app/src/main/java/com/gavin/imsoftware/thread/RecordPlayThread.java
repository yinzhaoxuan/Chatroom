package com.gavin.imsoftware.thread;

import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.util.Log;

import com.gavin.imsoftware.constant.ContentFlag;
import com.gavin.imsoftware.tool.SystemConstant;

import java.io.BufferedInputStream;
import java.io.FileInputStream;

/**
 * Created by gavin on 2017/5/7.
 */

public class RecordPlayThread extends Thread {
    private int bufferSize;
    private AudioTrack mTrack;
    private String path;
    private boolean runFlag = false;
    public RecordPlayThread() {
        bufferSize = AudioRecord.getMinBufferSize(
                SystemConstant.SAMPLE_RATE_IN_HZ,
                SystemConstant.CHANNEL_CONFIG,
                SystemConstant.AUDIO_FORMAT);
        mTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                SystemConstant.SAMPLE_RATE_IN_HZ,
                SystemConstant.CHANNEL_CONFIG,
                SystemConstant.AUDIO_FORMAT, bufferSize * 2,
                AudioTrack.MODE_STREAM);
    }
    public void run() {
        try {
            BufferedInputStream dis = new BufferedInputStream(new FileInputStream(path));
            byte[] buffer = new byte[bufferSize];
            //AudioTrack是一种流，所以要一边播放一边读取
            int length = 0;
            //开始播放
            mTrack.play();
            while ((length = dis.read(buffer)) != -1 && runFlag) {
                //将数据写到AudioTrack中
                mTrack.write(buffer, 0, length);
            }
            mTrack.stop();
            mTrack.release();
            mTrack = null;
            Log.i(ContentFlag.TAG, "play is over");
            sleep(1000);
            runFlag = false;
            Log.i(ContentFlag.TAG, "thread is over");
            dis.close();
        } catch (Exception e) {
            Log.i(ContentFlag.TAG, "thread is closed");
            e.printStackTrace();
        }
    }
    public void setPath(String path) {
        this.path = path;
    }
    public boolean isRunFlag() {
        return runFlag;
    }
    public void setRunFlag(boolean runFlag) {
        this.runFlag = runFlag;
    }
}
