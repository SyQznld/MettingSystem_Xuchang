package com.appler.mettingsystem_xuchang.utils;

import android.content.Context;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.appler.mettingsystem_xuchang.metting.MeetingData;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


public class CommonUtil {


    public static final String HUIYI_PATH_NAME = "会议列表";
    /**
     * 获取SD卡路径
     *
     * @return
     */
    public static String getSDPath() {
        String sdPath = null;
        // 判断sd卡是否存在
        boolean sdCardExit = Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
        if (sdCardExit) {
            Log.i("", "SD卡是否存在: " + sdCardExit);
            // 获取根目录
            sdPath = Environment.getExternalStorageDirectory().toString();
        }

        return sdPath;
    }


    public static String getStoragePath(Context mContext, boolean is_removale) {

        StorageManager mStorageManager = (StorageManager) mContext.getSystemService(Context.STORAGE_SERVICE);
        Class<?> storageVolumeClazz = null;
        try {
            storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");
            Method getVolumeList = mStorageManager.getClass().getMethod("getVolumeList");
            Method getPath = storageVolumeClazz.getMethod("getPath");
            Method isRemovable = storageVolumeClazz.getMethod("isRemovable");
            Object result = getVolumeList.invoke(mStorageManager);
            final int length = Array.getLength(result);
            for (int i = 0; i < length; i++) {
                Object storageVolumeElement = Array.get(result, i);
                String path = (String) getPath.invoke(storageVolumeElement);
                boolean removable = (Boolean) isRemovable.invoke(storageVolumeElement);
                if (is_removale == removable) {
                    return path;
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 获取某个文件夹下所有文件夹名称以及路径
     * */
    public static List<MeetingData> getAllHuiYiList(String path) {
        Log.i("", "getAllFileNamesByRoot111111111111: " + path);
        File file = new File(path);
        File[] files = file.listFiles();
        Log.i("", "getAllFileNamesByRoot2222222: " + files.length + files);
        if (files == null) {
            Log.e("error", "空目录");
            return null;
        }
        List<MeetingData> s = new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()){

            MeetingData meetingData = new MeetingData();
            meetingData.setMettingName(files[i].getName());
            meetingData.setMettingPath(files[i].getAbsolutePath());
            s.add(meetingData);
            }
        }
        return s;
    }





    /**
     * 获取某个文件夹下所有文件夹名称以及路径
     * */
    public static List<MeetingData> getAllFileNamesByRoot(String path) {
        Log.i("", "getAllFileNamesByRoot111111111111: " + path);
        File file = new File(path);
        File[] files = file.listFiles();
        Log.i("", "getAllFileNamesByRoot2222222: " + files.length + files);
        if (files == null) {
            Log.e("error", "空目录");
            return null;
        }
        List<MeetingData> s = new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
            MeetingData meetingData = new MeetingData();
            meetingData.setMettingName(files[i].getName());
            meetingData.setMettingPath(files[i].getAbsolutePath());
            s.add(meetingData);
        }
        return s;
    }


    /**
     * 读取指定目录下的所有TXT文件的文件内容
     */
    public static String getFileContent(File file) {
        String content = "";
        if (file.isDirectory()) {    //检查此路径名的文件是否是一个目录(文件夹)
            Log.i("zeng", "The File doesn't not exist "
                    + file.getName().toString() + file.getPath().toString());
        } else {
            if (file.getName().endsWith(".txt")) {//文件格式为txt文件
                try {
                    InputStream instream = new FileInputStream(file);
                    if (instream != null) {
                        InputStreamReader inputreader = new InputStreamReader(instream, "UTF-8");
                        BufferedReader buffreader = new BufferedReader(inputreader);
                        String line = "";
                        //分行读取
                        while ((line = buffreader.readLine()) != null) {
                            content += line + "\n";
                        }
                        instream.close();        //关闭输入流
                    }
                } catch (java.io.FileNotFoundException e) {
                    Log.d("TestFile", "The File doesn't not exist.");
                } catch (IOException e) {
                    Log.d("TestFile", e.getMessage());
                }
            }
        }
        return content;
    }


    /**
     * 在设置完ListView的Adapter后，根据ListView的子项目重新计算ListView的高度，然后把高度再作为LayoutParams设置给ListView，这样它的高度就正确了
     * 只要在设置ListView的Adapter后调用此静态方法即可让ListView正确的显示在其父ListView的ListItem中。但是要注意的是，子ListView的每个Item必须是LinearLayout，不能是其他的，因为其他的Layout(如RelativeLayout)没有重写onMeasure()，所以会在onMeasure()时抛出异常。
     * https://www.cnblogs.com/zhujiabin/p/5751768.html
     * */
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }




    // 将字符串写入到文本文件中
    public static void writeTxtToFile(String strcontent, String filePath, String fileName) {
        //生成文件夹之后，再生成文件，不然会出错
        makeFilePath(filePath, fileName);

        // 每次写入时，都换行写
        String strContent = strcontent + "\r\n";
        try {
            File file = new File(filePath + fileName);
            if (!file.exists()) {
                Log.d("TestFile", "Create the file:" + filePath + fileName);
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            RandomAccessFile raf = new RandomAccessFile(file, "rwd");
            raf.seek(file.length());
            raf.write(strContent.getBytes());
            raf.close();
        } catch (Exception e) {
            Log.e("TestFile", "Error on write File:" + e);
        }
    }

    // 生成文件
    public static File makeFilePath(String filePath, String fileName) {
        File file = null;
        makeRootDirectory(filePath);
        try {
            file = new File(filePath + fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    // 生成文件夹
    public static void makeRootDirectory(String filePath) {
        File file = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                file.mkdir();
            }
        } catch (Exception e) {
            Log.i("error:", e + "");
        }
    }


}
