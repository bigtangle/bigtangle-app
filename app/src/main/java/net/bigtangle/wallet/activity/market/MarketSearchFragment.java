package net.bigtangle.wallet.activity.market;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Switch;

import com.yarolegovich.lovelydialog.LovelyInfoDialog;

import net.bigtangle.core.ECKey;
import net.bigtangle.core.OrderRecord;
import net.bigtangle.core.Sha256Hash;
import net.bigtangle.core.response.OrderdataResponse;
import net.bigtangle.params.ReqCmd;
import net.bigtangle.utils.Json;
import net.bigtangle.utils.MarketOrderItem;
import net.bigtangle.utils.OkHttp3Util;
import net.bigtangle.utils.WalletUtil;
import net.bigtangle.wallet.R;
import net.bigtangle.wallet.activity.SPUtil;
import net.bigtangle.wallet.activity.market.adapter.CurAdapter;
import net.bigtangle.wallet.activity.market.adapter.MarketOrderItemListAdapter;
import net.bigtangle.wallet.components.BaseLazyFragment;
import net.bigtangle.wallet.components.WrapContentLinearLayoutManager;
import net.bigtangle.wallet.core.HttpService;
import net.bigtangle.wallet.core.WalletContextHolder;
import net.bigtangle.wallet.core.constant.HttpConnectConstant;
import net.bigtangle.wallet.core.constant.LogConstant;
import net.bigtangle.wallet.core.http.HttpNetComplete;
import net.bigtangle.wallet.core.http.HttpNetRunaDispatch;
import net.bigtangle.wallet.core.http.HttpRunaExecute;
import net.bigtangle.wallet.core.utils.CommonUtil;
import net.bigtangle.wallet.core.utils.UpdateUtil;

import org.apache.commons.lang3.StringUtils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;

public class MarketSearchFragment extends BaseLazyFragment implements SwipeRefreshLayout.OnRefreshListener {


    @BindView(R.id.only_me_switch)
    Switch onlyMeSwitch;

    @BindView(R.id.search_button)
    Button searchButton;

    @BindView(R.id.recycler_view_container)
    RecyclerView recyclerViewContainer;

    @BindView(R.id.swipe_container)
    SwipeRefreshLayout swipeContainer;


    private List<net.bigtangle.utils.MarketOrderItem> itemList;

    private MarketOrderItemListAdapter mAdapter;

    private ListView curListView;

    private CurAdapter adapter;

    private int lastPress = 0;

    private boolean delState = false;

