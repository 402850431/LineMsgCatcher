package com.example.linemsgcatch.remote.input;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.linemsgcatch.data.manager.AppManagerKt;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Android使用Volley实现上传文件功能
 * https://www.jb51.net/article/153404.htm
 */
public class FileEntity {
    /**
     * 参数名称
     */
    public String mName;
    /**
     * 上传的文件名
     */
    public String mFileName;
    /**
     * 需要上传的文件
     */
    public File mFile;
    /**
     * 文件的 mime，需要根据文档查询<br/>
     * 默认使用 application/octet-stream  任意的二进制数据
     */
    public String mMime = "application/octet-stream";

    public FileEntity(String mName, String mFileName, File mFile) {
        this.mName = mName;
        this.mFileName = mFileName;
        this.mFile = getFile(mFile);
    }

    public FileEntity(String mName, String mFileName, File mFile, String mMime) {
        this.mName = mName;
        this.mFileName = mFileName;
        this.mFile = getFile(mFile);
        this.mMime = mMime;
    }

    public byte[] getFileBytes() {
        FileInputStream fileInputStream = null;
        ByteArrayOutputStream outputStream = null;
        try {
            fileInputStream = new FileInputStream(mFile);
            outputStream = new ByteArrayOutputStream();
            int len;
            byte[] bytes = new byte[1024];
            while ((len = fileInputStream.read(bytes)) != -1){
                outputStream.write(bytes,0,len);
            }
            return outputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private File getFile(File f){
        File file = new File(AppManagerKt.currentActivity().getCacheDir(), "Image");
        try {
            file.createNewFile();

            Bitmap bitmap = getResizedBitmap(BitmapFactory.decodeFile(f.getPath()), 1024);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100 /*ignored for PNG*/, bos);
            byte[] bitmapdata = bos.toByteArray();
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }
}
