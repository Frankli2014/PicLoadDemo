package com.ok.picload.utlis;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.Environment;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.WeakHashMap;

public class ImageLoader {
    private static final String TAG = "ImageLoader";
    public static final String IMAGE_CACHE_PAHT = "/com.bjcsxq.chat.carfriend/head/_hd/";
    private static final int MAX_CAPACITY = 90;

    /**
     * 二级缓存，使用软引用，在内存不足的情况下，虚拟机会自动回收，有效避免了oom异常，提高程序的健壮性
     */
    public WeakHashMap<String, SoftReference<Bitmap>> softImageCache = new WeakHashMap<>(MAX_CAPACITY / 2);

    /**
     * 内存缓存 一级缓存。map 键：图片的名称（最终会转化为路径） 值就是对于的bitmap对象
     */
    public Map<String, Bitmap> imageCache = new LinkedHashMap<String, Bitmap>(
            MAX_CAPACITY / 2, 0.75f, true) {
        private static final long serialVersionUID = -3546825527749923596L;

        protected boolean removeEldestEntry(Entry<String, Bitmap> eldest) {
            if (size() > MAX_CAPACITY) {// 当一级缓存中的bitmap个数大于阈值时，将最久没有使用的值，添加到使用二级缓存的软引用中
                softImageCache.put(eldest.getKey(), new SoftReference<Bitmap>(
                        eldest.getValue()));
                return true;
            }
            return false;
        }
    };



    /**
     * 获取sd卡的根目录
     *
     * @return null表示sd卡未挂载
     */
    public String getSDPath() {
        // 判断sd卡是否挂载
        boolean sdCardExist = Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
        // 判断sd卡是否存在
        if (sdCardExist) {
            return Environment.getExternalStorageDirectory().getAbsolutePath(); // 获取根目录
        } else {
            return null;
        }
    }

    /**
     * 获取sd卡上图片缓存的目录
     *
     * @return null 表示sd卡不可用
     */
    public String getImageCacheDir() {
        String sdPath = getSDPath();
        if (sdPath != null) {
            return sdPath + IMAGE_CACHE_PAHT;
        } else {
            return null;
        }
    }

    /**
     * 放入内存缓存中缓存
     *
     * @param path
     * @param value
     */
    public void addImage2Cache(String path, Bitmap value) {
        if (value == null || path == null) {
            return;
        }
        synchronized (imageCache) {
            imageCache.put(path, value);
        }
    }

    /**
     * 把bitmap对象存入到sd卡中
     */
    public boolean saveImage2SD(Bitmap bitmap, String fileName) {
        if (bitmap == null) {
            return false;
        }
        String destFile = null;
        if (DeviceUtils.externalMemoryAvailable()) {
            FileOutputStream b = null;
            File file = new File(getImageCacheDir());
            if (!file.exists()) {
                file.mkdirs();
            }
            destFile = getImageCacheDir() + fileName + ".jpg";
            File newFile = new File(destFile);
            if (newFile.exists()) {
                newFile.delete();
            }
            try {
                b = new FileOutputStream(destFile);
                boolean compress = bitmap.compress(Bitmap.CompressFormat.JPEG,
                        100, b);// 把数据写入文件
                if (compress) {
                }
                return true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    b.flush();
                    b.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    /**
     * 从内存或者缓存文件中获取bitmap
     */
    public Bitmap getBitmapFromNative(String path) {
        Bitmap bitmap = null;
        // 从一级缓存中拿图片
        bitmap = getFromImageCache(path);
        if (bitmap != null) {
            return bitmap;
        }
        // 从二级缓存中拿图片
        bitmap = getFromSoftImageCache(path);
        if (bitmap != null) {
            return bitmap;
        }
        // 从本地文件拿图片
        bitmap = getImageFromFile(path);
        if (bitmap != null) {
            // 添加到内存缓存
            addImage2Cache(path, bitmap);
        }

        return bitmap;
    }

    /**
     * 从一级缓存中拿
     *
     * @param path
     * @return
     */
    private Bitmap getFromImageCache(String path) {
        Bitmap bitmap = null;
        synchronized (imageCache) {
            bitmap = imageCache.get(path);
            if (bitmap != null) {// 拿到后要改变一下位置:将最近访问的元素放到链的头部，提高下一次访问该元素的检索速度（LRU算法）
                imageCache.remove(path);
                imageCache.put(path, bitmap);
            }
            else {
                imageCache.remove(path);
            }
        }
        return bitmap;
    }

    /**
     * 从二级缓存中拿
     *
     * @param path
     * @return
     */
    private Bitmap getFromSoftImageCache(String path) {
        Bitmap bitmap = null;
        SoftReference<Bitmap> softReference = softImageCache.get(path);
        if (softReference != null) {
            bitmap = softReference.get();
            if (bitmap == null) {// 由于内存吃紧，软引用已经被gc回收了
                softImageCache.remove(path);
            }
        }
        return bitmap;
    }

    /**
     * 从本地文件加载图片
     *
     * @param path
     * @return
     */
    private Bitmap getImageFromFile(String path) {
        // 拼装路径
        if (DeviceUtils.externalMemoryAvailable()) {
            String destPath = getImageCacheDir() + path + ".jpg";
            Bitmap bitmap = null;
            if (new File(destPath).exists()) {
                bitmap = BitmapFactory.decodeFile(destPath);
            }
            return bitmap;
        } else {
            return null;
        }
    }



    /**
     * 图片变成圆角的方法
     *
     * @param source 原bitmap
     * @param radius 圆角大小
     * @return
     */
    public Bitmap toRoundCorners(final Bitmap source, final float radius) {
        if (source == null)
            return null;
        int width = source.getWidth();
        int height = source.getHeight();
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(0xff424242);
        Bitmap clipped = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(clipped);
        canvas.drawRoundRect(new RectF(0, 0, width, height), radius, radius,
                paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        Bitmap rounded = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(rounded);
        canvas.drawBitmap(source, 0, 0, null);
        canvas.drawBitmap(clipped, 0, 0, paint);
        return rounded;
    }

    private LoadListener loadListener;

//	public void loadImage(final String username,LoadListener listener) {
//
//		loadListener = listener;
//		GlobalParameter.pool.execute(new Runnable() {
//
//			public void run() {
//				Bitmap bitmap = loadBitmap(username);
//				if(bitmap==null){
//					ImageUtils.drawableToBitmap(getResources()
//							.getDrawable(R.drawable.notext));
//				}
//
//				userIcon = imageLoader.toRoundCorners(bitmap, 4f);
//				Message msg = handler.obtainMessage();
//				msg.obj = bitmap;
//				msg.what = 0;
//				handler.sendMessage(msg);
//			}
//		});
//	}


    public interface LoadListener {
        public void loadFinish(Bitmap b);
    }
}
