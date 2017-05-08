package com.gavin.imsoftware.Service;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.gavin.imsoftware.bean.Message;
import com.gavin.imsoftware.bean.User;
import com.gavin.imsoftware.constant.ContentFlag;
import com.gavin.imsoftware.db.MessageDbHelper;
import com.gavin.imsoftware.impl.IhandleMessge;
import com.gavin.imsoftware.tool.StreamTool;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by gavin on 2017/5/6.
 */

public class MessageService {
    private Context context;
    private User user;
    private Socket socket;
    private DataOutputStream output;
    private DataInputStream input;
    private Map<Integer, Bitmap> imgMap = new HashMap<Integer, Bitmap>();	//缓存在线用户头像数据

    public MessageService(Context context) {
        this.context = context;
    }

    /**
     * 建立连接
     * */
    public void startConnect(User user, IhandleMessge handle) throws IOException{
        this.user = user;
        String ip = user.getIp();
        String port = user.getPort();
        long id = user.getId();
        try {
            SocketAddress socketAddress = new InetSocketAddress(InetAddress.getByName(ip),
                    Integer.parseInt(port));
            socket = new Socket();
            socket.connect(socketAddress, 5*1000);
            output = new DataOutputStream(socket.getOutputStream());
            input = new DataInputStream(socket.getInputStream());
            // 处理用户登陆
            String str = ContentFlag.OFFLINE_FLAG + id;
            output.writeUTF(str);
            //缓存其他登陆者的头像数据
            int fileNums = input.readInt();//图片文件的数量
            for (int i = 0; i < fileNums; i++) {
                int tempId = Integer.parseInt(input.readUTF());
                byte[] datas = StreamTool.readStream(input);
                Bitmap tempImg = BitmapFactory.decodeByteArray(datas, 0, datas.length);
                imgMap.put(tempId, tempImg);
            }
            //接受消息
            receiveMsg(handle);
        } catch (IOException e) {
            throw new IOException("fail connect to the server");
        }
    }

    /**
     * 应用退出
     * */
    public void quitApp() {
        String sendStr = "";
        if (user != null) {
            sendStr = ContentFlag.OFFLINE_FLAG + this.user.getId();
        }
        if (socket != null && socket.isClosed()) {
            if (!socket.isOutputShutdown()) {
                try {
                    output.writeUTF(sendStr);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (input != null) {
                            input.close();
                        }
                        if (output != null) {
                            output.close();
                        }
                        if (socket != null) {
                            socket.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void receiveMsg(IhandleMessge handle) throws IOException {
        try {
            while (true) {
                String msgCtn = input.readUTF();
                if (msgCtn.startsWith(ContentFlag.ONLINE_FLAG)) {
                    String json = input.readUTF();
                    Message msg = parseJsonToObject(json);
                    Log.i(ContentFlag.TAG, msg.toString());
                    byte[] datas = StreamTool.readStream(input);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(datas, 0, datas.length);
                    msg.setBitmap(bitmap);
                    handle.handleMsg(msg);
                    imgMap.put(msg.getId(), bitmap);
                } else if (msgCtn.startsWith(ContentFlag.OFFLINE_FLAG)) {
                    String json = input.readUTF();
                    Message msg = parseJsonToObject(json);
                    handle.handleMsg(msg);
                    imgMap.remove(msg.getId());
                } else if (msgCtn.startsWith(ContentFlag.RECORD_FLAG)) {
                    String filename = msgCtn.substring(ContentFlag.RECORD_FLAG.length());
                    File dir = new File(Environment.getExternalStorageDirectory() + "/recordMsg/");
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    File file= new File(dir, filename);
                    String json = input.readUTF();
                    Message msg = parseJsonToObject(json);
                    msg.setRecord_path(file.getAbsolutePath());
                    msg.setBitmap(imgMap.get(msg.getId()));
                    msg.setIfyuyin(true);
                    saveRecordFile(file);
                } else {
                    Message msg = parseJsonToObject(msgCtn);
                    msg.setBitmap(imgMap.get(msg.getId()));
                    handle.handleMsg(msg);
                }
            }
        } catch (Exception e) {
            if (!socket.isClosed()) {
                throw new IOException("fail connect to the server");
            }
        }
    }

    /**
     *
     * 保存录音文件
     */
    private void saveRecordFile(File file) throws IOException {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            byte[] datas = StreamTool.readStream(input);
            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(datas);
        }
    }


    /**
     * 解析json字符串
     * @param json
     * @return
     */
    public Message parseJsonToObject(String json){
        try {
            JSONArray arrays = new JSONArray(json);
            JSONObject jsonObject = arrays.getJSONObject(0);
            int userId = Integer.parseInt(jsonObject.getString(MessageDbHelper.MessageColumns.ID));	//用户的ID
            String send_person = jsonObject.getString(MessageDbHelper.MessageColumns.SEND_PERSON);	//发送者
            String send_ctn = jsonObject.getString(MessageDbHelper.MessageColumns.SEND_CTN);			//发送内容
            String send_date = jsonObject.getString(MessageDbHelper.MessageColumns.SEND_DATE);		//发送时间
            String msg_id = jsonObject.getString(MessageDbHelper.MessageColumns.MSG_ID);
            Message msg = new Message();
            msg.setId(userId);
            msg.setSend_ctn(send_ctn);
            msg.setSend_person(send_person);
            msg.setSend_date(send_date);
            msg.setMsgId(msg_id);
            msg.setIfyuyin(false);
            if(jsonObject.has("recordTime")){
                String recordTime = jsonObject.getString("recordTime");
                msg.setRecordTime(Long.valueOf(recordTime));
            }
            return msg;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 发送消息
     * @param ctn
     * @throws Exception
     */

    public void sendMsg(String ctn) throws Exception {
        output.writeUTF(ctn);
    }

    /**
     * 发送语音消息
     * @param file
     * @param recordTime
     * @throws Exception
     */
    public void sendRecordMsg(File file, long recordTime) throws Exception {
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            String flagLine = ContentFlag.RECORD_FLAG + file.getName();
            //写入标识行
            output.writeUTF(flagLine);
            //写入语音时间
            output.writeLong(recordTime);
            byte[] buffer = new byte[2048];
            int length = 0;
            //写入文件的大小
            output.writeInt((int) file.length());
            while ((length = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, length);
            }
            output.flush();

        } catch (Exception e) {
            throw new Exception();
        } finally {
            try {
                if (file != null) {
                    file.delete();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
