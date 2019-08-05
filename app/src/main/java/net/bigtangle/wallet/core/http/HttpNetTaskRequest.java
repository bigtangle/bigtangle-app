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
    private boolean showDialog;

    public HttpNetTaskRequest(Context context, boolean showDialog) {
        this.context = context;
        this.showDialog = showDialog;
    }

    public HttpNetTaskRequest(Context context) {
        this(context, true);
    }

    public void httpRequest(ReqCmd reqCmd, byte[] b, HttpNetComplete httpNetComplete) {
        HttpNetProgress httpNetProgress = null;
        if (showDialog) {
            final ProgressDialog progressDialog = ProgressDialog.show(context, context.getString(R.string.dialog_please_wait), "数据努力加载中...");
            httpNetProgress = new HttpNetProgress() {
                @Override
                public void endProgress() {
                    progressDialog.dismiss();
                }
            };
        }
        new HttpNetTaskDispatch(context, httpNetComplete, httpNetProgress, reqCmd, b).execute();
    }

    public void httpRequest(ReqCmd reqCmd, String s, HttpNetComplete httpNetComplete) {
        HttpNetProgress httpNetProgress = null;
        if (showDialog) {
            final ProgressDialog progressDialog = ProgressDialog.show(context, context.getString(R.string.dialog_please_wait), context.getString(R.string.network_request_loading));
            httpNetProgress = new HttpNetProgress() {
                @Override
                public void endProgress() {
                    progressDialog.dismiss();
                }
            };
        }
        new HttpNetTaskDispatch(context, httpNetComplete, httpNetProgress, reqCmd, s.getBytes()).execute();
    }

    public void httpRequest(ReqCmd reqCmd, Object o, HttpNetComplete httpNetComplete) {
        HttpNetProgress httpNetProgress = null;
        if (showDialog) {
            final ProgressDialog progressDialog = ProgressDialog.show(context, context.getString(R.string.dialog_please_wait), context.getString(R.string.network_request_loading));
            httpNetProgress = new HttpNetProgress() {
                @Override
                public void endProgress() {
                    progressDialog.dismiss();
                }
            };
        }
        byte[] b = new byte[0];
        try {
            b = Json.jsonmapper().writeValueAsString(o).getBytes();
        } catch (JsonProcessingException e) {
            new LovelyInfoDialog(this.context)
                    .setTopColorRes(R.color.colorPrimary)
                    .setIcon(R.drawable.ic_info_white_24px)
                    .setTitle(context.getString(R.string.data_parsing))
                    .setMessage(context.getString(R.string.network_response_data_failed))
                    .show();
            return;
        }
        new HttpNetTaskDispatch(context, httpNetComplete, httpNetProgress, reqCmd, b).execute();
    }
}
