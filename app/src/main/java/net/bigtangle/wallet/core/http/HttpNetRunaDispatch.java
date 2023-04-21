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

import net.bigtangle.wallet.R;
import net.bigtangle.wallet.core.constant.LogConstant;
import net.bigtangle.wallet.core.constant.MessageStateCode;
import net.bigtangle.wallet.core.exception.ToastException;
import net.bigtangle.wallet.core.utils.UpdateUtil;

import java.util.HashMap;

public class HttpNetRunaDispatch {

    private Context context;

    private HttpNetComplete httpNetComplete;

    private HttpNetProgress httpNetProgress;

    private HttpRunaExecute httpRunaExecute;

    public HttpNetRunaDispatch(Context context, HttpNetComplete httpNetComplete, HttpRunaExecute httpRunaExecute) {
        this(context, httpNetComplete, null, httpRunaExecute);
        final ProgressDialog progressDialog = ProgressDialog.show(context, context.getString(R.string.dialog_please_wait), context.getString(R.string.network_request_loading));
        this.httpNetProgress = new HttpNetProgress() {
            @Override
            public void endProgress() {
                progressDialog.dismiss();
            }
        };
    }

    public HttpNetRunaDispatch(Context context, HttpNetComplete httpNetComplete, final HttpNetProgress httpNetProgress, HttpRunaExecute httpRunaExecute) {
        this.context = context;
        this.httpNetComplete = httpNetComplete;
        this.httpNetProgress = httpNetProgress;
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
                } catch (ToastException e) {
                    Message message = new Message();
                    message.what = MessageStateCode.TOAST_ERROR;
                    message.obj = e.getToastMessage();
                    httpNetCompleteHandler.sendMessage(message);
                } catch (Exception e) {
                    Log.e(LogConstant.TAG, context.getString(R.string.wallet_http_request), e);
                    Message message = new Message();
                    message.what = MessageStateCode.WALLET_ERROR;
                    message.obj = UpdateUtil.showExceptionInfo(e);
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
                        .setTitle(context.getString(R.string.dialog_title_info))
                        .setMessage(context.getString(R.string.network_request_failed))
                        .setPositiveButton(android.R.string.ok, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                new HttpNetRunaDispatch(context, httpNetComplete, httpRunaExecute).execute();
                            }
                        }).setNegativeButton(android.R.string.cancel, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                }).show();

                return;
            } else if (message.what == MessageStateCode.WALLET_ERROR) {
                HashMap<String, Object> infoMap = (HashMap<String, Object>) message.obj;
                new LovelyInfoDialog(context)
                        .setTopColorRes(R.color.colorPrimary)
                        .setIcon(R.drawable.ic_error_white_24px)
                        .setTitle(infoMap.get("eName").toString())
                        .setMessage(infoMap.get("eInfo").toString())
                        .show();
                return;
            } else {
                HashMap<String, Object> infoMap = (HashMap<String, Object>) message.obj;
                if (infoMap!=null&&!infoMap.isEmpty()&&!infoMap.containsKey("eInfo"))
                new LovelyInfoDialog(context)
                        .setTopColorRes(R.color.colorPrimary)
                        .setIcon(R.drawable.ic_error_white_24px)
                        .setTitle("")
                        .setMessage(infoMap.get("eInfo").toString())
                        .show();
                return;
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