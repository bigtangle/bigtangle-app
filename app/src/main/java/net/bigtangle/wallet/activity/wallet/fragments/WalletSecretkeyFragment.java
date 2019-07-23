package net.bigtangle.wallet.activity.wallet.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.leon.lfilepickerlibrary.LFilePicker;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import net.bigtangle.core.ECKey;
import net.bigtangle.core.Utils;
import net.bigtangle.wallet.R;
import net.bigtangle.wallet.activity.wallet.adapters.WalletSecretkeyItemListAdapter;
import net.bigtangle.wallet.activity.wallet.model.WalletSecretkeyItem;
import net.bigtangle.wallet.components.SecretkeyDialog;
import net.bigtangle.wallet.components.WrapContentLinearLayoutManager;
import net.bigtangle.wallet.core.WalletContextHolder;
import net.bigtangle.wallet.core.constant.LogConstant;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;

public class WalletSecretkeyFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final int REQUESTCODE_FROM_ACTIVITY = 1000;

    @BindView(R.id.recycler_view_container)
    RecyclerView recyclerViewContainer;

    @BindView(R.id.swipe_container)
    SwipeRefreshLayout swipeContainer;

    @BindView(R.id.add_key_button)
    Button addKeyButton;

    @BindView(R.id.new_key_button)
    Button newKeyButton;

    @BindView(R.id.import_key_button)
    Button importKeyButton;

    private WalletSecretkeyItemListAdapter mAdapter;

    private List<WalletSecretkeyItem> itemList;

    public static WalletSecretkeyFragment newInstance() {
        return new WalletSecretkeyFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (this.itemList == null) {
            this.itemList = new ArrayList<WalletSecretkeyItem>();
        }
    }

    private void initData() {
        this.itemList.clear();
        List<ECKey> issuedKeys = WalletContextHolder.get().wallet().walletKeys(WalletContextHolder.getAesKey());
        if (issuedKeys != null && !issuedKeys.isEmpty()) {
            for (ECKey ecKey : issuedKeys) {
                WalletSecretkeyItem walletSecretkeyItem = new WalletSecretkeyItem();
                walletSecretkeyItem.setAddress(ecKey.toAddress(WalletContextHolder.networkParameters).toBase58());
                walletSecretkeyItem.setPubKeyHex(ecKey.getPublicKeyAsHex());
                this.itemList.add(walletSecretkeyItem);
            }
        }
        this.mAdapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wallet_secretkey, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.mAdapter = new WalletSecretkeyItemListAdapter(getContext(), itemList);
        swipeContainer.setOnRefreshListener(this);
        LinearLayoutManager layoutManager = new WrapContentLinearLayoutManager(getContext());
        this.recyclerViewContainer.setHasFixedSize(true);
        this.recyclerViewContainer.setLayoutManager(layoutManager);
        this.recyclerViewContainer.setAdapter(mAdapter);

        this.addKeyButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                showDialog();
            }
        });

        this.newKeyButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                new LovelyStandardDialog(getContext(), LovelyStandardDialog.ButtonLayout.HORIZONTAL)
                        .setTopColorRes(R.color.colorPrimary)
                        .setButtonsColor(Color.WHITE)
                        .setIcon(R.drawable.ic_error_white_24px)
                        .setTitle(getContext().getString(R.string.dialog_title_info))
                        .setMessage(getContext().getString(R.string.dialog_secretkey_add_key_message))
                        .setPositiveButton(android.R.string.ok, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ECKey ecKey = new ECKey();
                                WalletContextHolder.get().wallet().importKey(ecKey);

                                initData();
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                            }
                        })
                        .show();
            }
        });

        this.importKeyButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new LFilePicker()
                        .withSupportFragment(WalletSecretkeyFragment.this)
                        .withRequestCode(REQUESTCODE_FROM_ACTIVITY)
                        .withStartPath("/storage/emulated/0/Download")
                        .withIsGreater(false)
                        .withFileSize(500 * 1024)
                        .start();
            }
        });

        initData();
    }

    @Override
    public void onRefresh() {
        this.initData();
        this.swipeContainer.setRefreshing(false);
        this.mAdapter.notifyDataSetChanged();
    }

    private void showDialog() {
        SecretkeyDialog dialog = new SecretkeyDialog(
                getContext(), R.style.CustomDialogStyle);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
        dialog.setPositiveButton(new SecretkeyDialog.OnGetWalletSecretKeyListenter() {
            @Override
            public void getWalletSecretKey(String publicKey, String privateKey) {
                byte[] pubKeyBuf = Utils.HEX.decode(publicKey);
                byte[] privKeyBuf = Utils.HEX.decode(privateKey);

                ECKey ecKey = ECKey.fromPrivateAndPrecalculatedPublic(privKeyBuf, pubKeyBuf);
                WalletContextHolder.get().wallet().importKey(ecKey);

                initData();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUESTCODE_FROM_ACTIVITY) {
                List<String> list = data.getStringArrayListExtra("paths");
                for (String filepath : list) {
                    Log.d(LogConstant.TAG, filepath);
                }
            }
        }
    }
}
