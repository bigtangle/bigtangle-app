package net.bigtangle.wallet.core.utils;

import android.content.Context;

public class RoutePathUtil {

    public static String getBasePath(Context context) {
        return context.getFilesDir().getPath();
    }
}
