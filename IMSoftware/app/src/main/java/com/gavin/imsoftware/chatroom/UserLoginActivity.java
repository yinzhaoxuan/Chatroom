package com.gavin.imsoftware.chatroom;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.gavin.imsoftware.R;
import com.gavin.imsoftware.Service.UserService;
import com.gavin.imsoftware.bean.User;

import java.util.List;

public class UserLoginActivity extends Activity {
    private EditText edit_ip;
    private EditText edit_port;
    private Spinner spin_name;
    private UserService service;
    private ImageView view_image;
    private User curUser;	//保存当前选择的用户

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.login);
        service = new UserService(this);
        edit_ip = (EditText) findViewById(R.id.ip);
        edit_port = (EditText) findViewById(R.id.port);
        spin_name = (Spinner) findViewById(R.id.name);
        view_image = (ImageView) findViewById(R.id.image);
        spin_name.setOnItemSelectedListener(new SpinnerItemClickListner());
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.login_title);	//自定义标题布局文件
        initDatas();
    }

    private final class SpinnerItemClickListner implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent, View view, int position,
                                   long id) {
            Spinner spinner = (Spinner) parent;
            curUser = (User) spinner.getItemAtPosition(position);
            Bitmap bitmap = BitmapFactory.decodeFile(curUser.getImg());
            edit_ip.setText(curUser.getIp());
            edit_port.setText(curUser.getPort());
            view_image.setImageBitmap(bitmap);
        }
        @Override
        public void onNothingSelected(AdapterView<?> arg0) {

        }
    }

    /**
     * 初始化已注册的用户
     */
    private void initDatas() {
        List<User> userList = (List<User>) getIntent().getSerializableExtra(
                "regisUsers");
        // 存在已注册的用户，否则跳转到注册页面
        spin_name.setAdapter(new SpinnerNameAdapter(this, userList,
                R.layout.spinner));
        curUser = userList.get(0);
        Bitmap bitmap = BitmapFactory.decodeFile(curUser.getImg());
        edit_ip.setText(curUser.getIp());
        edit_port.setText(curUser.getPort());
        view_image.setImageBitmap(bitmap);
    }

    /**
     * 处理取消按钮事件
     */
    public void endLogin(View view){
        this.finish();
    }

    public void chat_back(View view){
        this.finish();
    }
    private Handler handle = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                Intent intent = new Intent(UserLoginActivity.this,
                        ChatActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        }
    };

    /**
     * 处理登录按钮事件
     */
    public void startLogin(View view){
        final String ip = edit_ip.getText().toString().trim();
        final String port = edit_port.getText().toString().trim();
        if(ip.equals("") || ip == null || port.equals("") || port == null){
            Toast.makeText(this, R.string.tip_input, Toast.LENGTH_SHORT);
            return;
        }
        curUser.setIp(ip);
        curUser.setPort(port);
        curUser.setFlag(1);
        new Thread(new Runnable() {
            @Override
            public void run() {
                service.convertUser(curUser);
                handle.sendEmptyMessage(1);
            }
        }).start();
    }

    private final class SpinnerNameAdapter extends BaseAdapter {
        private List<User> list;
        private int resource;
        private LayoutInflater inflater;
        public SpinnerNameAdapter(Context context,
                                  List<User> userList, int spinnerItem) {
            this.list = userList;
            this.resource = spinnerItem;
            this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView;
            if(convertView == null){
                convertView = inflater.inflate(resource, null);
                textView = (TextView) convertView.findViewById(R.id.name);
                ViewHolder holder = new ViewHolder();
                holder.textView = textView;
                convertView.setTag(holder);
            }else{
                ViewHolder holder = (ViewHolder) convertView.getTag();
                textView = holder.textView;
            }
            User user = list.get(position);
            textView.setText(user.getName());
            return convertView;
        }

        @Override
        public View getDropDownView(int position, View convertView,
                                    ViewGroup parent) {
            TextView textView;
            ImageView imageView;
            if(convertView == null){
                convertView = inflater.inflate(R.layout.spinner_item, null);
                textView = (TextView) convertView.findViewById(R.id.name);
                imageView = (ImageView) convertView.findViewById(R.id.image);
                ViewHolder holder = new ViewHolder();
                holder.textView = textView;
                holder.imageView = imageView;
                convertView.setTag(holder);
            }else{
                ViewHolder holder = (ViewHolder) convertView.getTag();
                textView = holder.textView;
                imageView = holder.imageView;
            }
            User user = list.get(position);
            Bitmap bitmap = BitmapFactory.decodeFile(user.getImg());
            textView.setText(user.getName());
            imageView.setImageBitmap(bitmap);
            return convertView;
        }
    }
    private class ViewHolder{
        TextView textView;
        ImageView imageView;
    }

}
