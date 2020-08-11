package com.ok.picload.adpter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ok.picload.R;
import com.ok.picload.bean.ResponseFoo;

public class FooAdapter extends BaseAdapter {

    private ResponseFoo foo;
    private Context context;

    public FooAdapter(ResponseFoo foo, Context context) {
        this.foo = foo;
        this.context = context;
    }

    @Override
    public int getCount() {
        return foo.getList().size();
    }

    @Override
    public Object getItem(int position) {
        return foo.getList().get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_gird_img, null);
            holder = new ViewHolder();
            holder.iv = (ImageView) convertView.findViewById(R.id.item_iv);
            holder.tv = (TextView) convertView.findViewById(R.id.item_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        ResponseFoo.ListBean listBean = foo.getList().get(position);
        Glide.with(context).load(listBean.getThumb()).into(holder.iv);
        holder.tv.setText(listBean.getTitle());
        return convertView;
    }

    static class ViewHolder {
        ImageView iv;
        TextView tv;
    }
}
