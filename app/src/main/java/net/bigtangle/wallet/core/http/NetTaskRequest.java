package net.bigtangle.wallet.core.http;

import android.app.ProgressDialog;
import android.content.Context;

public class HttpNetTaskRequest {

    private Context context;

    public HttpNetTaskRequest(Context context) {
        this.context = context;
    }

    public void httpRequest(HttpNetComplete httpNetComplete, String serviceAndMethod, Object... params) {
        //实例化网络等待对话框
        final ProgressDialog progressDialog = ProgressDialog.show(context, "请稍候", "数据努力加载中...");
        //设置回调
        HttpNetProgress netProgress = new HttpNetProgress() {
            @Override
            public void endProgress() {
                progressDialog.dismiss();
            }
        };
        new HttpNetTaskDispatch(context, httpNetComplete, netProgress, serviceAndMethod, params).execute();
    }
}
