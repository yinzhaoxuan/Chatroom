package com.cms.service;

import com.cms.bean.User;
import com.cms.constant.ContentFlag;
import com.cms.tool.FormatDate;
import com.cms.tool.StreamTool;
import com.cms.tool.XmlParser;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.System.out;

/**
 * Created by gavin on 2017/5/4.
 * 服务端socket处理类
 */
public class ChatServer {
    private ExecutorService mExecutorService; //线程池
    private int port; //监听端口
    private boolean quit = false;
    private ServerSocket mServer;
    private List<SocketTask> mTaskList = new ArrayList<SocketTask>();

    public ChatServer(int port) {
        this.port = port;
        //创建线程池，池中有(cpu个数*50)条线程
        mExecutorService = Executors.newFixedThreadPool(Runtime.getRuntime()
            .availableProcessors() * 50);

    }

    public void quit() {
        this.quit = true;
        try {
            for (SocketTask task : mTaskList) {
                task.mInputStream.close();
            }
            mServer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //启动服务器
    public void start() throws Exception {
        mServer = new ServerSocket(port);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(!quit) {
                    try {
                        out.println("等待用户socket请求");
                        Socket socket = mServer.accept();
                        //为支持多用户并发访问，采用线程池管理每一个用户的连接请求
                        SocketTask newTask = new SocketTask(socket);
                        mTaskList.add(newTask);
                        mExecutorService.execute(newTask);
                        out.println("启动一个线程开始处理socket请求");
                    } catch (Exception e) {
                        out.println("服务器终止，关闭所有线程");
                    }
                }
            }
        }).start();
    }

    /**
     * 内部线程类,负责与每个客户端的数据通信
     *
     */
    private final class SocketTask implements Runnable {
        private Socket s;
        private DataInputStream mInputStream;
        private DataOutputStream mOutputStream;
        private User curUser;

        public SocketTask(Socket socket) {
            s = socket;
            try {
                mInputStream = new DataInputStream(s.getInputStream());
                mOutputStream = new DataOutputStream(s.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //发送消息
        public void sendMsg(String msg, byte[] datas) {
            try {
                if (msg != null) {
                    mOutputStream.writeUTF(msg);
                }
                if (datas != null) {
                    mOutputStream.writeInt(datas.length);
                    mOutputStream.write(datas, 0, datas.length);
                }
                mOutputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                while (true) {
                    String msgCtn = mInputStream.readUTF();
                    if (msgCtn.startsWith(ContentFlag.REGOSTER_FLAG)) {
                        String name = msgCtn.substring(ContentFlag.REGOSTER_FLAG.length()).trim();
                        String userId = XmlParser.saveUserInfo(name, mInputStream);
                        out.println("生成的用户id" + userId);
                        this.sendMsg(userId, null);
                        mTaskList.remove(this);
                        break;
                    } else if (msgCtn.startsWith(ContentFlag.ONLINE_FLAG)) {
                        //处理登陆消息
                        //获取用户唯一指定标示
                        String loginId = msgCtn.substring(
                                ContentFlag.ONLINE_FLAG.length()).trim();
                        out.println("当前登录用户的ID：" + loginId);
                        curUser = XmlParser.queryUserById(Integer
                                .parseInt(loginId));
                        // 将聊天室其它所有在线用户的头像数据缓存到该登录用户客户端
                        int imgNums = mTaskList.size() - 1;// 文件的数量
                        out.println("当前在线的人数：" + mTaskList.size());
                        this.mOutputStream.writeInt(imgNums);
                        if (imgNums > 0) {
                            for (SocketTask task : mTaskList) {
                                if (task != this) {
                                    long userId = task.curUser.getId();
                                    File img = new File("image//"
                                            + XmlParser.queryUserById(userId)
                                            .getImg());
                                    FileInputStream flleInput = new FileInputStream(img);
                                    byte data[] = StreamTool.readStream(flleInput);
                                    this.sendMsg(String.valueOf(userId), data);
                                }
                            }
                        }
                        // 加载用户头像资源
                        File imgFile = new File("image//" + curUser.getImg());
                        FileInputStream flleInput = new FileInputStream(imgFile);
                        byte datas[] = StreamTool.readStream(flleInput);
                        String send_person = curUser.getName(); // 发送者
                        String send_ctn = "进入聊天室！"; // 发送内容
                        String send_date = FormatDate.getCurDate(); // 发送时间
                        StringBuilder json = new StringBuilder();
                        json.append("[{");
                        json.append("id:").append(loginId)
                                .append(",send_person:\"").append(send_person)
                                .append("\",send_ctn:\"").append(send_ctn)
                                .append("\",send_date:\"").append(send_date)
                                .append("\",msg_id:\"").append(XmlParser.getUniqueMsgId());
                        json.append("\"}]");
                        System.out.println("json:" + json);
                        // 循环向连接的每个Socket客户端发送登录消息
                        for (SocketTask tast : mTaskList) {
                            System.out.println("循环向客户端发送消息");
                            tast.sendMsg(
                                    ContentFlag.ONLINE_FLAG
                                            + this.curUser.getId(), null);
                            tast.sendMsg(json.toString(), datas);
                        }
                        flleInput.close();
                    } else if (msgCtn.startsWith(ContentFlag.OFFLINE_FLAG)) {
                        // 处理退出消息
                        mTaskList.remove(this);
                        // 获得退出用户的ID
                        String id = msgCtn.substring(
                                ContentFlag.OFFLINE_FLAG.length()).trim();
                        StringBuilder json = new StringBuilder();
                        json.append("[{");
                        json.append("id:").append(this.curUser.getId())
                                .append(",send_person:\"")
                                .append(this.curUser.getName())
                                .append("\",send_ctn:\"").append("退出聊天室！")
                                .append("\",send_date:\"").append(FormatDate.getCurDate())
                                .append("\",msg_id:\"").append(XmlParser.getUniqueMsgId());
                        json.append("\"}]");
                        for (SocketTask tast : mTaskList) {
                            if (tast != this) {
                                tast.sendMsg(ContentFlag.OFFLINE_FLAG + id,
                                        null);
                                tast.sendMsg(json.toString(), null);
                            }
                        }
                        System.out.println("用户" + curUser.getName() + "退出！,关闭线程"
                                + Thread.currentThread().getName());
                        break;
                    } else if (msgCtn.startsWith(ContentFlag.RECORD_FLAG)) {
                        // 处理录音消息
                        String filename = msgCtn
                                .substring(ContentFlag.RECORD_FLAG.length());
                        long recordTime = mInputStream.readLong();
                        byte datas[] = StreamTool.readStream(mInputStream);
                        StringBuilder json = new StringBuilder();
                        json.append("[{");
                        json.append("id:").append(this.curUser.getId())
                                .append(",send_person:\"")
                                .append(this.curUser.getName())
                                .append("\",send_ctn:\"")
                                .append(recordTime / 1000 + "\'")
                                .append("\",send_date:\"")
                                .append(FormatDate.getCurDate())
                                .append("\",msg_id:\"").append(XmlParser.getUniqueMsgId())
                                .append("\",recordTime:\"").append(recordTime);
                        json.append("\"}]");
                        System.out.println("json:" + json);
                        // 循环向连接的每个Socket客户端发送消息
                        for (SocketTask tast : mTaskList) {
                            tast.sendMsg(ContentFlag.RECORD_FLAG + filename,
                                    null);
                            tast.sendMsg(json.toString(), datas);
                        }
                    } else {
                        // 处理普通消息
                        StringBuilder json = new StringBuilder();
                        json.append("[{");
                        json.append("id:").append(this.curUser.getId())
                                .append(",send_person:\"")
                                .append(this.curUser.getName())
                                .append("\",send_ctn:\"").append(msgCtn)
                                .append("\",send_date:\"")
                                .append(FormatDate.getCurDate())
                                .append("\",msg_id:\"").append(XmlParser.getUniqueMsgId());
                        json.append("\"}]");
                        for (SocketTask tast : mTaskList) {
                            tast.sendMsg(json.toString(), null);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                mTaskList.remove(this);
                out.println("关闭线程" + Thread.currentThread().getName());
            } finally {
                try {
                    if (null != mInputStream)
                        mInputStream.close();
                    if (null != mOutputStream)
                        mOutputStream.close();
                    if (null != s)
                        s.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
