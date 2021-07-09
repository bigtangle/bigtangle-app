package net.bigtangle.wallet.activity.wallet;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
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
import net.bigtangle.wallet.activity.wallet.adapters.WalletAccountItemListAdapter;
import net.bigtangle.wallet.activity.wallet.dialog.WalletDownfileDialog;
import net.bigtangle.wallet.activity.wallet.dialog.WalletPasswordDialog;
import net.bigtangle.wallet.activity.wallet.model.WalletAccountItem;
import net.bigtangle.wallet.components.BaseLazyFragment;
import net.bigtangle.wallet.components.WrapContentLinearLayoutManager;
import net.bigtangle.wallet.core.BrowserAccessTokenContext;
import net.bigtangle.wallet.core.LocalStorageContext;
import net.bigtangle.wallet.core.WalletContextHolder;
import net.bigtangle.wallet.core.constant.LogConstant;
import net.bigtangle.wallet.core.http.HttpNetComplete;
import net.bigtangle.wallet.core.http.HttpNetTaskRequest;

import org.apache.commons.collections4.CollectionUtils;

import java.io.File;
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


    @BindView(R.id.aliverify_button)
    Button aliverifyButton;


    @BindView(R.id.help_button)
    Button helpButton;

    @BindView(R.id.refresh_button)
    Button refreshButton;
    @BindView(R.id.load_key_button)
    Button loadKeyButton;

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
        for (ECKey ecKey : WalletContextHolder.get().walletKeys()) {
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


        this.refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshData();
            }
        });

        this.aliverifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            PackageManager packageManager = getActivity().getPackageManager();
                            if (checkPackInfo("com.alibaba.sample")) {
                                Intent intent = packageManager.getLaunchIntentForPackage("com.alibaba.sample");
                                startActivity(intent);
                            } else {
                                Intent intent = new Intent();
                                intent.setAction("android.intent.action.VIEW");
                                Uri content_url = Uri.parse(
                                        "http://bigtangle.oss-cn-beijing.aliyuncs.com/app/identity_verify.apk");//此处填链接
                                intent.setData(content_url);
                                startActivity(intent);
                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });

        this.helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

        this.loadKeyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new WalletDownfileDialog(getContext(), R.style.CustomDialogStyle).setListenter(new WalletDownfileDialog.OnWalletDownfileListenter() {
                    @Override
                    public void downloadFileStatus(boolean success, Exception e) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (success) {
                                    File file = new File(LocalStorageContext.get().readWalletDirectory() + "download.wallet");
                                    String directory = file.getParent() + "/";
                                    String filename = file.getName();
                                    String prefix = filename.contains(".") ? filename.substring(0, filename.lastIndexOf(".")) : filename;
                                    WalletContextHolder.get().reloadWalletFile(directory, prefix);
                                    if (WalletContextHolder.get().checkWalletHavePassword()) {
                                        new WalletPasswordDialog(getContext(), R.style.CustomDialogStyle)
                                                .setListenter(new WalletPasswordDialog.OnWalletVerifyPasswordListenter() {

                                                    @Override
                                                    public void verifyPassword(String password) {
                                                        onLazyLoad();
                                                    }
                                                }).show();
                                    } else {
                                        onLazyLoad();
                                    }
                                    LocalStorageContext.get().writeWalletPath(directory, prefix);
                                    Toast toast = Toast.makeText(getContext(), getContext().getString(R.string.download_wallet_file_success), Toast.LENGTH_SHORT);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();
                                } else {
                                    Toast toast = Toast.makeText(getContext(), getContext().getString(R.string.download_wallet_file_fail) + e.getMessage(), Toast.LENGTH_SHORT);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();
                                }
                            }
                        });
                    }
                }).show();
            }
        });

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
