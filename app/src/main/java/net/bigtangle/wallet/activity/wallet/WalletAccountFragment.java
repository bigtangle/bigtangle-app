package net.bigtangle.wallet.activity.wallet;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.yarolegovich.lovelydialog.LovelyInfoDialog;

import net.bigtangle.core.Coin;
import net.bigtangle.core.ECKey;
import net.bigtangle.utils.Json;
import net.bigtangle.core.Utils;
import net.bigtangle.core.response.GetBalancesResponse;
import net.bigtangle.params.ReqCmd;
import net.bigtangle.wallet.R;
import net.bigtangle.wallet.activity.RegActivity;
import net.bigtangle.wallet.activity.wallet.adapters.WalletAccountItemListAdapter;
import net.bigtangle.wallet.activity.wallet.model.WalletAccountItem;
import net.bigtangle.wallet.components.BaseLazyFragment;
import net.bigtangle.wallet.components.WrapContentLinearLayoutManager;
import net.bigtangle.wallet.core.BrowserAccessTokenContext;
import net.bigtangle.wallet.core.WalletContextHolder;
import net.bigtangle.wallet.core.constant.LogConstant;
import net.bigtangle.wallet.core.http.HttpNetComplete;
import net.bigtangle.wallet.core.http.HttpNetTaskRequest;
import net.bigtangle.wallet.core.update.UpdateManager;
import net.bigtangle.wallet.core.utils.UpdateUtil;

