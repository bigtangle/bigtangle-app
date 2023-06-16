package net.bigtangle.wallet.activity.settings;

import android.os.Bundle;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import net.bigtangle.core.UserSettingData;
import net.bigtangle.core.UserSettingDataInfo;
import net.bigtangle.wallet.R;
import net.bigtangle.wallet.activity.SPUtil;
import net.bigtangle.wallet.activity.settings.adapter.ContactItemListAdapter;
import net.bigtangle.wallet.activity.settings.dialog.ContactAddDialog;
import net.bigtangle.wallet.activity.settings.model.ContactInfoItem;
import net.bigtangle.wallet.components.WrapContentLinearLayoutManager;
import net.bigtangle.wallet.core.WalletContextHolder;
import net.bigtangle.wallet.core.constant.HttpConnectConstant;
import net.bigtangle.wallet.core.http.URLUtil;
import net.bigtangle.wallet.core.utils.CommonUtil;

import java.io.InputStream;
import java.util.ArrayList;
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
                                initData();
                            }
                        }).show();
            }
        });
    }

    private void initData() {

        String un = SPUtil.get(this, "username", "").toString();
        InputStream stream = CommonUtil.loadFromDB(un, this);
        WalletContextHolder.loadWallet(stream);

        try {
           // WalletContextHolder.wallet.setServerURL(HttpConnectConstant.HTTP_SERVER_URL);
            UserSettingDataInfo userSettingDataInfo = new URLUtil().calculateUserdata().get();
            if (userSettingDataInfo != null) {
                List<UserSettingData> userSettingDataList = userSettingDataInfo.getUserSettingDatas();
                if (userSettingDataList != null) {
                    itemList.clear();
                    for (UserSettingData userSettingData : userSettingDataList) {
                        if (userSettingData.getDomain().equals("ContactInfo")) {
                            ContactInfoItem contactInfoItem = ContactInfoItem.build(userSettingData.getValue(), userSettingData.getKey());
                            itemList.add(contactInfoItem);
                        }
                    }
                    mAdapter.notifyDataSetChanged();
                }
            }
        } catch (Exception e) {

        }
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
