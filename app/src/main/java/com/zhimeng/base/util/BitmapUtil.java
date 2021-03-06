package com.zhimeng.base.util;

import android.content.ContentResolver;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * author Xianzhuo Rong
 * time   2016/6/20.
 * email rongxianzhuo@gmail.com
 * github https://github.com/rongxianzhuo
 */
public class BitmapUtil {

    public static File getThumbnail(ContentResolver resolver, String path) {
        //先得到缩略图的URL和对应的图片id
        int id = getMediaStoreImageId(resolver, path);
        if (id == -1) return null;
        Cursor cursor = resolver.query(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Thumbnails.IMAGE_ID, MediaStore.Images.Thumbnails.DATA}
                , MediaStore.Images.Thumbnails.IMAGE_ID + "=" + id, null, null);
        if (cursor == null || !cursor.moveToFirst()) return null;
        String s = cursor.getString(1);
        cursor.close();
        return new File(s);
    }

    public static int getMediaStoreImageId(ContentResolver resolver, String path) {
        Cursor cursor = resolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA}
                , null, null, null);
        if (cursor == null || !cursor.moveToFirst()) return -1;
        do {
            if (cursor.getString(1).equals(path)) {
                int id = cursor.getInt(0);;
                cursor.close();
                return id;
            }
        } while (cursor.moveToNext());
        cursor.close();
        return -1;
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
        if (bitmap == null) return null;
        try {
            Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(output);
            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
            final RectF rectF = new RectF(new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()));
            final float roundPx = bitmap.getWidth() / 2;
            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(Color.BLACK);
            canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            final Rect src = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
            canvas.drawBitmap(bitmap, src, rect, paint);
            return output;
        } catch (Exception e) {
            return bitmap;
        }
    }

    public static Bitmap scale(Bitmap image, int newWidth, int newHeight) {
        int width = image.getWidth();
        int height = image.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        try {
            return Bitmap.createBitmap(image, 0, 0, width, height, matrix, true);
        } catch (Exception e) {
            Log.e("AndroidBase", e.getMessage());
        }
        return null;
    }

    public static byte[] bitmapToBytes(Bitmap bm) {
        if (bm == null) return null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    public static boolean saveBitmap(String path, Bitmap image) {
        if (image == null) return false;
        File f = new File(path);
        File father = f.getParentFile();
        if (father == null) {
            Log.e("saveBitmap", "unknown error with path = " + path);
            return false;
        }
        if (!father.exists() && !father.mkdirs()) {
            Log.e("saveBitmap", "can not create file " + path);
            return false;
        }
        FileOutputStream fOut;
        try {
            fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            Log.e("saveBitmap", e.getMessage());
            return false;
        }
        image.compress(Bitmap.CompressFormat.PNG, 100, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static boolean saveBitmap(ContentResolver resolver, Uri uri, Bitmap image) {
        if (image == null) return false;
        OutputStream fOut;
        try {
            fOut = resolver.openOutputStream(uri);
        } catch (Exception e) {
            Log.e("saveBitmap", e.getMessage());
            return false;
        }
        image.compress(Bitmap.CompressFormat.PNG, 100, fOut);
        try {
            assert fOut != null;
            fOut.flush();
            fOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 读取图片属性：旋转的角度
     * @param path 图片绝对路径
     * @return degree旋转的角度
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 读取图片属性：旋转的角度
     * @param stream 图片stream
     * @return degree旋转的角度
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static int readPictureDegree(InputStream stream) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(stream);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    public static Bitmap rotateImage(int angle, Bitmap bitmap) {
        Matrix matrix = new Matrix();
        ;
        matrix.postRotate(angle);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    /**
     * 读取图片
     * @param path 文件路径
     * @param maxSize 最大宽或高
     * @param rotate 旋转角度
     * @return 图片
     */
    public static Bitmap readBitmap(String path, int maxSize, int rotate) {
        File file = new File(path);
        if (!file.exists()) return null;
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, opts);
        // 计算图片缩放比例
        int max = Math.max(opts.outWidth, opts.outHeight);
        opts.inSampleSize = 1;
        while (max > maxSize) {
            opts.inSampleSize = opts.inSampleSize * 2;
            max = max / 2;
        }
        opts.inJustDecodeBounds = false;
        Bitmap image = BitmapFactory.decodeFile(path, opts);
        if (rotate == 0) return image;
        Bitmap toReturn = rotateImage(rotate, image);
        image.recycle();
        return toReturn;
    }

    /**
     * 读取图片
     * @param path 文件路径
     * @param maxPix 最大像素（宽乘以高）
     * @param rotate 旋转角度
     * @return 图片
     */
    public static Bitmap readBitmapByMaxPix(String path, long maxPix, int rotate) {
        File file = new File(path);
        if (!file.exists()) return null;
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, opts);
        // 计算图片缩放比例
        long max = opts.outWidth * opts.outHeight;
        opts.inSampleSize = 1;
        while (max > maxPix) {
            opts.inSampleSize = opts.inSampleSize * 2;
            max = max / 4;
        }
        opts.inJustDecodeBounds = false;
        Bitmap image = BitmapFactory.decodeFile(path, opts);
        if (rotate == 0) return image;
        Bitmap toReturn = rotateImage(rotate, image);
        image.recycle();
        return toReturn;
    }

    /**
     * 读取图片
     * @param resolver ContentResolver
     * @param uri 图片uri
     * @param maxPix 最大像素（宽乘以高）
     * @return 图片
     */
    public static Bitmap readBitmapByMaxPix(ContentResolver resolver, Uri uri, long maxPix, int rotate) throws Exception {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(resolver.openInputStream(uri), null, opts);
        // 计算图片缩放比例
        long max = opts.outWidth * opts.outHeight;
        opts.inSampleSize = 1;
        while (max > maxPix) {
            opts.inSampleSize = opts.inSampleSize * 2;
            max = max / 4;
        }
        opts.inJustDecodeBounds = false;
        Bitmap image = BitmapFactory.decodeStream(resolver.openInputStream(uri), null, opts);
        if (rotate == 0) return image;
        Bitmap toReturn = rotateImage(rotate, image);
        image.recycle();
        return toReturn;
    }
}
