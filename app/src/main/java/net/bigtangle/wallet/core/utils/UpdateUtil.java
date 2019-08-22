package net.bigtangle.wallet.core.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;

import net.bigtangle.core.Coin;
import net.bigtangle.core.UTXO;
import net.bigtangle.core.Utils;
import net.bigtangle.wallet.core.HttpService;

import java.util.ArrayList;
import java.util.List;

public class UpdateUtil {

    /**
     * 检查是否存在SDCard
     *
     * @return
     */
    public static boolean hasSdcard() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 2 * 获取版本号 3 * @return 当前应用的版本号 4
     */
    public static int getVersion(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(),
                    0);
            String version = info.versionName;
            int versioncode = info.versionCode;
            System.out.println("VersionName:"+version +"-->VersionCode:"+versioncode);
            return versioncode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