import org.apache.commons.collections4.CollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class WalletAccountFragment extends BaseLazyFragment implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.recycler_view_container)
    RecyclerView recyclerViewContainer;

    @BindView(R.id.swipe_container)
    SwipeRefreshLayout swipeContainer;

    private WalletAccountItemListAdapter mAdapter;

    private List<WalletAccountItem> itemList;

    @BindView(R.id.shop_button)
    Button shopButton;

    @BindView(R.id.recharge_button)
    Button rechargeButton;
    @BindView(R.id.mining_button)
    Button miningButton;

    @BindView(R.id.payoff_button)
    Button payoffButton;

    @BindView(R.id.help_button)
    Button helpButton;

    @BindView(R.id.refresh_button)
    Button refreshButton;
    @BindView(R.id.reg_button)
    Button regButton;
    String code = "";
    private UpdateManager mUpdateManager;
    public static WalletAccountFragment newInstance() {
        return new WalletAccountFragment();
    }

    @Override
    public void onLazyLoad() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if (this.itemList == null) {
            this.itemList = new ArrayList<WalletAccountItem>();
        }
        setFroceLoadData(true);
        this.mAdapter = new WalletAccountItemListAdapter(getContext(), itemList);
    }


    public void refreshData() {
        List<String> keyStrHex = new ArrayList<String>();

        for (ECKey ecKey : WalletContextHolder.walletKeys()) {
            keyStrHex.add(Utils.HEX.encode(ecKey.getPubKeyHash()));
        }
        new HttpNetTaskRequest(this.getContext()).httpRequest(ReqCmd.getBalances, keyStrHex, new HttpNetComplete() {
            @Override
            public void completeCallback(byte[] jsonStr) {
                try {
                    GetBalancesResponse getBalancesResponse = Json.jsonmapper().readValue(jsonStr, GetBalancesResponse.class);
                    itemList.clear();
                    for (Coin coin : getBalancesResponse.getBalance()) {
                        if (!coin.isZero()) {
                            WalletAccountItem walletAccountItem = WalletAccountItem.build(coin,
                                    getBalancesResponse.getTokennames());
                            itemList.add(walletAccountItem);
                        }
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.notifyDataSetChanged();
                        }
                    });
                } catch (IOException e) {
                    Log.e(LogConstant.TAG, "ReqCmd.getBalances", e);
                }
            }
        });
    }

    @Override
    public View initView(LayoutInflater inflater, @Nullable ViewGroup container) {
        return inflater.inflate(R.layout.fragment_wallet_account, container, false);
    }

    @Override
    public void initEvent() {

        this.regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), RegActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        this.shopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    code = BrowserAccessTokenContext.check(getContext());
                    if ("".equals(code))
                        Toast.makeText(getContext(), "网络慢,请重试", Toast.LENGTH_LONG).show();
                    else if ("405".equals(code))
                        new LovelyInfoDialog(getContext())
                                .setTopColorRes(R.color.colorPrimary)
                                .setIcon(R.drawable.ic_info_white_24px)
                                .setTitle(R.string.dialog_title_error)
                                .setMessage("请先注册或登录")
                                .show();
                    else {
                        BrowserAccessTokenContext.open(getContext(), WalletContextHolder.getMBigtangle() +
                                "/shop/browse.jsf", code);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });
        this.rechargeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<ECKey> ecKeys = WalletContextHolder.walletKeys();
                if (CollectionUtils.isEmpty(ecKeys)) {
                    new LovelyInfoDialog(getContext())
                            .setTopColorRes(R.color.colorPrimary)
                            .setIcon(R.drawable.ic_info_white_24px)
                            .setTitle(R.string.dialog_title_error)
                            .setMessage(R.string.current_wallet_eckeys_empty)
                            .show();
                    return;
                }

                ECKey ecKey = ecKeys.get(0);
                final String address = ecKey.toAddress(WalletContextHolder.networkParameters).toBase58();

                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse(WalletContextHolder.getMBigtangle() +
                        "/public/recharge.jsf?address=" + address);//此处填链接
                intent.setData(content_url);
                startActivity(intent);
            }
        });
        this.payoffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    code = BrowserAccessTokenContext.check(getContext());
                    if ("".equals(code))
                        Toast.makeText(getContext(), "网络慢,请重试", Toast.LENGTH_LONG).show();
                    else if ("405".equals(code))
                        new LovelyInfoDialog(getContext())
                                .setTopColorRes(R.color.colorPrimary)
                                .setIcon(R.drawable.ic_info_white_24px)
                                .setTitle(R.string.dialog_title_error)
                                .setMessage("请先注册或登录")
                                .show();
                    else {
                        BrowserAccessTokenContext.open(getContext(), WalletContextHolder.getMBigtangle() +
                                "/shop/payoff.jsf", code);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });
        this.miningButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    code = BrowserAccessTokenContext.check(getContext());
                    if ("".equals(code))
                        Toast.makeText(getContext(), "网络慢,请重试", Toast.LENGTH_LONG).show();
                    else if ("405".equals(code))
                        new LovelyInfoDialog(getContext())
                                .setTopColorRes(R.color.colorPrimary)
                                .setIcon(R.drawable.ic_info_white_24px)
                                .setTitle(R.string.dialog_title_error)
                                .setMessage("请先注册或登录")
                                .show();
                    else {
                        BrowserAccessTokenContext.open(getContext(), WalletContextHolder.getMBigtangle() +
                                "/shop/miningreward.jsf", code);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });
        this.refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshData();
            }
        });


        this.helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkVersion()) {
                    UpdateUtil.closeApp();
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Intent intent = new Intent();
                            intent.setAction("android.intent.action.VIEW");
                            Uri content_url = Uri.parse(
                                    "https://www.bigtangle.xyz");//此处填链接
                            intent.setData(content_url);
                            startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });


    }
    private boolean checkVersion() {

        //这里来检测版本是否需要更新
        getActivity().setContentView(R.layout.progress);
        mUpdateManager = new UpdateManager(getContext());
        return mUpdateManager.checkUpdateInfo();
    }
    /**
     * 检查包是否存在
     *
     * @param packname
     * @return
     */
    private boolean checkPackInfo(String packname) {
        PackageInfo packageInfo = null;
        try {
            packageInfo = getActivity().getPackageManager().getPackageInfo(packname, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return packageInfo != null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.swipeContainer.setOnRefreshListener(this);

        this.recyclerViewContainer.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new WrapContentLinearLayoutManager(getContext());
        this.recyclerViewContainer.setLayoutManager(layoutManager);
        this.recyclerViewContainer.setAdapter(this.mAdapter);
    }

    @Override
    public void onRefresh() {
        this.swipeContainer.setRefreshing(false);
        this.refreshData();
    }
}
