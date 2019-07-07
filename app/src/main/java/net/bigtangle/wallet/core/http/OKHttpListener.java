package net.bigtangle.wallet.core.http;

public interface OKHttpListener {

    void handleMessage(String response);

    void onError();
}
