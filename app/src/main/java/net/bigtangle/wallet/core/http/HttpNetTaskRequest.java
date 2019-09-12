package net.bigtangle.wallet.core.http;

import android.app.ProgressDialog;
import android.content.Context;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.yarolegovich.lovelydialog.LovelyInfoDialog;

import net.bigtangle.core.Json;
import net.bigtangle.params.ReqCmd;
import net.bigtangle.wallet.R;
import net.bigtangle.wallet.core.utils.UpdateUtil;

import java.util.HashMap;

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
            final ProgressDialog progressDialog = ProgressDialog.show(context, context.getString(R.string.dialog_please_wait), context.getString(R.string.data_efforts_request_loading));
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
            HashMap<String,Object> infoMap = UpdateUtil.showExceptionInfo(e);
            new LovelyInfoDialog(this.context)
                    .setTopColorRes(R.color.colorPrimary)
                    .setIcon(R.drawable.ic_info_white_24px)
                    .setTitle(infoMap.get("eName").toString())
                    .setMessage(infoMap.get("eInfo").toString())
                    .show();
            return;
        }
        new HttpNetTaskDispatch(context, httpNetComplete, httpNetProgress, reqCmd, b).execute();
    }
}
