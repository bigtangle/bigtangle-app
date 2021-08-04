package net.bigtangle.wallet.core.http;

import android.util.JsonReader;
import android.util.Log;

import net.bigtangle.apps.data.Certificate;
import net.bigtangle.apps.data.IdentityData;
import net.bigtangle.apps.data.SignedData;
import net.bigtangle.core.ECKey;
import net.bigtangle.core.KeyValue;
import net.bigtangle.core.Token;
import net.bigtangle.core.TokenType;
import net.bigtangle.core.UTXO;
import net.bigtangle.core.UserSettingDataInfo;
import net.bigtangle.core.Utils;
import net.bigtangle.core.response.GetBalancesResponse;
import net.bigtangle.encrypt.ECIESCoder;
import net.bigtangle.params.ReqCmd;
import net.bigtangle.utils.Json;
import net.bigtangle.utils.OkHttp3Util;

import net.bigtangle.utils.SignedDataWithToken;
import net.bigtangle.utils.WalletUtil;
import net.bigtangle.wallet.Wallet;
import net.bigtangle.wallet.activity.wallet.model.CertificateVO;
import net.bigtangle.wallet.activity.wallet.model.IdentityVO;
import net.bigtangle.wallet.core.WalletContextHolder;
import net.bigtangle.wallet.core.constant.HttpConnectConstant;
import net.bigtangle.wallet.core.constant.LogConstant;
import net.bigtangle.wallet.core.utils.CommonUtil;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class URLUtil {
    private ExecutorService executor
            = Executors.newSingleThreadExecutor();

    public Future<byte[]> calculate(String url) {
        return executor.submit(() -> {
            OkHttpClient okHttpClient = new OkHttpClient();
            final Request request = new Request.Builder()
                    .url(url)
                    .get()//默认就是GET请求，可以不写
                    .build();
            Call call = okHttpClient.newCall(request);

            Response response = call.execute();
            return response.body().bytes();
        });
    }

    public Future<String> calculateString(String url) {
        return executor.submit(() -> {
            OkHttpClient okHttpClient = new OkHttpClient();
            final Request request = new Request.Builder()
                    .url(url)
                    .get()//默认就是GET请求，可以不写
                    .build();
            Call call = okHttpClient.newCall(request);

            Response response = call.execute();
            return response.body().string();
        });
    }

    public void downloadWalletFile(String signin, String password, String filename) {
        String url = WalletContextHolder.getMBigtangle() +
                "/public/walletfilepullout?signin=" + signin + "&password=" + password;


        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        Log.d("bigtangle-wallet", url);

        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(160, TimeUnit.SECONDS)//设置连接超时时间
                .readTimeout(600, TimeUnit.SECONDS)//设置读取超时时间;
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() != 200) {
                    return;
                }
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                File file = new File(filename);

                boolean success = true;
                try {

                    is = response.body().byteStream();
                    long total = response.body().contentLength();
                    fos = new FileOutputStream(file);
                    long sum = 0;
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                        sum += len;
                        int progress = (int) (sum * 1.0f / total * 100);
                        //下载中更新进度条
                        //listener.onDownloading(progress);
                    }
                    fos.flush();
                    //下载完成
                    success = true;
                } catch (Exception e) {
                    success = false;
                } finally {

                    try {
                        if (is != null) {
                            is.close();
                        }
                        if (fos != null) {
                            fos.close();
                        }
                    } catch (IOException e) {

                    }
                }

            }
        });

    }

    public void downloadWalletFileToDB(String signin, String password) {
        Log.i("test","signin:"+signin+";password:"+password);
        String url = WalletContextHolder.getMBigtangle() +
                "/public/walletfilepullout?signin=" + signin + "&password=" + password;


        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        Log.d("bigtangle-wallet", url);

        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(160, TimeUnit.SECONDS)//设置连接超时时间
                .readTimeout(600, TimeUnit.SECONDS)//设置读取超时时间;
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("test","onFailure",e);
                e.printStackTrace();

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("bigtangle-wallet", "response.code()" + response.code());
                if (response.code() != 200) {
                    return;
                }
                if (response.body().byteStream() != null) {
                    try {
                        WalletContextHolder.loadWallet(response.body().byteStream());

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else {
                    Log.i("test","response.body().byteStream() == null");
                }

            }
        });
    }

    public Future<String> getIdtoken(ECKey userKey) {
        return executor.submit(() -> {
            String idtoken = CommonUtil.getIdtoken(userKey);
            return idtoken;
        });
    }


    public Future<List<CertificateVO>> calculateCertificate() {
        return executor.submit(() -> {

            Wallet wallet = WalletContextHolder.wallet;
            List<CertificateVO> certificates = new ArrayList<CertificateVO>();

            List<SignedDataWithToken> sds = null;
            try {
                sds = WalletUtil.signedTokenList(wallet.walletKeys(WalletContextHolder.getAesKey()), TokenType.certificate, HttpConnectConstant.HTTP_SERVER_URL);
                if (sds != null && !sds.isEmpty()) {
                    for (SignedDataWithToken s : sds) {
                        Certificate certificate = new Certificate()
                                .parse(Utils.HEX.decode(s.getSignedData().getSerializedData()));
                        certificates.add(new CertificateVO(certificate, s.getToken().getTokennameDisplay()));

                    }
                }
            } catch (Exception e) {
                Log.i(LogConstant.TAG, "error2：" + e.getMessage());
                e.printStackTrace();
            }

            return certificates;
        });
    }

    public Future<List<IdentityVO>> calculateIdentity() {
        return executor.submit(() -> {
            List<IdentityVO> identities = new ArrayList<IdentityVO>();
            Wallet wallet = WalletContextHolder.wallet;
            List<SignedDataWithToken> sds = WalletUtil.signedTokenList(wallet.walletKeys(WalletContextHolder.getAesKey()), TokenType.identity, HttpConnectConstant.HTTP_SERVER_URL);

            if (sds != null && !sds.isEmpty()) {
                for (SignedDataWithToken s : sds) {
                    IdentityData identityData = new IdentityData()
                            .parse(Utils.HEX.decode(s.getSignedData().getSerializedData()));
                    identities.add(new IdentityVO(identityData, s.getToken().getTokennameDisplay()));

                }
            }
            return identities;
        });
    }

    public Future<UserSettingDataInfo> calculateUserdata() {
        return executor.submit(() -> {
            List<ECKey> issuedKeys = WalletContextHolder.walletKeys();
            ECKey pubKeyTo = issuedKeys.get(0);
            WalletContextHolder.wallet.setServerURL(HttpConnectConstant.HTTP_SERVER_URL);
            UserSettingDataInfo userSettingDataInfo = WalletContextHolder.wallet.getUserSettingDataInfo(pubKeyTo, false);
            return userSettingDataInfo;
        });
    }

    public Future<String> calcTokenDisplay() {
        return executor.submit(() -> {
            OkHttpClient okHttpClient = new OkHttpClient();
            final Request request = new Request.Builder()
                    .url(WalletContextHolder.getMBigtangle() + "/chartdata/tokendata")
                    .get()//默认就是GET请求，可以不写
                    .build();
            Call call = okHttpClient.newCall(request);

            Response response = call.execute();
            String json = response.body().string();
            return json;
        });
    }

}
