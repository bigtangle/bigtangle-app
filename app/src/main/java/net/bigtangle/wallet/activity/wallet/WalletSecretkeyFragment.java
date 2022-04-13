package net.bigtangle.wallet.activity.wallet;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
import net.bigtangle.wallet.WalletProtobufSerializer;
import net.bigtangle.wallet.activity.BackupActivity;
import net.bigtangle.wallet.activity.SPUtil;
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
import net.bigtangle.wallet.core.utils.CommonUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import static android.app.Activity.RESULT_OK;
import static net.bigtangle.wallet.core.WalletContextHolder.wallet;

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

    @BindView(R.id.load_key_button)
    Button loadKeyButton;
    @BindView(R.id.import_key_button)
    Button importKeyButton;
    @BindView(R.id.backup_button)
    Button backupButton;

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
        setFroceLoadData(true);
    }

    @Override
    public void onLazyLoad() {
        this.itemList.clear();
        String un = SPUtil.get(getContext(), "username", "").toString();
        InputStream stream = CommonUtil.loadFromDB(un, getContext());

        WalletContextHolder.loadWallet(stream);

        List<ECKey> issuedKeys = WalletContextHolder.walletKeys();
        if (issuedKeys != null && !issuedKeys.isEmpty()) {
            for (ECKey ecKey : issuedKeys) {
                WalletSecretkeyItem walletSecretkeyItem = new WalletSecretkeyItem();
                walletSecretkeyItem.setAddress(ecKey.toAddress(WalletContextHolder.networkParameters).toBase58());
                walletSecretkeyItem.setPubKeyHex(ecKey.getPublicKeyAsHex());
                walletSecretkeyItem.setPrivateKey(ecKey.getPrivateKeyAsHex());
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


        this.importKeyButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String dirs="/storage/emulated/0/Download/";
                new LFilePicker()
                        .withSupportFragment(WalletSecretkeyFragment.this)
                        .withRequestCode(REQUESTCODE_FROM_ACTIVITY)
                        .withStartPath(dirs)
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
                    public void downloadFileStatus(boolean success, Exception e) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (success) {
                                    String un = SPUtil.get(getContext(), "username", "").toString();
                                    InputStream stream = CommonUtil.loadFromDB(un, getContext());
                                    WalletContextHolder.loadWallet(stream);
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



        this.addKeyButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                showDialog();
            }
        });
      this.backupButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), BackupActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
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
                                try {
                                    ECKey ecKey = new ECKey();
                                    WalletContextHolder.wallet.importKey(ecKey);
                                    ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                                    new WalletProtobufSerializer().writeWallet(wallet, outStream);
                                    byte[] a = outStream.toByteArray();
                                    CommonUtil.updateDB("bigtangle", a, getContext());
                                    onLazyLoad();
                                } catch (Exception e) {

                                }

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
                                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                                new WalletProtobufSerializer().writeWallet(wallet, outStream);
                                byte[] a = outStream.toByteArray();
                                CommonUtil.updateDB("bigtangle", a, getContext());
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
                    InputStream uodateStram = new FileInputStream(file);
                    byte[] updateBytes = CommonUtil.urlTobyte(uodateStram);

                    String un = SPUtil.get(getContext(), "username", "").toString();
                    CommonUtil.updateDB(un, updateBytes, getContext());
                    InputStream stream = CommonUtil.loadFromDB(un, getContext());
                    WalletContextHolder.loadWallet(stream);

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
