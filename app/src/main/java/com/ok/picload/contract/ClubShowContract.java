package com.ok.picload.contract;

import com.ok.picload.bean.ResponseFoo;
import com.ok.picload.okhttp.OkHttpUtil;


public interface ClubShowContract {

    interface ViewInter {

        void loadOver(ResponseFoo responseFoo);

    }

    interface ModelInter {
        void getImgList(String q, String sn, String pn, OkHttpUtil.RequestCallback callback);

    }
}
