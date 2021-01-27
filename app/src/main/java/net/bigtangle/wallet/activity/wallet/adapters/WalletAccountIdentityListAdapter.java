package net.bigtangle.wallet.activity.wallet.adapters;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.bigtangle.wallet.R;
import net.bigtangle.wallet.activity.wallet.model.WalletAccountHisItem;
import net.bigtangle.wallet.activity.wallet.model.WalletAccountIdentiyItem;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WalletAccountIdentityListAdapter extends RecyclerView.Adapter<WalletAccountIdentityListAdapter.ItemViewHolder> {

    private Context mContext;
    private List<WalletAccountIdentiyItem> itemList;

    public WalletAccountIdentityListAdapter(Context context, List<WalletAccountIdentiyItem> itemList) {
        this.mContext = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public WalletAccountIdentityListAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_wallet_account_identity_item, parent, false);
        WalletAccountIdentityListAdapter.ItemViewHolder viewHolder = new WalletAccountIdentityListAdapter.ItemViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull WalletAccountIdentityListAdapter.ItemViewHolder holder, int position) {
        holder.bind(itemList.get(position));
    }

    @Override
    public int getItemCount() {
        return itemList != null ? itemList.size() : 0;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.token_id_text_view)
        TextView identiynumberTextView;

        @BindView(R.id.token_name_text_view)
        TextView nameTextView;

        @BindView(R.id.amount_text_view)
        TextView sexTextView;

        @BindView(R.id.address_text_view)
        TextView homeaddressTextView;

        @BindView(R.id.memo_text_view)
        TextView birthdayTextView;

        @BindView(R.id.memo_text_view)
        ImageView photoImageView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(WalletAccountIdentiyItem walletAccountIdentityItem) {
            this.identiynumberTextView.setText(walletAccountIdentityItem.getIdentitynumber());
            this.nameTextView.setText(walletAccountIdentityItem.getName());
            this.sexTextView.setText(walletAccountIdentityItem.getSex());
            this.homeaddressTextView.setText(walletAccountIdentityItem.getHomeaddress());
            this.birthdayTextView.setText(walletAccountIdentityItem.getBirthday());

            byte[] photo = walletAccountIdentityItem.getPhoto();
            if (photo != null)
                photoImageView.setImageBitmap(BitmapFactory.decodeByteArray(photo, 0, photo.length));
        }
    }
}
