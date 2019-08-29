package net.bigtangle.wallet.core.update;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.yarolegovich.lovelydialog.LovelyInfoDialog;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import net.bigtangle.wallet.R;
import net.bigtangle.wallet.core.LocalStorageContext;
import net.bigtangle.wallet.core.constant.LogConstant;
import net.bigtangle.wallet.core.utils.UpdateUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public class UpdateManager {

    private Context mContext;

    private AppNetInfo appNetInfo;

    //返回的 app 包 版本号
    private String apkVersionUrl = "https://bigtangle.oss-cn-beijing.aliyuncs.com/download/app.version";

    private Dialog downloadDialog;

    private String apkDownloadPath;

    private String apkDownloadName;

    /* 进度条与通知ui刷新的handler和msg常量 */
    private ProgressBar mProgress;

    private static final int DOWN_UPDATE = 1;

    private static final int DOWN_OVER = 2;

    private int progress;

    private Thread downLoadThread;

    private boolean interceptFlag = false;

    private Activity mActivity;

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DOWN_UPDATE:
                    mProgress.setProgress(progress);
                    break;
                case DOWN_OVER:
                    installApk();
                    break;
                default:
                    break;
            }
        }

        ;
    };

    public UpdateManager(Context context,Activity activity) {
        this.mContext = context;
        this.mActivity = activity;
        this.apkDownloadPath = LocalStorageContext.get().readWalletDirectory();
        this.apkDownloadName = "app-release.apk";
    }

    /**
     * 外部接口让主Activity调用
     */
    public boolean checkUpdateInfo() {
        FutureTask<AppNetInfo> futureTask = new FutureTask<AppNetInfo>(new Callable<AppNetInfo>() {
            @Override
            public AppNetInfo call() throws Exception {
                URL url = new URL(apkVersionUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(4000);
                connection.setRequestMethod("GET");
                if (connection.getResponseCode() != 200) {
                    return null;
                }
                InputStream is = connection.getInputStream();
                InputStreamReader isReader = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isReader);
                StringBuffer sb = new StringBuffer();
                String data = "";
                while ((data = br.readLine()) != null) {
                    sb.append(data);
                }
                String jsonString = sb.toString();
                AppNetInfo appNetInfo = new Gson().fromJson(jsonString, AppNetInfo.class);
                return appNetInfo;
            }
        });
        // 启动线程请求当前应用程序版本号
        new Thread(futureTask).start();
        // 处理网络请求后的appNetInfo
        try {
            appNetInfo = futureTask.get();
            Log.i(LogConstant.TAG, "dig");
        } catch (Exception e) {
            return false;
        }

        // 检查版本是否需要更新
        if (isNeedUpdate()) {
            showNoticeDialog();
            return true;
        } else {
            return false;
        }
    }

    private void showNoticeDialog() {
        new LovelyStandardDialog(mContext, LovelyStandardDialog.ButtonLayout.HORIZONTAL)
                .setTopColorRes(R.color.colorPrimary)
                .setButtonsColor(Color.WHITE)
                .setIcon(R.drawable.ic_error_white_24px)
                .setTitle("软件版本更新")
                .setMessage("有最新的软件包哦，亲快下载吧~")
                .setPositiveButton("下载", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDownloadDialog();
                    }
                }).setNegativeButton("以后再说", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        UpdateUtil.closeApp();
                    }
        }).show();
    }

    private void showDownloadDialog() {
        AlertDialog.Builder builder = new Builder(mContext);
        builder.setTitle("app 版本更新");

        final LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.progress, null);
        mProgress = (ProgressBar) v.findViewById(R.id.progress);

        builder.setView(v);
        builder.setNegativeButton("取消", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                interceptFlag = true;
                UpdateUtil.closeApp();
            }
        });
        downloadDialog = builder.create();
        downloadDialog.show();

        downloadApk();
    }

    private Runnable mdownApkRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                URL url = new URL(appNetInfo.getDownloadUrl());

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.connect();
                int length = conn.getContentLength();
                InputStream is = conn.getInputStream();

                File file = new File(apkDownloadPath);
                if (!file.exists()) {
                    file.mkdir();
                }
                File ApkFile = new File(apkDownloadPath + apkDownloadName);
                FileOutputStream fos = new FileOutputStream(ApkFile);

                int count = 0;
                byte buf[] = new byte[1024];

                do {
                    int numread = is.read(buf);
                    count += numread;
                    progress = (int) (((float) count / length) * 100);
                    //更新进度
                    mHandler.sendEmptyMessage(DOWN_UPDATE);
                    if (numread <= 0) {
                        //下载完成通知安装
                        mHandler.sendEmptyMessage(DOWN_OVER);
                        break;
                    }
                    fos.write(buf, 0, numread);
                } while (!interceptFlag);//点击取消就停止下载.

                fos.close();
                is.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * 下载apk
     */

    private void downloadApk() {
        downLoadThread = new Thread(mdownApkRunnable);
        downLoadThread.start();
    }

    /**
     * 安装apk
     */
    public void installApk() {
        checkHaveInstallPermission();
        File apkfile = new File(apkDownloadPath + apkDownloadName);
        if (!apkfile.exists()) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        // 由于没有在Activity环境下启动Activity,设置下面的标签
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            intent.setDataAndType(Uri.fromFile(apkfile), "application/vnd.android.package-archive");
//            intent.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
        } else {//Android7.0之后获取uri要用contentProvider
            intent.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        mContext.startActivity(intent);
    }

    //安装应用的流程
    private void checkHaveInstallPermission() {
        boolean haveInstallPermission;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //先获取是否有安装未知来源应用的权限
            haveInstallPermission = mContext.getPackageManager().canRequestPackageInstalls();
            if (!haveInstallPermission) {//没有权限
                new LovelyStandardDialog(mActivity)
                        .setTopColorRes(R.color.colorPrimary)
                        .setIcon(R.drawable.ic_error_white_24px)
                        .setTitle(mContext.getString(R.string.dialog_title_info))
                        .setMessage("安装应用需要打开未知来源权限，请去设置中开启权限")
                        .setPositiveButton("去允许", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startInstallPermissionSettingActivity();
                            }
                        }).show();
                return;
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startInstallPermissionSettingActivity() {
        //注意这个是8.0新API
        Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
        mActivity.startActivityForResult(intent, 100);
    }

    /**
     * 判断是否可以更新版本
     *
     * @return
     */
    private boolean isNeedUpdate() {
        if (this.appNetInfo == null) {
            return false;
        }
        int appNetVersion = appNetInfo.getVersionCode();
        if (UpdateUtil.getVersion(mContext) > 0 && appNetVersion > UpdateUtil.getVersion(mContext)) {
            return true;
        }
        return false;
    }
}
