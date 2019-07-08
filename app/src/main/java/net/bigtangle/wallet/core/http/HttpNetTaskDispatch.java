package net.bigtangle.wallet.core.http;

import java.util.HashMap;
import java.util.Map;

import org.jiangtao.clouds.app.data.ApplicationDataContext;
import org.jiangtao.clouds.app.utils.JSONUtils;
import org.jiangtao.clouds.app.utils.LoggerUtils;
import org.jiangtao.transfer.bean.entity.JSONResult;
import org.jiangtao.transfer.bean.utils.ErrorCode;

import android.annotation.SuppressLint;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Handler;
import android.os.Message;

import net.bigtangle.params.ReqCmd;

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
            Map<String, String> errors = new HashMap<String, String>();
            errors.put("alert", "网络处于断开状态");
            JSONResult jsonResult = new JSONResult(errors, ErrorCode.NETWORK_ERROR);
            if (netComplete != null) {
                netComplete.complete(jsonResult);
            }
            endProgress();
            return;
        }
        //网络请求要通过线程进行回调
        new Thread(new Runnable() {
            @Override
            public void run() {
                execute0();
                netProgressHandler.sendEmptyMessage(0);
            }
        }).start();
    }

    public void execute0() {
        LoggerUtils.debug("sssssssssss");
        Object jsonData = doInBackground();
        String s = (String) jsonData;
        JSONResult jsonResult;
        if (s.equals("")) {
            Map<String, String> errors = new HashMap<String, String>();
            errors.put("alert", "连接服务器超时");
            jsonResult = new JSONResult(errors, ErrorCode.NETWORK_ERROR);
        } else {
            jsonResult = JSONUtils.fromJSON((String) s, JSONResult.class);
        }
        if (netComplete != null) {
            Message message = new Message();
            LoggerUtils.info("响应信息 ---------------------------------------------------");
            LoggerUtils.info("errorCode:" + jsonResult.getErrorCode());
            LoggerUtils.info("errors:" + jsonResult.getErrors());
            LoggerUtils.info("data:" + jsonResult.getData());
            LoggerUtils.info("------------------------------------------------------------");
            message.obj = jsonResult;
            netCompleteHandler.sendMessage(message);
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler netCompleteHandler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            try {
                JSONResult jsonResult = (JSONResult) message.obj;
                //如果当前数据为网络错误
                if (jsonResult.getErrorCode() == ErrorCode.NETWORK_ERROR) {
                    final String alert = jsonResult.getErrors().get("alert") == null ? "" : jsonResult.getErrors().get("alert");
                    Builder builder = new Builder(ui);
                    builder.setMessage(alert + "，是否重新？").setTitle("提示").setPositiveButton("确认",
                            new OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //实例化网络等待对话框
                                    final ProgressDialog progressDialog = ProgressDialog.show(ui, "请稍候", "数据努力加载中...");
                                    //点击确认重新获取数据
                                    NetProgress netProgress = new NetProgress() {
                                        @Override
                                        public void endProgress() {
                                            progressDialog.dismiss();
                                        }
                                    };
                                    new NetTaskDispatch(ui, netComplete, netProgress, serviceAndMethod, params).execute();
                                    dialog.dismiss();
                                }
                            }
                    ).setNegativeButton("取消",
                            new OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }
                    ).show();
                    return;
                }
                //处理提示信息
                netComplete.alert(ui, jsonResult);
                if (jsonResult.getErrorCode() == ErrorCode.USER_LOGIN) {
                    applicationDataContext.clearUser();
                    applicationDataContext.checkAccountIsLoginAndForward(ui);
                    ui.finish();
                    return;
                }
                //处理回调接口
                netComplete.complete(jsonResult);
            } catch (Exception e) {
                e.printStackTrace();
            }
            super.handleMessage(message);
        }
    };

    private Handler netProgressHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (httpNetProgress != null) {
                httpNetProgress.endProgress();
            }
            super.handleMessage(msg);
        }
    };

    private String doInBackground() {
        String s = HttpUtil.sendRequest(serviceAndMethod, params);
        return s;
    }

}