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
import net.bigtangle.wallet.activity.SPUtil;
import net.bigtangle.wallet.activity.wallet.adapters.WalletAccountCertificateListAdapter;
import net.bigtangle.wallet.activity.wallet.adapters.WalletAccountIdentityListAdapter;
import net.bigtangle.wallet.activity.wallet.model.CertificateVO;
import net.bigtangle.wallet.activity.wallet.model.WalletAccountCertificateItem;
import net.bigtangle.wallet.activity.wallet.model.WalletAccountIdentiyItem;
import net.bigtangle.wallet.components.BaseLazyFragment;
import net.bigtangle.wallet.components.WrapContentLinearLayoutManager;
import net.bigtangle.wallet.core.WalletContextHolder;
import net.bigtangle.wallet.core.constant.LogConstant;
import net.bigtangle.wallet.core.http.URLUtil;
import net.bigtangle.wallet.core.utils.CommonUtil;

import java.io.InputStream;
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
        List<CertificateVO> certificates=null;
        try {
            String un = SPUtil.get(getContext(), "username", "").toString();
            InputStream stream = CommonUtil.loadFromDB(un, getContext());
            WalletContextHolder.loadWallet(stream);
            Log.i(LogConstant.TAG, "initdata 1" );
            certificates = new URLUtil().calculateCertificate().get();
        } catch (Exception e) {
            Log.i(LogConstant.TAG, "error1:"+e.getMessage() );
            e.printStackTrace();
        }
        itemList.clear();

        if (certificates != null && !certificates.isEmpty()) {
            for (CertificateVO certificateVO : certificates) {
                WalletAccountCertificateItem walletAccountCertificateItem = new WalletAccountCertificateItem();
                walletAccountCertificateItem.setTokenid(certificateVO.getTokenid());
                walletAccountCertificateItem.setDescription(certificateVO.getCertificate().getDescription());
                walletAccountCertificateItem.setPhoto(certificateVO.getCertificate().getFile());
                walletAccountCertificateItem.setIdtoken(certificateVO.getTokenid());
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
