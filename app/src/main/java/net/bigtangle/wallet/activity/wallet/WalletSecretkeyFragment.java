package net.bigtangle.wallet.activity.wallet;

import android.content.Intent;
import android.graphics.Color;
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

import com.leon.lfilepickerlibrary.LFilePicker;
import com.yarolegovich.lovelydialog.LovelyInfoDialog;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import net.bigtangle.core.ECKey;
import net.bigtangle.core.Utils;
import net.bigtangle.wallet.R;
import net.bigtangle.wallet.activity.wallet.adapters.WalletSecretkeyItemListAdapter;
import net.bigtangle.wallet.activity.wallet.dialog.WalletDownfileDialog;
import net.bigtangle.wallet.activity.wallet.dialog.WalletPasswordDialog;
import net.bigtangle.wallet.activity.wallet.dialog.WalletSecretkeyDialog;
import net.bigtangle.wallet.activity.wallet.model.WalletSecretkeyItem;
import net.bigtangle.wallet.components.BaseLazyFragment;
import net.bigtangle.wallet.components.WrapContentLinearLayoutManager;
import net.bigtangle.wallet.core.LocalStorageContext;
import net.bigtangle.wallet.core.WalletContextHolder;
import net.bigtangle.wallet.core.constant.LogConstant;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import static android.app.Activity.RESULT_OK;

public class WalletSecretkeyFragment extends BaseLazyFragment implements SwipeRefreshLayout.OnRefreshListener {

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

    @BindView(R.id.load_key_button)
    Button loadKeyButton;

    public static WalletSecretkeyFragment newInstance() {
        return new WalletSecretkeyFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (this.itemList == null) {
            this.itemList = new ArrayList<WalletSecretkeyItem>();
        }
        setFroceLoadData(true);
    }

    @Override
    public void onLazyLoad() {
        this.itemList.clear();
        List<ECKey> issuedKeys = WalletContextHolder.walletKeys();
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
    public View initView(LayoutInflater inflater, @Nullable ViewGroup container) {
        return inflater.inflate(R.layout.fragment_wallet_secretkey, container, false);
    }

    @Override
    public void initEvent() {
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
                                WalletContextHolder.wallet.importKey(ecKey);

                                onLazyLoad();
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
                        .withStartPath(LocalStorageContext.get().readWalletDirectory())
                        .withIsGreater(false)
                        .withFileSize(500 * 1024)
                        .start();
            }
        });

        this.loadKeyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new WalletDownfileDialog(getContext(), R.style.CustomDialogStyle).setListenter(new WalletDownfileDialog.OnWalletDownfileListenter() {
                    @Override
                    public void downloadFileStatus(boolean success,Exception e) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (success) {
                                    File file = new File(LocalStorageContext.get().readWalletDirectory() + "download.wallet");
                                    String directory = file.getParent() + "/";
                                    String filename = file.getName();
                                    String prefix = filename.contains(".") ? filename.substring(0, filename.lastIndexOf(".")) : filename;
                                    WalletContextHolder.get().reloadWalletFile(directory, prefix);
                                    if (WalletContextHolder.checkWalletHavePassword()) {
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
                                    Toast toast = Toast.makeText(getContext(), getContext().getString(R.string.download_wallet_file_fail), Toast.LENGTH_SHORT);
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.mAdapter = new WalletSecretkeyItemListAdapter(getContext(), itemList);
        swipeContainer.setOnRefreshListener(this);

        LinearLayoutManager layoutManager = new WrapContentLinearLayoutManager(getContext());
        this.recyclerViewContainer.setHasFixedSize(true);
        this.recyclerViewContainer.setLayoutManager(layoutManager);
        this.recyclerViewContainer.setAdapter(mAdapter);
    }

    @Override
    public void onRefresh() {
        this.onLazyLoad();
        this.swipeContainer.setRefreshing(false);
        this.mAdapter.notifyDataSetChanged();
    }

    private void showDialog() {
        new WalletSecretkeyDialog(getContext(), R.style.CustomDialogStyle)
                .setListenter(new WalletSecretkeyDialog.OnGetWalletSecretKeyListenter() {
                    @Override
                    public void getWalletSecretKey(String publicKey, String privateKey) {
                        try {
                            byte[] pubKeyBuf = Utils.HEX.decode(publicKey);
                            byte[] privKeyBuf = Utils.HEX.decode(privateKey);
                            ECKey ecKey = ECKey.fromPrivateAndPrecalculatedPublic(privKeyBuf, pubKeyBuf);
                            if (WalletContextHolder.getAesKey() == null) {
                                WalletContextHolder.wallet.importKey(ecKey);
                            } else {
                                List<ECKey> walletKeys = new ArrayList<ECKey>();
                                walletKeys.add(ecKey);
                                WalletContextHolder.wallet.importKeysAndEncrypt(walletKeys,
                                        WalletContextHolder.getAesKey());
                            }
                            onLazyLoad();
                        } catch (Exception e) {
                            new LovelyInfoDialog(getContext())
                                    .setTopColorRes(R.color.colorPrimary)
                                    .setIcon(R.drawable.ic_error_white_24px)
                                    .setTitle(getContext().getString(R.string.dialog_title_error))
                                    .setMessage(getContext().getString(R.string.dialog_wallet_secretkey_add_key_failed))
                                    .show();
                        }
                    }
                }).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUESTCODE_FROM_ACTIVITY) {
                List<String> list = data.getStringArrayListExtra("paths");
                if (list.isEmpty()) {
                    new LovelyInfoDialog(getContext())
                            .setTopColorRes(R.color.colorPrimary)
                            .setIcon(R.drawable.ic_error_white_24px)
                            .setTitle(getContext().getString(R.string.dialog_title_error))
                            .setMessage(getContext().getString(R.string.current_selection_file))
                            .show();
                    return;
                }
                try {
                    File file = new File(list.get(0));
                    String directory = file.getParent() + "/";
                    String filename = file.getName();
                    String prefix = filename.contains(".") ? filename.substring(0, filename.lastIndexOf(".")) : filename;
                    WalletContextHolder.get().reloadWalletFile(directory, prefix);
                    LocalStorageContext.get().writeWalletPath(directory, prefix);
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
                } catch (Exception e) {
                    Log.e(LogConstant.TAG, "wallet file", e);
                    new LovelyInfoDialog(getContext())
                            .setTopColorRes(R.color.colorPrimary)
                            .setIcon(R.drawable.ic_error_white_24px)
                            .setTitle(getContext().getString(R.string.dialog_title_error))
                            .setMessage(getContext().getString(R.string.current_selection_file_error))
                            .show();
                }
            }
        }
    }
}
