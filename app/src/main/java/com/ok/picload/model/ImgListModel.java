package com.ok.picload.model;

import com.ok.picload.config.HttpHostConfig;
import com.ok.picload.contract.ClubShowContract;
import com.ok.picload.okhttp.OkHttpUtil;

import java.util.HashMap;

public class ImgListModel implements ClubShowContract.ModelInter {
    private String TAG = "MainActivity";

    //注释
    @Override
    public void getImgList(String q, String sn, String pn, OkHttpUtil.RequestCallback callback) {

        String url = HttpHostConfig.httpHos + "/j";
        HashMap<String, String> params = new HashMap<>();
        params.put("q", q);
        params.put("sn", sn);
        params.put("pn", pn);
        OkHttpUtil.getInstance().get(url, params, callback);
    }



}
