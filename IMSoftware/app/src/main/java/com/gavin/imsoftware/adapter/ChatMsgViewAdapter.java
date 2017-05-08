package com.gavin.imsoftware.adapter;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.MessageQueue;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gavin.imsoftware.R;
import com.gavin.imsoftware.Service.RecordPlayService;
import com.gavin.imsoftware.bean.Message;
import com.gavin.imsoftware.bean.User;
import com.gavin.imsoftware.constant.ContentFlag;
import com.gavin.imsoftware.tool.ExpressionUtil;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by gavin on 2017/5/7.
 */

public class ChatMsgViewAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;
    private List<Message> msgList;
    private User curUser;
    private Timer mTimer = new Timer();
    RecordPlayService mPlayService = new RecordPlayService();
    public static String currMsgId="";		//当前正在播放语音的ID

    public ChatMsgViewAdapter(Context context, List<Message> msgList, User curUser) {
        mContext = context;
        this.msgList = msgList;
        this.curUser = curUser;
        mInflater = LayoutInflater.from(this.mContext);
    }

    @Override
    public int getCount() {
        return msgList.size();
    }

    @Override
    public Object getItem(int position) {
        return msgList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        // 区别两种view的类型，标注两个不同的变量来分别表示各自的类型
        Message msg = msgList.get(position);
        if (msg.getId() == curUser.getId()) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Message msg = msgList.get(position);
        ViewHolder viewHolder = null;
        if (convertView == null) {
            if (msg.getId() == curUser.getId()) {
                convertView = mInflater.inflate(R.layout.chat_item_right, null);
            } else {
                convertView = mInflater.inflate(R.layout.chat_item_left, null);
            }
            viewHolder = new ViewHolder();
            viewHolder.tvSendTime = (TextView) convertView.findViewById(R.id.tv_sendtime);
            viewHolder.tvUserName = (TextView) convertView.findViewById(R.id.tv_username);
            viewHolder.msgBgView = (View) convertView.findViewById(R.id.chat_msg_bg);
            viewHolder.ivUserImage = (ImageView) convertView.findViewById(R.id.iv_userhead);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tvSendTime.setText(msg.getSend_date());
        viewHolder.tvUserName.setText(msg.getSend_person());
        SpannableString spannableString = ExpressionUtil.
                getExpressionString(mContext, msg.getSend_ctn());
        TextView tvContent = (TextView) viewHolder.msgBgView.findViewById(R.id.tv_chatcontent);
        final ImageView ivPlay = (ImageView) viewHolder.msgBgView.findViewById(R.id.iv_play_voice);
        tvContent.setText(spannableString);
        if (msg.isIfyuyin()) {
            ivPlay.setVisibility(View.VISIBLE);
            //处理语音消息的单击事件
            viewHolder.msgBgView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String path = msg.getRecord_path();
                    if (path != null && !"".equals(path)) {
                        try {
                            if (currMsgId.equals(msg.getMsgId())) {
                                Log.i(ContentFlag.TAG, "playService.ifThreadRun:"
                                        + mPlayService.ifThreadRun());
                            }
                            if(currMsgId.equals(msg.getMsgId()) && mPlayService.ifThreadRun()) {
                                mPlayService.stop();
                                return;
                            }
                            //根据类型选择左右不同的动画
                            final int type = getItemViewType(position);
                            if(type == 0){
                                ivPlay.setBackgroundResource(R.drawable.chatto_voice_play_frame);
                            }else{
                                ivPlay.setBackgroundResource(R.drawable.chatfrom_voice_play_frame);
                            }
                            final AnimationDrawable animation = (AnimationDrawable) ivPlay.getBackground();
                            //播放动画
                            final long recordTime = msg.getRecordTime();
                            currMsgId = msg.getMsgId();
                            mPlayService.play(path, animation, ivPlay, type);
                            mContext.getMainLooper().myQueue().addIdleHandler(new MessageQueue.IdleHandler() {
                                @Override
                                public boolean queueIdle() {
                                    return false;
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        } else {
            ivPlay.setVisibility(View.GONE);
            viewHolder.msgBgView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
        if (msg.getBitmap() != null) {
            viewHolder.ivUserImage.setImageBitmap(msg.getBitmap());
        }
        return convertView;
    }

    private final class RecordTimeTask extends TimerTask {
        private AnimationDrawable animation;
        private ImageView ivPlay;
        private int type;

        public RecordTimeTask(AnimationDrawable animation, ImageView ivPlay, int type) {
            this.animation = animation;
            this.ivPlay = ivPlay;
            this.type = type;
        }

        @Override
        public void run() {
            if (animation.isRunning()) {
                animation.stop();
                Log.i(ContentFlag.TAG, "timer task:" + animation.hashCode());
                animation = null;
                android.os.Message msg = handle.obtainMessage();
                msg.obj = ivPlay;
                msg.arg1 = this.type;
                handle.sendMessage(msg);
            }
        }
    }

    private Handler handle = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            ImageView ivPlay = (ImageView) msg.obj;
            int type = msg.arg1;
            if (type == 0) {
                ivPlay.setBackgroundResource(R.drawable.chatto_voice_playing);
            } else {
                ivPlay.setBackgroundResource(R.drawable.chatfrom_voice_playing);
            }
        }
    };

    private class ViewHolder {
        public TextView tvSendTime;
        public TextView tvUserName;
        public View msgBgView;
        public ImageView ivUserImage;
    }
}
