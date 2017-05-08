package com.cms.bean;

/**
 * Created by gavin on 2017/5/4.
 */
public class Message {
    private String send_ctn;
    private String send_person;
    private String send_date;
    private boolean ifSound = false;
    private long recordTime; //持续时间
    private String msgId;

    public Message(String send_ctn, String send_person, String send_date) {
        super();
        this.send_ctn = send_ctn;
        this.send_person = send_person;
        this.send_date = send_date;
    }

    public String getSend_ctn() {
        return send_ctn;
    }

    public void setSend_ctn(String send_ctn) {
        this.send_ctn = send_ctn;
    }

    public String getSend_person() {
        return send_person;
    }

    public void setSend_person(String send_person) {
        this.send_person = send_person;
    }

    public String getSend_date() {
        return send_date;
    }

    public void setSend_date(String send_date) {
        this.send_date = send_date;
    }

    public boolean isIfSound() {
        return ifSound;
    }

    public void setIfSound(boolean ifSound) {
        this.ifSound = ifSound;
    }

    public long getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(long recordTime) {
        this.recordTime = recordTime;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }
}
