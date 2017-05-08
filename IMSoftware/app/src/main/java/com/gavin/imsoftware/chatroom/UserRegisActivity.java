package com.gavin.imsoftware.chatroom;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.gavin.imsoftware.R;
import com.gavin.imsoftware.Service.RegisterService;
import com.gavin.imsoftware.constant.ContentFlag;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class UserRegisActivity extends Activity implements View.OnClickListener{

    private EditText edit_ip;
    private EditText edit_port;
    private EditText edit_name;
    private ImageView view_image;
    private PopupWindow popuWindow;
    private View parent;
    private View contentView;
    private Button btn_camera;
    private Button btn_photo;
    private static final int REQUE_CODE_CAMERA = 1;
    private static final int REQUE_CODE_PHOTO = 2;
    private static final int REQUE_CODE_CROP = 3;
    private Bitmap cropBitmap;	//保存裁剪后的图片
    private File cameraFile;	//保存拍照后的图片文件
    private Dialog dialog;
    private RegisterService service = new RegisterService();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.register);
        edit_ip = (EditText) findViewById(R.id.ip);
        edit_port = (EditText) findViewById(R.id.port);
        edit_name = (EditText) findViewById(R.id.name);
        view_image = (ImageView) findViewById(R.id.image);
        contentView = this.getLayoutInflater().inflate(R.layout.popu, null);
        btn_camera = (Button) contentView.findViewById(R.id.camara);
        btn_photo = (Button) contentView.findViewById(R.id.photo);
        parent = findViewById(R.id.regisMain);
        dialog = new Dialog(this, R.style.dialog);
        dialog.setContentView(R.layout.dialog_layout);
        btn_camera.setOnClickListener(this);
        btn_photo.setOnClickListener(this);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.regis_title);	//自定义标题布局文件

    }
    /**
     * 显示PopupWindow用于选择图片上传方式
     */
    public void showPopuWindow(View v){
        if(null == popuWindow){
            popuWindow = new PopupWindow(contentView,LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
            popuWindow.setFocusable(true);
            popuWindow.setBackgroundDrawable(new BitmapDrawable());
            popuWindow.setAnimationStyle(R.style.popu_animation);
        }
        popuWindow.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.photo:
                intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, REQUE_CODE_PHOTO);
                break;
            case R.id.camara:
                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File fileDir;
                if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    fileDir = new File(Environment.getExternalStorageDirectory()+"/user_photos");
                    if(!fileDir.exists()){
                        fileDir.mkdirs();
                    }
                }else{
                    Toast.makeText(this, R.string.sd_noexit, Toast.LENGTH_SHORT);
                    return;
                }
                cameraFile = new File(fileDir.getAbsoluteFile()+"/"+System.currentTimeMillis()+".jpg");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile));
                startActivityForResult(intent, REQUE_CODE_CAMERA);
                break;
            default:
                break;
        }
        popuWindow.dismiss();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUE_CODE_CAMERA:
                    startPhotoZoom(Uri.fromFile(cameraFile));
                    break;
                case REQUE_CODE_PHOTO:
                    if(null!= data){
                        startPhotoZoom(data.getData());
                    }
                    break;
                case REQUE_CODE_CROP:
                    cropBitmap = data.getParcelableExtra("data");
                    if(cropBitmap!= null){
                        view_image.setImageBitmap(cropBitmap);
                    }
                    break;
                default:
                    break;
            }
            //
//			Uri uri = data.getData();
//			Log.v(ContentFlag.TAG, uri.toString());
//			ContentResolver resolver = this.getContentResolver();
//			try {
//				Bitmap bitmap = BitmapFactory.decodeStream(resolver.openInputStream(uri));
//				/* 将Bitmap设定到ImageView */
//				view_image.setImageBitmap(bitmap);
//			} catch (FileNotFoundException e) {
//				Log.e("Exception", e.getMessage(), e);
//			}
			//
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    /**
     * 裁剪图片方法实现
     *
     */
    private void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 100);
        intent.putExtra("outputY", 100);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
        intent.putExtra("noFaceDetection", true);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, REQUE_CODE_CROP);
    }

    /**
     * 处理取消按钮事件
     * @author Administrator
     */
    public void endRegister(View view){
        this.finish();
    }

    public void chat_back(View view){
        this.finish();
    }

    private Handler handle = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            dialog.dismiss();
            switch (msg.what) {
                case 1:
                    //注册成功的标识
                    Intent intent = new Intent(UserRegisActivity.this, ChatActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    break;
                case 2:
                    Toast.makeText(UserRegisActivity.this, R.string.conn_fail, Toast.LENGTH_SHORT).show();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    /**
     * 处理注册按钮事件
     * @author Administrator
     */
    public void startRegister(View view){
        final String ip = edit_ip.getText().toString().trim();
        final String port = edit_port.getText().toString().trim();
        final String name = edit_name.getText().toString();
        if(ip.equals("") || ip == null || port.equals("") || port == null || name.equals("")){
            Toast.makeText(this, R.string.tip_input, Toast.LENGTH_SHORT).show();
            return;
        }
        if(null == cropBitmap){
            Toast.makeText(this, R.string.image_input, Toast.LENGTH_SHORT).show();
            return;
        }
        dialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    service.registerUser(UserRegisActivity.this, cropBitmap, ip, port, name);
                    handle.sendEmptyMessage(1);
                } catch (IOException e) {
                    handle.sendEmptyMessage(2);
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
