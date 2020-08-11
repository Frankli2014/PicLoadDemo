package com.ok.picload.presenter;

import com.alibaba.fastjson.JSON;
import com.ok.picload.bean.ResponseFoo;
import com.ok.picload.contract.ClubShowContract;
import com.ok.picload.model.ImgListModel;
import com.ok.picload.okhttp.OkHttpUtil;

public class ImgListPresenter {


    private ClubShowContract.ViewInter viewInter;
    private ClubShowContract.ModelInter modelInter;

    public ImgListPresenter(ClubShowContract.ViewInter viewInter) {
        this.viewInter = viewInter;
        modelInter = new ImgListModel();
    }
    public void onDetach() {
        if (viewInter != null) {
            viewInter = null;
        }
    }
    //注释
    public void getimglist(String q, String sn, String pn) {
        modelInter.getImgList(q, sn, pn, new OkHttpUtil.RequestCallback() {
            @Override
            public void successful(String content) {
                ResponseFoo postDataBean= JSON.parseObject(content,ResponseFoo.class);
                viewInter.loadOver(postDataBean);
            }

            @Override
            public void onFailure(String content) {
                viewInter.loadOver(null);
            }
        });
    }
}
