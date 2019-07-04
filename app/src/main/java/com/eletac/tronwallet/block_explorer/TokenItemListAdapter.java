package com.eletac.tronwallet.block_explorer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.eletac.tronwallet.R;

public class TokenItemListAdapter extends RecyclerView.Adapter<TokenItemListAdapter.TokenItemViewHolder> {

    private Context mContext;
    private boolean showFiltered;

    public TokenItemListAdapter(Context context) {
        mContext = context;
        showFiltered = false;
    }

    @NonNull
    @Override
    public TokenItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_be_token_item, parent, false);
        TokenItemViewHolder viewHolder = new TokenItemViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TokenItemViewHolder holder, int position) {
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class TokenItemViewHolder extends RecyclerView.ViewHolder {
        private Context mContext;

        private TextView mName_TextView;
        private TextView mDescription_TextView;
        private TextView mSupply_TextView;
        private TextView mIssuer_TextView;
        private TextView mStart_TextView;
        private TextView mEnd_TextView;
        private TextView mLeft_TextView;
        private ProgressBar mLeft_ProgressBar;
        private Button mParticipate_Button;


        public TokenItemViewHolder(View itemView) {
            super(itemView);
            mContext = itemView.getContext();

            mName_TextView = itemView.findViewById(R.id.TokenBE_name_textView);
            mDescription_TextView = itemView.findViewById(R.id.TokenBE_description_textView);
            mSupply_TextView = itemView.findViewById(R.id.TokenBE_supply_textView);
            mIssuer_TextView = itemView.findViewById(R.id.TokenBE_issuer_textView);
            mStart_TextView = itemView.findViewById(R.id.TokenBE_start_textView);
            mEnd_TextView = itemView.findViewById(R.id.TokenBE_end_textView);
            mLeft_TextView = itemView.findViewById(R.id.TokenBE_left_textView);
            mLeft_ProgressBar = itemView.findViewById(R.id.TokenBE_left_progressBar);
            mParticipate_Button = itemView.findViewById(R.id.TokenBE_participate_button);

            mParticipate_Button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
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
