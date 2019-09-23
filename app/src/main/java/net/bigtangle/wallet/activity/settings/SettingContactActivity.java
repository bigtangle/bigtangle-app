package net.bigtangle.wallet.activity.settings;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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
import net.bigtangle.wallet.activity.settings.dialog.ContactAddDialog;
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

public class SettingContactActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.recycler_view_container)
    RecyclerView recyclerViewContainer;

    @BindView(R.id.swipe_container)
    SwipeRefreshLayout swipeContainer;

    @BindView(R.id.toolbar_localMain)
    Toolbar toolbarLocalMain;

    @BindView(R.id.add_contact_button)
    Button addContactButton;

    private ContactItemListAdapter mAdapter;

    private List<ContactInfoItem> itemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_contact);
        ButterKnife.bind(this);

        if (this.itemList == null) {
            this.itemList = new ArrayList<ContactInfoItem>();
        }

        this.swipeContainer.setOnRefreshListener(this);

        this.recyclerViewContainer.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new WrapContentLinearLayoutManager(this);
        this.recyclerViewContainer.setLayoutManager(layoutManager);

        this.mAdapter = new ContactItemListAdapter(this, this.itemList);
        this.mAdapter.setListenter(new ContactItemListAdapter.OnContactRemCallbackListenter() {
            @Override
            public void refreshView() {
                try {
                    Thread.sleep(2000L);
                } catch (InterruptedException e) {
                }
                initData();
            }
        });
        this.recyclerViewContainer.setAdapter(this.mAdapter);

        setSupportActionBar(toolbarLocalMain);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        this.initData();

        this.addContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ContactAddDialog(SettingContactActivity.this, R.style.CustomDialogStyle)
                        .setListenter(new ContactAddDialog.OnContactAddCallbackListenter() {

                            @Override
                            public void refreshView() {
                                try {
                                    Thread.sleep(2000);
                                } catch (InterruptedException e) {
                                }
                                initData();
                            }
                        }).show();
            }
        });
    }

    private void initData() {
        HashMap<String, String> requestParam = new HashMap<String, String>();

        List<ECKey> issuedKeys = WalletContextHolder.get().walletKeys();
        ECKey pubKeyTo = issuedKeys.get(0);

        requestParam.put("pubKey", pubKeyTo.getPublicKeyAsHex());
        requestParam.put("dataclassname", DataClassName.CONTACTINFO.name());

        new HttpNetRunaDispatch(this, new HttpNetComplete() {
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

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}
