package net.bigtangle.wallet.activity.scanlogin;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import net.bigtangle.core.ECKey;
import net.bigtangle.core.Utils;
import net.bigtangle.encrypt.ECIESCoder;
import net.bigtangle.wallet.R;
import net.bigtangle.wallet.activity.SPUtil;
import net.bigtangle.wallet.components.BaseLazyFragment;
import net.bigtangle.wallet.core.BrowserAccessTokenContext;
import net.bigtangle.wallet.core.WalletContextHolder;
import net.bigtangle.wallet.core.http.URLUtil;
import net.bigtangle.wallet.core.utils.CommonUtil;

import org.json.JSONObject;

import java.io.InputStream;
import java.util.concurrent.Future;

import butterknife.BindView;

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
    @BindView(R.id.vpn_button)
    Button vpnButton;
    @BindView(R.id.vpnfile_button)
    Button vpnfileButton;
    String code = "";
    //qr code scanner object
    private IntentIntegrator qrScan;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFroceLoadData(true);
        qrScan = new IntentIntegrator(this.getActivity()).forSupportFragment(this);

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
            public void onClick(View v) {    //initiating the qr code scan
                qrScan.initiateScan();
            }
        });
        this.vpnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    code = BrowserAccessTokenContext.check(getContext());
                    if ("".equals(code))
                        Toast.makeText(getContext(), "网络慢,请重试", Toast.LENGTH_LONG).show();
                    else if ("405".equals(code))
                        Toast.makeText(getContext(), "请先注册或登录", Toast.LENGTH_LONG).show();
                    else {
                        BrowserAccessTokenContext.open(getContext(), "http://bigtangle.oss-cn-beijing.aliyuncs.com/download/ics-openvpn-0.7.23.apk", code);
                    }
                } catch (Exception e) {
                    Log.e("bigtangle", "", e);
                }


            }
        });

        this.vpnfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    code = BrowserAccessTokenContext.check(getContext());
                    if ("".equals(code))
                        Toast.makeText(getContext(), "网络慢,请重试", Toast.LENGTH_LONG).show();
                    else if ("405".equals(code))
                        Toast.makeText(getContext(), "请先注册或登录", Toast.LENGTH_LONG).show();
                    else {
                        BrowserAccessTokenContext.open(getContext(), "http://bigtangle.oss-cn-beijing.aliyuncs.com/download/bigtangle-de.ovpn", code);
                    }
                } catch (Exception e) {
                    Log.e("bigtangle", "", e);
                }


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
                    String un = SPUtil.get(getContext(), "username", "").toString();
                    InputStream stream = CommonUtil.loadFromDB(un, getContext());
                    WalletContextHolder.loadWallet(stream);

                    ECKey ecKey = WalletContextHolder.walletKeys().get(0);
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
