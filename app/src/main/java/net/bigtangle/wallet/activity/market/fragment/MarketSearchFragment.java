package net.bigtangle.wallet.activity.market.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;

import net.bigtangle.wallet.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author lijian
 * @date 2019-07-06 00:06:01
 */
public class MarketSearchFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.text_input)
    TextInputEditText textInput;
    @BindView(R.id.release_btn)
    RadioButton releaseBtn;
    @BindView(R.id.match_btn)
    RadioButton matchBtn;
    @BindView(R.id.status_radio_group)
    RadioGroup statusRadioGroup;
    @BindView(R.id.switch_btn)
    Switch switchBtn;
    @BindView(R.id.search_btn)
    Button searchBtn;
    @BindView(R.id.recyclerViewContainer)
    RecyclerView recyclerViewContainer;
    @BindView(R.id.swipeContainer)
    SwipeRefreshLayout swipeContainer;

    public MarketSearchFragment() {
    }

    public static MarketSearchFragment newInstance() {
        MarketSearchFragment fragment = new MarketSearchFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_market_search, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onRefresh() {
    }
}
