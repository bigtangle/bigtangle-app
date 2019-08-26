package net.bigtangle.wallet.activity.token;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import net.bigtangle.core.Json;
import net.bigtangle.params.ReqCmd;
import net.bigtangle.wallet.R;
import net.bigtangle.wallet.activity.token.adapter.TokenInfoItemListAdapter;
import net.bigtangle.wallet.activity.token.model.TokenInfoItem;
import net.bigtangle.wallet.components.BaseLazyFragment;
import net.bigtangle.wallet.components.WrapContentLinearLayoutManager;
import net.bigtangle.wallet.core.constant.LogConstant;
import net.bigtangle.wallet.core.http.HttpNetComplete;
import net.bigtangle.wallet.core.http.HttpNetTaskRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

/**
 * @author lijian
 * @date 2019-07-06 00:06:01
 */
public class TokenSearchFragment extends BaseLazyFragment implements SwipeRefreshLayout.OnRefreshListener {

    public static TokenSearchFragment newInstance() {
        return new TokenSearchFragment();
    }

    @BindView(R.id.recycler_view_container)
    RecyclerView recyclerViewContainer;

    @BindView(R.id.swipe_container)
    SwipeRefreshLayout swipeContainer;

    @BindView(R.id.address_text_input)
    TextInputEditText addressTextInput;

    @BindView(R.id.search_button)
    Button searchButton;

    private List<TokenInfoItem> itemList;

    private TokenInfoItemListAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (this.itemList == null) {
            this.itemList = new ArrayList<TokenInfoItem>();
        }
        setFroceLoadData(true);
        this.mAdapter = new TokenInfoItemListAdapter(getContext(), itemList);
    }

    @Override
    public void onLazyLoad() {
        String address = addressTextInput.getText().toString();
        HashMap<String, Object> requestParam = new HashMap<String, Object>();
        requestParam.put("name", address);

        new HttpNetTaskRequest(this.getContext()).httpRequest(ReqCmd.getTokens, requestParam, new HttpNetComplete() {
            @Override
            public void completeCallback(String jsonStr) {
                try {
                    Map<String, Object> data = Json.jsonmapper().readValue(jsonStr, Map.class);
                    List<Map<String, Object>> list = (List<Map<String, Object>>) data.get("tokens");
                    if (list != null) {
                        itemList.clear();
                        for (Map<String, Object> map : list) {
                            TokenInfoItem tokenInfoItem = new TokenInfoItem();
                            tokenInfoItem.setConfirmed((Boolean) map.get("confirmed"));
                            tokenInfoItem.setTokenId((String) map.get("tokenid"));
                            tokenInfoItem.setTokenIndex((Integer) map.get("tokenindex"));
                            tokenInfoItem.setTokenName((String) map.get("tokennameDisplay"));
                            tokenInfoItem.setDescription((String) map.get("description"));
                            tokenInfoItem.setDomainMame((String) map.get("domainname"));
                            tokenInfoItem.setSignNumber((Integer) map.get("signnumber"));
                            tokenInfoItem.setTokenType((Integer) map.get("tokentype"));
                            tokenInfoItem.setTokenStop((Boolean) map.get("tokenstop"));
                            tokenInfoItem.setAmount(String.valueOf(map.get("amount")));
                            itemList.add(tokenInfoItem);
                        }
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.notifyDataSetChanged();
                        }
                    });
                } catch (IOException e) {
                    Log.e(LogConstant.TAG, "ReqCmd.getTokens", e);
                }
            }
        });
    }

    @Override
    public View initView(LayoutInflater inflater, @Nullable ViewGroup container) {
        return inflater.inflate(R.layout.fragment_token_search, container, false);
    }

    @Override
    public void initEvent() {
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.swipeContainer.setOnRefreshListener(this);

        LinearLayoutManager layoutManager = new WrapContentLinearLayoutManager(getContext());
        this.recyclerViewContainer.setHasFixedSize(true);
        this.recyclerViewContainer.setLayoutManager(layoutManager);
        this.recyclerViewContainer.setAdapter(mAdapter);

        this.searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLazyLoad();
            }
        });
    }

    @Override
    public void onRefresh() {
        this.onLazyLoad();
        this.swipeContainer.setRefreshing(false);
        this.mAdapter.notifyDataSetChanged();
    }
}
