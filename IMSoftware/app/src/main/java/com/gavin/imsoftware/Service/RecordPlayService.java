package com.gavin.imsoftware.Service;

import android.graphics.drawable.AnimationDrawable;
import android.widget.ImageView;

import com.gavin.imsoftware.R;
import com.gavin.imsoftware.thread.RecordPlayThread;

/**
 * Created by gavin on 2017/5/7.
 */

public class RecordPlayService {
    private AnimationDrawable animation = null;
    private ImageView ivPlay = null;
    private int type;
    private RecordPlayThread thread;
    //开始播放
    public void play(String path, AnimationDrawable animation, ImageView ivPlay, int type) {
        if (thread != null && thread.isRunFlag()) {
            thread.setRunFlag(false);
            stopAnimatin();
        }
        this.animation = animation;
        this.ivPlay = ivPlay;
        this.type = type;
        thread = new RecordPlayThread();
        animation.start();
        thread.setPath(path);
        thread.setRunFlag(true);
        thread.start();
    }
    //停止播放
    public void stop() {
        if (thread.isAlive()) {
            thread.setRunFlag(false);
            stopAnimatin();
        }
    }
    //停止动画
    public void stopAnimatin() {
        if (type == 0) {
            ivPlay.setBackgroundResource(R.drawable.chatto_voice_playing);
        } else {
            ivPlay.setBackgroundResource(R.drawable.chatfrom_voice_playing);
        }
        if (animation != null) {
            animation.stop();
            animation = null;
        }
    }
    public boolean ifThreadRun() {
        return thread.isRunFlag();
    }
}
