package net.bigtangle.wallet.core.http;

import android.util.Log;

import net.bigtangle.apps.data.Certificate;
import net.bigtangle.apps.data.IdentityData;
import net.bigtangle.core.ECKey;
import net.bigtangle.core.Token;
import net.bigtangle.wallet.activity.wallet.model.CertificateVO;
import net.bigtangle.wallet.activity.wallet.model.IdentityVO;
import net.bigtangle.wallet.core.constant.LogConstant;
import net.bigtangle.wallet.core.utils.CommonUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import okhttp3.Call;
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

    public Future<List<IdentityVO>> calculateIdentity(ECKey signerKey, ECKey userKey) {
        return executor.submit(() -> {
            List<IdentityVO> identityDatas = new ArrayList<IdentityVO>();
            Map<String, Token> tokennames = new HashMap<String, Token>();
            Log.i(LogConstant.TAG, "calculateIdentity");
            CommonUtil.identityList(signerKey, userKey, identityDatas, tokennames);
            Log.i(LogConstant.TAG, "calculateIdentity identityDatas.size()" + identityDatas.size());
            return identityDatas;
        });
    }

    public Future<String> getIdtoken(ECKey userKey) {
        return executor.submit(() -> {
            String idtoken = CommonUtil.getIdtoken(userKey);
            return idtoken;
        });
    }

    public Future<List<CertificateVO>> calculateCertificate(ECKey signerKey, ECKey userKey) {
        return executor.submit(() -> {
            List<CertificateVO> certificates = new ArrayList<CertificateVO>();
            Map<String, Token> tokennames = new HashMap<String, Token>();
            Log.i(LogConstant.TAG, "calculateCertificate");
            CommonUtil.certificateList(signerKey, userKey, certificates, tokennames);
            Log.i(LogConstant.TAG, "calculateCertificate certificates.size()" + certificates.size());
            return certificates;
        });
    }
}
