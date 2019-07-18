package net.bigtangle.wallet.components;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import net.bigtangle.wallet.R;

import java.util.List;

public class TokenItemListAdapter extends BaseAdapter {

    private Context mContext;
    private List<TokenItem> itemList;

    public TokenItemListAdapter(Context context, List<TokenItem> itemList) {
        this.mContext = context;
        this.itemList = itemList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater _LayoutInflater = LayoutInflater.from(mContext);
        convertView = _LayoutInflater.inflate(R.layout.list_token_item, null);
        if (convertView != null) {
            TextView tokenNameTextView = convertView.findViewById(R.id.tokenNameTextView);
            TextView tokenIdTextView = convertView.findViewById(R.id.tokenIdTextView);

            TokenItem tokenItem = this.itemList.get(position);
            tokenNameTextView.setText(tokenItem.getTokenName());
            tokenIdTextView.setText(tokenItem.getTokenId());
        }
        return convertView;
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public Object getItem(int position) {
        return itemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
