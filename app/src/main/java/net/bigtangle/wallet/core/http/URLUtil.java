package net.bigtangle.wallet.core.http;

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
}
