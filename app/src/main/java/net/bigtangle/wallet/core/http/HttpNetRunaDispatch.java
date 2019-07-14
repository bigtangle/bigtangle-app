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

import net.bigtangle.wallet.R;
import net.bigtangle.wallet.core.constant.MessageStateCode;

public class HttpNetRunaDispatch {

    private final Context context;

    private HttpNetComplete httpNetComplete;

    private HttpNetProgress httpNetProgress;

    private HttpRunaExecute httpRunaExecute;

    public HttpNetRunaDispatch(Context context, HttpNetComplete httpNetComplete, final HttpNetProgress httpNetProgress, HttpRunaExecute httpRunaExecute) {
        this.context = context;
        this.httpNetComplete = httpNetComplete;
        this.httpNetProgress = httpNetProgress;
        this.httpRunaExecute = httpRunaExecute;
    }

    public HttpNetRunaDispatch(Context context, HttpNetComplete httpNetComplete, HttpRunaExecute httpRunaExecute) {
        this.context = context;
        this.httpNetComplete = httpNetComplete;
        final ProgressDialog progressDialog = ProgressDialog.show(context, "请稍候", "数据努力加载中...");
        httpNetProgress = new HttpNetProgress() {
            @Override
            public void endProgress() {
                progressDialog.dismiss();
            }
        };
        this.httpRunaExecute = httpRunaExecute;
    }

    public void execute() {
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
                    if (httpRunaExecute != null) {
                        httpRunaExecute.execute();
                    }
                    Message message = new Message();
                    message.what = MessageStateCode.SUCCESS;
                    httpNetCompleteHandler.sendMessage(message);
                } catch (Exception e) {
                    Message message = new Message();
                    message.what = MessageStateCode.WALLET_ERROR;
                    httpNetCompleteHandler.sendMessage(message);
                } finally {
                    httpNetProgressHandler.sendEmptyMessage(0);
                }
            }
        }).start();
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
                                new HttpNetRunaDispatch(context, httpNetComplete, httpNetProgress, httpRunaExecute).execute();
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                            }
                        })
                        .show();

                return;
            } else if (message.what == MessageStateCode.WALLET_ERROR) {
                new LovelyInfoDialog(context)
                        .setTopColorRes(R.color.colorPrimary)
                        .setIcon(R.drawable.ic_error_white_24px)
                        .setTitle("操作失败")
                        .setMessage("钱包操作失败，请稍候重试")
                        .show();
                return;
            }
            if (httpNetComplete != null) {
                httpNetComplete.completeCallback("");
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
}