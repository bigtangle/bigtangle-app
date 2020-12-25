package net.bigtangle.wallet.activity.scanlogin;

import android.content.Intent;
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
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import net.bigtangle.core.Coin;
import net.bigtangle.core.ECKey;
import net.bigtangle.params.ReqCmd;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

/**
 * @author lijian
 * @date 2019-07-06 00:06:01
 */
public class ScanLoginFragment extends BaseLazyFragment  {

    public static ScanLoginFragment newInstance() {
        return new ScanLoginFragment();
    }
    @BindView(R.id.qrscanlogin_button)
    Button qrscanloginButton;
    //qr code scanner object
    private IntentIntegrator qrScan;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFroceLoadData(true);
        qrScan = new IntentIntegrator(this.getActivity()).forSupportFragment(this);;
        qrScan.setOrientationLocked(false);
    }
    @Override
    public void onLazyLoad() {

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
            public void onClick(View v)     {    //initiating the qr code scan
                qrScan.initiateScan();
            }});
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
        Toast.makeText(getContext(),  result.getContents() ,Toast.LENGTH_LONG  ).show();

        if (result != null) {
            //if qrcode has nothing in it
            if (result.getContents() == null) {
                //    Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
            } else {
                //if qr contains data
                String string=result.getContents();
                ECKey ecKey = WalletContextHolder.get().walletKeys().get(0);
                HashMap<String, Object> requestParam = new HashMap<String, Object>();
                requestParam.put("pubKey",ecKey.getPublicKeyAsHex());
                requestParam.put("uuid",string);

                try {
                    String jsonStr = OkHttp3Util.post("https://testcc.bigtangle.xyz/public/afterScan",
                            Json.jsonmapper().writeValueAsString(requestParam).getBytes());
                } catch (IOException e) {

                }
            }

        }
    }
}
