package net.bigtangle.wallet.core;

import net.bigtangle.core.Coin;
import net.bigtangle.core.ContactInfo;
import net.bigtangle.core.DataClassName;
import net.bigtangle.core.ECKey;
import net.bigtangle.core.Json;
import net.bigtangle.core.MyHomeAddress;
import net.bigtangle.core.NetworkParameters;
import net.bigtangle.core.Token;
import net.bigtangle.core.UTXO;
import net.bigtangle.core.UploadfileInfo;
import net.bigtangle.core.Utils;
import net.bigtangle.core.WatchedInfo;
import net.bigtangle.core.http.server.resp.GetBalancesResponse;
import net.bigtangle.core.http.server.resp.GetOutputsResponse;
import net.bigtangle.core.http.server.resp.GetTokensResponse;
import net.bigtangle.params.ReqCmd;
import net.bigtangle.utils.OkHttp3Util;
import net.bigtangle.wallet.activity.transaction.model.TokenItem;
import net.bigtangle.wallet.core.constant.HttpConnectConstant;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HttpService {

    public static List<TokenItem> getTokensItemList() throws Exception {
        Map<String, Object> requestParam = new HashMap<String, Object>();
        requestParam.put("name", null);

        String response = OkHttp3Util.post(HttpConnectConstant.HTTP_SERVER_URL + ReqCmd.getTokens.name(),
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

        List<TokenItem> tokenItemList = new ArrayList<>();
        for (UTXO utxo : getBalancesResponse.getOutputs()) {
            Coin c = utxo.getValue();
            if (c.isZero()) {
                continue;
            }
            byte[] tokenid = c.getTokenid();
            TokenItem tokenItem = new TokenItem();
            tokenItem.setTokenId(Utils.HEX.encode(tokenid));
            Token token = getBalancesResponse.getTokennames().get(tokenItem.getTokenId());
            if (token != null) {
                tokenItem.setTokenName(token.getTokennameDisplay());
            } else {
                tokenItem.setTokenName(tokenItem.getTokenId());
            }
            if (!utxo.isMultiSig()) {
                tokenItemList.add(tokenItem);
            }
        }

        return tokenItemList;
    }

    public static HashMap<String, Set<String>> getValidTokenAddressResult() throws Exception {
        List<String> keyStrHex = new ArrayList<String>();
        for (ECKey ecKey : WalletContextHolder.get().walletKeys()) {
            keyStrHex.add(Utils.HEX.encode(ecKey.getPubKeyHash()));
        }

        String response = OkHttp3Util.post(HttpConnectConstant.HTTP_SERVER_URL + ReqCmd.getBalances.name(),
                Json.jsonmapper().writeValueAsString(keyStrHex).getBytes());
        GetBalancesResponse getBalancesResponse = Json.jsonmapper().readValue(response, GetBalancesResponse.class);

        HashMap<String, Set<String>> tokenResult = new HashMap<String, Set<String>>();
        for (UTXO utxo : getBalancesResponse.getOutputs()) {
            Coin c = utxo.getValue();
            if (c.isZero()) {
                continue;
            }
            byte[] tokenid = c.getTokenid();
            String address = utxo.getAddress();
            String key = Utils.HEX.encode(tokenid);

            Set<String> addressList = tokenResult.get(key);
            if (addressList == null) {
                addressList = new HashSet<String>();
                tokenResult.put(key, addressList);
            }
            addressList.add(address);
        }

        return tokenResult;
    }

    public static Set<String> getValidAddressSet() throws Exception {
        List<String> keyStrHex = new ArrayList<String>();
        for (ECKey ecKey : WalletContextHolder.get().walletKeys()) {
            keyStrHex.add(Utils.HEX.encode(ecKey.getPubKeyHash()));
        }

        String response = OkHttp3Util.post(HttpConnectConstant.HTTP_SERVER_URL + ReqCmd.getBalances.name(),
                Json.jsonmapper().writeValueAsString(keyStrHex).getBytes());
        GetBalancesResponse getBalancesResponse = Json.jsonmapper().readValue(response, GetBalancesResponse.class);

        Set<String> addressSet = new HashSet<String>();
        for (UTXO utxo : getBalancesResponse.getOutputs()) {
            Coin c = utxo.getValue();
            if (c.isZero()) {
                continue;
            }
            String address = utxo.getAddress();
            if (utxo.getTokenId().trim().equals(NetworkParameters.BIGTANGLE_TOKENID_STRING)) {
                addressSet.add(address);
            }
        }

        return addressSet;
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
        String response = OkHttp3Util.post(HttpConnectConstant.HTTP_SERVER_URL + ReqCmd.getTokens.name(),
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
        byte[] bytes = OkHttp3Util.post(HttpConnectConstant.HTTP_SERVER_URL + ReqCmd.getUserData.name(),
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
