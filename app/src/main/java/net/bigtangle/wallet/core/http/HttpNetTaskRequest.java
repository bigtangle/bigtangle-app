package net.bigtangle.wallet.core.http;

import android.app.ProgressDialog;
import android.content.Context;

import net.bigtangle.params.ReqCmd;

public class HttpNetTaskRequest {

    private Context context;

    public HttpNetTaskRequest(Context context) {
        this.context = context;
    }

    public void httpRequest(HttpNetComplete httpNetComplete, ReqCmd reqCmd, byte[] b) {
        final ProgressDialog progressDialog = ProgressDialog.show(context, "请稍候", "数据努力加载中...");
        HttpNetProgress netProgress = new HttpNetProgress() {
            @Override
            public void endProgress() {
                progressDialog.dismiss();
            }
        };
        new HttpNetTaskDispatch(context, httpNetComplete, netProgress, reqCmd, b).execute();
    }
}
