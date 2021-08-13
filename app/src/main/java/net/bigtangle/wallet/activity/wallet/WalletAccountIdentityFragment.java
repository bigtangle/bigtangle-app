package net.bigtangle.wallet.activity.wallet;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import net.bigtangle.wallet.R;
import net.bigtangle.wallet.activity.AliMainActivity;
import net.bigtangle.wallet.activity.SPUtil;
import net.bigtangle.wallet.activity.wallet.adapters.WalletAccountIdentityListAdapter;
import net.bigtangle.wallet.activity.wallet.model.IdentityVO;
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

import butterknife.BindView;

public class WalletAccountIdentityFragment extends BaseLazyFragment implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.recycler_view_container)
    RecyclerView recyclerViewContainer;

    @BindView(R.id.swipe_container)
    SwipeRefreshLayout swipeContainer;

    @BindView(R.id.alistart_button)
    Button aliverifyButton;
    private WalletAccountIdentityListAdapter mAdapter;

    private List<WalletAccountIdentiyItem> itemList;

    public static WalletAccountIdentityFragment newInstance() {
        return new WalletAccountIdentityFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (this.itemList == null) {
            this.itemList = new ArrayList<WalletAccountIdentiyItem>();
        }
        setFroceLoadData(true);
        this.mAdapter = new WalletAccountIdentityListAdapter(getContext(), this.itemList);

    }

    private void initData() {

        String un = SPUtil.get(getContext(), "username", "").toString();
        InputStream stream = CommonUtil.loadFromDB(un, getContext());
        WalletContextHolder.loadWallet(stream);

        List<IdentityVO> identityDatas = null;
        try {
            identityDatas = new URLUtil().calculateIdentity().get();

        } catch (Exception e) {

        }

        itemList.clear();

        if (identityDatas != null && !identityDatas.isEmpty()) {
            for (IdentityVO identityVO : identityDatas) {
                WalletAccountIdentiyItem walletAccountIdentiyItem = new WalletAccountIdentiyItem();
                walletAccountIdentiyItem.setTokenid(identityVO.getTokenid());
                walletAccountIdentiyItem.setName(identityVO.getIdentityData().getIdentityCore().getSurname());
                walletAccountIdentiyItem.setIdentitynumber(identityVO.getIdentityData().getIdentificationnumber());
                walletAccountIdentiyItem.setHomeaddress(identityVO.getIdentityData().getIdentityCore().getPlaceofbirth());
                walletAccountIdentiyItem.setSex(getSex(identityVO.getIdentityData().getIdentityCore().getSex()));
                walletAccountIdentiyItem.setPhoto(identityVO.getIdentityData().getPhoto());
                walletAccountIdentiyItem.setIdtoken(identityVO.getTokenid());
                Log.i(LogConstant.TAG, "initData " + walletAccountIdentiyItem.getIdentitynumber());
                itemList.add(walletAccountIdentiyItem);
            }
            Log.i(LogConstant.TAG, "initData itemList.size()" + itemList.size());
            this.mAdapter.notifyDataSetChanged();
        }

    }

    private String getSex(String sex) {
        return "Male".equals(sex) ? "男" : "女";
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
        return inflater.inflate(R.layout.fragment_wallet_identity, container, false);
    }

    @Override
    public void initEvent() {
        this.aliverifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AliMainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }


}
