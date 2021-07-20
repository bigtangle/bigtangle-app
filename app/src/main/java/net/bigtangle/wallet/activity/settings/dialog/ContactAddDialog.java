package net.bigtangle.wallet.activity.settings.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.yarolegovich.lovelydialog.LovelyInfoDialog;

import net.bigtangle.core.Block;
import net.bigtangle.core.Contact;
import net.bigtangle.core.ContactInfo;
import net.bigtangle.core.DataClassName;
import net.bigtangle.core.ECKey;
import net.bigtangle.core.UserSettingData;
import net.bigtangle.core.UserSettingDataInfo;
import net.bigtangle.utils.Json;
import net.bigtangle.core.MultiSignBy;
import net.bigtangle.core.Sha256Hash;
import net.bigtangle.core.Transaction;
import net.bigtangle.core.Utils;
import net.bigtangle.params.ReqCmd;
import net.bigtangle.utils.OkHttp3Util;
import net.bigtangle.wallet.R;
import net.bigtangle.wallet.activity.SPUtil;
import net.bigtangle.wallet.core.HttpService;
import net.bigtangle.wallet.core.WalletContextHolder;
import net.bigtangle.wallet.core.constant.HttpConnectConstant;
import net.bigtangle.wallet.core.http.HttpNetComplete;
import net.bigtangle.wallet.core.http.HttpNetRunaDispatch;
import net.bigtangle.wallet.core.http.HttpRunaExecute;
import net.bigtangle.wallet.core.utils.CommonUtil;

import org.apache.commons.lang3.StringUtils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ContactAddDialog extends Dialog {

    @BindView(R.id.contact_name_text_input)
    TextInputEditText contactNameTextInput;

    @BindView(R.id.address_text_input)
    TextInputEditText addressTextInput;

    private Context context;

    protected View mContextView = null;

    @BindView(R.id.negative_button)
    Button negativeButton;

    @BindView(R.id.positive_button)
    Button positiveButton;

    private String address;

    private ContactAddDialog.OnContactAddCallbackListenter listenter;

    public ContactAddDialog(Context context, int theme) {
        super(context, theme);
        this.context = context;
    }

    public ContactAddDialog setAddress(String address) {
        this.address = address;
        return this;
    }

    @Override
    public void show() {
        this.setCanceledOnTouchOutside(false);
        this.setCancelable(false);
        super.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContextView = LayoutInflater.from(context).inflate(R.layout.dialog_setting_contact, null);
        setContentView(mContextView);
        ButterKnife.bind(this, mContextView);
        initView();
    }

    private void initView() {
        if (!StringUtils.isBlank(this.address)) {
            this.addressTextInput.setText(address);
        }
        if (negativeButton != null) {
            negativeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
        }
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pubKeyStr = contactNameTextInput.getText().toString();
                if (StringUtils.isBlank(pubKeyStr)) {
                    new LovelyInfoDialog(context)
                            .setTopColorRes(R.color.colorPrimary)
                            .setIcon(R.drawable.ic_error_white_24px)
                            .setTitle(context.getString(R.string.dialog_title_error))
                            .setMessage(context.getString(R.string.contacts_not_empty))
                            .show();
                    return;
                }
                String privKeyStr = addressTextInput.getText().toString();
                if (StringUtils.isBlank(privKeyStr)) {
                    new LovelyInfoDialog(context)
                            .setTopColorRes(R.color.colorPrimary)
                            .setIcon(R.drawable.ic_error_white_24px)
                            .setTitle(context.getString(R.string.dialog_title_error))
                            .setMessage(context.getString(R.string.address_not_empty))
                            .show();
                    return;
                }
                new HttpNetRunaDispatch(context, new HttpNetComplete() {
                    @Override
                    public void completeCallback(byte[] jsonStr) {
                        dismiss();
                        if (listenter != null) {
                            listenter.refreshView();
                        }
                    }
                }, new HttpRunaExecute() {
                    @Override
                    public void execute() throws Exception {
                        String un = SPUtil.get(context, "username", "").toString();
                        InputStream stream = CommonUtil.loadFromDB(un, context);
                        WalletContextHolder.loadWallet(stream);
                        List<ECKey> issuedKeys = WalletContextHolder.walletKeys();
                        ECKey pubKeyTo = issuedKeys.get(0);

                        UserSettingDataInfo userSettingDataInfo0 = WalletContextHolder.wallet.getUserSettingDataInfo(pubKeyTo, false);
                        if (userSettingDataInfo0 == null) {
                            userSettingDataInfo0 = new UserSettingDataInfo();
                        }
                        List<UserSettingData> contacts = userSettingDataInfo0.getUserSettingDatas();
                        if (contacts == null || contacts.isEmpty()) {
                            contacts = new ArrayList<UserSettingData>();
                        }
                        UserSettingData userSettingData = new UserSettingData();
                        userSettingData.setDomain("ContactInfo");
                        userSettingData.setKey(addressTextInput.getText().toString());
                        userSettingData.setValue(contactNameTextInput.getText().toString());
                        contacts.add(userSettingData);
                        Transaction transaction = new Transaction(WalletContextHolder.networkParameters);
                        userSettingDataInfo0.setUserSettingDatas(contacts);
                        transaction.setDataClassName(DataClassName.UserSettingDataInfo.name());
                        transaction.setData(userSettingDataInfo0.toByteArray());
                        WalletContextHolder.wallet.saveUserdata(pubKeyTo, transaction, false);
                    }
                }).execute();
            }
        });
    }

    public ContactAddDialog setListenter(ContactAddDialog.OnContactAddCallbackListenter listenter) {
        this.listenter = listenter;
        return this;
    }

    public interface OnContactAddCallbackListenter {

        void refreshView();
    }
}
