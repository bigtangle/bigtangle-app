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
import net.bigtangle.core.Json;
import net.bigtangle.core.MultiSignBy;
import net.bigtangle.core.Sha256Hash;
import net.bigtangle.core.Transaction;
import net.bigtangle.core.Utils;
import net.bigtangle.params.ReqCmd;
import net.bigtangle.utils.OkHttp3Util;
import net.bigtangle.wallet.R;
import net.bigtangle.wallet.core.HttpService;
import net.bigtangle.wallet.core.WalletContextHolder;
import net.bigtangle.wallet.core.constant.HttpConnectConstant;
import net.bigtangle.wallet.core.http.HttpNetComplete;
import net.bigtangle.wallet.core.http.HttpNetRunaDispatch;
import net.bigtangle.wallet.core.http.HttpRunaExecute;

import org.apache.commons.lang3.StringUtils;

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
                            .setMessage("联系人不可以为空")
                            .show();
                    return;
                }
                String privKeyStr = addressTextInput.getText().toString();
                if (StringUtils.isBlank(privKeyStr)) {
                    new LovelyInfoDialog(context)
                            .setTopColorRes(R.color.colorPrimary)
                            .setIcon(R.drawable.ic_error_white_24px)
                            .setTitle(context.getString(R.string.dialog_title_error))
                            .setMessage("地址不可以为空")
                            .show();
                    return;
                }
                new HttpNetRunaDispatch(context, new HttpNetComplete() {
                    @Override
                    public void completeCallback(String jsonStr) {
                        dismiss();
                        if (listenter != null) {
                            listenter.refreshView();
                        }
                    }
                }, new HttpRunaExecute() {
                    @Override
                    public void execute() throws Exception {
                        HashMap<String, String> requestParam = new HashMap<String, String>();
                        byte[] data = OkHttp3Util.post(HttpConnectConstant.HTTP_SERVER_URL + ReqCmd.getTip.name(),
                                Json.jsonmapper().writeValueAsString(requestParam));
                        Block block = WalletContextHolder.networkParameters.getDefaultSerializer().makeBlock(data);
                        block.setBlockType(Block.Type.BLOCKTYPE_USERDATA);

                        List<ECKey> issuedKeys = WalletContextHolder.get().walletKeys();
                        ECKey pubKeyTo = issuedKeys.get(0);

                        Transaction coinbase = new Transaction(WalletContextHolder.networkParameters);
                        Contact contact = new Contact();
                        contact.setName(contactNameTextInput.getText().toString());
                        contact.setAddress(addressTextInput.getText().toString());
                        ContactInfo contactInfo = (ContactInfo) HttpService.getUserdata(DataClassName.CONTACTINFO.name());

                        List<Contact> list = contactInfo.getContactList();
                        list.add(contact);
                        contactInfo.setContactList(list);

                        coinbase.setDataClassName(DataClassName.CONTACTINFO.name());
                        coinbase.setData(contactInfo.toByteArray());

                        Sha256Hash sighash = coinbase.getHash();

                        ECKey.ECDSASignature party1Signature = pubKeyTo.sign(sighash, WalletContextHolder.get().getAesKey());
                        byte[] buf1 = party1Signature.encodeToDER();

                        List<MultiSignBy> multiSignBies = new ArrayList<MultiSignBy>();
                        MultiSignBy multiSignBy0 = new MultiSignBy();
                        multiSignBy0.setAddress(pubKeyTo.toAddress(WalletContextHolder.networkParameters).toBase58());
                        multiSignBy0.setPublickey(Utils.HEX.encode(pubKeyTo.getPubKey()));
                        multiSignBy0.setSignature(Utils.HEX.encode(buf1));
                        multiSignBies.add(multiSignBy0);
                        coinbase.setDataSignature(Json.jsonmapper().writeValueAsBytes(multiSignBies));

                        block.addTransaction(coinbase);
                        block.solve();

                        OkHttp3Util.post(HttpConnectConstant.HTTP_SERVER_URL + ReqCmd.saveBlock.name(), block.bitcoinSerialize());
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
