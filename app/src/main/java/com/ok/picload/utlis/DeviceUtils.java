package com.ok.picload.utlis;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;


import java.io.File;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.UUID;


public class DeviceUtils {

    static final int ERROR = -1;
    private static final String TAG = "DeviceUtils";

    static public boolean externalMemoryAvailable() {
        return Environment.MEDIA_MOUNTED
                .equals(Environment.getExternalStorageState());
    }

    static public long getAvailableInternalMemorySize() {

        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize;
    }

    static public long getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return totalBlocks * blockSize;
    }

    public static int getScreenWith(Context context) {
        WindowManager manager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        return display.getWidth();
    }

    public static int getScreenHeight(Context context) {
        WindowManager manager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        return display.getHeight();
    }


    /**
     * 获取屏幕的比例
     *
     * @return
     */
    public static float getScaledDensity(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        float value = dm.scaledDensity;
        return value;
    }

    static public long getAvailableExternalMemorySize() {
        if (externalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            return availableBlocks * blockSize;
        } else {
            return ERROR;
        }
    }

    static public long getTotalExternalMemorySize() {
        if (externalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long totalBlocks = stat.getBlockCount();
            return totalBlocks * blockSize;
        } else {
            return ERROR;
        }
    }

    static public String formatSize(long size) {
        String suffix = null;

        if (size >= 1024) {
            suffix = "KiB";
            size /= 1024;
            if (size >= 1024) {
                suffix = "MiB";
                size /= 1024;
            }
        }

        StringBuilder resultBuffer = new StringBuilder(Long.toString(size));

        int commaOffset = resultBuffer.length() - 3;
        while (commaOffset > 0) {
            resultBuffer.insert(commaOffset, ',');
            commaOffset -= 3;
        }

        if (suffix != null)
            resultBuffer.append(suffix);
        return resultBuffer.toString();
    }

    /**
     * 根据手机的分辨率从 dip 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

//    /**
//     * 判断网络是否有效
//     *
//     * @param context
//     * @return
//     */
//    public static boolean isNetWorkAvailable(Context context) {
//        try {
//            ConnectivityManager cm = (ConnectivityManager) context
//                    .getSystemService(Context.CONNECTIVITY_SERVICE);
//            NetworkInfo netinfo = cm.getActiveNetworkInfo();
//            return netinfo.isAvailable();
//        } catch (Exception e) {
//
//            e.printStackTrace();
//        }
//        return false;
//    }

    /**
     * 返回当前应用的程序版本号
     *
     * @return
     */
    public static String getVersion(Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo packInfo = pm.getPackageInfo(context.getPackageName(),
                    0);
            return packInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }







    public static Bitmap scaleBitmap(Bitmap bitmap, int w, int h) {
        Matrix matrix = new Matrix();

        float sx = 1f;
        float sy = 1.5f;
        if (w != 0) {
            sx = (float) w / (float) bitmap.getWidth();
        }

        if (h != 0) {
            sy = (float) h / bitmap.getWidth();
        }
        sx = 1.5f;
        matrix.postScale(sx, sy); // 长和宽放大缩小的比例
        // matrix.postTranslate(sx, sy);

        Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight(), matrix, true);
        return resizeBmp;
    }

    public static Bitmap setBitmapSize(Bitmap bitmap, int w, int h) {
        Matrix matrix = new Matrix();

        float sx = 1f;
        float sy = 1f;
        if (w != 0) {
            sx = (float) w / (float) bitmap.getWidth();
        }

        if (h != 0) {
            sy = (float) h / bitmap.getWidth();
        }

        matrix.postScale(sx, sy); // 长和宽放大缩小的比例
        // matrix.postTranslate(sx, sy);

        Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight(), matrix, true);
        return resizeBmp;
    }

    /**
     * 获取手机ip
     *
     * @return
     */
    public static String getLocalIpAddress() {
        StringBuilder sb = new StringBuilder();
        try {

            for (Enumeration en = NetworkInterface.getNetworkInterfaces(); en
                    .hasMoreElements(); ) {

                NetworkInterface intf = (NetworkInterface) en.nextElement();

                for (Enumeration enumIpAddr = intf.getInetAddresses(); enumIpAddr
                        .hasMoreElements(); ) {

                    InetAddress inetAddress = (InetAddress) enumIpAddr
                            .nextElement();

                    if (!inetAddress.isLoopbackAddress()) {
                        String ip = inetAddress.getHostAddress().toString();
                        if (!ip.contains("::") && !ip.contains(":")) {
                            return inetAddress.getHostAddress().toString();
                        }

                    }

                }

            }

        } catch (SocketException ex) {


        }
        return null;
    }

    /**
     * /data/data/com.xp110.word/
     *
     * @param context
     * @return
     */
    public static String getInternalDirectory(Context context) {
        String pageName = context.getPackageName();
        return Environment.getDataDirectory() + "/data/" + pageName + "/";
    }

    public static String getAppVersion(Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo packInfo = pm.getPackageInfo(context.getPackageName(),
                    0);
            return packInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 将版本号转换为浮点型
     *
     * @param version
     * @return
     */

    public static float conversVersion(String version) {
        String firstNum = version.substring(0, 1);
        String newVersion = firstNum + "." + version.substring(1, version.length()).replace(".",
                "");
        float nV = 0;
        try {
            nV = Float.parseFloat(newVersion);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return nV;
    }

//    public static int getVersionCode(Context context)//获取版本号(内部识别号)
//    {
//        try {
//            PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
//            return pi.versionCode;
//        } catch (NameNotFoundException e) {
//            e.printStackTrace();
//            return 0;
//        }
//    }




    /**
     * SrollView嵌套listview 解决listview显示不全的问题
     */
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(),
                View.MeasureSpec.AT_MOST);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0) {
                view.setLayoutParams(new AbsListView.LayoutParams(desiredWidth,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
            }
            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();

    }


    /**
     * @param str
     * @return 转换格式
     */
    public static String encodeStrUif8(String str) {
        str = str.trim();
        try {
            str = URLEncoder.encode(URLEncoder.encode(str, "UTF-8"), "UTF-8");

        } catch (Exception ex) {
            return ex.getMessage().toString();
        }
        return str;
    }

    public static String getOSVersion() {
        return Build.VERSION.RELEASE;
    }



    public static String getDataYyMm() {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy/MM");
        String date = sDateFormat.format(new java.util.Date());
        return date;
    }


    /**
     * 得到系统版本
     *
     * @return
     */
    public static String getSystemVersion() {
        return Build.VERSION.RELEASE;
    }

    /**
     * 获取手机型号
     *
     * @return 手机型号
     */
    public static String getSystemModel() {
        return Build.MODEL;
    }

    /**
     * 获取手机厂商
     *
     * @return 手机厂商
     */
    public static String getDeviceBrand() {
        return Build.BRAND;
    }

}
