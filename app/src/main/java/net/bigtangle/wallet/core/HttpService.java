package net.bigtangle.wallet.core;

import android.util.Log;

import net.bigtangle.core.ContactInfo;
import net.bigtangle.core.DataClassName;
import net.bigtangle.core.ECKey;
import net.bigtangle.utils.Json;
import net.bigtangle.core.MyHomeAddress;
import net.bigtangle.core.Token;
import net.bigtangle.core.UTXO;
import net.bigtangle.core.UploadfileInfo;
import net.bigtangle.core.Utils;
import net.bigtangle.core.WatchedInfo;
import net.bigtangle.core.response.GetBalancesResponse;
import net.bigtangle.core.response.GetOutputsResponse;
import net.bigtangle.core.response.GetTokensResponse;
import net.bigtangle.params.ReqCmd;
import net.bigtangle.utils.OkHttp3Util;
import net.bigtangle.wallet.activity.transaction.model.TokenItem;
import net.bigtangle.wallet.activity.wallet.WalletAccountFragment;
import net.bigtangle.wallet.activity.wallet.dialog.WalletDownfileDialog;
import net.bigtangle.wallet.core.constant.HttpConnectConstant;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpService {

    public static void downloadWalletFile(String signin, String password, String filename, WalletDownfileDialog.OnWalletDownfileListenter listenter) throws Exception {
        //
//        https://m.bigtangle.com.cn/vm/walletfiledownload?id=201905250100000005&userid=201905250100000004
//                    https://testcc.bigtangle.xyz
        String url = WalletAccountFragment.HTTPS_M_BIGTANGLE +
                "/public/walletfilepullout?signin=" + signin + "&password=" + password;

//        String url = "http://10.0.2.2:8080/cc/vm/walletfilepullout?signin=" + signin + "&password=" + password;

        //String url = "https://testm.bigtangle.xyz/vm/walletfilepullout?signin=" + signin + "&password=" + password;

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
                listenter.downloadFileStatus(false);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() != 200) {
                    listenter.downloadFileStatus(false);
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
                listenter.downloadFileStatus(success);
            }
        });
    }

    public static List<TokenItem> getTokensItemList() throws Exception {
        Map<String, Object> requestParam = new HashMap<String, Object>();
        requestParam.put("name", null);

        String response = OkHttp3Util.post(HttpConnectConstant.HTTP_SERVER_URL + ReqCmd.searchExchangeTokens.name(),
                Json.jsonmapper().writeValueAsString(requestParam).getBytes());
        GetTokensResponse getTokensResponse = Json.jsonmapper().readValue(response, GetTokensResponse.class);

        List<TokenItem> tokenItemList = new ArrayList<TokenItem>();
        for (Token token : getTokensResponse.getTokens()) {
            TokenItem tokenItem = new TokenItem();
            tokenItem.setTokenId(token.getTokenid());
            tokenItem.setTokenName(token.getTokennameDisplay());
            tokenItemList.add(tokenItem);
        }

        return tokenItemList;
    }

    public static List<TokenItem> getValidTokenItemList() throws Exception {
        List<String> keyStrHex = new ArrayList<String>();
        for (ECKey ecKey : WalletContextHolder.get().walletKeys()) {
            keyStrHex.add(Utils.HEX.encode(ecKey.getPubKeyHash()));
        }

        String response = OkHttp3Util.post(HttpConnectConstant.HTTP_SERVER_URL + ReqCmd.getBalances.name(),
                Json.jsonmapper().writeValueAsString(keyStrHex).getBytes());
        GetBalancesResponse getBalancesResponse = Json.jsonmapper().readValue(response, GetBalancesResponse.class);

        List<TokenItem> tokenItemList = new ArrayList<TokenItem>();
        for (Token token : getBalancesResponse.getTokennames().values()) {
            TokenItem tokenItem = new TokenItem();
            tokenItem.setTokenId(token.getTokenid());
            tokenItem.setTokenName(token.getTokennameDisplay());
            tokenItemList.add(tokenItem);
        }

        return tokenItemList;
    }

    public static List<UTXO> getUTXOWithPubKeyHash(List<String> pubKeyHashs, String tokenid) throws Exception {
        List<UTXO> listUTXO = new ArrayList<UTXO>();
        String response = OkHttp3Util.post(HttpConnectConstant.HTTP_SERVER_URL + ReqCmd.getOutputs.name(),
                Json.jsonmapper().writeValueAsString(pubKeyHashs).getBytes());
        GetOutputsResponse getOutputsResponse = Json.jsonmapper().readValue(response, GetOutputsResponse.class);
        for (UTXO utxo : getOutputsResponse.getOutputs()) {
            if (!utxo.getTokenId().equals(tokenid)) {
                continue;
            }
            if (utxo.getValue().getValue().signum() > 0) {
                listUTXO.add(utxo);
            }
        }
        return listUTXO;
    }

    public static Map<String, String> getTokenNameMap() throws Exception {
        Map<String, Object> requestParam = new HashMap<String, Object>();
        String response = OkHttp3Util.post(HttpConnectConstant.HTTP_SERVER_URL + ReqCmd.searchExchangeTokens.name(),
                Json.jsonmapper().writeValueAsString(requestParam).getBytes());

        GetTokensResponse getTokensResponse = Json.jsonmapper().readValue(response, GetTokensResponse.class);
        Map<String, String> map = new HashMap<String, String>();
        for (Token tokens : getTokensResponse.getTokens()) {
            map.put(tokens.getTokenid(), tokens.getTokennameDisplay());
        }
        return map;
    }

    public static Serializable getUserdata(String type) throws IOException {
        HashMap<String, String> requestParam = new HashMap<String, String>();
        // 读取 ECKey
        List<ECKey> issuedKeys = WalletContextHolder.get().walletKeys();

        ECKey pubKeyTo = issuedKeys.get(0);

        if (DataClassName.TOKEN.name().equals(type) || DataClassName.LANG.name().equals(type)
                || DataClassName.SERVERURL.name().equals(type) || DataClassName.BlockSolveType.name().equals(type)) {
            type = DataClassName.WATCHED.name();
        }
        requestParam.put("pubKey", pubKeyTo.getPublicKeyAsHex());
        requestParam.put("dataclassname", type);
        byte[] bytes = OkHttp3Util.postAndGetBlock(HttpConnectConstant.HTTP_SERVER_URL + ReqCmd.getUserData.name(),
                Json.jsonmapper().writeValueAsString(requestParam));
        if (DataClassName.CONTACTINFO.name().equals(type)) {
            if (bytes == null || bytes.length == 0) {
                return new ContactInfo();
            }
            ContactInfo contactInfo = new ContactInfo().parse(bytes);
            return contactInfo;
        } else if (DataClassName.MYHOMEADDRESS.name().equals(type)) {
            if (bytes == null || bytes.length == 0) {
                return new MyHomeAddress();
            }
            MyHomeAddress myHomeAddress = new MyHomeAddress().parse(bytes);
            return myHomeAddress;
        } else if (DataClassName.UPLOADFILE.name().equals(type)) {
            if (bytes == null || bytes.length == 0) {
                return new UploadfileInfo();
            }
            UploadfileInfo uploadfileInfo = new UploadfileInfo().parse(bytes);
            return uploadfileInfo;
        } else if (DataClassName.SERVERURL.name().equals(type) || DataClassName.LANG.name().equals(type)
                || DataClassName.TOKEN.name().equals(type) || DataClassName.WATCHED.name().equals(type)) {
            WatchedInfo watchedInfo = null;

            if (bytes == null || bytes.length == 0) {
                return new WatchedInfo();
            }
            watchedInfo = new WatchedInfo().parse(bytes);

            return watchedInfo;
        } else {
            return null;
        }
    }
}
