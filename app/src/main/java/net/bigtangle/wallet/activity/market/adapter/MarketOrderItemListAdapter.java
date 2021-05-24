package net.bigtangle.wallet.activity.market.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import net.bigtangle.core.ECKey;
import net.bigtangle.core.Sha256Hash;
import net.bigtangle.utils.MarketOrderItem;
import net.bigtangle.wallet.R;
import net.bigtangle.wallet.core.WalletContextHolder;
import net.bigtangle.wallet.core.constant.HttpConnectConstant;
import net.bigtangle.wallet.core.exception.ToastException;
import net.bigtangle.wallet.core.http.HttpNetComplete;
import net.bigtangle.wallet.core.http.HttpNetRunaDispatch;
import net.bigtangle.wallet.core.http.HttpRunaExecute;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MarketOrderItemListAdapter extends RecyclerView.Adapter<MarketOrderItemListAdapter.ItemViewHolder> {

    private Context mContext;

    private List<MarketOrderItem> itemList;

    private MarketOrderItemListAdapter.OnOrderRemCallbackListener onOrderRemCallbackListener;

    public MarketOrderItemListAdapter(Context context, List<MarketOrderItem> itemList) {
        this.mContext = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_market_order, parent, false);
        ItemViewHolder viewHolder = new ItemViewHolder(view);

        return viewHolder;
    }

    public void setOnOrderRemCallbackListener(OnOrderRemCallbackListener onOrderRemCallbackListener) {
        this.onOrderRemCallbackListener = onOrderRemCallbackListener;
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

        @BindView(R.id.amount_text_view)
        TextView amountTextView;

        @BindView(R.id.address_text_view)
        TextView addressTextView;

        @BindView(R.id.type_text_view)
        TextView typeTextView;

        @BindView(R.id.status_text_view)
        TextView statusTextView;

        @BindView(R.id.validate_to_text_view)
        TextView validateToTextView;

        @BindView(R.id.validate_from_text_view)
        TextView validateFromTextView;

        @BindView(R.id.token_name_text_view)
        TextView tokenNameTextView;

        @BindView(R.id.market_order_item_line)
        LinearLayout itemLine;

        @BindView(R.id.cancel_pending_text_view)
        TextView cancelPendingTextView;

        @BindView(R.id.total_text_view)
        TextView totalTextView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(MarketOrderItem marketOrderItem) {

            this.priceTextView.setText(marketOrderItem.getPrice());
            this.amountTextView.setText(String.valueOf(marketOrderItem.getAmount()));
            this.addressTextView.setText(marketOrderItem.getAddress());
            this.typeTextView.setText(marketOrderItem.getType());
            this.statusTextView.setText("");
            this.validateToTextView.setText(marketOrderItem.getValidateTo());
            this.validateFromTextView.setText(marketOrderItem.getValidateFrom());
            this.tokenNameTextView.setText(marketOrderItem.getTokenName());
            this.cancelPendingTextView.setText(marketOrderItem.isCancelPending() ? mContext.getString(R.string.yes) : mContext.getString(R.string.no));
            this.totalTextView.setText(marketOrderItem.getTotal());
            itemLine.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    LovelyStandardDialog lovelyStandardDialog = new LovelyStandardDialog(mContext, LovelyStandardDialog.ButtonLayout.HORIZONTAL);
                    lovelyStandardDialog.setTopColorRes(R.color.colorPrimary);
                    lovelyStandardDialog.setButtonsColor(Color.WHITE);
                    lovelyStandardDialog.setIcon(R.drawable.ic_error_white_24px);
                    lovelyStandardDialog.setTitle(mContext.getString(R.string.whether_to_delete));
                    lovelyStandardDialog.setMessage(mContext.getString(R.string.whether_to_delete_user_order_data));
                    lovelyStandardDialog.setPositiveButton(mContext.getString(R.string.ok), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new HttpNetRunaDispatch(mContext, new HttpNetComplete() {
                                @Override
                                public void completeCallback(byte[] jsonStr) {
                                    if (onOrderRemCallbackListener != null) {
                                        onOrderRemCallbackListener.refreshView();
                                    }
                                    Toast toast = Toast.makeText(mContext, R.string.order_remove_success, Toast.LENGTH_SHORT);
                                    toast.setGravity(Gravity.BOTTOM, 0, 0);
                                    toast.show();
                                }
                            }, new HttpRunaExecute() {
                                @Override
                                public void execute() throws Exception {
                                    removeOrderDo(marketOrderItem);
                                }
                            }).execute();
                        }
                    });
                    lovelyStandardDialog.setNegativeButton(mContext.getString(R.string.cancel), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        }
                    });
                    lovelyStandardDialog.show();
                    return true;
                }
            });
        }
    }

    private void removeOrderDo(MarketOrderItem marketOrderItem) throws Exception {
        if (marketOrderItem == null) {
            return;
        }
        WalletContextHolder.get().wallet().setServerURL(HttpConnectConstant.HTTP_SERVER_URL);
        Sha256Hash hash = Sha256Hash.wrap(marketOrderItem.getInitialBlockHashHex());
        ECKey legitimatingKey = null;

        boolean find = false;
        List<ECKey> keys = WalletContextHolder.get().wallet().walletKeys(WalletContextHolder.get().getAesKey());
        for (ECKey ecKey : keys) {
            if (marketOrderItem.getAddress().equals(ecKey.toAddress(WalletContextHolder.networkParameters).toString())) {
                legitimatingKey = ecKey;
                WalletContextHolder.get().wallet().cancelOrder(hash, legitimatingKey);
                find = true;
                break;
            }
        }

        if (!find) {
            throw new ToastException(mContext.getString(R.string.order_remove_fail));
        }
    }

    public interface OnOrderRemCallbackListener {

        void refreshView();
    }
}
