package net.bigtangle.wallet.activity.transaction.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import net.bigtangle.core.Contact;
import net.bigtangle.core.ContactInfo;
import net.bigtangle.core.DataClassName;
import net.bigtangle.core.ECKey;
import net.bigtangle.core.Json;
import net.bigtangle.params.ReqCmd;
import net.bigtangle.utils.OkHttp3Util;
import net.bigtangle.wallet.R;
import net.bigtangle.wallet.activity.settings.adapter.ContactItemListAdapter;
import net.bigtangle.wallet.activity.settings.model.ContactInfoItem;
import net.bigtangle.wallet.components.WrapContentLinearLayoutManager;
import net.bigtangle.wallet.core.WalletContextHolder;
import net.bigtangle.wallet.core.constant.HttpConnectConstant;
import net.bigtangle.wallet.core.http.HttpNetComplete;
import net.bigtangle.wallet.core.http.HttpNetRunaDispatch;
import net.bigtangle.wallet.core.http.HttpRunaExecute;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ContactChooseDialog extends Dialog implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.recycler_view_container)
    RecyclerView recyclerViewContainer;

    @BindView(R.id.swipe_container)
    SwipeRefreshLayout swipeContainer;

    @BindView(R.id.cancel_button)
    Button cancelButton;

    private ContactItemListAdapter mAdapter;

    private List<ContactInfoItem> itemList;

    private Context context;

    protected View mContextView = null;

    public ContactChooseDialog(Context context, int theme) {
        super(context, theme);
        this.context = context;
    }

    private OnContactChooseItemListener listener;

    public ContactChooseDialog setListener(OnContactChooseItemListener listener) {
        this.listener = listener;
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
        mContextView = LayoutInflater.from(context).inflate(R.layout.dialog_transaction_contact, null);
        setContentView(mContextView);
        ButterKnife.bind(this, mContextView);
        if (this.itemList == null) {
            this.itemList = new ArrayList<ContactInfoItem>();
        }

        this.swipeContainer.setOnRefreshListener(this);

        this.recyclerViewContainer.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new WrapContentLinearLayoutManager(getContext());
        this.recyclerViewContainer.setLayoutManager(layoutManager);

        this.mAdapter = new ContactItemListAdapter(getContext(), this.itemList);
        this.mAdapter.setListenter(new ContactItemListAdapter.OnContactCooCallbackListenter() {
            @Override
            public void refreshView(ContactInfoItem item) {
                if (listener != null) {
                    listener.chooseAddress(item.getAddress());
                }
                dismiss();
            }
        });
        this.recyclerViewContainer.setAdapter(this.mAdapter);

        this.cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.chooseAddress("");
                }
                dismiss();
            }
        });

        initData();
    }

    private void initData() {
        HashMap<String, String> requestParam = new HashMap<String, String>();

        List<ECKey> issuedKeys = WalletContextHolder.get().walletKeys();
        ECKey pubKeyTo = issuedKeys.get(0);

        requestParam.put("pubKey", pubKeyTo.getPublicKeyAsHex());
        requestParam.put("dataclassname", DataClassName.CONTACTINFO.name());

        new HttpNetRunaDispatch(getContext(), new HttpNetComplete() {
            @Override
            public void completeCallback(String jsonStr) {
                mAdapter.notifyDataSetChanged();
            }
        }, new HttpRunaExecute() {
            @Override
            public void execute() throws Exception {
                itemList.clear();
                byte[] bytes = OkHttp3Util.postAndGetBlock(HttpConnectConstant.HTTP_SERVER_URL + ReqCmd.getUserData.name(),
                        Json.jsonmapper().writeValueAsString(requestParam));

                if (bytes == null || bytes.length == 0) {
                    return;
                }
                ContactInfo contactInfo = new ContactInfo().parse(bytes);
                List<Contact> list = contactInfo.getContactList();
                if (list != null && !list.isEmpty()) {
                    for (Contact contact : list) {
                        ContactInfoItem contactInfoItem = ContactInfoItem.build(contact.getName(), contact.getAddress());
                        itemList.add(contactInfoItem);
                    }
                }
            }
        }).execute();
    }

    @Override
    public void onRefresh() {
        this.initData();
        this.swipeContainer.setRefreshing(false);
        this.mAdapter.notifyDataSetChanged();
    }

    public interface OnContactChooseItemListener {

        public void chooseAddress(String address);
    }
}
