package com.eletac.tronwallet.block_explorer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eletac.tronwallet.R;

public class NodeItemListAdapter extends RecyclerView.Adapter<NodeItemListAdapter.NodeItemViewHolder> {

    private Context mContext;

    private boolean showFiltered;

    public NodeItemListAdapter(Context context) {
        mContext = context;
    }

    @NonNull
    @Override
    public NodeItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_node_item, parent, false);
        NodeItemViewHolder viewHolder = new NodeItemViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull NodeItemViewHolder holder, int position) {
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class NodeItemViewHolder extends RecyclerView.ViewHolder {
        private Context mContext;

        private TextView mIP_TextView;
        private TextView mPort_TextView;

        public NodeItemViewHolder(View itemView) {
            super(itemView);
            mContext = itemView.getContext();

            mIP_TextView = itemView.findViewById(R.id.Node_ip_textView);
            mPort_TextView = itemView.findViewById(R.id.Node_port_textView);
        }

        public void bind() {
        }
    }

    public boolean isShowFiltered() {
        return showFiltered;
    }

    public void setShowFiltered(boolean showFiltered) {
        this.showFiltered = showFiltered;
    }
}
