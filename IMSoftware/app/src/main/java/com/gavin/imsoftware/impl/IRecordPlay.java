package com.gavin.imsoftware.impl;

import android.graphics.drawable.AnimationDrawable;
import android.widget.ImageView;

public interface IRecordPlay {
	public void play(String path, AnimationDrawable animation, ImageView ivPlay, int type);
	public void stop();
}
