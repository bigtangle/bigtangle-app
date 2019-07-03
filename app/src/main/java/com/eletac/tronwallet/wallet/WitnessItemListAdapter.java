package com.eletac.tronwallet.wallet;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.eletac.tronwallet.R;

import java.util.HashMap;

public class WitnessItemListAdapter extends RecyclerView.Adapter<WitnessItemListAdapter.WitnessItemViewHolder> {

    public static final String VOTES_UPDATED = "com.eletac.tronwallet.witness_adapter.votes_updated";

    private Context mContext;
    private boolean mShowVoteEditText;

    private HashMap<String, String> mVotes;

    private boolean showFiltered = false;

    public WitnessItemListAdapter(Context context) {
        mContext = context;
        mShowVoteEditText = false;
        mVotes = null;
    }

    public WitnessItemListAdapter(Context context, boolean showVoteEditText, HashMap<String, String> votes) {
        mContext = context;
        mShowVoteEditText = showVoteEditText;
        mVotes = votes;
    }

    @NonNull
    @Override
    public WitnessItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_vote_witness_item, parent, false);
        WitnessItemViewHolder viewHolder = new WitnessItemViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull WitnessItemViewHolder holder, int position) {
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class WitnessItemViewHolder extends RecyclerView.ViewHolder {
        private Context mContext;

        private TextView mRank_TextView;
        private TextView mUrl_TextView;
        private TextView mAddress_TextView;
        private TextView mTotalVotes_TextView;
        private TextView mLastBlock_TextView;
        private TextView mProduced_TextView;
        private TextView mMissed_TextView;
        private EditText mVoteNumber_EditText;

        private TextView mTronSocietyAd_TextView;
        private ImageView mTronSocietyAdBorder_ImageView;

        private TextWatcher mTextWatcher;

        public WitnessItemViewHolder(View itemView) {
            super(itemView);
            mContext = itemView.getContext();

            mRank_TextView = itemView.findViewById(R.id.Witness_rank_textView);
            mUrl_TextView = itemView.findViewById(R.id.Witness_url_textView);
            mAddress_TextView = itemView.findViewById(R.id.Witness_address_textView);
            mTotalVotes_TextView = itemView.findViewById(R.id.Witness_total_votes_textView);
            mLastBlock_TextView = itemView.findViewById(R.id.Witness_last_block_textView);
            mProduced_TextView = itemView.findViewById(R.id.Witness_produced_textView);
            mMissed_TextView = itemView.findViewById(R.id.Witness_missed_textView);
            mVoteNumber_EditText = itemView.findViewById(R.id.Witness_votes_editText);
            mTronSocietyAd_TextView = itemView.findViewById(R.id.Witness_tron_society_ad_textView);
            mTronSocietyAdBorder_ImageView = itemView.findViewById(R.id.Witness_tron_society_ad_border_imageView);

            mVoteNumber_EditText.setVisibility(mShowVoteEditText ? View.VISIBLE : View.GONE);

            if (mShowVoteEditText) {
                mTextWatcher = new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                };

                mVoteNumber_EditText.addTextChangedListener(mTextWatcher);
            }
        }

    }

    public boolean isShowFiltered() {
        return showFiltered;
    }

    public void setShowFiltered(boolean showFiltered) {
        this.showFiltered = showFiltered;
    }
}
