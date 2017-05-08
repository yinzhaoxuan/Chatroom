package com.gavin.imsoftware.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.gavin.imsoftware.R;

/**
 * Created by gavin on 2017/5/7.
 */

/**
 * 表情页面GridView内容适配器
 */
public class ExpressionGvAdapter extends BaseAdapter {
    private int index; //当前页码
    private int pageItemCount;//每页显示的条目数
    private int[] imageIds; //数据集合(表情资源id)
    private LayoutInflater mInflater;

    public ExpressionGvAdapter(int index, int pageItemCount, int[] imageIds,
                               LayoutInflater inflater) {
        this.index = index;
        this.pageItemCount = pageItemCount;
        this.imageIds = imageIds;
        mInflater = inflater;
    }

    @Override
    public int getCount() {
        int start = index * pageItemCount; //起始位置
        int counts = 0;
        for (int i = start; i < imageIds.length; i++) {
            if (counts == pageItemCount) {
                break;
            }
            counts++;
        }
        return counts;
    }

    @Override
    public Object getItem(int position) {
        return imageIds[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.express_item, null);
            viewHolder = new ViewHolder();
            viewHolder.mImageView = (ImageView) convertView.findViewById(R.id.iv_expre_item);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        int start = index * pageItemCount; //起始位置
        position = position + start;
        viewHolder.mImageView.setImageResource(imageIds[position]);
        return convertView;
    }

    private final class ViewHolder{
        ImageView mImageView;
    }
}
