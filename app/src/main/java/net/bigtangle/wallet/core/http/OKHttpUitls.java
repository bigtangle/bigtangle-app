package net.bigtangle.wallet.core.http;

import net.bigtangle.core.Json;
import net.bigtangle.core.Token;
import net.bigtangle.core.http.server.resp.GetTokensResponse;
import net.bigtangle.params.ReqCmd;
import net.bigtangle.utils.OkHttp3Util;
import net.bigtangle.wallet.core.constant.HttpConnectConstant;

import org.spongycastle.jce.exception.ExtIOException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OKHttpUitls {

    public static void post(String url, byte[] b, OKHttpListener listener) {
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(MediaType.parse("application/octet-stream; charset=utf-8"), b);
        Request request = new Request.Builder().url(url).post(body).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                listener.handleMessage(json);
            }
        });
    }

    public static Map<String, String> getTokenHexNameMap() {
        Map<String, String> result = new HashMap<String, String>();
        try {
            Map<String, Object> requestParam = new HashMap<String, Object>();
            String response = OkHttp3Util.post(HttpConnectConstant.HTTP_SERVER_URL + ReqCmd.getTokens.name(),
                    Json.jsonmapper().writeValueAsString(requestParam).getBytes());

            GetTokensResponse getTokensResponse = Json.jsonmapper().readValue(response, GetTokensResponse.class);
            for (Token tokens : getTokensResponse.getTokens()) {
                result.put(tokens.getTokenid(), tokens.getTokenname());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
