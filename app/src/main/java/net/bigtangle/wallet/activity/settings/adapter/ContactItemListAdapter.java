package net.bigtangle.wallet.activity.settings.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import net.bigtangle.core.Block;
import net.bigtangle.core.Contact;
import net.bigtangle.core.ContactInfo;
import net.bigtangle.core.DataClassName;
import net.bigtangle.core.ECKey;
import net.bigtangle.utils.Json;
import net.bigtangle.core.MultiSignBy;
import net.bigtangle.core.Sha256Hash;
import net.bigtangle.core.Transaction;
import net.bigtangle.core.Utils;
import net.bigtangle.params.ReqCmd;
import net.bigtangle.utils.OkHttp3Util;
import net.bigtangle.wallet.R;
import net.bigtangle.wallet.activity.SPUtil;
import net.bigtangle.wallet.activity.settings.model.ContactInfoItem;
import net.bigtangle.wallet.core.HttpService;
import net.bigtangle.wallet.core.WalletContextHolder;
import net.bigtangle.wallet.core.constant.HttpConnectConstant;
import net.bigtangle.wallet.core.http.HttpNetComplete;
import net.bigtangle.wallet.core.http.HttpNetRunaDispatch;
import net.bigtangle.wallet.core.http.HttpRunaExecute;
import net.bigtangle.wallet.core.utils.CommonUtil;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.alibaba.security.rp.RPSDK.getContext;

public class ContactItemListAdapter extends RecyclerView.Adapter<ContactItemListAdapter.ItemViewHolder> {

    private Context mContext;
    private List<ContactInfoItem> itemList;
    private OnContactRemCallbackListenter onContactRemCallbackListenter;
    private OnContactCooCallbackListenter onContactCooCallbackListenter;

    public ContactItemListAdapter(Context context, List<ContactInfoItem> itemList) {
        this.mContext = context;
        this.itemList = itemList;
    }

    public void setListenter(OnContactRemCallbackListenter onContactRemCallbackListenter) {
        this.onContactRemCallbackListenter = onContactRemCallbackListenter;
    }

    public void setListenter(OnContactCooCallbackListenter onContactCooCallbackListenter) {
        this.onContactCooCallbackListenter = onContactCooCallbackListenter;
    }

    @NonNull
    @Override
    public ContactItemListAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_contact_item, parent, false);
        ContactItemListAdapter.ItemViewHolder viewHolder = new ContactItemListAdapter.ItemViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ContactItemListAdapter.ItemViewHolder holder, int position) {
        holder.bind(itemList.get(position));
    }

    @Override
    public int getItemCount() {
        return itemList != null ? itemList.size() : 0;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.contact_name_text_view)
        TextView contactNameTextView;

        @BindView(R.id.address_text_view)
        TextView addressTextView;

        @BindView(R.id.contact_item_line)
        LinearLayout itemLine;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(ContactInfoItem item) {
            this.contactNameTextView.setText(item.getContactName());
            this.addressTextView.setText(item.getAddress());

            this.itemLine.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    new LovelyStandardDialog(mContext, LovelyStandardDialog.ButtonLayout.HORIZONTAL)
                            .setTopColorRes(R.color.colorPrimary)
                            .setButtonsColor(Color.WHITE)
                            .setIcon(R.drawable.ic_error_white_24px)
                            .setTitle(mContext.getString(R.string.whether_to_delete))
                            .setMessage(mContext.getString(R.string.whether_to_delete_user_contact_data))
                            .setPositiveButton(mContext.getString(R.string.ok), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    new HttpNetRunaDispatch(mContext, new HttpNetComplete() {
                                        @Override
                                        public void completeCallback(byte[] jsonStr) {
                                            if (onContactRemCallbackListenter != null) {
                                                onContactRemCallbackListenter.refreshView();
                                            }
                                        }
                                    }, new HttpRunaExecute() {
                                        @Override
                                        public void execute() throws Exception {
                                            removeContact(item);
                                        }
                                    }).execute();
                                }
                            }).setNegativeButton(mContext.getString(R.string.cancel), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        }
                    }).show();
                    return true;
                }
            });

            this.itemLine.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onContactCooCallbackListenter != null) {
                        TextView contactNameTextView = v.findViewById(R.id.contact_name_text_view);
                        TextView addressTextView = v.findViewById(R.id.address_text_view);
                        onContactCooCallbackListenter.refreshView(
                                ContactInfoItem.build(contactNameTextView.getText().toString(), addressTextView.getText().toString()));
                    }
                }
            });
        }
    }

    private void removeContact(ContactInfoItem item) throws Exception {
        if (item == null) {
            return;
        }
        String name = item.getContactName();
        String address = item.getAddress();

        HashMap<String, String> requestParam = new HashMap<String, String>();

        byte[] data = OkHttp3Util.postAndGetBlock(HttpConnectConstant.HTTP_SERVER_URL + ReqCmd.getTip.name(),
                Json.jsonmapper().writeValueAsString(requestParam));

        Block block = WalletContextHolder.networkParameters.getDefaultSerializer().makeBlock(data);
        block.setBlockType(Block.Type.BLOCKTYPE_USERDATA);

        Transaction coinbase = new Transaction(WalletContextHolder.networkParameters);

        ContactInfo contactInfo = (ContactInfo) HttpService.getUserdata(DataClassName.CONTACTINFO.name());
        List<Contact> list = contactInfo.getContactList();
        List<Contact> tempList = new ArrayList<Contact>();
        for (Contact contact : list) {
            if (name.trim().equals(contact.getName().trim())
                    && address.trim().equals(contact.getAddress().trim())) {
                continue;
            }
            tempList.add(contact);
        }
        contactInfo.setContactList(tempList);

        coinbase.setDataClassName(DataClassName.CONTACTINFO.name());
        coinbase.setData(contactInfo.toByteArray());
        String un = SPUtil.get(mContext, "username", "").toString();
        InputStream stream = CommonUtil.loadFromDB(un, mContext);
        WalletContextHolder.loadWallet(stream);
        try {
            Thread.sleep(2000);
        }catch (Exception e){

        }
        List<ECKey> issuedKeys = WalletContextHolder.walletKeys();
        ECKey pubKeyTo = issuedKeys.get(0);

        Sha256Hash sighash = coinbase.getHash();
        ECKey.ECDSASignature party1Signature = pubKeyTo.sign(sighash, WalletContextHolder.getAesKey());
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

    public interface OnContactRemCallbackListenter {

        void refreshView();
    }

    public interface OnContactCooCallbackListenter {

        void refreshView(ContactInfoItem item);
    }
}
