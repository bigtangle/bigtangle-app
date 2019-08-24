package net.bigtangle.wallet.core.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import net.bigtangle.wallet.core.constant.LogConstant;

public class UpdateUtil {

    /**
     * 2 * 获取版本号 3 * @return 当前应用的版本号 4
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
     * 2 * 获取版本号 3 * @return 当前应用的版本号 4
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
        return "V.1.0.0";
    }

    public static  void closeApp(){
        android.os.Process.killProcess(android.os.Process.myPid());
    }

}
