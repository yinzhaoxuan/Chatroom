package com.gavin.imsoftware.bean;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by gavin on 2017/5/4.
 */

public class User implements Serializable {
    private long id;		//主键ID
    private String ip;		//连接服务器的ip地址
    private String port;	//连接服务器的port号
    private String name;	//用户登录名
    private String img; 	//上传的头像
    private Bitmap bitmap;	//头像位图对象
    private int flag;		//当前用户标识

    public User() {
    }
    public User(String ip, String port, String name, String img, int flag) {
        super();
        this.ip = ip;
        this.port = port;
        this.name = name;
        this.img = img;
        this.flag = flag;
    }
    public User(int id, String ip, String port, String name, String img) {
        super();
        this.id = id;
        this.ip = ip;
        this.port = port;
        this.name = name;
        this.img = img;
    }
    public User(String ip, String port, String name, Bitmap bitmap, int flag) {
        super();
        this.ip = ip;
        this.port = port;
        this.name = name;
        this.bitmap = bitmap;
        this.flag = flag;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }
}
