package net.bigtangle.wallet.activity;

import android.view.View;

public interface OnItemClickListener {

    interface Normal {

        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);

        class Builder implements Normal {

            @Override
            public void onItemClick(View view, int position) {

            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }
    }
}

