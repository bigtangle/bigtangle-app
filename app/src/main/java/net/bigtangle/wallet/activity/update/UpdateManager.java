package net.bigtangle.wallet.activity.update;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import com.google.gson.Gson;

import net.bigtangle.wallet.R;
import net.bigtangle.wallet.core.utils.UpdateUtil;

public class UpdateManager {

    private Context mContext;

    private AppNetInfo appNetInfo;

    //返回的 app 包 版本号
    private String apkVersionUrl = "https://bigtangle.org/app-version";

    private Dialog noticeDialog;

    private Dialog downloadDialog;

    /* 下载包安装路径 */
    private static final String savePath = "/sdcard/update/";

    private static final String saveFileName = savePath + "app-release.apk";

    /* 进度条与通知ui刷新的handler和msg常量 */
    private ProgressBar mProgress;


    private static final int DOWN_UPDATE = 1;

    private static final int DOWN_OVER = 2;

    private int progress;

    private Thread downLoadThread;

    private boolean interceptFlag = false;

    private Handler mHandler = new Handler(){
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
        };
    };

    public UpdateManager(Context context) {
        this.mContext = context;
        new Thread(getAppNetInfo);
    }

    private Runnable getAppNetInfo = new Runnable() {
        @Override
        public void run() {
            try {
                URL url = new URL(apkVersionUrl);
                //开启一个连接
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setConnectTimeout(10000);
                connection.setReadTimeout(4000);
                connection.setRequestMethod("GET");
                if(connection.getResponseCode()==200){
                    InputStream is = connection.getInputStream();
                    InputStreamReader isReader = new InputStreamReader(is);
                    BufferedReader br = new BufferedReader(isReader);
                    StringBuffer sb = new StringBuffer();
                    String data = "";
                    while ((data = br.readLine()) != null){
                        sb.append(data);
                    }
                    String jsonString = sb.toString();
                    System.out.println("JsonString:"+jsonString);
                    appNetInfo = new Gson().fromJson(jsonString,AppNetInfo.class);
                    System.out.println("AppNetInfo:"+appNetInfo.toString());
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };

    //外部接口让主Activity调用
    public void checkUpdateInfo(){
        // 检查使用有SD卡
        if (!UpdateUtil.hasSdcard()){
            noSdcardDialog();
        }
        // 检查版本是否需要更新
        if (isNeedUpdate()){
            showNoticeDialog();
        }
    }

    private void showNoticeDialog(){
        AlertDialog.Builder builder = new Builder(mContext);
        builder.setTitle("软件版本更新");
        builder.setMessage("有最新的软件包哦，亲快下载吧~");
        builder.setPositiveButton("下载", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                showDownloadDialog();
            }
        });
        builder.setNegativeButton("以后再说", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        noticeDialog = builder.create();
        noticeDialog.show();
    }
    private void showDownloadDialog(){
        AlertDialog.Builder builder = new Builder(mContext);
        builder.setTitle("app 版本更新");

        final LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.progress, null);
        mProgress = (ProgressBar)v.findViewById(R.id.progress);

        builder.setView(v);
        builder.setNegativeButton("取消", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                interceptFlag = true;
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

                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.connect();
                int length = conn.getContentLength();
                InputStream is = conn.getInputStream();

                File file = new File(savePath);
                if(!file.exists()){
                    file.mkdir();
                }
                String apkFile = saveFileName;
                File ApkFile = new File(apkFile);
                FileOutputStream fos = new FileOutputStream(ApkFile);

                int count = 0;
                byte buf[] = new byte[1024];

                do{
                    int numread = is.read(buf);
                    count += numread;
                    progress =(int)(((float)count / length) * 100);
                    //更新进度
                    mHandler.sendEmptyMessage(DOWN_UPDATE);
                    if(numread <= 0){
                        //下载完成通知安装
                        mHandler.sendEmptyMessage(DOWN_OVER);
                        break;
                    }
                    fos.write(buf,0,numread);
                }while(!interceptFlag);//点击取消就停止下载.

                fos.close();
                is.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch(IOException e){
                e.printStackTrace();
            }
        }
    };

    /**
     * 下载apk
     */

    private void downloadApk(){
        downLoadThread = new Thread(mdownApkRunnable);
        downLoadThread.start();
    }
    /**
     * 安装apk
     */
    private void installApk(){
        File apkfile = new File(saveFileName);
        if (!apkfile.exists()) {
            return;
        }
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
        mContext.startActivity(i);

    }

    private boolean isNeedUpdate() {
        int appNetVersion = appNetInfo.getVersionCode();
        if (UpdateUtil.getVersion(mContext) > 0 && appNetVersion > UpdateUtil.getVersion(mContext)){
            return  true;
        };
        return false;
    }

    private void noSdcardDialog() {
        AlertDialog.Builder builder = new Builder(this.mContext);
        builder.setTitle("没有 sd 卡").
                setMessage("请您安装 sd 卡.").
                setPositiveButton("确定", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
    }
}
