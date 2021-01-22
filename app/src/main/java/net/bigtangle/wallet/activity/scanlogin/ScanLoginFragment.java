package net.bigtangle.wallet.activity.scanlogin;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import net.bigtangle.core.Coin;
import net.bigtangle.core.ECKey;
import net.bigtangle.core.Utils;
import net.bigtangle.encrypt.ECIESCoder;
import net.bigtangle.params.ReqCmd;
import net.bigtangle.utils.Gzip;
import net.bigtangle.utils.Json;
import net.bigtangle.utils.MonetaryFormat;
import net.bigtangle.utils.OkHttp3Util;
import net.bigtangle.wallet.R;
import net.bigtangle.wallet.activity.scanlogin.adapter.TokenInfoItemListAdapter;
import net.bigtangle.wallet.activity.scanlogin.model.TokenInfoItem;
import net.bigtangle.wallet.activity.transaction.model.TokenItem;
import net.bigtangle.wallet.components.BaseLazyFragment;
import net.bigtangle.wallet.components.WrapContentLinearLayoutManager;
import net.bigtangle.wallet.core.BrowserAccessTokenContext;
import net.bigtangle.wallet.core.WalletContextHolder;
import net.bigtangle.wallet.core.constant.HttpConnectConstant;
import net.bigtangle.wallet.core.constant.LogConstant;
import net.bigtangle.wallet.core.http.HttpNetComplete;
import net.bigtangle.wallet.core.http.HttpNetTaskRequest;
import net.bigtangle.wallet.core.http.URLUtil;
import net.bigtangle.wallet.core.utils.CommonUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import butterknife.BindView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author lijian
 * @date 2019-07-06 00:06:01
 */
public class ScanLoginFragment extends BaseLazyFragment {

    public static ScanLoginFragment newInstance() {
        return new ScanLoginFragment();
    }

    @BindView(R.id.qrscanlogin_button)
    Button qrscanloginButton;
    @BindView(R.id.qrcode_image)
    ImageView qrcodeImageView;

    //qr code scanner object
    private IntentIntegrator qrScan;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFroceLoadData(true);
        qrScan = new IntentIntegrator(this.getActivity()).forSupportFragment(this);
        ;
        qrScan.setOrientationLocked(false);
    }

    @Override
    public void onLazyLoad() {
        List<ECKey> issuedKeys = WalletContextHolder.get().walletKeys();
        String address=issuedKeys.get(0).toAddress(WalletContextHolder.networkParameters).toBase58();
        String content="{\"address\":\""+address+"\"}";
        Bitmap bitmap = CommonUtil.createQRCodeBitmap(content, 500, 500,"UTF-8","H", "1", Color.BLACK, Color.WHITE);

        qrcodeImageView.setImageBitmap(bitmap);
    }

    @Override
    public View initView(LayoutInflater inflater, @Nullable ViewGroup container) {
        View view = inflater.inflate(R.layout.fragment_scanlogin, container, false);
        return view;
    }

    @Override
    public void initEvent() {
        this.qrscanloginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {    //initiating the qr code scan
                qrScan.initiateScan();
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        Toast.makeText(getContext(), result.getContents(), Toast.LENGTH_LONG).show();

        if (result != null) {
            //if qrcode has nothing in it
            if (result.getContents() == null) {
                //    Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
            } else {
                //if qr contains data
                String string = result.getContents();
                try {
                    JSONObject obj = new JSONObject(string);
                    String uuid = obj.getString("uuid");
                    ECKey ecKey = WalletContextHolder.get().walletKeys().get(0);
                    String jsonStr = "";
                    String url = obj.getString("url");
                    String url1 = url + "?flag=0&uuid=" + uuid + "&pubKey=" + ecKey.getPublicKeyAsHex();
                    String url2 = url + "?flag=1&uuid=" + uuid + "&pubKey=" + ecKey.getPublicKeyAsHex() + "&useraccesstoken=" + jsonStr;

                    Future<String> future = new URLUtil().calculateString(url1);

                    jsonStr = future.get();
                    byte[] bytes = Utils.HEX.decode(jsonStr);
                    byte[] decryptedPayload = ECIESCoder.decrypt(ecKey.getPrivKey(), bytes);
                    if (uuid.equals(new String(decryptedPayload))) {
                        Future<String> future2 = new URLUtil().calculateString(url2);
                        jsonStr = future2.get();

                    } else {
                        Toast.makeText(getContext(), "decrypt no equal", Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }

        }
    }
}
