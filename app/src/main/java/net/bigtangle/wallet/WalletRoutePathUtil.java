package net.bigtangle.wallet;

import android.content.Context;

import java.io.File;

public class WalletRoutePathUtil {

    public static boolean existAnyWallet(Context context) {
        File file = new File(formatFilePath(context, WalletConstant.WALLET_FILE_PREFIX));
        return file.exists();
    }

    public static String formatFilePath(Context context, String filename) {
        return context.getFilesDir().getPath() + "/" + filename;
    }

    public static String getBasePath(Context context) {
        return context.getFilesDir().getPath();
    }
}
