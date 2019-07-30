package net.bigtangle.wallet.activity.market;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.yarolegovich.lovelydialog.LovelyInfoDialog;

import net.bigtangle.wallet.R;
import net.bigtangle.wallet.components.BaseLazyFragment;

import butterknife.BindView;

public class MarketOrderFragment extends BaseLazyFragment {

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

    @BindView(R.id.startShowDialog)
    TextInputEditText dateBeginInput;

    @BindView(R.id.endShowDialog)
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
    public void onLazyLoad() {
    }

    @Override
    public View initView(LayoutInflater inflater, @Nullable ViewGroup container) {
        return inflater.inflate(R.layout.fragment_market_order, container, false);
    }

    @Override
    public void initEvent() {
        passSpinner.setAdapter(tokenAdapter);
        passSpinner.setSelection(0);

        addressSpinner.setAdapter(addressAdapter);
        addressSpinner.setSelection(0);

        this.searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //todo
                new LovelyInfoDialog(getContext())
                        .setTopColorRes(R.color.colorPrimary)
                        .setIcon(R.drawable.ic_error_white_24px)
                        .setTitle(getContext().getString(R.string.dialog_title_error))
                        .setMessage(getContext().getString(R.string.network_response_data_failed))
                        .show();
            }
        });
    }
}
