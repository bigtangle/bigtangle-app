package net.bigtangle.wallet.core.http;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.yarolegovich.lovelydialog.LovelyInfoDialog;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import net.bigtangle.core.Json;
import net.bigtangle.params.ReqCmd;
import net.bigtangle.utils.OkHttp3Util;
import net.bigtangle.wallet.R;
import net.bigtangle.wallet.core.constant.HttpConnectConstant;
import net.bigtangle.wallet.core.constant.LogConstant;
import net.bigtangle.wallet.core.constant.MessageStateCode;
import net.bigtangle.wallet.core.exception.ToastException;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.text.MessageFormat;
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
        Log.d(LogConstant.TAG, reqCmd.name());
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
        Message message = new Message();
        try {
            String jsonStr = doInBackground();
            message.obj = jsonStr;
            message.what = MessageStateCode.SUCCESS;
            httpNetCompleteHandler.sendMessage(message);
        } catch (ToastException e) {
            message.what = MessageStateCode.TOAST_ERROR;
            message.obj = e.getToastMessage();
            httpNetCompleteHandler.sendMessage(message);
        } catch (Exception e) {
            message.what = MessageStateCode.NETWORK_ERROR;
            httpNetCompleteHandler.sendMessage(message);
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
                        .setIcon(R.drawable.ic_error_white_24px)
                        .setTitle(context.getString(R.string.dialog_title_error))
                        .setMessage(context.getString(R.string.network_request_failed))
                        .setPositiveButton(android.R.string.ok, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final ProgressDialog progressDialog = ProgressDialog.show(context, context.getString(R.string.dialog_please_wait), context.getString(R.string.network_request_loading));
                                HttpNetProgress httpNetProgress = new HttpNetProgress() {
                                    @Override
                                    public void endProgress() {
                                        progressDialog.dismiss();
                                    }
                                };
                                new HttpNetTaskDispatch(context, httpNetComplete, httpNetProgress, reqCmd, buf).execute();
                            }
                        }).setNegativeButton(android.R.string.cancel, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                            }
                }).show();
                return;
            } else if (message.what == MessageStateCode.TOAST_ERROR) {
                Toast toast = Toast.makeText(context, (String) message.obj, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM, 0, 0);
                toast.show();
            } else {
                String jsonStr = (String) message.obj;
                HashMap<String, Object> result = null;
                try {
                    result = (HashMap) Json.jsonmapper().readValue(jsonStr, HashMap.class);
                } catch (IOException e) {
                    new LovelyInfoDialog(context)
                            .setTopColorRes(R.color.colorPrimary)
                            .setIcon(R.drawable.ic_error_white_24px)
                            .setTitle(context.getString(R.string.dialog_title_error))
                            .setMessage(context.getString(R.string.network_response_data_failed))
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
                        String str = MessageFormat.format(context.getString(R.string.server_processing_failed), msg);
                        new LovelyStandardDialog(context, LovelyStandardDialog.ButtonLayout.HORIZONTAL)
                                .setTopColorRes(R.color.colorPrimary)
                                .setButtonsColor(Color.WHITE)
                                .setIcon(R.drawable.ic_error_white_24px)
                                .setTitle(context.getString(R.string.dialog_title_error))
                                .setMessage(str)
                                .setPositiveButton(android.R.string.ok, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        final ProgressDialog progressDialog = ProgressDialog.show(context, context.getString(R.string.dialog_please_wait), context.getString(R.string.network_request_loading));
                                        HttpNetProgress httpNetProgress = new HttpNetProgress() {
                                            @Override
                                            public void endProgress() {
                                                progressDialog.dismiss();
                                            }
                                        };
                                        new HttpNetTaskDispatch(context, httpNetComplete, httpNetProgress, reqCmd, buf).execute();
                                    }
                                }).setNegativeButton(android.R.string.cancel, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                    }
                        }).show();
                        return;
                    }
                }

                if (httpNetComplete != null) {
                    httpNetComplete.completeCallback((String) message.obj);
                }
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