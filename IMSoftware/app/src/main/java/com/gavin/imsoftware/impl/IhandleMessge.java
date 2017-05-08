package com.gavin.imsoftware.impl;


import com.gavin.imsoftware.bean.Message;

/**
 * 接收并处理消息的接口
 * @author Administrator
 */
public interface IhandleMessge {
	public void handleMsg(Message msg);
}
