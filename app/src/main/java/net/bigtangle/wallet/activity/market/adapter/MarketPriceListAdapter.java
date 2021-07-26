package net.bigtangle.wallet.activity.market.adapter;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import net.bigtangle.core.ECKey;
import net.bigtangle.core.Sha256Hash;
import net.bigtangle.utils.MarketOrderItem;
import net.bigtangle.wallet.R;
import net.bigtangle.wallet.activity.market.MarketPublishFragment;
import net.bigtangle.wallet.activity.market.model.MarketPrice;
import net.bigtangle.wallet.activity.wallet.WalletAccountHisActivity;
import net.bigtangle.wallet.core.WalletContextHolder;
import net.bigtangle.wallet.core.constant.HttpConnectConstant;
import net.bigtangle.wallet.core.exception.ToastException;
import net.bigtangle.wallet.core.http.HttpNetComplete;
import net.bigtangle.wallet.core.http.HttpNetRunaDispatch;
import net.bigtangle.wallet.core.http.HttpRunaExecute;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MarketPriceListAdapter extends RecyclerView.Adapter<MarketPriceListAdapter.ItemViewHolder> {

    private Context mContext;

    private List<MarketPrice> itemList;

    public MarketPriceListAdapter(Context context, List<MarketPrice> itemList) {
        this.mContext = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_market_price, parent, false);
        ItemViewHolder viewHolder = new ItemViewHolder(view);

        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.bind(itemList.get(position));
    }

    @Override
    public int getItemCount() {
        return itemList != null ? itemList.size() : 0;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {


        @BindView(R.id.price_text_view)
        TextView priceTextView;

        @BindView(R.id.tokenid_text_view)
        TextView tokenidTextView;

        @BindView(R.id.token_name_text_view)
        TextView tokennameTextView;

        @BindView(R.id.executedQuantity_text_view)
        TextView executedQuantityView;


        @BindView(R.id.chart_button)
        Button chartButton;
        @BindView(R.id.other_button)
        Button otherButton;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(MarketPrice marketPrice) {

            this.priceTextView.setText(marketPrice.getPrice());
            if (marketPrice.getPrice().indexOf("+") > 0)
                priceTextView.setTextColor(Color.RED);
            else priceTextView.setTextColor(Color.GREEN);
            this.tokenidTextView.setText(marketPrice.getTokenid());
            this.tokennameTextView.setText(marketPrice.getTokenname());
            this.executedQuantityView.setText(marketPrice.getExecutedQuantity());
            if(marketPrice.getUrl() ==null || "".equals(marketPrice.getUrl() ))
                    {
                otherButton.setVisibility(View.INVISIBLE);
                    }
            this.chartButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Intent intent = new Intent();
                                intent.setAction("android.intent.action.VIEW");
                                Uri content_url = Uri.parse(
                                        "https://m.bigtangle.xyz/chartdata/chart.html?tokenid=" + marketPrice.getTokenid());//此处填链接
                                intent.setData(content_url);
                                mContext.startActivity(intent);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
            });
            this.otherButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Intent intent = new Intent();
                                intent.setAction("android.intent.action.VIEW");
                                Uri content_url = Uri.parse(marketPrice.getUrl());//此处填链接
                                intent.setData(content_url);
                                mContext.startActivity(intent);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
            });
        }
    }


}
