package net.bigtangle.wallet.activity.market;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import net.bigtangle.wallet.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author lijian
 * @date 2019-07-06 00:06:01
 */
public class MarketOrderFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.buy_btn)
    RadioButton buyBtn;
    @BindView(R.id.sell_btn)
    RadioButton sellBtn;
    @BindView(R.id.state_radio_group)
    RadioGroup statusRadioGroup;
    @BindView(R.id.address_spinner)
    Spinner addressSpinner;
    @BindView(R.id.pass_spinner)
    Spinner passSpinner;
    @BindView(R.id.unit_price_input)
    TextInputEditText unitPriceInput;
    @BindView(R.id.num_input)
    TextInputEditText numInput;
    @BindView(R.id.date_begin_input)
    TextInputEditText dateBeginInput;
    @BindView(R.id.date_end_input)
    TextInputEditText dateEndInput;
    @BindView(R.id.search_btn)
    Button searchBtn;

    ArrayAdapter<String> tokenAdapter;
    ArrayAdapter<String> addressAdapter;

    private String[] addressArray = {"1", "2", "3", "4"};
    private String[] tokenArray = {"1", "2", "3", "4"};

    public MarketOrderFragment() {
    }

    public static MarketOrderFragment newInstance() {
        MarketOrderFragment fragment = new MarketOrderFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addressAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, addressArray);
        addressAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        tokenAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, tokenArray);
        tokenAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_market_order, container, false);
        ButterKnife.bind(this, view);

        passSpinner.setAdapter(tokenAdapter);
        passSpinner.setSelection(0);

        addressSpinner.setAdapter(addressAdapter);
        addressSpinner.setSelection(0);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
