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

        @BindView(R.id.identitynumber_text_view)
        TextView identitynumberTextView;

        @BindView(R.id.name_text_view)
        TextView nameTextView;

        @BindView(R.id.sex_text_view)
        TextView sexTextView;

        @BindView(R.id.homeaddress_text_view)
        TextView homeaddressTextView;

        @BindView(R.id.birthday_text_view)
        TextView birthdayTextView;

        @BindView(R.id.photo_image_view)
        ImageView photoImageView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(WalletAccountIdentiyItem walletAccountIdentityItem) {
            this.identitynumberTextView.setText(walletAccountIdentityItem.getIdentitynumber());
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
