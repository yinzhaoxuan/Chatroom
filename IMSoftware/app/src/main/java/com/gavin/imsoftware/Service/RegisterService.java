package com.gavin.imsoftware.Service;

import android.content.Context;
import android.graphics.Bitmap;

import com.gavin.imsoftware.bean.User;
import com.gavin.imsoftware.constant.ContentFlag;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * Created by gavin on 2017/5/6.
 */

public class RegisterService {
    /**
     * 注册用户
     * */
    public void registerUser(Context context, Bitmap cropBitmap, String ip, String port,
                             String name) throws IOException {
        UserService userService = new UserService(context);
        ByteArrayOutputStream byteoutput = new ByteArrayOutputStream();
        cropBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteoutput);
        byte datas[] = byteoutput.toByteArray();
        DataOutputStream output = null;
        DataInputStream input = null;
        Socket socket = null;
        long rowId = 0;
        try {
            User user = new User(ip, port, name, cropBitmap, 1);
            SocketAddress socketAddress = new InetSocketAddress(InetAddress.getByName(ip),
                    Integer.parseInt(port));
            socket = new Socket();
            socket.connect(socketAddress, 3000);
            output = new DataOutputStream(socket.getOutputStream());
            input = new DataInputStream(socket.getInputStream());
            String flagLine = ContentFlag.REGOSTER_FLAG + name;
            output.writeUTF(flagLine);
            //output.writeInt(datas.length);
            output.write(datas);

            //读取服务器分配给用户的唯一标识
            rowId = Long.valueOf(input.readUTF());
            user.setId(rowId);
            userService.insertUser(user);
//            if (cropBitmap != null) {
//                cropBitmap.recycle();
//            }
        } catch (IOException e) {
            throw new IOException("fail connect to the server");
        } finally {
            try {
                if(byteoutput != null)
                    byteoutput.close();
                if (output != null){
                    output.flush();
                    output.close();
                }
                if (input != null)
                    input.close();
                if (socket != null)
                    socket.close();
                if (cropBitmap != null) {
                cropBitmap.recycle();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
