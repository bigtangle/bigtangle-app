package net.bigtangle.wallet.core.http;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.yarolegovich.lovelydialog.LovelyInfoDialog;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import net.bigtangle.core.Json;
import net.bigtangle.params.ReqCmd;
import net.bigtangle.utils.OkHttp3Util;
import net.bigtangle.wallet.R;
import net.bigtangle.wallet.core.constant.HttpConnectConstant;
import net.bigtangle.wallet.core.constant.MessageStateCode;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.HashMap;

@SuppressLint("HandlerLeak")
public class HttpNetTaskDispatch {

    private final Context context;

    private HttpNetComplete httpNetComplete;

    private HttpNetProgress httpNetProgress;

    private ReqCmd reqCmd;

    private byte[] buf;

    public HttpNetTaskDispatch(Context context, HttpNetComplete httpNetComplete, final HttpNetProgress httpNetProgress, ReqCmd reqCmd, byte[] b) {
        this.context = context;
        this.httpNetComplete = httpNetComplete;
        this.httpNetProgress = httpNetProgress;
        this.reqCmd = reqCmd;
        this.buf = b;
    }

    public void execute() {
        //判断当前网络连接状态
        if (!HttpNetUtil.checkNetworkState(context)) {
            if (httpNetProgress != null) {
                httpNetProgress.endProgress();
            }

            Message message = new Message();
            message.what = MessageStateCode.NETWORK_ERROR;
            httpNetCompleteHandler.sendMessage(message);
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    newThreadHttpPostRequestProcess();
                } finally {
                    httpNetProgressHandler.sendEmptyMessage(0);
                }
            }
        }).start();
    }

    public void newThreadHttpPostRequestProcess() {
        try {
            String jsonStr = doInBackground();
            Message message = new Message();
            message.obj = jsonStr;
            message.what = MessageStateCode.SUCCESS;
            httpNetCompleteHandler.sendMessage(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler httpNetCompleteHandler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            if (message.what == MessageStateCode.NETWORK_ERROR) {
                new LovelyStandardDialog(context, LovelyStandardDialog.ButtonLayout.HORIZONTAL)
                        .setTopColorRes(R.color.colorPrimary)
                        .setButtonsColor(Color.WHITE)
                        .setIcon(R.drawable.ic_info_white_24px)
                        .setTitle("提示")
                        .setMessage("网络请求失败，是否重新？")
                        .setPositiveButton(android.R.string.ok, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final ProgressDialog progressDialog = ProgressDialog.show(context, "请稍候", "数据努力加载中...");
                                HttpNetProgress httpNetProgress = new HttpNetProgress() {
                                    @Override
                                    public void endProgress() {
                                        progressDialog.dismiss();
                                    }
                                };
                                new HttpNetTaskDispatch(context, httpNetComplete, httpNetProgress, reqCmd, buf).execute();
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                            }
                        })
                        .show();
                return;
            }

            String jsonStr = (String) message.obj;
            HashMap<String, Object> result = null;
            try {
                result = (HashMap) Json.jsonmapper().readValue(jsonStr, HashMap.class);
            } catch (IOException e) {
                new LovelyInfoDialog(context)
                        .setTopColorRes(R.color.colorPrimary)
                        .setIcon(R.drawable.ic_info_white_24px)
                        .setTitle("数据解析")
                        .setMessage("当前响应数据解析失败")
                        .show();
                return;
            }
            if (result.get("errorcode") != null) {
                int error = (Integer) result.get("errorcode");
                if (error > 0) {
                    String msg = (String) result.get("message");
                    if (StringUtils.isBlank(msg)) {
                        msg = "";
                    }
                    new LovelyStandardDialog(context, LovelyStandardDialog.ButtonLayout.HORIZONTAL)
                            .setTopColorRes(R.color.colorPrimary)
                            .setButtonsColor(Color.WHITE)
                            .setIcon(R.drawable.ic_info_white_24px)
                            .setTitle("提示")
                            .setMessage("服务器操作失败, msg " + msg + "，是否重新？")
                            .setPositiveButton(android.R.string.ok, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    final ProgressDialog progressDialog = ProgressDialog.show(context, "请稍候", "数据努力加载中...");
                                    HttpNetProgress httpNetProgress = new HttpNetProgress() {
                                        @Override
                                        public void endProgress() {
                                            progressDialog.dismiss();
                                        }
                                    };
                                    new HttpNetTaskDispatch(context, httpNetComplete, httpNetProgress, reqCmd, buf).execute();
                                }
                            })
                            .setNegativeButton(android.R.string.cancel, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                }
                            })
                            .show();
                    return;
                }
            }

            if (httpNetComplete != null) {
                //处理回调接口
                httpNetComplete.completeCallback((String) message.obj);
            }
        }
    };

    private Handler httpNetProgressHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (httpNetProgress != null) {
                httpNetProgress.endProgress();
            }
            super.handleMessage(msg);
        }
    };

    private String doInBackground() throws Exception {
        String s = OkHttp3Util.post(HttpConnectConstant.HTTP_SERVER_URL + reqCmd.name(), buf);
        return s;
    }
}