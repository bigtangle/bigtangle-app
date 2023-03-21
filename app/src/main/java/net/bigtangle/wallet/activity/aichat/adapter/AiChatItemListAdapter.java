package net.bigtangle.wallet.activity.aichat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.bigtangle.core.TokenType;
import net.bigtangle.wallet.R;
import net.bigtangle.wallet.activity.aichat.model.AiChatItem;
import net.bigtangle.wallet.activity.token.model.TokenInfoItem;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AiChatItemListAdapter extends RecyclerView.Adapter<AiChatItemListAdapter.ItemViewHolder> {

    private Context mContext;
    private List<AiChatItem> itemList;

    public AiChatItemListAdapter(Context context, List<AiChatItem> itemList) {
        this.mContext = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_aichat, parent, false);
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


        @BindView(R.id.info_text_view)
        TextView infoTextView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(AiChatItem aiChatItem) {
            this.infoTextView.setText(aiChatItem.getInfo());
        }
    }
}
