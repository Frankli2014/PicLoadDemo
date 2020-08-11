package com.ok.picload.view;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;

public class SpacesItemDecoration  extends RecyclerView.ItemDecoration  {
    private HashMap<String, Integer> spaceValue; //间隔
    private boolean includeEdge; //是否包含四周边距
    private int spanCount; //列数


    public static final String TOP_SPACE = "top_space";
    public static final String BOTTOM_SPACE = "bottom_space";
    public static final String LEFT_SPACE = "left_space";
    public static final String RIGHT_SPACE = "right_space";

    public SpacesItemDecoration(int spanCount, HashMap<String, Integer> spaceValue, boolean includeEdge) {
        this.spanCount = spanCount;
        this.spaceValue = spaceValue;
        this.includeEdge = includeEdge;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

        //这里是关键，需要根据你有几列来判断
        int position = parent.getChildAdapterPosition(view); // item position
        int column = position % spanCount; // item column


        if (includeEdge) {
            outRect.left = spaceValue.get(LEFT_SPACE) - column * spaceValue.get(LEFT_SPACE) / spanCount;
            outRect.right = (column + 1) * spaceValue.get(RIGHT_SPACE) / spanCount;
            if (position < spanCount) { // top edge 判断是首行
                outRect.top = spaceValue.get(TOP_SPACE);
            }
            outRect.bottom = spaceValue.get(BOTTOM_SPACE); // item bottom
        } else {
            outRect.left = column * spaceValue.get(LEFT_SPACE) / spanCount;
            outRect.right = spaceValue.get(RIGHT_SPACE) - (column + 1) * spaceValue.get(RIGHT_SPACE) / spanCount;
            if (position >= spanCount) {//不是首行
                outRect.top = spaceValue.get(TOP_SPACE); // item top
            }
            //只有首行设置上边距
            //if (position >= spanCount) {//不是首行
            //   outRect.top = spaceValue.get(TOP_SPACE); // item top
            //}else {
            //   outRect.top = 20; //首行
            //}

            //只有下边距
//                if (position > (parent.getAdapter().getItemCount() - spanCount)-1) {
//                    outRect.bottom = 100;
//                }
        }
    }

}
