package net.bigtangle.wallet.activity.scanlogin.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.bigtangle.core.TokenType;
import net.bigtangle.wallet.R;
import net.bigtangle.wallet.activity.scanlogin.model.TokenInfoItem;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TokenInfoItemListAdapter extends RecyclerView.Adapter<TokenInfoItemListAdapter.ItemViewHolder> {

    private Context mContext;
    private List<TokenInfoItem> itemList;

    public TokenInfoItemListAdapter(Context context, List<TokenInfoItem> itemList) {
        this.mContext = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_token_search, parent, false);
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

        @BindView(R.id.token_id_text_view)
        TextView tokenIdTextView;

        @BindView(R.id.token_name_text_view)
        TextView tokenNameTextView;

        @BindView(R.id.token_type_text_view)
        TextView tokenTypeTextView;

        @BindView(R.id.amount_text_view)
        TextView amountTextView;

        @BindView(R.id.domainname_text_view)
        TextView domainnameTextView;

        @BindView(R.id.sign_number_text_view)
        TextView signNumberTextView;

        @BindView(R.id.description_text_view)
        TextView descriptionTextView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(TokenInfoItem tokenInfoItem) {
            this.tokenIdTextView.setText(tokenInfoItem.getTokenId());
            this.tokenNameTextView.setText(tokenInfoItem.getTokenName());

            for (TokenType tokenType : TokenType.values()) {
                if (tokenType.ordinal() == tokenInfoItem.getTokenType()) {
                    this.tokenTypeTextView.setText(tokenType.name());
                }
            }

            this.amountTextView.setText(String.valueOf(tokenInfoItem.getAmount()));
            this.domainnameTextView.setText(tokenInfoItem.getDomainMame());
            this.signNumberTextView.setText(String.valueOf(tokenInfoItem.getSignNumber()));
            this.descriptionTextView.setText(tokenInfoItem.getDescription());
        }
    }
}
