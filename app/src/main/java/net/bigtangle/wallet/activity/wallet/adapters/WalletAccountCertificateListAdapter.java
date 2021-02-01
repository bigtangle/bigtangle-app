package net.bigtangle.wallet.activity.wallet.adapters;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.bigtangle.wallet.R;
import net.bigtangle.wallet.activity.wallet.model.WalletAccountCertificateItem;
import net.bigtangle.wallet.activity.wallet.model.WalletAccountIdentiyItem;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WalletAccountCertificateListAdapter extends RecyclerView.Adapter<WalletAccountCertificateListAdapter.ItemViewHolder> {

    private Context mContext;
    private List<WalletAccountCertificateItem> itemList;

    public WalletAccountCertificateListAdapter(Context context, List<WalletAccountCertificateItem> itemList) {
        this.mContext = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public WalletAccountCertificateListAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_wallet_account_certificate_item, parent, false);
        ItemViewHolder viewHolder = new ItemViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull WalletAccountCertificateListAdapter.ItemViewHolder holder, int position) {
        holder.bind(itemList.get(position));
    }

    @Override
    public int getItemCount() {
        return itemList != null ? itemList.size() : 0;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.certificate_description_text_view)
        TextView descriptionTextView;

        @BindView(R.id.certificate_photo_image_view)
        ImageView photoImageView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(WalletAccountCertificateItem walletAccountCertificateItem) {
            this.descriptionTextView.setText(walletAccountCertificateItem.getDescription());
            descriptionTextView.setMovementMethod(new ScrollingMovementMethod());

            byte[] photo = walletAccountCertificateItem.getPhoto();
            if (photo != null)
                photoImageView.setImageBitmap(BitmapFactory.decodeByteArray(photo, 0, photo.length));
        }
    }
}
