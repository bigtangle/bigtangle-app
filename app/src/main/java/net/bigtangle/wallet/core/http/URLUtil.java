package net.bigtangle.wallet.core.http;

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


    public Future<String> getIdtoken(ECKey userKey) {
        return executor.submit(() -> {
            String idtoken = CommonUtil.getIdtoken(userKey);
            return idtoken;
        });
    }


    public Future<List<CertificateVO>> calculateCertificate() {
        return executor.submit(() -> {

            Wallet wallet = WalletContextHolder.get().wallet();
            List<CertificateVO> certificates = new ArrayList<CertificateVO>();

            List<SignedDataWithToken> sds = null;
            try {
                sds = WalletUtil.signedTokenList(wallet.walletKeys(WalletContextHolder.get().getAesKey()), TokenType.certificate, HttpConnectConstant.HTTP_SERVER_URL);
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
            Wallet wallet = WalletContextHolder.get().wallet();
            List<SignedDataWithToken> sds = WalletUtil.signedTokenList(wallet.walletKeys(WalletContextHolder.get().getAesKey()), TokenType.identity, HttpConnectConstant.HTTP_SERVER_URL);

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
            List<ECKey> issuedKeys = WalletContextHolder.get().walletKeys();
            ECKey pubKeyTo = issuedKeys.get(0);
            WalletContextHolder.get().wallet().setServerURL(HttpConnectConstant.HTTP_SERVER_URL);
            UserSettingDataInfo userSettingDataInfo = WalletContextHolder.get().wallet().getUserSettingDataInfo(pubKeyTo, false);
            return userSettingDataInfo;
        });
    }

}
