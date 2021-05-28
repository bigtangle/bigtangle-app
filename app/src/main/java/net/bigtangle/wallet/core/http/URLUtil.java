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

    public Future<List<CertificateVO>> calculateCertificate() {
        return executor.submit(() -> {
            Log.i(LogConstant.TAG, "line1");
            Wallet wallet = WalletContextHolder.get().wallet();
            List<CertificateVO> certificates = new ArrayList<CertificateVO>();
            Log.i(LogConstant.TAG, "line2");
            List<ECKey> keys = wallet.walletKeys(WalletContextHolder.get().getAesKey());
            for (ECKey k : keys
            ) {
                Log.i(LogConstant.TAG, "pubkey" + k.getPublicKeyAsHex());
            }
            List<SignedDataWithToken> sds = null;
            try {
                sds = WalletUtil.signedTokenList(wallet.walletKeys(WalletContextHolder.get().getAesKey()), TokenType.certificate,HttpConnectConstant.HTTP_SERVER_URL);
                Log.i(LogConstant.TAG, "SignedDataWithToken-certificates-size()" + certificates.size());
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
            List<SignedDataWithToken> sds = WalletUtil.signedTokenList(wallet.walletKeys(WalletContextHolder.get().getAesKey()), TokenType.identity,HttpConnectConstant.HTTP_SERVER_URL);
            Log.i(LogConstant.TAG, "SignedDataWithToken-identities-size()" + identities.size());
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

    public Future<List<IdentityVO>> calculateIdentityA() {
        return executor.submit(() -> {
            List<IdentityVO> identities = new ArrayList<IdentityVO>();
            Wallet wallet = WalletContextHolder.get().wallet();
            List<SignedDataWithToken> sds = signedTokenList(wallet.walletKeys(WalletContextHolder.get().getAesKey()), TokenType.identity);
            Log.i(LogConstant.TAG, "SignedDataWithToken-identities-size()" + identities.size());
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

    public Future<List<CertificateVO>> calculateCertificateA() {
        return executor.submit(() -> {
            Log.i(LogConstant.TAG, "line1");
            Wallet wallet = WalletContextHolder.get().wallet();
            List<CertificateVO> certificates = new ArrayList<CertificateVO>();
            Log.i(LogConstant.TAG, "line2");
            List<ECKey> keys = wallet.walletKeys(WalletContextHolder.get().getAesKey());
            for (ECKey k : keys) {
                Log.i(LogConstant.TAG, "pubkey" + k.getPublicKeyAsHex());
            }
            List<SignedDataWithToken> sds = null;
            try {
                sds = signedTokenList(wallet.walletKeys(WalletContextHolder.get().getAesKey()), TokenType.certificate);
                Log.i(LogConstant.TAG, "SignedDataWithToken-certificates-size()" + sds.size());
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

    public List<SignedDataWithToken> signedTokenList(List<ECKey> userKeys, TokenType tokenType) throws Exception {
        List<SignedDataWithToken> signedTokenList = new ArrayList<SignedDataWithToken>();
        List<String> keys = new ArrayList<String>();
        for (ECKey k : userKeys) {
            keys.add(Utils.HEX.encode(k.getPubKeyHash()));
        }
        Log.i(LogConstant.TAG, "HTTP_SERVER_URL=="+HttpConnectConstant.HTTP_SERVER_URL);
        byte[] response = OkHttp3Util.post(HttpConnectConstant.HTTP_SERVER_URL +"/"+ ReqCmd.getBalances.name(),
                Json.jsonmapper().writeValueAsString(keys).getBytes());

        GetBalancesResponse balancesResponse = Json.jsonmapper().readValue(response, GetBalancesResponse.class);
        Log.i(LogConstant.TAG, "line=="+balancesResponse.getOutputs().size());
        for (UTXO utxo : balancesResponse.getOutputs()) {
            Token token = balancesResponse.getTokennames().get(utxo.getTokenId());
            Log.i(LogConstant.TAG, "line3");
            if (tokenType.ordinal() == token.getTokentype()) {
                signedTokenListAdd(utxo, userKeys, token, signedTokenList);
            }
        }
        return signedTokenList;
    }

    private void signedTokenListAdd(UTXO utxo, List<ECKey> userkeys, Token token,
                                    List<SignedDataWithToken> signedTokenList) throws Exception {
        if (token == null || token.getTokenKeyValues() == null) {

            return;
        }
        for (KeyValue kvtemp : token.getTokenKeyValues().getKeyvalues()) {
            ECKey signerKey = getSignedKey(userkeys, kvtemp.getKey());
            Log.i(LogConstant.TAG, "line4");
            if (signerKey != null) {
                try {
                    Log.i(LogConstant.TAG, "line5");
                    byte[] decryptedPayload = ECIESCoder.decrypt(signerKey.getPrivKey(),
                            Utils.HEX.decode(kvtemp.getValue()));
                    signedTokenList.add(new SignedDataWithToken(new SignedData().parse(decryptedPayload), token));
                    // sdata.verify();
                    break;
                } catch (Exception e) {

                }
            }
        }
    }

    private ECKey getSignedKey(List<ECKey> userkeys, String pubKey) {
        for (ECKey userkey : userkeys) {
            if (userkey.getPublicKeyAsHex().equals(pubKey)) {
                return userkey;
            }
        }
        return null;
    }
}
