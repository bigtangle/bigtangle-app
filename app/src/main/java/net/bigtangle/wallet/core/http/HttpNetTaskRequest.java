package net.bigtangle.wallet.core.http;

import android.app.ProgressDialog;
import android.content.Context;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.yarolegovich.lovelydialog.LovelyInfoDialog;

import net.bigtangle.core.Json;
import net.bigtangle.params.ReqCmd;
import net.bigtangle.wallet.R;

public class HttpNetTaskRequest {

    private Context context;

    public HttpNetTaskRequest(Context context) {
        this.context = context;
    }

    public void httpRequest(ReqCmd reqCmd, byte[] b, HttpNetComplete httpNetComplete) {
        final ProgressDialog progressDialog = ProgressDialog.show(context, "请稍候", "数据努力加载中...");
        HttpNetProgress netProgress = new HttpNetProgress() {
            @Override
            public void endProgress() {
                progressDialog.dismiss();
            }
        };
        new HttpNetTaskDispatch(context, httpNetComplete, netProgress, reqCmd, b).execute();
    }

    public void httpRequest(ReqCmd reqCmd, String s, HttpNetComplete httpNetComplete) {
        final ProgressDialog progressDialog = ProgressDialog.show(context, "请稍候", "数据努力加载中...");
        HttpNetProgress netProgress = new HttpNetProgress() {
            @Override
            public void endProgress() {
                progressDialog.dismiss();
            }
        };
        new HttpNetTaskDispatch(context, httpNetComplete, netProgress, reqCmd, s.getBytes()).execute();
    }

    public void httpRequest(ReqCmd reqCmd, Object o, HttpNetComplete httpNetComplete) {
        final ProgressDialog progressDialog = ProgressDialog.show(context, "请稍候", "数据努力加载中...");
        HttpNetProgress netProgress = new HttpNetProgress() {
            @Override
            public void endProgress() {
                progressDialog.dismiss();
            }
        };
        byte[] b = new byte[0];
        try {
            b = Json.jsonmapper().writeValueAsString(o).getBytes();
        } catch (JsonProcessingException e) {
            new LovelyInfoDialog(this.context)
                    .setTopColorRes(R.color.colorPrimary)
                    .setIcon(R.drawable.ic_info_white_24px)
                    .setTitle("数据解析")
                    .setMessage("当前请求数据解析失败")
                    .show();
            return;
        }
        new HttpNetTaskDispatch(context, httpNetComplete, netProgress, reqCmd, b).execute();
    }
}
