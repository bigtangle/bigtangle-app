package net.bigtangle.wallet.core.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import net.bigtangle.wallet.core.constant.LogConstant;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;

public class UpdateUtil {

    /**
     *  获取版本号
     */
    public static int getVersion(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            String versionName = info.versionName;
            int versionCode = info.versionCode;
            Log.d(LogConstant.TAG, "VersionName:" + versionName + "-->VersionCode:" + versionCode);
            return versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     *  获取版本序列
     */
    public static String getVersionName(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            String versionName = info.versionName;
            int versionCode = info.versionCode;
            Log.d(LogConstant.TAG, "VersionName:" + versionName + "-->VersionCode:" + versionCode);
            return versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "V1.0.0";
    }

    public static  HashMap<String,Object> showExceptionInfo(Exception e) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        e.printStackTrace(printWriter);
        printWriter.close();
        map.put("eName",e.getMessage());
        map.put("eInfo",writer.toString());
        return  map;
    }

    public static  void closeApp(){
        android.os.Process.killProcess(android.os.Process.myPid());
    }

}
