package net.bigtangle.wallet.activity.wallet;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.bigtangle.apps.data.Certificate;
import net.bigtangle.apps.data.IdentityData;
import net.bigtangle.core.ECKey;
import net.bigtangle.wallet.R;
import net.bigtangle.wallet.activity.wallet.adapters.WalletAccountCertificateListAdapter;
import net.bigtangle.wallet.activity.wallet.adapters.WalletAccountIdentityListAdapter;
import net.bigtangle.wallet.activity.wallet.model.WalletAccountCertificateItem;
import net.bigtangle.wallet.activity.wallet.model.WalletAccountIdentiyItem;
import net.bigtangle.wallet.components.BaseLazyFragment;
import net.bigtangle.wallet.components.WrapContentLinearLayoutManager;
import net.bigtangle.wallet.core.WalletContextHolder;
import net.bigtangle.wallet.core.constant.LogConstant;
import net.bigtangle.wallet.core.http.URLUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import butterknife.BindView;

public class WalletAccountCertificateFragment extends BaseLazyFragment implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.recycler_view_container)
    RecyclerView recyclerViewContainer;

    @BindView(R.id.swipe_container)
    SwipeRefreshLayout swipeContainer;


    private WalletAccountCertificateListAdapter mAdapter;

    private List<WalletAccountCertificateItem> itemList;

    public static WalletAccountCertificateFragment newInstance() {
        return new WalletAccountCertificateFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (this.itemList == null) {
            this.itemList = new ArrayList<WalletAccountCertificateItem>();
        }
        setFroceLoadData(true);
        this.mAdapter = new WalletAccountCertificateListAdapter(getContext(), this.itemList);
    }

    private void initData() {

        List<Certificate> certificates = new ArrayList<Certificate>();
        String idtoken = "";
        try {
            for (ECKey ecKey : WalletContextHolder.get().walletKeys()) {
                Future<List<Certificate>> future = new URLUtil().calculateCertificate(ecKey, ecKey);
                Future<String> future2 = new URLUtil().getIdtoken(ecKey);
                String temp = future2.get();
                if (temp != null && !"".equals(temp.trim()))
                    idtoken = temp;
                Log.i(LogConstant.TAG, "future Certificate  ");
                certificates.addAll(future.get());
                Log.i(LogConstant.TAG, "initData certificates.size()" + certificates.size());
            }
        } catch (Exception e) {

        }
        itemList.clear();
        Log.i(LogConstant.TAG, "initData adapter certificates.size()" + certificates.size());
        if (certificates != null && !certificates.isEmpty()) {
            for (Certificate certificate : certificates) {
                WalletAccountCertificateItem walletAccountCertificateItem = new WalletAccountCertificateItem();
                walletAccountCertificateItem.setDescription(certificate.getDescription());
                walletAccountCertificateItem.setPhoto(certificate.getFile());
                walletAccountCertificateItem.setIdtoken(idtoken);
                Log.i(LogConstant.TAG, "initData " + walletAccountCertificateItem.getDescription());
                itemList.add(walletAccountCertificateItem);
            }
            Log.i(LogConstant.TAG, "initData itemList.size()" + itemList.size());
            this.mAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onRefresh() {
        this.swipeContainer.setRefreshing(false);
        this.onLazyLoad();

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
    public void onLazyLoad() {
        initData();
    }

    @Override
    public View initView(LayoutInflater inflater, @Nullable ViewGroup container) {
        return inflater.inflate(R.layout.fragment_wallet_certificate, container, false);
    }

    @Override
    public void initEvent() {

    }
}
