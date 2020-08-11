package com.ok.picload.adpter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ok.picload.R;
import com.ok.picload.bean.ResponseFoo;
import com.ok.picload.utlis.DeviceUtils;

import java.util.List;

public class RecyclerPostAdapter extends RecyclerView.Adapter<RecyclerPostAdapter.GridViewHolder> implements View.OnClickListener {


    private Context mContext;
    //RecyclerView所需的类
    private List<ResponseFoo.ListBean> items;

    private boolean mShowFooter = true;

    private FooterViewHolder footerViewHolder;

    private final int TYPE_ITEM = 0;
    private final int TYPE_FOOTER = 1;

    int width;
    //构造方法，一般需要接收两个参数 1.上下文 2.集合对象（包含了我们所需要的数据）
    public RecyclerPostAdapter(Context mContext, List<ResponseFoo.ListBean> items){
        this.mContext = mContext;
        this.items = items;
        width = DeviceUtils.getScreenWith(mContext);
    }

    public void setListDatas(List data) {
        this.items = data;
    }

    @Override
    public GridViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        if (viewType == TYPE_ITEM) {
            //转换一个ViewHolder对象，决定了item的样式，参数1.上下文 2.XML布局资源 3.null
            View itemView = View.inflate(mContext, R.layout.item_gird_img, null);
            //创建一个ViewHodler对象
            GridViewHolder gridViewHolder = new GridViewHolder(itemView);
            //把ViewHolder传出去
            return gridViewHolder;
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.footer, null);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            footerViewHolder = new FooterViewHolder(view);

            return null;
        }


    }

    @Override
    public void onBindViewHolder(GridViewHolder holder, int position) {
        ResponseFoo.ListBean item = items.get(position);
        holder.setData(item);
    }

    @Override
    public int getItemCount() {
        return null != items ? items.size() : 0;
    }

    @Override
    public void onClick(View view) {

    }

    public class GridViewHolder extends RecyclerView.ViewHolder {
        private ImageView itemIcon;
        private TextView itemName;

        public GridViewHolder(View itemView) {
            super(itemView);
            itemIcon = (ImageView) itemView.findViewById(R.id.item_iv);
            itemName = (TextView) itemView.findViewById(R.id.item_name);

        }

        public void setData(ResponseFoo.ListBean item){
            // 根据屏幕宽度动态设置图片宽高
            int imageWidth = (width / 3 - 20);
            ViewGroup.LayoutParams lp = itemIcon.getLayoutParams();
            lp.width = imageWidth;
            lp.height = imageWidth;
            itemIcon.setLayoutParams(lp);
            itemName.setText(item.getTitle().replaceAll("<em>","").replaceAll("</em>",""));
            Glide.with(mContext).load(item.getThumb()).into(itemIcon);
        }

    }

    public void setFooterStyle(boolean isEnd) {
        if (footerViewHolder != null) {
            if (isEnd) {
                footerViewHolder.progressBarLoad.setVisibility(View.VISIBLE);
                footerViewHolder.tvLoad.setText("正在加载");
            } else {
                footerViewHolder.progressBarLoad.setVisibility(View.GONE);
                footerViewHolder.tvLoad.setText("没有更多数据了");

            }
        }

    }
    public boolean isShowFooter() {
        return this.mShowFooter;
    }
    public void setShowFooter(boolean showFooter) {

        this.mShowFooter = showFooter;
    }

    public class FooterViewHolder extends  RecyclerView.ViewHolder {
        public ProgressBar progressBarLoad;
        public TextView tvLoad;

        public FooterViewHolder(View view) {
            super(view);
            progressBarLoad = (ProgressBar) itemView.findViewById(R.id.pragass);
            tvLoad = (TextView) itemView.findViewById(R.id.more_data_msg);

        }

    }

}