    public static MarketSearchFragment newInstance() {
        return new MarketSearchFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (this.itemList == null) {
            this.itemList = new ArrayList<MarketOrderItem>();
        }
        setFroceLoadData(true);
        this.mAdapter = new MarketOrderItemListAdapter(getContext(), itemList);
//        longClickEvent();
    }

    ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            // 首先回调的方法 返回int表示是否监听该方向
            int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;//拖拽
            int swipeFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;//侧滑删除
            return makeMovementFlags(dragFlags, swipeFlags);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            // 滑动事件
            Collections.swap(itemList, viewHolder.getAdapterPosition(), target.getAdapterPosition());
            mAdapter.notifyItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            // 侧滑事件
            cancelOrderDo(itemList.get(viewHolder.getAdapterPosition()));
            itemList.remove(viewHolder.getAdapterPosition());
            mAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
        }

        @Override
        public boolean isLongPressDragEnabled() {
            // 是否可拖拽
            return true;
        }
    });

    private void cancelOrderDo(MarketOrderItem marketOrderItem) {
        try {
            String un = SPUtil.get(getContext(), "username", "").toString();
            InputStream stream = CommonUtil.loadFromDB(un, getContext());
            WalletContextHolder.loadWallet(stream);

           // WalletContextHolder.wallet.setServerURL(HttpConnectConstant.HTTP_SERVER_URL);
            Sha256Hash hash = Sha256Hash.wrap(marketOrderItem.getInitialBlockHashHex());
            WalletContextHolder.wallet.cancelOrder(hash, WalletContextHolder.get().getAesKey(), marketOrderItem.getAddress());

        } catch (Exception e) {
            HashMap<String, Object> infoMap = UpdateUtil.showExceptionInfo(e);
            ;
            new LovelyInfoDialog(getContext())
                    .setTopColorRes(R.color.colorPrimary)
                    .setIcon(R.drawable.ic_error_white_24px)
                    .setTitle(infoMap.get("eName").toString())
                    .setMessage(infoMap.get("eInfo").toString())
                    .show();

        }
    }

    private void longClickEvent() {
//        curListView = (ListView) findViewById(R.id.lv_contents);
//        adapter = new CurAdapter();
        curListView.setAdapter((ListAdapter) mAdapter);

        curListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (delState) {
                    if (lastPress < parent.getCount()) {
                        View delview = parent.getChildAt(lastPress).findViewById(R.id.linear_del);
                        if (null != delview) {
                            delview.setVisibility(View.GONE);
                        }
                    }
                    delState = false;
                    return;
                } else {
                    Log.d("click:", position + "");
                }
//                lastPress = position;
            }
        });
        curListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            private View delview;

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                if (lastPress < parent.getCount()) {
                    delview = parent.getChildAt(lastPress).findViewById(R.id.linear_del);
                    if (null != delview) {
                        delview.setVisibility(View.GONE);
                    }
                }

                delview = view.findViewById(R.id.linear_del);
                delview.setVisibility(View.VISIBLE);

                delview.findViewById(R.id.tv_del).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        delview.setVisibility(View.GONE);
                        itemList.remove(position);
                        adapter.notifyDataSetChanged();
                    }
                });
                delview.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        delview.setVisibility(View.GONE);
                    }
                });

                lastPress = position;
                delState = true;
                return true;
            }
        });
    }

    @Override
    public void onLazyLoad() {

    }

    private void initOrderdata() {
        String state = "publish";

        HashMap<String, Object> requestParam = new HashMap<String, Object>();
        requestParam.put("state", state);
        requestParam.put("spent", "publish".equals(state) ? "false" : "true");
        if (onlyMeSwitch.isChecked()) {
            String un = SPUtil.get(getContext(), "username", "").toString();
            InputStream stream = CommonUtil.loadFromDB(un, getContext());
            WalletContextHolder.loadWallet(stream);

            List<ECKey> walletKeys = WalletContextHolder.walletKeys();
            List<String> addressList = new ArrayList<String>();
            for (ECKey ecKey : walletKeys) {
                addressList.add(ecKey.toAddress(WalletContextHolder.networkParameters).toString());
            }
            requestParam.put("addresses", addressList);
        }

        new HttpNetRunaDispatch(this.getContext(), new HttpNetComplete() {
            @Override
            public void completeCallback(byte[] jsonStr) {
            }
        }, new HttpRunaExecute() {

            @Override
            public void execute() throws Exception {
                try {
                    Map<String, String> tokenNameMap = HttpService.getTokenNameMap();

                    byte[] jsonStr = OkHttp3Util.post(HttpConnectConstant.HTTP_SERVER_URL + ReqCmd.getOrders.name(),
                            Json.jsonmapper().writeValueAsString(requestParam).getBytes());

                    OrderdataResponse orderdataResponse = Json.jsonmapper().readValue(jsonStr, OrderdataResponse.class);
                    itemList.clear();
                    List<net.bigtangle.utils.MarketOrderItem> tempList = new ArrayList<MarketOrderItem>();

                    if (orderdataResponse.getAllOrdersSorted() != null && orderdataResponse.getAllOrdersSorted().size() > 0) {
                        for (OrderRecord orderRecord : orderdataResponse.getAllOrdersSorted()) {
                            MarketOrderItem marketOrderItem = MarketOrderItem.build(orderRecord, orderdataResponse.getTokennames(), WalletContextHolder.networkParameters, getString(R.string.buy), getString(R.string.sell));

                            String tokenName = tokenNameMap.get(marketOrderItem.getTokenId());
                            if (StringUtils.isBlank(tokenName)) {
                                tokenName = marketOrderItem.getTokenId();
                            }
                            marketOrderItem.setTokenName(tokenName);
                            if (getString(R.string.buy).equals(marketOrderItem.getType())) {
                                marketOrderItem.setType("buy");
                            } else {
                                marketOrderItem.setType("sell");
                            }
                            tempList.add(marketOrderItem);

                        }
                        if (tempList != null && !tempList.isEmpty()) {
                            tempList = WalletUtil.resetOrderList(tempList);
                            for (MarketOrderItem marketOrderItem : tempList) {
                                if ("buy".equals(marketOrderItem.getType()))
                                    marketOrderItem.setType(getString(R.string.buy));
                                else
                                    marketOrderItem.setType(getString(R.string.sell));
                                itemList.add(marketOrderItem);
                            }
                        }
                    }


                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.notifyDataSetChanged();
                        }
                    });
                } catch (Exception e) {
                    Log.e(LogConstant.TAG, "reqCmd getOrders failure to parse data", e);
                }
            }
        }).execute();
    }

    @Override
    public View initView(LayoutInflater inflater, @Nullable ViewGroup container) {
        return inflater.inflate(R.layout.fragment_market_search, container, false);
    }

    @Override
    public void initEvent() {
        this.searchButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                initOrderdata();
            }
        });


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.swipeContainer.setOnRefreshListener(this);
        LinearLayoutManager layoutManager = new WrapContentLinearLayoutManager(getContext());
        this.recyclerViewContainer.setHasFixedSize(true);
        this.recyclerViewContainer.setLayoutManager(layoutManager);
        this.recyclerViewContainer.setAdapter(mAdapter);
//        helper.attachToRecyclerView(this.recyclerViewContainer);
    }

    @Override
    public void onRefresh() {
        this.initOrderdata();
        this.swipeContainer.setRefreshing(false);
        this.mAdapter.notifyDataSetChanged();
    }
}
