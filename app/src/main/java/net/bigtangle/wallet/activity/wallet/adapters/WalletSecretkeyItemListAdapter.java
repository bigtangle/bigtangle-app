package net.bigtangle.wallet.activity.wallet.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yarolegovich.lovelydialog.LovelyInfoDialog;

import net.bigtangle.wallet.R;
import net.bigtangle.wallet.activity.BackupActivity;
import net.bigtangle.wallet.activity.PrivatekeyActivity;
import net.bigtangle.wallet.activity.wallet.WalletAccountHisActivity;
import net.bigtangle.wallet.activity.wallet.dialog.PrivatekeyDialog;
import net.bigtangle.wallet.activity.wallet.dialog.WalletPasswordDialog;
import net.bigtangle.wallet.activity.wallet.model.WalletSecretkeyItem;
import net.bigtangle.wallet.core.WalletContextHolder;
import net.bigtangle.wallet.core.utils.CommonUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WalletSecretkeyItemListAdapter extends RecyclerView.Adapter<WalletSecretkeyItemListAdapter.ItemViewHolder> {

    private Context mContext;
    private List<WalletSecretkeyItem> itemList;

    public WalletSecretkeyItemListAdapter(Context context, List<WalletSecretkeyItem> itemList) {
        this.mContext = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_wallet_secretkey, parent, false);
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

        @BindView(R.id.addressTextView)
        TextView addressTextView;

        @BindView(R.id.pubkeyTextView)
        TextView pubkeyTextView;
        @BindView(R.id.address_qrcode_image)
        ImageView addressQrcodeImageView;
        @BindView(R.id.btn_privatekey)
        Button privatekeyButton;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(WalletSecretkeyItem walletSecretkeyItem) {
            addressTextView.setText(walletSecretkeyItem.getAddress());
            pubkeyTextView.setText(walletSecretkeyItem.getPubKeyHex());
            String content = "{\"tokenid\":\"" + walletSecretkeyItem.getAddress() + "\"}";
            Bitmap bitmap = CommonUtil.createQRCodeBitmap(content, 500, 500, "UTF-8", "H", "1", Color.BLACK, Color.WHITE);
            addressQrcodeImageView.setImageBitmap(bitmap);
            this.privatekeyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (WalletContextHolder.checkWalletHavePassword()) {
                        new PrivatekeyDialog(mContext, R.style.CustomDialogStyle)
                                .setListenter(new PrivatekeyDialog.OnWalletVerifyPasswordListenter() {

                                    @Override
                                    public void verifyPassword(String password) {
                                        Intent intent = new Intent(mContext, PrivatekeyActivity.class);
                                        intent.putExtra("privatekey", walletSecretkeyItem.getPrivateKey());
                                        mContext.startActivity(intent);
                                    }
                                }).show();
                    } else {
                        Intent intent = new Intent(mContext, PrivatekeyActivity.class);
                        intent.putExtra("privatekey", walletSecretkeyItem.getPrivateKey());
                        mContext.startActivity(intent);
                    }
                }
            });
        }
    }
}
